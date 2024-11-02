package br.com.opengroup.cida

import LocalDatabaseHelper
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import br.com.opengroup.cida.api.RetrofitHelper
import br.com.opengroup.cida.api.UsuarioAPI
import br.com.opengroup.cida.database.FirestoreLogger
import br.com.opengroup.cida.model.UsuarioRequest
import br.com.opengroup.cida.model.UsuarioResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UsuarioActivity : AppCompatActivity() {

    private lateinit var etUserName: EditText
    private lateinit var etUserEmail: EditText
    private lateinit var etUserPhone: EditText
    private lateinit var btnSave: Button
    private lateinit var btnLogout: Button
    private lateinit var btnDelete: Button
    private lateinit var loadingContainer: CardView
    private lateinit var progressBar: ProgressBar
    private lateinit var txtLoadingStatus: TextView
    private lateinit var toolbar: Toolbar
    private lateinit var usuario: UsuarioResponse
    private var isLoading = false

    private val retrofit by lazy { RetrofitHelper.retrofit }

    private val credentials by lazy {LocalDatabaseHelper(this).selectCredenciais()}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usuario)

        etUserName = findViewById(R.id.etUserName)
        etUserEmail = findViewById(R.id.etUserEmail)
        etUserPhone = findViewById(R.id.etUserPhone)
        btnSave = findViewById(R.id.btnSave)
        btnLogout = findViewById(R.id.btnLogout)
        btnDelete = findViewById(R.id.btnDelete)
        loadingContainer = findViewById(R.id.loadingContainer)
        progressBar = findViewById(R.id.progressBar)
        txtLoadingStatus = findViewById(R.id.txtLoadingStatus)
        toolbar = findViewById(R.id.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        btnSave.setOnClickListener {
            if(!isLoading)
                saveUserData()
        }

        btnLogout.setOnClickListener {
            if(!isLoading)
                logout()
        }
        btnDelete.setOnClickListener {
            if (!isLoading) showDeleteConfirmationDialog()
        }
    }

    override fun onStart() {
        super.onStart()
        showLoading(true)

        CoroutineScope(Dispatchers.IO).launch {
            val usuarioAPI = retrofit.create(UsuarioAPI::class.java)

            try {
                val response = usuarioAPI.consultarUsuario(credentials?.second?.first!!)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        usuario = response.body()!!
                        etUserName.setText(usuario.nome)
                        etUserEmail.setText(credentials?.second?.first!!)
                        etUserPhone.setText(usuario.telefone)
                    } else {
                        FirestoreLogger.log("Erro ao carregar dados do usuário, erro: ${response.message()}", credentials?.second?.first!!)
                        Toast.makeText(this@UsuarioActivity, "Failed to load user data", Toast.LENGTH_SHORT).show()

                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    FirestoreLogger.log("Erro ao carregar dados do usuário, erro: ${e.message}", credentials?.second?.first!!)
                    Toast.makeText(this@UsuarioActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } finally {
                withContext(Dispatchers.Main) {
                    showLoading(false)
                }
            }
        }
    }
    private fun showDeleteConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmar exclusão")
        builder.setMessage("Você tem certeza que deseja excluir sua conta? Esta ação não pode ser desfeita.")
        builder.setPositiveButton("Sim") { dialog, _ ->
            dialog.dismiss()
            deleteUser()
        }
        builder.setNegativeButton("Não") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }
    private fun deleteUser() {
        showLoading(true)
        CoroutineScope(Dispatchers.IO).launch {
            val usuarioAPI = retrofit.create(UsuarioAPI::class.java)
            try {
                val response = usuarioAPI.deletarUsuario(usuario.idUsuario.toString())
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        FirestoreLogger.log("Conta apagada com sucesso", credentials?.second?.first!!)
                        Toast.makeText(this@UsuarioActivity, "Conta apagada com sucesso", Toast.LENGTH_SHORT).show()
                        logout()
                    } else {
                        FirestoreLogger.log("Erro ao apagar conta, erro: ${response.message()}", credentials?.second?.first!!)
                        Toast.makeText(this@UsuarioActivity, "Erro ao apagar conta", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    FirestoreLogger.log("Erro ao apagar conta, erro: ${e.message}", credentials?.second?.first!!)
                    Toast.makeText(this@UsuarioActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } finally {
                withContext(Dispatchers.Main) {
                    showLoading(false)
                }
            }
        }
    }

    private fun saveUserData() {
        showLoading(true)
        val userName = etUserName.text.toString()
        val userEmail = etUserEmail.text.toString()
        val userPhone = etUserPhone.text.toString()

        val usuarioRequest = UsuarioRequest(
            email = userEmail,
            senha = credentials?.second?.second!!,
            nome = userName,
            numDocumento = usuario.numDocumento,
            telefone = userPhone
        )

        CoroutineScope(Dispatchers.IO).launch {
            val usuarioAPI = retrofit.create(UsuarioAPI::class.java)

            try {
                val response = usuarioAPI.atualizarUsuario(usuario.idUsuario.toString(), usuarioRequest)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val usuarioResponse: UsuarioResponse? = response.body()

                        if (usuarioResponse != null) {
                            LocalDatabaseHelper(this@UsuarioActivity).updateCredenciais(usuarioResponse.idUsuario, credentials?.second?.first!!, usuarioRequest.senha)
                        }

                        FirestoreLogger.log("Dados de usuário atualizados com sucesso", credentials?.second?.first!!)
                        Toast.makeText(this@UsuarioActivity, "Dados de usuário atualizados com sucesso!", Toast.LENGTH_SHORT).show()
                    } else {
                        FirestoreLogger.log("Erro ao atualizar dados, erro: ${response.message()}", credentials?.second?.first!!)
                        Toast.makeText(this@UsuarioActivity, "Erro ao atualizar dados", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    FirestoreLogger.log("Erro ao atualizar dados, erro: ${e.message}", credentials?.second?.first!!)
                    Toast.makeText(this@UsuarioActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } finally {
                withContext(Dispatchers.Main) {
                    showLoading(false)
                }
            }
        }
    }

    private fun logout() {
        LocalDatabaseHelper(this).removeCredentials()
        val intent = Intent(this, MainActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }

    private fun showLoading(show: Boolean) {
        isLoading = show
        loadingContainer.visibility = if (show) View.VISIBLE else View.GONE
    }
}