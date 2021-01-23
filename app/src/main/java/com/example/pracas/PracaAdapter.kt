package com.example.pracas

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.pracas.db.Praca
import kotlinx.android.synthetic.main.activity_lista_linha.view.*
import java.io.IOException
import java.io.InputStream

class PracaAdapter (context: Context, pracas: ArrayList<Praca>, assetManager: AssetManager) : BaseAdapter() {

    private var pracas: ArrayList<Praca>
    private var inflator: LayoutInflater
    private var assetManager: AssetManager

    init{
        this.pracas = pracas
        this.inflator = LayoutInflater.from(context)
        this.assetManager = assetManager
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val praca = this.pracas[position]
        val view = this.inflator.inflate(R.layout.activity_lista_linha, parent, false)
        val img = this.getBitmapFromAsset(praca?.foto)
        view.foto.setImageBitmap(img)
        view.nome.text = praca?.nome
        view.endereco.text = praca?.endereco
        return view
    }

    override fun getItem(position: Int): Any {
        return this.pracas[position]
    }

    override fun getItemId(position: Int): Long {
        return this.pracas[position]?.id
    }

    override fun getCount(): Int {
        return this.pracas?.size
    }

    private fun getBitmapFromAsset(path: String?): Bitmap {
        val assetManager = this.assetManager
        var istr: InputStream? = null
        try{
            istr = assetManager.open("img/$path")
        }catch(e: IOException){
            istr = assetManager.open("img/imgError.png")
        }
        return BitmapFactory.decodeStream(istr)
    }
}