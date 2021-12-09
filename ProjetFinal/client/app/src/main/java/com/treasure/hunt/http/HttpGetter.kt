package com.treasure.hunt.http

import android.net.Uri
import com.treasure.hunt.data.Treasure
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class HttpGetter(private val urlString: String, val queryParams: Map<String, String>?){
    private var jsonString = ""
    suspend fun run() = withContext(Dispatchers.IO) {
        try {
            var urlConnection: HttpURLConnection?
            val url = URL(urlString)
            urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "POST"
            urlConnection.readTimeout = 15000
            urlConnection.connectTimeout = 15000
            urlConnection.doOutput = true
            urlConnection.doInput = true


//             params inspired from: https://stackoverflow.com/a/29053050/11725219
            val builder = Uri.Builder()
            if(queryParams != null) {
                for (i in queryParams.keys) {
                    builder.appendQueryParameter(i, queryParams[i])
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
            }
            urlConnection.connect()
            urlConnection.responseCode

            val br = BufferedReader(InputStreamReader(urlConnection.inputStream))
            val sb = StringBuilder()
            var line: String?
            while (br.readLine().also { line = it } != null) {
                sb.append(
                    """
                        $line
                        
                        """.trimIndent()
                )
            }
            br.close()
            jsonString = sb.toString()
            println("JSON: $jsonString")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun parseJson(jsonString: String): ArrayList<Treasure> {
        var arrayList: ArrayList<Treasure> = ArrayList()
        try {
            val jsonArray = JSONArray(jsonString)
            println(jsonArray)
            arrayList = ConvertJsonToPartiesList(jsonArray)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return arrayList
    }

    val listePartiesFromJSON: ArrayList<Treasure>
        get() = parseJson(jsonString)

    companion object {
        private fun ConvertJsonToPartiesList(array: JSONArray): ArrayList<Treasure> {
            val arrayList: ArrayList<Treasure> = ArrayList()
            try {
                for (i in 0 until array.length()) {
                    arrayList.add(Treasure(array.getJSONObject(i)))
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return arrayList
        }
    }
}