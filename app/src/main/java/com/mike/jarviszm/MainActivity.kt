package com.mike.jarviszm
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.widget.Button
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var txtLlama: TextView
    private lateinit var txtVosk: TextView
    private lateinit var txtLog: TextView
    private var llamaPath: String? = null
    private var voskPath: String? = null
    private var server: JarvisServer? = null

    private val pickLlama = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val file = copyUriToFile(it, "model.gguf")
            llamaPath = file.absolutePath
            txtLlama.text = "AI: ${file.name} (${file.length()/1024/1024}MB)"
        }
    }
    private val pickVosk = registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        uri?.let {
            val folder = File(filesDir, "vosk")
            folder.mkdirs()
            // Vosk model needs files - user must select folder containing am/fst
            voskPath = folder.absolutePath
            txtVosk.text = "Vosk: $voskPath (copy model files to Downloads/vosk first)"
            txtLog.text = "Note: For Vosk, download vosk-model-small-en-us-0.15.zip, unzip to Download/vosk, then select that folder"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        txtLlama = findViewById(R.id.txtLlamaPath)
        txtVosk = findViewById(R.id.txtVoskPath)
        txtLog = findViewById(R.id.txtServerLog)
        findViewById<Button>(R.id.btnSelectLlama).setOnClickListener { pickLlama.launch("*/*") }
        findViewById<Button>(R.id.btnSelectVosk).setOnClickListener { pickVosk.launch(null) }
        findViewById<Button>(R.id.btnStartServer).setOnClickListener { startServer() }
    }

    private fun startServer() {
        if (llamaPath == null) { txtLog.text = "Select AI model first! Download qwen2.5-0.5b Q4_0.gguf to Downloads"; return }
        val llama = LlamaHelper(llamaPath!!)
        val vosk = if(voskPath!=null) VoskHelper(voskPath!!) else null
        server = JarvisServer(8080, llama, vosk, txtLog, this)
        server?.start()
        val ip = getIp()
        txtLog.text = "✅ SERVER RUNNING\nhttp://$ip:8080\n\nPOST /v1/chat/completions\nBody: {\"prompt\":\"hi\"}\n\nGET / for status"
    }

    private fun getIp(): String {
        val wm = applicationContext.getSystemService(WIFI_SERVICE) as android.net.wifi.WifiManager
        val ip = wm.connectionInfo.ipAddress
        return String.format("%d.%d.%d.%d", ip and 0xff, ip shr 8 and 0xff, ip shr 16 and 0xff, ip shr 24 and 0xff)
    }

    private fun copyUriToFile(uri: Uri, name: String): File {
        val file = File(filesDir, name)
        contentResolver.openInputStream(uri)?.use { input ->
            file.outputStream().use { output -> input.copyTo(output) }
        }
        return file
    }
}
