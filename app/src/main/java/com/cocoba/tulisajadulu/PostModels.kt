package com.cocoba.tulisajadulu

import com.google.firebase.database.Exclude
import java.io.Serializable

/**
 * Created by Chandra on 06/03/19.
 * Need some help?
 * Contact me at y.pristyan.chandra@gmail.com
 */
data class PostModel(
    var id: String? = null,
    var title: String? = null,
    var content: String? = null
) : Serializable {


    @Exclude
    fun toMap(): HashMap<String, Any> {
        val parameter = HashMap<String, Any>()
        parameter["$id"] = mapOf("id" to id, "title" to title, "content" to content)
        return parameter
    }
}