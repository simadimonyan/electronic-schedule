package com.mycollege.schedule.data.network

import com.mycollege.schedule.data.network.api.LedgerAPI
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class Network {

    companion object {

        fun connect(url: String, timeout: Int): Document {
            return Jsoup.connect(url)
                .userAgent("Mozilla")
                .timeout(timeout)
                .get()
        }

    }

}

class RetrofitClient(private val urlString: String) {

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(urlString)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val ledgerApi: LedgerAPI by lazy {
        retrofit.create(LedgerAPI::class.java)
    }

}
