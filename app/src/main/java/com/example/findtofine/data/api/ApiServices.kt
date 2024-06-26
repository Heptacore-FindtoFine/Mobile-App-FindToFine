package com.example.findtofine.data.api

import com.example.findtofine.data.UpdateItemsStatus
import com.example.findtofine.data.UpdateTaskRequest
import com.example.findtofine.data.response.DeleteResponse
import com.example.findtofine.data.response.GetAllTaskResponseItem
import com.example.findtofine.data.response.GetTaskDetailResponse
import com.example.findtofine.data.response.LoginResponse
import com.example.findtofine.data.response.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
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
    ): RegisterResponse

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
        @Part("title") title: RequestBody,
        @Part image: MultipartBody.Part,
        @Part("startDate") startDate: String,
        @Part("finishDate") finishDate: String,
        @Part("location") location: RequestBody,
        @Part("description") description: RequestBody,
        @Part items: List<MultipartBody.Part>
    ): GetTaskDetailResponse

    @PUT("task/{id}/items")
    suspend fun updateTaskItems(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body updateItemsStatus: UpdateItemsStatus
    ) : GetTaskDetailResponse

    @DELETE("task/{id}")
    suspend fun deleteItems(
        @Header("Authorization") token: String,
        @Path("id") id: String,
    ) : DeleteResponse


}