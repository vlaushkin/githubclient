package com.laushkin.githubclient

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private val gitHubApi = makeGitHubApi()

    private val compositeDisposable = CompositeDisposable()

    private fun makeGitHubApi(): GithubApi {
        val retrofit = Retrofit.Builder()
                .baseUrl("https://api.github.com")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

        return retrofit.create(GithubApi::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            gitHubApi.listUsers(editText.text.toString())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy(
                            onSuccess = { list ->
                                if (!list.isEmpty()) {
                                    updateAvatar(list.first().owner)
                                    updateList(list)

                                    list.forEach { Log.d("dbg", "repo: $it") }
                                }
                            },
                            onError = {
                                it.printStackTrace()
                                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                            }
                    ).addTo(compositeDisposable)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        compositeDisposable.dispose()
    }

    private fun updateList(list: List<Repo>) {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = RepoAdapter(layoutInflater, list) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(it.htmlUrl)

            startActivity(intent)
        }
    }

    private fun updateAvatar(owner: Owner) {
        Glide.with(this).load(owner.avatarUrl).into(avatar)


    }

    class RepoViewHolder(val view: View, val onClick: (Repo) -> Unit): RecyclerView.ViewHolder(view) {
        private val name  = view.findViewById<TextView>(R.id.name)
        private val language = view.findViewById<TextView>(R.id.language)
        private val description = view.findViewById<TextView>(R.id.description)

        fun bind(repo: Repo) {
            name.text = repo.name
            language.text = repo.language
            description.text = repo.description

            view.setOnClickListener { onClick(repo) }
        }
    }

    class RepoAdapter(val layoutInflater: LayoutInflater,
                      val list: List<Repo>,
                      val onClick: (Repo) -> Unit): RecyclerView.Adapter<RepoViewHolder>() {
        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RepoViewHolder {
            val view = layoutInflater.inflate(R.layout.repo_row, p0, false)
            return RepoViewHolder(view, onClick)
        }

        override fun getItemCount() = list.size

        override fun onBindViewHolder(p0: RepoViewHolder, p1: Int) {
            p0.bind(list[p1])
        }

    }
}
