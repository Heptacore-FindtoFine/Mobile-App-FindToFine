package com.example.findtofine.data.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class RegisterResponse(

	@field:SerializedName("message")
	val message: String? = null
) : Parcelable
