package com.laushkin.githubclient

import com.google.gson.annotations.SerializedName

/**
 * @author Vasily Laushkin <vaslinux@gmail.com> on 17/10/2018.
 */
data class Owner(
        @SerializedName("login") val login: String,
        @SerializedName("avatar_url") val avatarUrl: String
)