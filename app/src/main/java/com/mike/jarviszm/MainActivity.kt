package com.example.jarvis

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import fi.iki.elonen.NanoHTTPD
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private var llamaPath = ""
    private var voskPath = ""
    private var server: NanoHTTPD? = null
    private lateinit var txtLlama: TextView
    private lateinit var txtVosk: TextView
    private lateinit var txtStatus: TextView

    private val pickLlama = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri?.let {
            llamaPath = it.toString()
            txtLlama.text = llamaPath
            contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }
    private val pickVosk = registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri: Uri? ->
        uri?.let {
            voskPath = it.toString()
            txtVosk.text = voskPath
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        txtLlama = findViewById(R.id.txtLlamaPath)
        txtVosk = findViewById(R.id.txtVoskPath)
        txtStatus = findViewById(R.id.txtStatus)

        findViewById<Button>(R.id.btnPickLlama).setOnClickListener {
            pickLlama.launch(arrayOf("*/*"))
        }
        findViewById<Button>(R.id.btnPickVosk).setOnClickListener {
            pickVosk.launch(null)
        }
        findViewById<Button>(R.id.btnStart).setOnClickListener {
            if (llamaPath.isEmpty()) {
                Toast.makeText(this, "Select AI model first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            startServer()
        }
    }

    private fun startServer() {
        if (server != null) return
        server = object : NanoHTTPD(8080) {
            override fun serve(session: IHTTPSession): Response {
                return newFixedLengthResponse("JARVIS RUNNING\nLLAMA: $llamaPath\nVOSK: $voskPath")
            }
        }
        try {
            server?.start()
            txtStatus.text = "Running on http://${getIp()}:8080"
        } catch (e: IOException) {
            txtStatus.text = "Error: ${e.message}"
        }
    }

    private fun getIp(): String {
        return try {
            val wm = applicationContext.getSystemService(WIFI_SERVICE) as android.net.wifi.WifiManager
            val ip = wm.connectionInfo.ipAddress
            "${ip and 0xff}.${ip shr 8 and 0xff}.${ip shr 16 and 0xff}.${ip shr 24 and 0xff}"
        } catch (e: Exception) { "phone-ip" }
    }

    override fun onDestroy() {
        server?.stop()
        super.onDestroy()
    }
}
