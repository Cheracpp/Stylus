package com.aymane.stylus.data.repository

import com.aymane.stylus.data.model.ApiErrorResponse
import com.aymane.stylus.data.model.GrammarCorrectionRequest
import com.aymane.stylus.data.model.GrammarCorrectionResponse
import com.aymane.stylus.data.remote.GrammarCorrectionApiService
import com.aymane.stylus.util.AppConstants
import com.aymane.stylus.util.Resource
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for handling grammar correction operations.
 *
 * @property apiService The API service for grammar correction.
 */
@Singleton
class GrammarRepository @Inject constructor(
    private val apiService: GrammarCorrectionApiService
) {

    suspend fun correctGrammar(text: String, language: String = AppConstants.Network.DEFAULT_LANGUAGE_CODE): Resource<GrammarCorrectionResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val request = GrammarCorrectionRequest(text = text, language = language)
                val response = apiService.correctGrammar(request)

                if (response.isSuccessful) {
                    response.body()?.let { correctionResponse ->
                        if (correctionResponse.success) {
                            Resource.Success(correctionResponse)
                        } else {
                            Resource.Error(correctionResponse.message ?: "Grammar correction failed")
                        }
                    } ?: Resource.Error(AppConstants.ErrorMessages.EMPTY_SERVER_RESPONSE)
                } else {
                    val errorMessage = try {
                        val errorResponse = Gson().fromJson(
                            response.errorBody()?.string(),
                            ApiErrorResponse::class.java
                        )
                        errorResponse.message ?: "${AppConstants.ErrorMessages.SERVER_ERROR_PREFIX}${response.code()}"
                    } catch (e: Exception) {
                        "${AppConstants.ErrorMessages.SERVER_ERROR_PREFIX}${response.code()} - ${response.message()}"
                    }
                    Resource.Error(errorMessage)
                }
            } catch (e: Exception) {
                Resource.Error("${AppConstants.ErrorMessages.NETWORK_ERROR_PREFIX}${e.localizedMessage ?: AppConstants.ErrorMessages.UNKNOWN_ERROR}")
            }
        }
    }
}