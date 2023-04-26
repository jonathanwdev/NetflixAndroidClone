package com.jonathanwdev.netflix.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.jonathanwdev.netflix.models.Category
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

class ImageTask(private val callback: CallBack) {
    private val handler = Handler(Looper.getMainLooper())
    private val executor = Executors.newSingleThreadExecutor()

    interface CallBack {
        fun onSuccess(bitmap: Bitmap)
    }

    fun execute(url:String) {
        executor.execute {
            var urlConnection: HttpURLConnection? = null
            var stream: InputStream? = null

            try {
                val requestUrl = URL(url)
                urlConnection =  requestUrl.openConnection() as HttpURLConnection
                urlConnection.readTimeout = 2000
                urlConnection.connectTimeout = 2000

                val statusCode = urlConnection.responseCode
                if(statusCode > 400) {
                    throw IOException("Erro na comunicação com o servidor")
                }

                stream = urlConnection.inputStream
                val bitmap = BitmapFactory.decodeStream(stream)


                handler.post {
                    callback.onSuccess(bitmap)
                }
            }catch(e: IOException){
                val message = e.message ?: "Erro desconhecido"
                Log.e("ERROR", message)
            }finally {
                urlConnection?.disconnect()
                stream?.close()

            }
        }
    }
}