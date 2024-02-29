package com.prafull.cleanteams

import android.app.Notification
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import android.widget.Toast

class NotificationListener: NotificationListenerService() {

    private lateinit var sharedPreferences: SharedPreferences
    private var titleList = listOf<String>()
    private var keywordList = listOf<String>()

    override fun onCreate() {
        super.onCreate()
        sharedPreferences = applicationContext.getSharedPreferences("SP", Context.MODE_PRIVATE)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        sbn?.notification?.extras?.let {
            if(sbn.packageName.orEmpty().contains("com.microsoft.teams")
                && sharedPreferences.getBoolean("active", false)) {
                runNotificationCleaningLogic(it, sbn.key)
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
    }

    private fun runNotificationCleaningLogic(bundle: Bundle, notificationKey: String) {
        val title = bundle.getString(Notification.EXTRA_TITLE).orEmpty() //"Group Name: Sender Name " - Group Msg format // "Sender Name " - DM Format
        val subtext = bundle.getString(Notification.EXTRA_SUB_TEXT) // no use
        val text = bundle.getString(Notification.EXTRA_TEXT).orEmpty() // text message content
        val convTitle = bundle.getString(Notification.EXTRA_CONVERSATION_TITLE) // empty for single sender, else group name
        var isValidTitle = false
        var isValidKeyword = false

        val savedTitles = sharedPreferences.getString("title",null).orEmpty()
        if(savedTitles.isNotEmpty()) titleList = savedTitles.split(",")
        val savedKeywords = sharedPreferences.getString("keyword","").orEmpty()
        if(savedKeywords.isNotEmpty()) keywordList = savedKeywords.split(",")
        /**
         * For group name: check convTitle before ':'
         * For sender name: check title
         * For message content: check text
         */

        for(current in titleList) {
            if(title.lowercase().contains(current.trim().toLowerCase())) {
                isValidTitle = true
                break
            }
        }

        for(current in keywordList) {
            if(text.lowercase().contains(current.trim().toLowerCase())) {
                isValidKeyword = true
                break
            }
        }

        if(isValidTitle || isValidKeyword) {
            //let notification go through
        } else {
            cancelNotification(notificationKey)
            Toast.makeText(applicationContext, "Notification blocked", Toast.LENGTH_SHORT).show()
        }
    }
}