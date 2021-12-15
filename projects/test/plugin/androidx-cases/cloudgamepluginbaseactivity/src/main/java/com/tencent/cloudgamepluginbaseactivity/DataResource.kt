/**
 * DataResource.kt
 * @author blinkjiang
 * @date 2019-07-24
 */
package com.tencent.cloudgamepluginbaseactivity

data class DataResource<out T>(
    val status: DataStatus,
    val data: T?,
    val message: String? = null,
    val module: Int = 0,
    val errorCode: Int = 0,
    val subCode: Int = 0
    ) {
    companion object {
        fun <T> success(data: T): DataResource<T> {
            return DataResource(DataStatus.SUCCESS, data)
        }

        fun <T> error(msg: String?): DataResource<T> {
            return DataResource(DataStatus.ERROR, null, msg)
        }

        fun <T> error(module: Int, errorCode: Int, subCode: Int): DataResource<T> {
            return DataResource(DataStatus.ERROR, null, null, module, errorCode, subCode)
        }

        fun <T> loading(data: T?): DataResource<T> {
            return DataResource(DataStatus.LOADING, data)
        }
    }
}
