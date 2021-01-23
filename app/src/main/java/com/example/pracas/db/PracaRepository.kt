package com.example.pracas.db

import android.content.Context
import android.util.Log
import com.example.pracas.constants.APP_NAME
import com.example.pracas.constants.PRACA_BD_NOME
import org.jetbrains.anko.db.*
import timber.log.Timber

class PracaRepository(val context: Context) {
    fun findAll(filter: String?) : ArrayList<Praca> = context.database.use {
        val pracas = ArrayList<Praca>()

        select(PRACA_BD_NOME, "id", "nome", "endereco", "telefoneResponsavel",  "fundadaem", "foto")
            //.whereArgs( "nome like {nome}", "nome" to filter)
            .parseList(object: MapRowParser<ArrayList<Praca>> {
                override fun parseRow(columns: Map<String, Any?>): ArrayList<Praca> {
                    val id = columns.getValue("id")
                    val nome = columns.getValue("nome")
                    val endereco = columns.getValue("endereco")
                    val tel = columns.getValue("telefoneResponsavel")
                    val fundadaem = columns.getValue("fundadaem")
                    val foto = columns.getValue("foto")

                    pracas.add(Praca(
                        id.toString()?.toLong(),
                        nome?.toString(),
                        endereco?.toString(),
                        tel?.toString(),
                        fundadaem?.toString()?.toLong(),
                        foto?.toString()))
                    return pracas
                }
            })

        pracas
    }

    fun create(praca: Praca) = context.database.use {
        var id = insert(PRACA_BD_NOME,
            "foto" to praca.foto,
            "nome" to praca.nome,
            "endereco" to praca.endereco,
            "telefoneResponsavel" to praca.telefoneResponsavel,
            "fundadaem" to praca.fundadaem)

        Log.i(APP_NAME, "Contato inserido com sucesso - Id: $id")
    }

    fun update(praca: Praca) = context.database.use {
        val updateResult = update(PRACA_BD_NOME,
            "foto" to praca.foto,
            "nome" to praca.nome,
            "endereco" to praca.endereco,
            "telefoneResponsavel" to praca.telefoneResponsavel,
            "fundadaem" to praca.fundadaem)
            .whereArgs("id = {id}","id" to praca.id).exec()
        Timber.d("Atualizada Praça de id $updateResult")
    }

    fun delete(id: Long) = context.database.use {
        delete(PRACA_BD_NOME, "id = {pracaId}", args = *arrayOf("pracaId" to id))
    }

    fun isPracaResponsavel(telefoneResponsavel: String) : Boolean = context.database.use{
        select(PRACA_BD_NOME, "count(*) as total")
            .whereArgs("telefoneResponsavel = {telefoneResponsavel}", "telefoneResponsavel" to telefoneResponsavel)
            .parseSingle(object: MapRowParser<Boolean> {
                override fun parseRow(columns: Map <String, Any?>): Boolean {
                    val total = columns.getValue("total")
                    return total.toString().toInt() > 0
                }
            })
    }
}