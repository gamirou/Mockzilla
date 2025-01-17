package com.apadmi.mockzilla.demo

import com.apadmi.mockzilla.lib.service.AuthHeaderProvider

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import okhttp3.OkHttpClient
import okhttp3.Protocol

import java.net.Proxy

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

sealed class DataResult<out T, out E> {
    fun dataOrNull() = when (this) {
        is Success -> ok
        is Failure -> null
    }

    fun errorOrNull() = when (this) {
        is Success -> null
        is Failure -> error
    }

    fun isSuccess() = this is Success

    /**
     * @property error
     */
    data class Failure<E>(val error: E) : DataResult<Nothing, E>()
    /**
     * @property ok
     */
    data class Success<T>(val ok: T) : DataResult<T, Nothing>()

    companion object
}

interface MockzillaTokenProvider {
    suspend fun getTokenHeader(): AuthHeaderProvider.Header
}
class Repository(private val baseUrl: String, private val tokenProvider: MockzillaTokenProvider) {
    suspend fun getCow(someRequestValue: String): DataResult<CowDto, String> {
        val response = HttpClient(OkHttp) {
            engine {
                preconfigured = OkHttpClient.Builder()
                    .proxy(Proxy.NO_PROXY)
                    .protocols(listOf(Protocol.HTTP_1_1)).build()
            }
        }.post(
            "$baseUrl/cow") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(GetCowRequestDto(someRequestValue)))

            // In release mode this matters, otherwise it can be omitted
            val header = tokenProvider.getTokenHeader()
            headers.append(header.key, header.value)
        }

        return if (response.status.isSuccess()) {
            DataResult.Success(Json.decodeFromString(response.bodyAsText()))
        } else {
            DataResult.Failure("${response.status} - ${response.bodyAsText()}")
        }
    }
}
