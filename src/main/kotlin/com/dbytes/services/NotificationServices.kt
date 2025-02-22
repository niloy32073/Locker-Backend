package com.dbytes.services

import com.dbytes.interfaces.NotificationRepository
import com.dbytes.models.Notification

class NotificationServices(private val notificationRepository: NotificationRepository) {
    suspend fun addNotification(notification: Notification) {
        notificationRepository.addNotification(notification)
    }
    suspend fun getNotifications(id:Long = 0): List<Notification> {
        return notificationRepository.getNotifications(id)
    }
}