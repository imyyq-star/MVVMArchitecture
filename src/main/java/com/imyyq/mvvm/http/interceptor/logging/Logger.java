package com.imyyq.mvvm.http.interceptor.logging;

import okhttp3.internal.platform.Platform;

public interface Logger {
    void log(int level, String tag, String msg);

    Logger DEFAULT = (level, tag, message) -> Platform.get().log(level, message, null);
}
