package com.mycollege.schedule

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import ru.ok.tracer.HasTracerConfiguration
import ru.ok.tracer.TracerConfiguration
import ru.ok.tracer.crash.report.CrashFreeConfiguration
import ru.ok.tracer.crash.report.CrashReportConfiguration
import ru.ok.tracer.crash.report.TracerCrashReport
import ru.ok.tracer.disk.usage.DiskUsageConfiguration
import ru.ok.tracer.heap.dumps.HeapDumpConfiguration
import ru.rustore.sdk.core.feature.model.FeatureAvailabilityResult
import ru.rustore.sdk.pushclient.RuStorePushClient
import ru.rustore.sdk.pushclient.common.logger.DefaultLogger

@HiltAndroidApp
class App : Application(), HasTracerConfiguration {

    override fun onCreate() {
        super.onCreate()

        RuStorePushClient.init(
            application = this,
            projectId = "V4hdGCkpzfi5kzq6Nbu2biCX1HRb-IaS",
            logger = DefaultLogger()
        )

        RuStorePushClient.checkPushAvailability()
            .addOnSuccessListener { result ->
                if (result is FeatureAvailabilityResult.Available) {

                    RuStorePushClient.getToken()
                        .addOnSuccessListener { resultToken ->
                            Log.d("App", "getToken onSuccess token = $resultToken")
                        }
                        .addOnFailureListener { throwable ->
                            Log.e("App", "getToken onFailure", throwable)
                            TracerCrashReport.report(throwable, issueKey = "RUSTORE_PUSH_CLIENT")
                        }

                }
            }

    }

    override val tracerConfiguration: List<TracerConfiguration>
        get() = listOf(
            CrashReportConfiguration.build {
                setEnabled(true)
                setSendAnr(true)
                setNativeEnabled(true)
            },
            CrashFreeConfiguration.build {
                setEnabled(true)
            },
            HeapDumpConfiguration.build {
                setEnabled(true)
            },
            DiskUsageConfiguration.build {
                setEnabled(true)
                setProbability(1)
            },
        )

}