package br.com.opengroup.cida

import DatabaseHelper
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.opengroup.cida.api.ArquivoApi
import br.com.opengroup.cida.api.RetrofitHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.Response
import java.io.File

class UploadActivity: AppCompatActivity() {
    private lateinit var btnSendDocuments: Button
    private lateinit var btnUpload: ImageView
    private lateinit var txtUpload: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var txtUploadStatus: TextView
    private lateinit var btnInsights: Button

    private val PICK_FILES_REQUEST_CODE = 100
    private var selectedFileParts: List<MultipartBody.Part> = listOf()
    private val retrofit by lazy { RetrofitHelper.retrofit }
    private var credentials: Pair<Int, Pair<String, String>>? = null
    private var isUploading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        initializeViews()
        setupClickListeners()
    }

    private fun initializeViews() {
        btnSendDocuments = findViewById(R.id.btnSendDocuments)
        btnUpload = findViewById(R.id.btnUpload)
        txtUpload = findViewById(R.id.txtUpload)
        progressBar = findViewById(R.id.progressBar)
        txtUploadStatus = findViewById(R.id.txtUploadStatus)
        btnInsights = findViewById(R.id.btnInsights)

        // Initially hide loading elements
        progressBar.visibility = View.GONE
        txtUploadStatus.visibility = View.GONE
    }

    private fun setupClickListeners() {
        txtUpload.setOnClickListener { if (!isUploading) openFilePicker() }
        btnUpload.setOnClickListener { if (!isUploading) openFilePicker() }
        btnSendDocuments.setOnClickListener { if (!isUploading) sendFiles() }
        btnInsights.setOnClickListener { startActivity(Intent(this, DashboardActivity::class.java)) }
    }

    private fun showLoading(show: Boolean) {
        isUploading = show
        runOnUiThread {
            progressBar.visibility = if (show) View.VISIBLE else View.GONE
            txtUploadStatus.visibility = if (show) View.VISIBLE else View.GONE
            btnSendDocuments.isEnabled = !show
            btnUpload.isEnabled = !show
            txtUpload.isEnabled = !show

            if (show) {
                btnSendDocuments.alpha = 0.5f
                txtUploadStatus.text = "Fazendo upload de arquivos, aguarde, isso pode demorar alguns minutos..."
            } else {
                btnSendDocuments.alpha = 1.0f
                txtUploadStatus.text = ""
            }
        }
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "*/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        startActivityForResult(intent, PICK_FILES_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_FILES_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val fileUris = mutableListOf<Uri>()

            if (data?.clipData != null) {
                val clipData = data.clipData
                for (i in 0 until clipData!!.itemCount) {
                    val uri = clipData.getItemAt(i).uri
                    fileUris.add(uri)
                }
            } else if (data?.data != null) {
                fileUris.add(data.data!!)
            }

            selectedFileParts = fileUris.mapNotNull { uri -> uriToFilePart(uri) }

            // Update UI to show number of files selected
            runOnUiThread {
                txtUpload.text = "${selectedFileParts.size} arquivo(s) selecionado(s)"
                btnSendDocuments.isEnabled = selectedFileParts.isNotEmpty()
            }
        }
    }

    private fun uriToFilePart(uri: Uri): MultipartBody.Part? {
        val file = uriToFile(uri) ?: return null
        val mimeType = contentResolver.getType(uri) ?: "application/octet-stream"
        val requestBody = RequestBody.create(MediaType.parse(mimeType), file)
        val originalFileName = getOriginalFileName(uri)
        return MultipartBody.Part.createFormData("files", originalFileName, requestBody)
    }

    private fun getOriginalFileName(uri: Uri): String {
        var fileName = "file_${System.currentTimeMillis()}"
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    fileName = cursor.getString(nameIndex)
                }
            }
        }
        return fileName
    }

    private fun uriToFile(uri: Uri): File? {
        val inputStream = contentResolver.openInputStream(uri) ?: return null
        val file = File(cacheDir, uri.lastPathSegment ?: "tempFile")
        file.outputStream().use { output -> inputStream.copyTo(output) }
        return file
    }

    private fun sendFiles() {
        credentials = DatabaseHelper(this).selectCredenciais()
        if (credentials == null) {
            Toast.makeText(this, "Credenciais não encontradas", Toast.LENGTH_SHORT).show()
            return
        }
        val userId = credentials!!.first.toString()

        if (selectedFileParts.isEmpty()) {
            Toast.makeText(this, "Nenhum Arquivo Selecionado", Toast.LENGTH_SHORT).show()
            return
        }

        // Show loading state
        showLoading(true)

        val api = retrofit.create(ArquivoApi::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Log upload start
                Log.d("UploadActivity", "Starting upload of ${selectedFileParts.size} files")

                val response = api.uploadArquivo(userId, selectedFileParts)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@UploadActivity, "Files uploaded successfully", Toast.LENGTH_SHORT).show()
                        // Reset selection after successful upload
                        selectedFileParts = listOf()
                        txtUpload.text = "Escolha seu arquivo"
                    } else {
                        Toast.makeText(this@UploadActivity, "Upload failed: ${response.errorBody()?.string()}", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("UploadActivity", "Upload error", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@UploadActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            } finally {
                // Hide loading state
                showLoading(false)
            }
        }
    }
}