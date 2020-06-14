package com.imyyq.mvvm.room

import android.util.ArrayMap

import androidx.room.Room
import androidx.room.RoomDatabase
import com.imyyq.mvvm.app.BaseApp

/**
 * 便捷创建 room 实例的工具，通常来说只需要实例化一次即可，数据库也应该只有一个
 */
object RoomUtil {
    private val mDBEntityMap = ArrayMap<String, RoomDatabase>()

    @Synchronized
    fun <T : RoomDatabase> getDB(
        cls: Class<T>, dbName: String,
        callback: RoomDatabase.Callback?
    ): T {
        val name = cls.name
        var database: RoomDatabase? = mDBEntityMap[name]
        if (database == null) {
            val builder = Room.databaseBuilder(
                BaseApp.getInstance(), cls,
                dbName
            )
            if (callback != null) {
                builder.addCallback(callback)
            }
            database = builder.build()
            mDBEntityMap[name] = database
        }
        @Suppress("UNCHECKED_CAST")
        return database as T
    }

    fun <T : RoomDatabase> getDB(cls: Class<T>, callback: RoomDatabase.Callback?): T {
        return getDB(cls, "default.db", callback)
    }

    fun <T : RoomDatabase> getDB(cls: Class<T>): T {
        return getDB(cls, null)
    }
}
