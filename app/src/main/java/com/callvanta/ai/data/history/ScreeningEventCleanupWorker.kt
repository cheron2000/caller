package com.callvanta.ai.data.history

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

/** Periodically removes screening events once they pass the approved 30-day retention window. */
class ScreeningEventCleanupWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : Worker(appContext, workerParams) {
    override fun doWork(): Result = try {
        ScreeningEventStore(applicationContext).loadEvents()
        Result.success()
    } catch (_: Exception) {
        Result.retry()
    }
}
