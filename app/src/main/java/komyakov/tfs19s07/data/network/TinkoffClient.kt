package komyakov.tfs19s07.data.network

import com.google.gson.GsonBuilder
import io.reactivex.Flowable
import io.reactivex.Single
import komyakov.tfs19s07.BuildConfig
import komyakov.tfs19s07.dto.Article
import komyakov.tfs19s07.dto.NewsHeader
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object TinkoffClient {

    private val service: TinkoffService

    init {
        val httpClient = OkHttpClient.Builder()
            .addNetworkInterceptor(
                HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BASIC)
            ).build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.ENDPOINT)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .client(httpClient)
            .build()

        service = retrofit.create(TinkoffService::class.java)
    }

    fun loadNewsHeaders(): Flowable<List<NewsHeader>> {
        return service.news()
            .flatMapPublisher { Flowable.just(it) }
            .map { it.payload }
    }

    fun loaArticle(id: String): Single<Article> {
        return service.article(id)
            .map { it.payload }
    }
}