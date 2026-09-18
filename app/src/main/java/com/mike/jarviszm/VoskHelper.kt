package com.mike.jarviszm
import org.vosk.Model
import org.vosk.Recognizer
import java.io.InputStream
class VoskHelper(path: String) {
    private val model = Model(path)
    private val rec = Recognizer(model, 16000f)
    fun transcribe(stream: InputStream): String {
        val bytes = stream.readBytes()
        rec.acceptWaveForm(bytes, bytes.size)
        return rec.result
    }
}
