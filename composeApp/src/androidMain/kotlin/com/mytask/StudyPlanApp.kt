package com.mytask

import android.app.Application
import co.touchlab.kermit.Logger

class StudyPlanApp : Application() {
    override fun onCreate() {
        super.onCreate()

        Logger.withTag("StudyPlanApp").d("onCreate")
    }
}