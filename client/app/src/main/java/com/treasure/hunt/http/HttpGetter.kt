package com.treasure.hunt.http

import com.treasure.hunt.data.Treasure
import org.json.JSONArray
import org.json.JSONException
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class HttpGetter(private val urlString: String) : Runnable {
    private var jsonString = ""
    override fun run() {
        try {
            var urlConnection: HttpURLConnection? = null
            val url = URL(urlString)
            urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "GET"
            urlConnection.readTimeout = 10000
            urlConnection.connectTimeout = 15000
            urlConnection.doOutput = true
            urlConnection.connect()
            val br = BufferedReader(InputStreamReader(url.openStream()))
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