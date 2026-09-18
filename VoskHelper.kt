package com.mike.jarviszm
import android.content.Context
import org.vosk.Model
import org.vosk.Recognizer
import java.io.InputStream

class VoskHelper(ctx: Context, modelPath: String) {
    private val model = Model(modelPath)
    private val rec = Recognizer(model, 16000f)
    fun transcribe(audioStream: InputStream): String {
        val bytes = audioStream.readBytes()
        rec.acceptWaveForm(bytes, bytes.size)
        return rec.result // json with "text": "hello jarvis"
    }
}
