package violett.pro.cvchat.data.repo

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.util.network.UnresolvedAddressException
import violett.pro.cvchat.data.remote.model.SendPublicKeyRequestDto
import violett.pro.cvchat.domain.model.errors.NetworkError
import violett.pro.cvchat.domain.repo.SendKeyRepo
import violett.pro.cvchat.domain.util.CustomResult

class SendKeyRepoImpl(
    private val client: HttpClient
) : SendKeyRepo {

    override suspend fun sendPublicKey(publicKey: String, tempId: String): CustomResult<Unit, NetworkError> {
        return try {
            val requestDto = SendPublicKeyRequestDto(
                publicKey = publicKey,
                tempId = tempId
            )
            client.post {
                url("http://192.168.1.100:8080/register/")
                setBody(requestDto)
                contentType(ContentType.Application.Json)
            }

            CustomResult.Success(Unit)

        } catch (e: Exception) {
            val error = when (e) {
                is UnresolvedAddressException -> NetworkError.NoInternet
                is io.ktor.client.plugins.RedirectResponseException -> NetworkError.Unknown("Redirect")
                is io.ktor.client.plugins.ClientRequestException -> {
                    when (e.response.status.value) {
                        403 -> NetworkError.Forbidden
                        404 -> NetworkError.Forbidden
                        in 500..599 -> NetworkError.ServerError
                        else -> NetworkError.Unknown("Client error: ${e.response.status}")
                    }
                }
                is io.ktor.client.plugins.ServerResponseException -> NetworkError.ServerError
                is kotlinx.serialization.SerializationException -> NetworkError.Serialization
                else -> NetworkError.Unknown(e.message ?: "Unknown error")
            }
            CustomResult.Failure(error)
        }
    }
}