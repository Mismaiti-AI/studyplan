package com.mytask.core.database

import androidx.room.RoomDatabaseConstructor

/**
 * Room KSP generates actual implementations for each platform.
 * DO NOT implement this yourself - Room handles it.
 */
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}