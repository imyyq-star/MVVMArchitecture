package com.imyyq.mvvm.base

import android.content.Intent

interface IActivityResult {
    fun onActivityResult(resultCode: Int, intent: Intent = Intent()) {}
}