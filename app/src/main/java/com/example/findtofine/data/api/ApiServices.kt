package com.example.findtofine.data.api

import com.example.findtofine.data.UpdateTaskRequest
import com.example.findtofine.data.response.GetAllTaskResponseItem
import com.example.findtofine.data.response.GetTaskDetailResponse
import com.example.findtofine.data.response.LoginResponse
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

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

    @GET("task")
    suspend fun getAllTask(
        @Header("Authorization") token: String
    ): List<GetAllTaskResponseItem?>

    @GET("task/{id}")
    suspend fun getDetailTask(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): GetTaskDetailResponse

    @PUT("task/{id}")
    suspend fun updateTask(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body updateTaskRequest: UpdateTaskRequest
    ): GetTaskDetailResponse

    @Multipart
    @POST("task")
    suspend fun uploadTask(
        @Header("Authorization") token: String,
        @Part("title") title: String,
        @Part image: MultipartBody.Part,
        @Part("startDate") startDate: String,
        @Part("finishDate") finishDate: String,
        @Part("location") location: String,
        @Part("description") description: String,
        @Part items: List<MultipartBody.Part>
    ): GetTaskDetailResponse


}