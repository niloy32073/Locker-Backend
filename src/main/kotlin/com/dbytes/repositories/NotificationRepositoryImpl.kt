package com.dbytes.repositories

import com.dbytes.interfaces.NotificationRepository
import com.dbytes.models.Notification
import com.dbytes.tables.NotificationTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll

class NotificationRepositoryImpl:NotificationRepository {
    override suspend fun addNotification(notification: Notification) {
        NotificationTable.insert {  it[message] = notification.message
        it[userId] = notification.userId
        it[timestamp] = notification.timestamp}
    }

    override suspend fun getNotifications(id: Long): List<Notification> {
        if(id != 0L){
            val notifications = NotificationTable.selectAll().where { NotificationTable.userId eq id }.map { Notification(
                id = it[NotificationTable.id],
                message = it[NotificationTable.message],
                timestamp = it[NotificationTable.timestamp],
                userId = it[NotificationTable.userId]
            ) }
            return notifications
        }
        else{
            val notifications = NotificationTable.selectAll().map { Notification(
                id = it[NotificationTable.id],
                message = it[NotificationTable.message],
                timestamp = it[NotificationTable.timestamp],
                userId = it[NotificationTable.userId]
            ) }
            return notifications
        }
    }

}