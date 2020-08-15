package com.imyyq.mvvm.bus

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

internal data class StickyData(
    val liveData: MutableLiveData<Any>,
    var registrants: Any?,
    var observer: Observer<Any>?,
    var isValueValid: Boolean
)