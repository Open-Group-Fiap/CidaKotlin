package br.com.opengroup.cida

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class UploadActivity: AppCompatActivity() {
    private lateinit var btnSendDocuments: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        btnSendDocuments = findViewById(R.id.btnSendDocuments)

        btnSendDocuments.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
        }

    }
}