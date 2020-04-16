package uk.co.nelsonwright.londonundergroundstatus.api

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface TflApiService {

    @GET("line/mode/tube,dlr,tflrail,overground,tram/status")
    fun getLinesStatusNow(
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

}