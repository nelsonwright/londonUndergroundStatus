package uk.co.nelsonwright.londonundergroundstatus.api

import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface TflApiService {

    @GET("line/mode/tube,dlr,tflrail,overground,tram/status")
    fun getLinesStatus(
        @Query("app_id") appID: String,
        @Query("app_key") appKey: String
    ): Observable<List<TubeLine>>

    @GET("line/mode/tube,dlr,tflrail,overground,tram/status")
    fun getLinesStatusForWeekend(
        @Query("app_id") appID: String,
        @Query("app_key") appKey: String,
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String,
        @Query("detail") detail: Boolean = true
    ): Observable<List<TubeLine>>

    companion object {
        fun create(): TflApiService {

            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(
                    RxJava2CallAdapterFactory.create()
                )
                .addConverterFactory(
                    GsonConverterFactory.create()
                )
                .baseUrl("https://api.tfl.gov.uk/")
                .build()

            return retrofit.create(TflApiService::class.java)
        }
    }

}