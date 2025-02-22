package com.dbytes.helpers

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// Firebase Cloud Messaging URL
private const val FCM_URL = "https://fcm.googleapis.com/v1/projects/locker-6dd72/messages:send"
// Replace with your actual Firebase server key
private const val SERVER_KEY = "AIzaSyD4h5rBhm7UFa9Wgrvtu08m2TZ1OzsxK_g"

@Serializable
data class FcmNotification(
    val title: String,
    val body: String
)

@Serializable
data class FcmMessage(
    val to: String,
    val notification: FcmNotification
)

object FcmService {
    private val client = HttpClient()

    suspend fun sendNotification(targetToken: String, title: String, body: String): Boolean {
        return try {
            val message = FcmMessage(
                to = targetToken,
                notification = FcmNotification(title, body)
            )

            val response: HttpResponse = client.post(FCM_URL) {
                header(HttpHeaders.Authorization, "Bearer $SERVER_KEY")
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                setBody(Json.encodeToString(FcmMessage.serializer(), message))
            }

            response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            println("FCM Error: ${e.message}")
            false
        }
    }
}