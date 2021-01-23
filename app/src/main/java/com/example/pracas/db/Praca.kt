package com.example.pracas.db

import java.io.Serializable

data class Praca (
    var id: Long = 0,
    var nome: String? = null,
    var endereco: String? = null,
    var telefoneResponsavel: String? = null,
    var fundadaem: Long? = null,
    var foto: String? = null
): Serializable {
    override fun toString(): String {
        return nome.toString()
    }
}