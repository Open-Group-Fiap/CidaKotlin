import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import br.com.opengroup.cida.database.FirestoreLogger

class LocalDatabaseHelper(context: Context) : SQLiteOpenHelper(context, "Database", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        val sqlCredenciais = "CREATE TABLE IF NOT EXISTS T_CREDENCIAIS(" +
                "id_credencial INTEGER NOT NULL PRIMARY KEY," +
                "email VARCHAR(100)," +
                "senha VARCHAR(100)" +
                ");"

        try {
            db?.execSQL(sqlCredenciais)
            Log.i("db_info", "Tabela de credenciais criada com sucesso")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.i("db_info", "Erro ao criar tabela de credenciais")
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    fun insertCredenciais(email: String, senha: String, id:Int) {
        val db = writableDatabase
        val sql = "INSERT INTO T_CREDENCIAIS(id_credencial, email, senha) VALUES($id, '$email', '$senha');"
        try {
            db.execSQL(sql)
            Log.i("db_info", "Credenciais inseridas com sucesso")
        } catch (e: Exception) {
            e.printStackTrace()
            FirestoreLogger.log("Erro ao inserir credenciais, ${e.message}", email)
            Log.i("db_info", "Erro ao inserir credenciais")
        }
    }

    fun selectCredenciais(): Pair<Int, Pair<String, String>>? {
        val db = readableDatabase
        val sql = "SELECT * FROM T_CREDENCIAIS;"
        val cursor = db.rawQuery(sql, null)
        var email: String? = null
        var senha: String? = null
        var id: Int? = null

        val indiceEmail = cursor.getColumnIndex("email")
        val indiceSenha = cursor.getColumnIndex("senha")
        val indiceId = cursor.getColumnIndex("id_credencial")

        if (cursor.moveToNext()) {
            email = cursor.getString(indiceEmail)
            senha = cursor.getString(indiceSenha)
            id = cursor.getInt(indiceId)
        }
        cursor.close()
        return if (email != null && senha != null) {
            Pair(id!!, Pair(email!!, senha!!))
        } else {
            null
        }
    }

    fun updateCredenciais(id: Int, email: String, senha: String) {
        if (selectCredenciais() == null) {
            insertCredenciais(email, senha, id)
        }
        val db = writableDatabase
        val sql = "UPDATE T_CREDENCIAIS SET id_credencial=$id, email='$email', senha='$senha' WHERE id_credencial=$id;"
        try {
            db.execSQL(sql)
            Log.i("db_info", "Credenciais atualizadas com sucesso")
        } catch (e: Exception) {
            e.printStackTrace()
            FirestoreLogger.log("Erro ao atualizar credenciais, ${e.message}", selectCredenciais()?.second?.first!!)
            Log.i("db_info", "Erro ao atualizar credenciais")
        }
    }

    fun removeCredentials() {
        val db = writableDatabase
        val credentials = selectCredenciais()
        val sql = "DELETE FROM T_CREDENCIAIS;"
        try {
            db.execSQL(sql)
            Log.i("db_info", "Credenciais removidas com sucesso")
        } catch (e: Exception) {
            e.printStackTrace()
            FirestoreLogger.log("Erro ao remover credenciais, ${e.message}", credentials?.second?.first!!)
            Log.i("db_info", "Erro ao remover credenciais")
        }
    }
}
