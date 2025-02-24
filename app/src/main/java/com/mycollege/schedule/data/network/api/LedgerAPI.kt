package com.mycollege.schedule.data.network.api

import com.mycollege.schedule.data.network.dto.PushTokenRequest
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface LedgerAPI {
    @POST("/ledger/pullTokenUp")
    @Headers("Content-Type: application/json")
    suspend fun pullTokenUp(@Body request: PushTokenRequest): String
}