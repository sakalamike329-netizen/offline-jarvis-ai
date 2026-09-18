package com.mike.jarviszm
import de.kherud.llama.LlamaModel
import de.kherud.llama.args.ModelArgs
class LlamaHelper(private val path: String) {
    private var model: LlamaModel? = null
    fun generate(prompt: String): String {
        if(model==null) model = LlamaModel(path, ModelArgs().withContextSize(1024).withThreads(4))
        return try { model!!.generate(prompt, 200).text } catch(e: Exception){ "Error: ${e.message}" }
    }
}
