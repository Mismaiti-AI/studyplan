package com.mytask.core.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import com.mytask.data.local.dao.AppConfigDao
import com.mytask.data.local.dao.AssignmentDao
import com.mytask.data.local.dao.ExamDao
import com.mytask.data.local.dao.ProjectDao
import com.mytask.data.local.entity.AppConfigEntity
import com.mytask.data.local.entity.AssignmentEntity
import com.mytask.data.local.entity.ExamEntity
import com.mytask.data.local.entity.ProjectEntity

@Database(
    entities = [
        AssignmentEntity::class,
        ExamEntity::class,
        ProjectEntity::class,
        AppConfigEntity::class
    ],
    version = 1,
    exportSchema = false
)
@ConstructedBy(AppDatabaseConstructor::class)  // ← REQUIRED for KMP
abstract class AppDatabase : RoomDatabase() {
    abstract fun assignmentDao(): AssignmentDao
    abstract fun examDao(): ExamDao
    abstract fun projectDao(): ProjectDao
    abstract fun appConfigDao(): AppConfigDao
}