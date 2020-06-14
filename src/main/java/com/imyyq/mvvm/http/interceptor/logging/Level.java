package com.imyyq.mvvm.http.interceptor.logging;

public interface Level {
    /**
     * No logs.
     */
    int NONE = 0;
    /**
     * <p>Example:
     * <pre>{@code
     *  - URL
     *  - Method
     *  - Headers
     *  - Body
     * }</pre>
     */
    int BASIC = 1;
    /**
     * <p>Example:
     * <pre>{@code
     *  - URL
     *  - Method
     *  - Headers
     * }</pre>
     */
    int HEADERS = 2;
    /**
     * <p>Example:
     * <pre>{@code
     *  - URL
     *  - Method
     *  - Body
     * }</pre>
     */
    int BODY = 3;
}
