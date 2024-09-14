package br.com.opengroup.cida

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class LoginActivity : AppCompatActivity() {
    private lateinit var btnEnter: Button
    private lateinit var tvCreateAccount: TextView
    override fun onCreate(savedInstanceState: Bundle?) {

        btnEnter = findViewById(R.id.btnEnter)
        tvCreateAccount = findViewById(R.id.tvCreateAccount)

        btnEnter.setOnClickListener {
            startActivity(Intent(this, UploadActivity::class.java))
        }
        tvCreateAccount.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }
}