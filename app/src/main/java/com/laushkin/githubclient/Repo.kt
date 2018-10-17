package com.laushkin.githubclient

import com.google.gson.annotations.SerializedName

/**
 * @author Vasily Laushkin <vaslinux@gmail.com> on 17/10/2018.
 */
data class Repo(
        @SerializedName("name") val name: String,
        @SerializedName("description") val description: String,
        @SerializedName("language") val language: String,
        @SerializedName("html_url") val htmlUrl: String,
        @SerializedName("owner") val owner: Owner
)