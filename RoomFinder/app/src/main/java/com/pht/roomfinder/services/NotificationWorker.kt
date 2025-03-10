package com.pht.roomfinder.services

import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.pht.roomfinder.utils.DataLocal
import me.leolin.shortcutbadger.ShortcutBadger

class NotificationWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        val notificationCount = DataLocal.getInstance().getInt("notification") + 1
        DataLocal.getInstance().putInt("notification", notificationCount)

        val intent = Intent("UPDATE_BADGE")
        intent.putExtra("count", notificationCount)
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)

        ShortcutBadger.applyCount(applicationContext, notificationCount)

        return Result.success()
    }
}