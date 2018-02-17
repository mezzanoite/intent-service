package br.com.mezzanotte.intentservice

import android.app.IntentService
import android.content.Intent
import android.os.Bundle
import android.os.ResultReceiver
import android.text.TextUtils
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

/**
 * Created by logonrm on 17/02/2018.
 */
class DownloadService : IntentService(DownloadService::class.java.name) {

    companion object {
        val STATUS_RUNNING = 0
        val STATUS_FINISHED = 1
        val STATUS_ERROR = 2
    }

    override fun onHandleIntent(intent: Intent?) {
        val receiver = intent!!.getParcelableExtra<ResultReceiver>("receiver")
        val url = intent.getStringExtra("url")
        val bundle = Bundle()

        if (!TextUtils.isEmpty(url)) {
            receiver.send(STATUS_RUNNING, Bundle.EMPTY)
            try {
                val results = downloadData(url)
                if (results != null && results.isNotEmpty()) {
                    bundle.putStringArray("result", results.toTypedArray())
                    receiver.send(STATUS_FINISHED, bundle)
                }
            } catch (e: Exception) {
                bundle.putString(Intent.EXTRA_TEXT, e.toString())
                receiver.send(STATUS_ERROR, bundle)
            }
        }
    }

    @Throws(IOException::class)
    private fun downloadData(requestURL: String): List<String?> {
        var inputStram: InputStream?
        var urlConnection: HttpURLConnection?
        var url = URL(requestURL)

        urlConnection = url.openConnection() as HttpURLConnection
        urlConnection.setRequestProperty("Content-Type", "application/json")
        urlConnection.setRequestProperty("Accept", "application/json")
        urlConnection.requestMethod = "GET"

        val statusCode = urlConnection.responseCode

        if (statusCode == 200) {
            inputStram = BufferedInputStream(urlConnection.inputStream)
            val response = convertInputStreamToStrin(inputStram)
            val result = parseResult(response)
            return result.toList()
        } else {
            throw IOException("Falha no download de dados")
        }
    }

    @Throws(IOException::class)
    private fun convertInputStreamToStrin(inputStream: InputStream?): String {
        val bufferedReader = BufferedReader(InputStreamReader(inputStream!!))
        var line = bufferedReader.readLine()
        var result = ""

        while (line != null) {
            result += line
            line = bufferedReader.readLine()
        }
        inputStream?.close()

        return result
    }

    private fun parseResult(result: String): Array<String?> {
        var nomePokemons: Array<String?> = arrayOf()
        try {
            val response = JSONObject(result)
            val pokemons = response.getJSONArray("results")
            nomePokemons = arrayOfNulls(pokemons.length())

            for (i in 0 until pokemons.length()) {
                val pokemon = pokemons.optJSONObject(i)
                val nomePokemon = pokemon.optString("name")
                nomePokemons[i] = nomePokemon
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return nomePokemons
    }

}

