package br.com.opengroup.cida

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.com.opengroup.cida.api.BootApi
import br.com.opengroup.cida.api.RetrofitHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class DashboardActivity: AppCompatActivity() {
    private val retrofit by lazy { RetrofitHelper.retrofit }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        CoroutineScope(Dispatchers.IO).launch {
            val boot = retrofit.create(BootApi::class.java).boot()
        }
    }
}