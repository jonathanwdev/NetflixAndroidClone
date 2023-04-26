package com.jonathanwdev.netflix.util

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.jonathanwdev.netflix.models.Category
import com.jonathanwdev.netflix.models.Movie
import com.jonathanwdev.netflix.models.MovieDetail
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

class MovieTask(private val callBack: CallBack) {
    private val handler = Handler(Looper.getMainLooper())
    private val executor = Executors.newSingleThreadExecutor()


    interface CallBack {
        fun onStartLoading()
        fun onSuccess(movieDetail: MovieDetail)
        fun onError(message: String)
    }

    fun execute(url: String) {
        callBack.onStartLoading()
        executor.execute {
            var urlConnection: HttpURLConnection? = null
            var buffer: BufferedInputStream? = null
            var stream: InputStream? = null
            try {
                val requestUrl = URL(url)
                urlConnection = requestUrl.openConnection() as HttpURLConnection
                urlConnection.readTimeout = 2000
                urlConnection.connectTimeout = 2000

                val statusCode = urlConnection.responseCode
                if (statusCode == 400) {
                    stream = urlConnection.errorStream
                    buffer = BufferedInputStream(stream)
                    val jsonString = toString(buffer)
                    val message = JSONObject(jsonString).getString("message")
                    throw IOException(message)
                }else if (statusCode > 400) {
                    throw IOException("Erro na comunicação com o servidor")
                }

                stream = urlConnection.inputStream
                buffer = BufferedInputStream(stream)
                val jsonAsString = this.toString(buffer)
                val movie = this.toMovie(jsonAsString)
                handler.post {
                    callBack.onSuccess(movie)
                }
            } catch (e: IOException) {
                val message = e.message ?: "Erro desconhecido"
                handler.post {
                    callBack.onError(message)
                }
            } finally {
                urlConnection?.disconnect()
                stream?.close()
                buffer?.close()
            }

        }
    }

    private fun toMovie(json: String): MovieDetail {
        val jsonRoot = JSONObject(json)
        val id = jsonRoot.getInt("id")
        val title = jsonRoot.getString("title")
        val desc = jsonRoot.getString("desc")
        val cast = jsonRoot.getString("cast")
        val coverUrl = jsonRoot.getString("cover_url")
        val jsonMovies = jsonRoot.getJSONArray("movie")

        val similars = mutableListOf<Movie>()

        for(i in 0 until jsonMovies.length()) {
            val item = jsonMovies.getJSONObject(i)
            val similarId = item.getInt("id")
            val similarCoverUrl = item.getString("cover_url")
            similars.add(
                Movie(similarId, similarCoverUrl)
            )
        }
        val movie = Movie(id, coverUrl, title, desc, cast)
        return MovieDetail(movie, similars)

    }

    private fun toString(stream: InputStream): String {
        val bytes = ByteArray(1024)
        val byteOutput = ByteArrayOutputStream()
        var read: Int
        while (true) {
            read = stream.read(bytes)
            if (read <= 0) break
            byteOutput.write(bytes, 0, read)
        }

        return String(byteOutput.toByteArray())
    }
}