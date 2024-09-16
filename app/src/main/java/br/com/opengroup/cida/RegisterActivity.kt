package br.com.opengroup.cida

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.opengroup.cida.api.RetrofitHelper
import br.com.opengroup.cida.api.UsuarioAPI
import br.com.opengroup.cida.model.Usuario
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.Date
import com.google.gson.Gson
import java.text.SimpleDateFormat

class RegisterActivity : AppCompatActivity() {
    private lateinit var btnCreateAccount: Button
    private lateinit var tvLogin: TextView
    private lateinit var tilCompanyName: TextInputLayout
    private lateinit var tilEmail: TextInputLayout
    private lateinit var tilPhone: TextInputLayout
    private lateinit var tilCNPJ: TextInputLayout
    private lateinit var tilPassword: TextInputLayout
    private lateinit var tilConfirmPassword: TextInputLayout

    private val retrofit by lazy { RetrofitHelper.retrofit }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        btnCreateAccount = findViewById(R.id.btnCreateAccount)
        tvLogin = findViewById(R.id.tvLogin)
        tilCompanyName = findViewById(R.id.tilCompanyName)
        tilEmail = findViewById(R.id.tilEmail)
        tilPhone = findViewById(R.id.tilPhone)
        tilCNPJ = findViewById(R.id.tilCNPJ)
        tilPassword = findViewById(R.id.tilPassword)
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword)

        btnCreateAccount.setOnClickListener {
            if(validateInputs())
                registerUser()
        }

        tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
    fun validateInputs(): Boolean {
        if(tilEmail.editText?.text.toString().isEmpty())
            Toast.makeText(this, "Email não pode ser vazio!", Toast.LENGTH_SHORT).show()
        else if(tilPassword.editText?.text.toString().isEmpty())
            Toast.makeText(this, "Senha não pode ser vazia!", Toast.LENGTH_SHORT).show()
        else if(tilConfirmPassword.editText?.text.toString().isEmpty())
            Toast.makeText(this, "Confirmar senha não pode ser vazia!", Toast.LENGTH_SHORT).show()
        else if(tilPassword.editText?.text.toString() != tilConfirmPassword.editText?.text.toString())
            Toast.makeText(this, "Senhas não coincidem!", Toast.LENGTH_SHORT).show()
        else if(tilCNPJ.editText?.text.toString().isEmpty())
            Toast.makeText(this, "CNPJ não pode ser vazio!", Toast.LENGTH_SHORT).show()
        else if(tilCompanyName.editText?.text.toString().isEmpty())
            Toast.makeText(this, "Nome da empresa não pode ser vazio!", Toast.LENGTH_SHORT).show()
        else if(tilPhone.editText?.text.toString().isEmpty())
            Toast.makeText(this, "Telefone não pode ser vazio!", Toast.LENGTH_SHORT).show()
        else
            return true
        return false
    }
    private fun getDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSXXX")
        val date = Date()
        return dateFormat.format(date)
    }

    private fun registerUser() {
        val api = retrofit.create(UsuarioAPI::class.java)
        val usuario = Usuario(
            email = tilEmail.editText?.text.toString(),
            senha = tilPassword.editText?.text.toString(),
            nome = tilCompanyName.editText?.text.toString(),
            tipoDocumento = 1,
            numDocumento = tilCNPJ.editText?.text.toString(),
            telefone = tilPhone.editText?.text.toString(),
            // Date now
            dataCriacao = getDate(),
            status = 0
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.cadastrarUsuario(usuario)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@RegisterActivity, "Usuário cadastrado com sucesso!", Toast.LENGTH_SHORT).show()
                        // Navigate to login or main activity
                        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                        finish()
                    } else {
                        Log.e("RegisterActivityError", response.body().toString())
                        Log.d("RegisterActivityError", Gson().toJson(usuario))
                        Toast.makeText(this@RegisterActivity, "Erro ao cadastrar usuário: ${response.body().toString()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("RegisterActivityError", e.message.toString())
                    Toast.makeText(this@RegisterActivity, "Erro de conexão: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}