package com.treasure.hunt.http

import android.net.Uri
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.text.MessageFormat

//    private val treasure_id, treasureId: Int,
//    private val user_id, userId: Int,
//    private val collected_timestamp, collectedTimestamp: String,

class HttpPoster(
    url: String?,
    val params: Map<String, String>
) :
    Runnable {
    private val urlString: String = MessageFormat.format(
        url,
        params
    )

    override fun run() {
        try {
            var urlConnection: HttpURLConnection? = null
            val url = URL(urlString)
            urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "POST"
            urlConnection.readTimeout = 200
            urlConnection.connectTimeout = 15000
            urlConnection.doInput = true
            urlConnection.doOutput = true

//             params inspired from: https://stackoverflow.com/a/29053050/11725219
            val builder = Uri.Builder();

            for( i in params.keys) {
                builder.appendQueryParameter(i, params[i])
            }

            val query = builder.build().encodedQuery
            val os = urlConnection.outputStream
            val writer = BufferedWriter(
                OutputStreamWriter(os, "UTF-8")
            )
            writer.write(query)
            writer.flush()
            writer.close()
            os.close()
            urlConnection.connect()
            urlConnection.responseCode
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}