package violett.pro.cvchat.data.remote

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import violett.pro.cvchat.domain.model.websocket.ClientMessage
import violett.pro.cvchat.domain.model.websocket.ServerResponse

class ChatSocketService(
    private val client: HttpClient,
    private val json: Json
) {
    private var session: DefaultClientWebSocketSession? = null

    fun connect(tempId: String): Flow<ServerResponse> = flow {
        try {

            client.webSocket(
                request = {
                    url("ws://192.168.1.100:8080/chat/")
                    parameter("id", tempId)
                }
            ) {
                session = this
                Log.d("ChatSocket", "Connected!")

                for (frame in incoming) {
                    if (frame is Frame.Text) {
                        val text = frame.readText()
                        try {
                            val event = json.decodeFromString<ServerResponse>(text)
                            emit(event)
                        } catch (e: Exception) {
                            Log.e("ChatSocket", "Parse error: ${e.message}")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("ChatSocket", "Connection error: ${e.localizedMessage}")
        } finally {
            session = null
            Log.d("ChatSocket", "Disconnected")
        }
    }

    suspend fun sendMessage(msg: ClientMessage) {
        val jsonString = json.encodeToString(msg)
        session?.send(Frame.Text(jsonString))
    }

    suspend fun disconnect() {
        session?.close()
        session = null
    }
}