package com.example.findtofine.data.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class GetAllTaskResponse(

	@field:SerializedName("GetAllTaskResponse")
	val getAllTaskResponse: List<GetAllTaskResponseItem?>? = null
) : Parcelable

@Parcelize
data class GetAllTaskResponseItem(

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("finishDate")
	val finishDate: String? = null,

	@field:SerializedName("location")
	val location: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("items")
	val items: Int? = null,

	@field:SerializedName("startDate")
	val startDate: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null,

	@field:SerializedName("status")
	val status: String? = null
) : Parcelable
