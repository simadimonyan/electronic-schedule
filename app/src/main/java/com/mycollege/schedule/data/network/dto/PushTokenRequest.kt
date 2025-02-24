package com.mycollege.schedule.data.network.dto

data class PushTokenRequest(
    private val androidId: String,
    private val phoneModel: String,
    private val pushToken: String,
    private val accessToken: String,
)