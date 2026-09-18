package com.mike.jarviszm
import android.widget.TextView
import android.content.Context
import org.nanohttpd.protocols.http.NanoHTTPD
import org.nanohttpd.protocols.http.response.Response
import org.nanohttpd.protocols.http.IHTTPSession
class JarvisServer(port: Int, val llama: LlamaHelper, val vosk: VoskHelper?, val log: TextView, val ctx: Context): NanoHTTPD(port) {
    override fun serve(session: IHTTPSession): Response {
        return when(session.uri) {
            "/" -> newFixedLengthResponse("Jarvis ZM Server - ${ctx.packageName} - POST /v1/chat/completions")
            "/v1/chat/completions" -> {
                val body = session.inputStream.readBytes().decodeToString()
                val prompt = if(body.contains("prompt")) body.substringAfter("\"prompt\":\"").substringBefore("\"") else body
                val ans = llama.generate(prompt)
                newFixedLengthResponse(Response.Status.OK, "application/json", "{\"choices\":[{\"text\":\"$ans\"}]}")
            }
            else -> newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "Use POST /v1/chat/completions")
        }
    }
}
