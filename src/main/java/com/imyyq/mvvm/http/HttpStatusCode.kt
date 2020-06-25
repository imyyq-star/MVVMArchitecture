package com.imyyq.mvvm.http

const val notHttpException = -100
const val entityNullable = -1
const val entityCodeNullable = -2

// 1xx消息
const val perhapsContinue = 100
const val switchingProtocols = 101
const val processing = 102

// 2xx成功
const val success = 200
const val created = 201
const val accepted = 202
const val nonAuthoritativeInformation = 203
const val noContent = 204
const val resetContent = 205
const val partialContent = 206
const val multiStatus = 207
const val alreadyReported = 208
const val imUsed = 226

// 3xx重定向
const val multipleChoices = 300
const val movedPermanently = 301
const val found = 302
const val seeOther = 303
const val notModified = 304
const val useProxy = 305
const val switchProxy = 306
const val temporaryRedirect = 307
const val permanentRedirect = 308

// 4xx客户端错误
const val badRequest = 400
const val unauthorized = 401
const val paymentRequired = 402
const val forbidden = 403
const val notFound = 404
const val methodNotAllowed = 405
const val notAcceptable = 406
const val proxyAuthenticationRequired = 407
const val requestTimeout = 408
const val conflict = 409
const val gone = 410
const val lengthRequired = 411
const val preconditionFailed = 412
const val requestEntityTooLarge = 413
const val requestUriTooLong = 414
const val unsupportedMediaType = 415
const val requestedRangeNotSatisfiable = 416
const val expectationFailed = 417
const val i_m_a_teapot = 418
const val misdirectedRequest = 421
const val unprocessableEntity = 422
const val locked = 423
const val failedDependency = 424
const val tooEarly = 425
const val upgradeRequired = 426
const val preconditionRequired = 428
const val tooManyRequests = 429
const val requestHeaderFieldsTooLarge = 431
const val unavailableForLegalReasons = 451

// 5xx服务器错误
const val internalServerError = 500
const val notImplemented = 501
const val badGateway = 502
const val serviceUnavailable = 503
const val gatewayTimeout = 504
const val httpVersionNotSupported = 505
const val variantAlsoNegotiates = 506
const val insufficientStorage = 507
const val loopDetected = 508
const val notExtended = 510
const val networkAuthenticationRequired = 511

// 非官方状态码
const val enhanceYourCalm = 420
const val noResponse = 444
const val blockedbyWindowsParentalControls = 450
const val requestHeaderTooLarge = 494
