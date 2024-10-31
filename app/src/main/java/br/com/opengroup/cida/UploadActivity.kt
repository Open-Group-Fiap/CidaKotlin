package br.com.opengroup.cida

import DatabaseHelper
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.opengroup.cida.api.ArquivoApi
import br.com.opengroup.cida.api.RetrofitHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.Response
import java.io.File

class UploadActivity: AppCompatActivity() {
    private lateinit var btnSendDocuments: Button
    private lateinit var btnUpload: ImageView
    private lateinit var txtUpload: TextView
    private val PICK_FILES_REQUEST_CODE = 100
    private var selectedFileParts: List<MultipartBody.Part> = listOf()
    private val retrofit by lazy { RetrofitHelper.retrofit }
    private var credentials: Pair<Int, Pair<String, String>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        btnSendDocuments = findViewById(R.id.btnSendDocuments)
        btnUpload = findViewById(R.id.btnUpload)
        txtUpload = findViewById(R.id.txtUpload)

        txtUpload.setOnClickListener { openFilePicker() }
        btnUpload.setOnClickListener { openFilePicker() }

        btnSendDocuments.setOnClickListener { sendFiles() }

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

            // Prepare files for upload
            selectedFileParts = fileUris.mapNotNull { uri -> uriToFilePart(uri) }
        }
    }
    private fun uriToFilePart(uri: Uri): MultipartBody.Part? {
        val file = uriToFile(uri) ?: return null
        val requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), file)
        return MultipartBody.Part.createFormData("files", file.name, requestBody)
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
            Toast.makeText(this, "No files selected", Toast.LENGTH_SHORT).show()
            return
        }

        val api = retrofit.create(ArquivoApi::class.java)
        // Make the upload call using Coroutines
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.uploadArquivo(userId, selectedFileParts)
                Log.d("UploadActivity", "Response: $response")
                if (response.isSuccessful) {
                    runOnUiThread { Toast.makeText(this@UploadActivity, "Files uploaded successfully", Toast.LENGTH_SHORT).show() }
                } else {
                    runOnUiThread { Toast.makeText(this@UploadActivity, "Upload failed", Toast.LENGTH_SHORT).show() }
                }
            } catch (e: Exception) {
                runOnUiThread { Toast.makeText(this@UploadActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show() }
            }
        }
    }


}