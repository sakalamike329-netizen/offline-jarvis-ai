package com.mike.jarviszm

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.mike.jarviszm.databinding.ActivityMainBinding
import org.nanohttpd.protocols.http.IHTTPSession
import org.nanohttpd.protocols.http.NanoHTTPD
import org.nanohttpd.protocols.http.response.Response
import org.nanohttpd.protocols.http.response.Response.newFixedLengthResponse
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var llamaPath: String? = null
    private var voskPath: String? = null
    private var server: JarvisServer? = null
    private var llama: LlamaHelper? = null
    private var vosk: VoskHelper? = null

    private val pickLlama = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            llamaPath = copyToAppStorage(it, "model.gguf")
            binding.txtLlamaPath.text = "AI Model: $llamaPath"
        }
    }
    private val pickVosk = registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri: Uri? ->
        uri?.let {
            voskPath = copyFolderToAppStorage(it, "vosk")
            binding.txtVoskPath.text = "Vosk: $voskPath"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSelectLlama.setOnClickListener { pickLlama.launch("*/*") }
        binding.btnSelectVosk.setOnClickListener { pickVosk.launch(null) }

        binding.btnStartServer.setOnClickListener {
            if(llamaPath==null){ binding.txtServerLog.text="Select AI model first!"; return@setOnClickListener }
            startJarvisServer()
        }
    }

    private fun startJarvisServer() {
        binding.txtServerLog.text = "Loading models..."
        llama = LlamaHelper(this, llamaPath!!)
        if(voskPath!=null) vosk = VoskHelper(this, voskPath!!)

        server = JarvisServer(8080, llama!!, vosk, binding)
        server?.start()
        val ip = getPhoneIp()
        binding.txtServerLog.text = "✅ SERVER RUNNING\n\nIP: http://$ip:8080\n\nEndpoints:\nPOST /v1/chat/completions\nPOST /stt (audio)\nGET / -> status\n\nNow connect other phones to this IP!"
    }

    private fun getPhoneIp(): String {
        val wm = applicationContext.getSystemService(WIFI_SERVICE) as android.net.wifi.WifiManager
        val ip = wm.connectionInfo.ipAddress
        return String.format("%d.%d.%d.%d", ip and 0xff, ip shr 8 and 0xff, ip shr 16 and 0xff, ip shr 24 and 0xff)
    }
    private fun copyToAppStorage(uri: Uri, name: String): String { /* copy file */ return filesDir.absolutePath+"/"+name }
    private fun copyFolderToAppStorage(uri: Uri, name: String): String { /* copy folder */ return filesDir.absolutePath+"/"+name }
}

class JarvisServer(port: Int, val llama: LlamaHelper, val vosk: VoskHelper?, val binding: ActivityMainBinding) : NanoHTTPD(port) {
    override fun serve(session: IHTTPSession): Response {
        return when(session.uri) {
            "/" -> newFixedLengthResponse("Jarvis ZM Server Running - Mike from Lusaka")
            "/v1/chat/completions" -> {
                val body = session.inputStream.readBytes().decodeToString()
                val prompt = extractPrompt(body) // parse json "prompt" or "messages"
                val answer = llama.generate(prompt) // OFFLINE LLM
                val json = """{"choices":[{"message":{"content":"$answer"}}]}"""
                newFixedLengthResponse(Response.Status.OK, "application/json", json)
            }
            "/stt" -> {
                val text = vosk?.transcribe(session.inputStream) ?: "vosk not loaded"
                newFixedLengthResponse(text)
            }
            else -> newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "Not found")
        }
    }
    private fun extractPrompt(json: String): String { return json.substringAfter("\"content\":\"").substringBefore("\"") }
}
