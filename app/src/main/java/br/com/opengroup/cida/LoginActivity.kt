package br.com.opengroup.cida

import LocalDatabaseHelper
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import br.com.opengroup.cida.api.RetrofitHelper
import br.com.opengroup.cida.api.UsuarioAPI
import br.com.opengroup.cida.database.FirestoreLogger
import br.com.opengroup.cida.model.Login
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    private lateinit var btnEnter: Button
    private lateinit var tvCreateAccount: TextView
    private lateinit var tilEmail: TextInputLayout
    private lateinit var tilPassword: TextInputLayout
    private lateinit var loadingContainer: CardView

    private var credentials: Pair<Int, Pair<String, String>>? = null
    private val retrofit by lazy { RetrofitHelper.retrofit }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnEnter = findViewById(R.id.btnEnter)
        tvCreateAccount = findViewById(R.id.tvCreateAccount)
        tilEmail = findViewById(R.id.tilEmail)
        tilPassword = findViewById(R.id.tilPassword)
        loadingContainer = findViewById(R.id.loadingContainer)

        btnEnter.setOnClickListener {
            if(validateInputs())
                loginUser()
        }
        tvCreateAccount.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        credentials = LocalDatabaseHelper(this).selectCredenciais()
        if(credentials != null) {
            startActivity(Intent(this, UploadActivity::class.java))
        }

    }
    private fun validateInputs(): Boolean {
        if(tilEmail.editText?.text.toString().isEmpty())
            Toast.makeText(this, "Email não pode ser vazio!", Toast.LENGTH_SHORT).show()
        else if(tilPassword.editText?.text.toString().isEmpty())
            Toast.makeText(this, "Senha não pode ser vazia!", Toast.LENGTH_SHORT).show()
        else
            return true
        return false
    }
    private fun loginUser() {
        showLoading(true)
        val api = retrofit.create(UsuarioAPI::class.java)
        val login = Login(
            email = tilEmail.editText?.text.toString(),
            senha = tilPassword.editText?.text.toString()
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.autenticarUsuario(login)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Log.d("LoginActivityLogin", "Usuario logado com sucesso!")
                        Toast.makeText(this@LoginActivity, "Usuário logado com sucesso! Aguarde...", Toast.LENGTH_SHORT).show()
                        val usuarioResponseResponse = api.consultarUsuario(login.email)
                        val idUsuario = usuarioResponseResponse.body()!!.idUsuario
                        LocalDatabaseHelper(this@LoginActivity).updateCredenciais(idUsuario, login.email, login.senha)
                        val intent = Intent(this@LoginActivity, UploadActivity::class.java)
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                        finish()
                    } else {
                        Log.e("LoginActivityLogin", "Erro ao logar usuário: ${response.message()}")
                        Toast.makeText(this@LoginActivity, "Erro ao logar usuário: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                FirestoreLogger.log("Erro ao logar usuário ${tilEmail.editText?.text.toString()}", tilEmail.editText?.text.toString())
                withContext(Dispatchers.Main) {
                    Log.e("LoginActivityLogin", "Erro de conexão: ${e.message}")
                    Toast.makeText(this@LoginActivity, "Erro de conexão: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
            finally {
                showLoading(false)
            }
        }

    }
    private fun showLoading(show: Boolean) {
        runOnUiThread {
            loadingContainer.visibility = if (show) View.VISIBLE else View.GONE
            btnEnter.isEnabled = !show
            btnEnter.alpha = if (show) 0.5f else 1.0f
        }
    }
}