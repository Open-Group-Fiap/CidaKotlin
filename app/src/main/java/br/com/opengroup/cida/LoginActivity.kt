package br.com.opengroup.cida

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import br.com.opengroup.cida.api.RetrofitHelper
import br.com.opengroup.cida.api.UsuarioAPI
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
    private val retrofit by lazy { RetrofitHelper.retrofit }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnEnter = findViewById(R.id.btnEnter)
        tvCreateAccount = findViewById(R.id.tvCreateAccount)
        tilEmail = findViewById(R.id.tilEmail)
        tilPassword = findViewById(R.id.tilPassword)

        btnEnter.setOnClickListener {
            loginUser()
        }
        tvCreateAccount.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

    }

    private fun loginUser() {
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
                        Toast.makeText(this@LoginActivity, "Usuário logado com sucesso!", Toast.LENGTH_SHORT).show()
                        // Navigate to login or main activity
                        startActivity(Intent(this@LoginActivity, UploadActivity::class.java))
                        finish()
                    } else {
                        Log.e("LoginActivityLogin", "Erro ao logar usuário: ${response.message()}")
                        Toast.makeText(this@LoginActivity, "Erro ao logar usuário: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("LoginActivityLogin", "Erro de conexão: ${e.message}")
                    Toast.makeText(this@LoginActivity, "Erro de conexão: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
}