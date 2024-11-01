package br.com.opengroup.cida.database

import LocalDatabaseHelper
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreLogger {
    companion object {
        fun log(mensagem: String, userEmail: String) {
            val bancoDados = FirebaseFirestore.getInstance()
            bancoDados.collection("Logs").document("$userEmail-${System.currentTimeMillis()}")
                .set(mapOf("mensagem" to mensagem))
        }
    }
}