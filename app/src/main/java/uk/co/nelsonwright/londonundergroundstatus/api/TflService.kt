package uk.co.nelsonwright.londonundergroundstatus.api

import com.itkacher.okhttpprofiler.OkHttpProfilerInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uk.co.nelsonwright.londonundergroundstatus.BuildConfig

object TflService {
    fun create(): TflApiInterface {
        val builder = OkHttpClient.Builder()

        if (BuildConfig.DEBUG) {
            builder.addInterceptor(OkHttpProfilerInterceptor())
        }
        val client = builder.build()

        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.tfl.gov.uk/")
            .client(client)
            .build()

        return retrofit.create(TflApiInterface::class.java)
    }
}