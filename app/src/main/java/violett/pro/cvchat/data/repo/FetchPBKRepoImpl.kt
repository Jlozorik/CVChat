package violett.pro.cvchat.data.repo

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.util.network.UnresolvedAddressException
import violett.pro.cvchat.data.remote.model.GetPublicKeyRequestDto
import violett.pro.cvchat.data.remote.model.SendPublicKeyRequestDto
import violett.pro.cvchat.domain.model.errors.NetworkError
import violett.pro.cvchat.domain.repo.FetchPBKRepo
import violett.pro.cvchat.domain.repo.SendKeyRepo
import violett.pro.cvchat.domain.util.CustomResult

class FetchPBKRepoImpl(
    private val client: HttpClient
) : FetchPBKRepo {

    override suspend fun fetchPBK(tempId: String): CustomResult<GetPublicKeyRequestDto, NetworkError> {
        return try {
            val response = client.get(
                urlString = "http://192.168.1.100:8080/key/${tempId}/"
            )

            if (response.status.isSuccess()) {
                val responseBody: GetPublicKeyRequestDto = response.body()
                CustomResult.Success(responseBody)
            } else {
                val error = when (response.status.value) {
                    403 -> NetworkError.Forbidden
                    404 -> NetworkError.NotFound
                    in 500..599 -> NetworkError.ServerError
                    else -> NetworkError.Unknown("Error: ${response.status}")
                }
                CustomResult.Failure(error)
            }

        } catch (e: Exception) {
            // Этот блок останется для сетевых ошибок (нет интернета, таймаут)
            val error = when (e) {
                is UnresolvedAddressException -> NetworkError.NoInternet
                is io.ktor.client.plugins.RedirectResponseException -> NetworkError.Unknown("Redirect")
                is kotlinx.serialization.SerializationException -> NetworkError.Serialization
                else -> NetworkError.Unknown(e.message ?: "Unknown error")
            }
            CustomResult.Failure(error)
        }
    }
}