package com.aymane.stylus.data.remote

import com.aymane.stylus.data.model.GrammarCorrectionRequest
import com.aymane.stylus.data.model.GrammarCorrectionResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import javax.inject.Singleton

/**
 * Retrofit service interface for grammar correction API endpoints.
 */
@Singleton
interface GrammarCorrectionApiService {

    @POST("correct")
    suspend fun correctGrammar(
        @Body request: GrammarCorrectionRequest
    ): Response<GrammarCorrectionResponse>
}
