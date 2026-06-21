package com.mesawa.cuidarproximocuidador.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.mesawa.cuidarproximocuidador.CuidarProximoCuidadorApp
import org.json.JSONArray
import org.json.JSONObject

class LocalSqlStore private constructor(context: Context) :
    SQLiteOpenHelper(context.applicationContext, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE registros_locais (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                uid TEXT NOT NULL,
                tipo TEXT NOT NULL,
                chave TEXT NOT NULL,
                payload TEXT NOT NULL,
                sincronizado INTEGER NOT NULL DEFAULT 0,
                atualizado_em INTEGER NOT NULL,
                UNIQUE(uid, tipo, chave) ON CONFLICT REPLACE
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX idx_registros_uid_tipo ON registros_locais(uid, tipo)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS registros_locais")
        onCreate(db)
    }

    fun salvarRegistro(
        uid: String,
        tipo: String,
        chave: String,
        payload: Map<String, Any?>,
        sincronizado: Boolean = false
    ) {
        if (uid.isBlank() || tipo.isBlank() || chave.isBlank()) return
        val values = ContentValues().apply {
            put("uid", uid)
            put("tipo", tipo)
            put("chave", chave)
            put("payload", JSONObject(limparMapa(payload)).toString())
            put("sincronizado", if (sincronizado) 1 else 0)
            put("atualizado_em", System.currentTimeMillis())
        }
        writableDatabase.insertWithOnConflict(
            "registros_locais",
            null,
            values,
            SQLiteDatabase.CONFLICT_REPLACE
        )
    }

    fun salvarEvento(
        uid: String,
        tipo: String,
        payload: Map<String, Any?>,
        sincronizado: Boolean = false
    ) {
        salvarRegistro(uid, tipo, "${System.currentTimeMillis()}_${tipo}", payload, sincronizado)
    }

    private fun limparMapa(map: Map<String, Any?>): Map<String, Any?> {
        return map.mapValues { limparValor(it.value) }
    }

    private fun limparValor(value: Any?): Any? {
        return when (value) {
            null, is String, is Number, is Boolean -> value
            is Map<*, *> -> JSONObject(value.entries.associate { it.key.toString() to limparValor(it.value) })
            is Iterable<*> -> JSONArray(value.map { limparValor(it) })
            is Array<*> -> JSONArray(value.map { limparValor(it) })
            else -> value.toString()
        }
    }

    companion object {
        private const val DB_NAME = "cuidar_proximo_cuidador.db"
        private const val DB_VERSION = 1

        val instance: LocalSqlStore by lazy {
            LocalSqlStore(CuidarProximoCuidadorApp.instance)
        }
    }
}
