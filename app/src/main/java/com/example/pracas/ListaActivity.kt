package com.example.pracas

import android.Manifest
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.telephony.SmsMessage
import android.util.Log
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.pracas.constants.APP_NAME
import com.example.pracas.db.Praca
import com.example.pracas.db.PracaRepository
import kotlinx.android.synthetic.main.activity_lista.*
import java.util.logging.Logger

class ListaActivity : AppCompatActivity() {

    private var pracas: ArrayList<Praca>? = null
    private var pracaSelecionada:Praca? = null
    val MY_PERMISSIONS_REQUEST_SMS_RECEIVE = 10;
    var receiver: BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista)
        toolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(toolbar)

        lista.setOnItemClickListener{ _, _, position, id ->
            Log.i(APP_NAME, "position: $position id: " + pracas?.get(position)?.id)
            val intent = Intent(this@ListaActivity, PracaActivity::class.java)
            intent.putExtra("praca", pracas?.get(position))
            startActivity(intent)
        }

        lista.setOnItemLongClickListener{ _, _, position, _ ->
            Log.i(APP_NAME, "Apagar Posição : $position " )
            pracaSelecionada = pracas?.get(position)

            false
        }

        setupPermissions()
        configureReceiver()
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        menuInflater.inflate(R.menu.menu_praca_contexto, menu)
        super.onCreateContextMenu(menu, v, menuInfo)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.excluir -> {
                AlertDialog.Builder(this@ListaActivity)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setTitle("Excluir")
                    .setMessage("Quer mesmo apagar ?")
                    .setPositiveButton("Quero"
                    ) { _, _ ->
                        PracaRepository(this).delete(this.pracaSelecionada!!.id)
                        carregaLista()
                    }.setNegativeButton("Nao", null).show()
                return false
            }
            R.id.enviasms -> {
                val intentSms = Intent(Intent.ACTION_VIEW)
                intentSms.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
                intentSms.data = Uri.parse("sms:" + pracaSelecionada?.telefoneResponsavel)
                intentSms.putExtra("sms_body", "Testando...")
                item.intent = intentSms
                return false
            }
            R.id.share -> {
                val intentShare = Intent(Intent.ACTION_SEND)
                intentShare.type = "text/plain"
                intentShare.putExtra(Intent.EXTRA_SUBJECT, "Assunto que será compartilhado")
                intentShare.putExtra(Intent.EXTRA_TEXT, "Texto que será compartilhado")
                startActivity(Intent.createChooser(intentShare, "Escolha como compartilhar"))
                return false
            }
            R.id.ligar -> {
                val intentLigar = Intent(Intent.ACTION_DIAL)
                intentLigar.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
                intentLigar.data = Uri.parse("tel:" + pracaSelecionada?.telefoneResponsavel)
                item.intent = intentLigar
                return false
            }
            R.id.visualizarmapa -> {
                val gmmIntentUri = Uri.parse("geo:0,0?q=" + pracaSelecionada?.endereco)
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                startActivity(mapIntent)
                return false
            }
            else -> return super.onContextItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        var inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.novo -> {
                val intent = Intent(this, PracaActivity::class.java)
                startActivity(intent)
                return false
            }

            R.id.sincronizar -> {
                Toast.makeText(this,"Sincronizar", Toast.LENGTH_LONG).show()
                return false
            }

            R.id.receber -> {
                Toast.makeText(this, "Receber", Toast.LENGTH_LONG).show()
                return false
            }

            R.id.mapa -> {
                Toast.makeText(this, "Mapa", Toast.LENGTH_LONG).show()
                return false
            }

            R.id.preferencias -> {
                Toast.makeText(this, "Preferências", Toast.LENGTH_LONG).show()
                return false
            }

            else -> return super.onOptionsItemSelected(item)
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_PERMISSIONS_REQUEST_SMS_RECEIVE) {
            Logger.getLogger(SmsMessage::class.java.name).warning("Permission RECEIVE SMS")
        }
    }


    override fun onResume() {
        super.onResume()
        carregaLista()
        registerForContextMenu(lista)
    }

    private fun carregaLista() {
        pracas = PracaRepository(this).findAll("")
        lista.adapter = PracaAdapter(applicationContext, pracas!!, assets)
    }

    private fun setupPermissions() {

        val list = listOf<String>(
            Manifest.permission.RECEIVE_SMS
        )

        ActivityCompat.requestPermissions(this,
            list.toTypedArray(), MY_PERMISSIONS_REQUEST_SMS_RECEIVE);

        val permission = ContextCompat.checkSelfPermission(this,
            Manifest.permission.READ_SMS)

        if (permission != PackageManager.GET_SERVICES) {
            Log.i("aula", "Permission to record denied")
        }
    }

    private fun configureReceiver() {
        val filter = IntentFilter()
        filter.addAction("com.example.pracas.SMSreceiver")
        filter.addAction("android.provider.Telephony.SMS_RECEIVED")
        receiver = SMSReceiver()
        registerReceiver(receiver, filter)
    }


}
