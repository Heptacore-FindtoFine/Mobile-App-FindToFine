package com.example.findtofine.data.api

import com.example.findtofine.data.response.LoginResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiServices {
    @FormUrlEncoded
    @POST("user/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @FormUrlEncoded
    @POST("user/register")
    suspend fun register(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse
}