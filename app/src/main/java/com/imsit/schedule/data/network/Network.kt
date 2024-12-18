package com.imsit.schedule.data.network

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

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