package com.example.pracas.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.pracas.constants.PRACA_BD_NOME
import org.jetbrains.anko.db.*

class BancoDadosHelper(context: Context) :
    ManagedSQLiteOpenHelper(ctx = context , name = "$PRACA_BD_NOME.db",  version = 9) {

    private val scriptSQLCreate = arrayOf(
        "INSERT INTO $PRACA_BD_NOME VALUES(1, 'Praça da Amizade', 'Avenida Nações Unidas - Centro - Bauru', '98989898989', 800210131000, 'praca01.jpg');",
        "INSERT INTO $PRACA_BD_NOME VALUES(2, 'Praça da Bandeira', 'Avenida Paulista, 1793 - Bela Vista - São Paulo - SP', '71717171717', 872094931000, 'praca02.jpg');",
        "INSERT INTO $PRACA_BD_NOME VALUES(3, 'Sir Arthur C. Doyle Square', '221B Baker Street - Marylebone - London', '23232322323', null, 'praca03.jpg');"
    )

    //singleton da classe
    companion object {
        private var instance: BancoDadosHelper? = null

        @Synchronized
        fun getInstance(ctx: Context): BancoDadosHelper {
            if (instance == null) {
                instance = BancoDadosHelper(ctx.applicationContext)
            }
            return instance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.createTable("praca", true,
            "id" to INTEGER + PRIMARY_KEY + UNIQUE,
            "nome" to TEXT + NOT_NULL,
            "endereco" to TEXT + NOT_NULL,
            "telefoneResponsavel" to TEXT + NOT_NULL,
            "fundadaem" to INTEGER,
            "foto" to TEXT
        )

        scriptSQLCreate.forEach { sql ->
            db.execSQL(sql)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.dropTable("praca", true)
        onCreate(db)
    }
}

val Context.database: BancoDadosHelper get() = BancoDadosHelper.getInstance(applicationContext)