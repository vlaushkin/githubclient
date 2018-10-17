package com.laushkin.githubclient

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * @author Vasily Laushkin <vaslinux@gmail.com> on 17/10/2018.
 */
interface GithubApi {
    @GET("users/{user}/repos")
    fun listUsers(@Path("user") login: String): Single<List<Repo>>
}