package com.imyyq.mvvm.app

import androidx.collection.ArrayMap
import androidx.collection.arrayMapOf
import com.imyyq.mvvm.base.BaseModel

/**
 * 仓库管理类
 */
@Suppress("UNCHECKED_CAST")
object RepositoryManager {
    private lateinit var mRepoMap: ArrayMap<Class<out BaseModel>, BaseModel>

    /**
     * 获取仓库实例，[isCache] 默认是 true，即缓存仓库实例
     */
    fun <M : BaseModel> getRepo(clz: Class<out M>, isCache: Boolean = true): M {
        // 不缓存，则直接创建
        if (!isCache) {
            return clz.newInstance()
        }
        // 缓存，则保存到 map 中，下次复用
        if (!this::mRepoMap.isInitialized) {
            mRepoMap = arrayMapOf()
        }
        var repo = mRepoMap[clz]
        if (repo == null) {
            repo = clz.newInstance()
            mRepoMap[clz] = repo
        }
        return repo as M
    }
}