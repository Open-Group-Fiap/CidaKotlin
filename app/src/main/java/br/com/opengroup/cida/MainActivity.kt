package br.com.opengroup.cida

import DatabaseHelper
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.opengroup.cida.api.BootApi
import br.com.opengroup.cida.api.RetrofitHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var btnCreateAccount: Button
    private lateinit var tvLogin: TextView
    private val retrofit by lazy { RetrofitHelper.retrofit }
    private var credentials: Pair<Int, Pair<String, String>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnCreateAccount = findViewById(R.id.btnCreateAccount)
        tvLogin = findViewById(R.id.tvLogin)

        btnCreateAccount.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                try {
                    val boot = retrofit.create(BootApi::class.java).boot()
                    if (boot.isSuccessful) {
                        break
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        this@MainActivity,
                        "Servidor ainda não está disponível, aguarde um pouco!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        credentials = DatabaseHelper(this).selectCredenciais()
        if(credentials != null) {
            startActivity(Intent(this, UploadActivity::class.java))
        }
    }
}