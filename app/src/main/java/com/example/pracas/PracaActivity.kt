package com.example.pracas

import android.app.DatePickerDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pracas.db.Praca
import com.example.pracas.db.PracaRepository
import kotlinx.android.synthetic.main.activity_praca.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

class PracaActivity : AppCompatActivity() {

    var cal = Calendar.getInstance()
    private var praca: Praca? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_praca)
        setSupportActionBar(toolbar_praca)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                                   dayOfMonth: Int) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView(cal)
            }
        }

        txtFundadaem.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                DatePickerDialog(this@PracaActivity,
                    dateSetListener,
                    // set DatePickerDialog to point to today's date when it loads up
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
            }
        })

        btnCadastro.setOnClickListener{

            praca?.nome = txtNome.text?.toString()
            praca?.endereco = txtEndereco.text?.toString()
            praca?.telefoneResponsavel = txtTel.text?.toString()
            praca?.fundadaem = cal.timeInMillis

            if(praca?.id == 0L)
                PracaRepository(this).create(praca!!)
            else
                PracaRepository(this).update(praca!!)
            Toast.makeText(this, "Praça cadastrada com sucesso", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun updateDateInView(cal : Calendar) {
        val myFormat = "dd/MM/yyyy - hh:mm:ss" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        val time = cal.getTime().time
        val strTime = sdf.format(time)
        txtFundadaem.text = strTime
    }

    override fun onResume() {
        super.onResume()
        praca = intent?.getSerializableExtra("praca") as Praca?
        if(praca != null){
            praca as Praca
            txtNome.setText(praca?.nome)
            txtEndereco.setText(praca?.endereco)
            txtTel.setText(praca?.telefoneResponsavel)
            val img = this.getBitmapFromAsset(praca?.foto)
            imgPraca.setImageBitmap(img)
            var call = Calendar.getInstance()
            if(praca?.fundadaem != null)
                call.timeInMillis = praca?.fundadaem!!
            updateDateInView(call)
        }else{
            praca = Praca()
        }
    }

    private fun getBitmapFromAsset(path: String?): Bitmap {
        var istr: InputStream? = null
        try{
            istr = assets.open("img/$path")
        }catch(e: IOException){
            istr = assets.open("img/imgError.png")
        }
        return BitmapFactory.decodeStream(istr)
    }
}