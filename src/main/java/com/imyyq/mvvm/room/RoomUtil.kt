package com.imyyq.mvvm.room

import android.util.ArrayMap

import androidx.room.Room
import androidx.room.RoomDatabase
import com.imyyq.mvvm.app.BaseApp

object RoomUtil {
    private val mDBEntityMap = ArrayMap<String, AbstractAppDatabase>()

    @Synchronized
    fun <T : AbstractAppDatabase> getDB(
        cls: Class<T>, dbName: String,
        callback: RoomDatabase.Callback?
    ): T {
        val name = cls.name
        var database: AbstractAppDatabase? = mDBEntityMap[name]
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
        return database as T
    }

    fun <T : AbstractAppDatabase> getDB(cls: Class<T>, callback: RoomDatabase.Callback?): T {
        return getDB(cls, "default.db", callback)
    }

    fun <T : AbstractAppDatabase> getDB(cls: Class<T>): T {
        return getDB(cls, null)
    }
}
