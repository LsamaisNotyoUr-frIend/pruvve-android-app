package com.fluture.pruvve.retrofittcalls

import com.google.gson.annotations.SerializedName

data class AccountType(
    @SerializedName("accountType")
    val accountType: String
)
data class AccountTypeResponse (
    val code: Int,
    val message:String
)