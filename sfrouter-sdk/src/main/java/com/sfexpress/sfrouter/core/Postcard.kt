package com.sfexpress.sfrouter.core

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.support.annotation.IntDef
import android.util.SparseArray
import com.sfexpress.sfrouter.annotation.models.RouteBaseData
import com.sfexpress.sfrouter.callback.NavigationCallback
import com.sfexpress.sfrouter.launcher.SFRouterManager
import com.sfexpress.sfrouter.service.SerializationService
import com.sfexpress.sfrouter.template.IProvider
import java.io.Serializable
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.util.*

/**
 * 路由导航的数据包，包含了需要携带的信息，本质上是一个RouteBaseData
 * Created by sf-zhangpeng on 2018/3/15.
 */
class Postcard constructor(path: String? = null, group: String? = null, uri: Uri? = null, bundle: Bundle? = null) : RouteBaseData() {

    // Base
    private var uri: Uri? = null
    private var tag: Any? = null  // 错误信息的tag
    private var extras: Bundle? = null // 需要携带的参数
    private var flags = -1  // 路由flag
    private var timeout = 300  // 导航超时时间，单位秒
    private var provider: IProvider? = null   // It will be set value, if this postcard was provider.
    private var isGreenChannel: Boolean = false
    private var serializationService: SerializationService? = null

    // Animation
    private var optionsBundle: Bundle? = null // The transition animation of activity
    private var enterAnim: Int = 0
    private var exitAnim: Int = 0

    fun getProvider(): IProvider? {
        return provider
    }

    fun setProvider(provider: IProvider): Postcard {
        this.provider = provider
        return this
    }

    init {
        setPath(path)
        setGroup(group)
        setUri(uri)
        this.extras = bundle ?: Bundle()
    }

    fun getTag(): Any? {
        return tag
    }

    fun setTag(tag: Any): Postcard {
        this.tag = tag
        return this
    }

    fun getExtras(): Bundle? {
        return extras
    }

    fun getFlag(): Int {
        return flags
    }

    fun getTimeout(): Int {
        return timeout
    }

    fun setTimeout(timeout: Int): Postcard {
        this.timeout = timeout
        return this
    }

    fun getUri(): Uri? {
        return uri
    }

    fun setUri(uri: Uri?): Postcard {
        this.uri = uri
        return this
    }

    fun getOptionsBundle(): Bundle? {
        return optionsBundle
    }

    fun getEnterAnim(): Int {
        return enterAnim
    }

    fun getExitAnim(): Int {
        return exitAnim
    }

    /**
     * 导航至postcard中携带的路径
     */
    @JvmOverloads
    fun navigation(context: Context? = null, callback: NavigationCallback? = null): Any? {
        return SFRouterManager.navigation(context, this, -1, callback)
    }

    /**
     * 导航至postcard中携带的路径
     */
    @JvmOverloads
    fun navigation(mContext: Activity, requestCode: Int = -1, callback: NavigationCallback? = null) {
        SFRouterManager.navigation(mContext, this, requestCode, callback)
    }

    /**
     * 设置绿色通道，绿色通道会忽略所有拦截器行为
     */
    fun greenChannel(): Postcard {
        this.isGreenChannel = true
        return this
    }

    fun isGreenChannel(): Boolean {
        return isGreenChannel
    }

    /**
     * 设置bundle，重复设置会覆盖
     */
    fun with(bundle: Bundle?): Postcard {
        if (null != bundle) {
            extras = bundle
        }
        return this
    }

    @Suppress("DEPRECATED_JAVA_ANNOTATION")
    @IntDef(Intent.FLAG_ACTIVITY_SINGLE_TOP.toLong(),
            Intent.FLAG_ACTIVITY_NEW_TASK.toLong(),
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION.toLong(),
            Intent.FLAG_DEBUG_LOG_RESOLUTION.toLong(),
            Intent.FLAG_FROM_BACKGROUND.toLong(),
            Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT.toLong(),
            Intent.FLAG_ACTIVITY_CLEAR_TASK.toLong(),
            Intent.FLAG_ACTIVITY_CLEAR_TOP.toLong(),
            Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS.toLong(),
            Intent.FLAG_ACTIVITY_FORWARD_RESULT.toLong(),
            Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY.toLong(),
            Intent.FLAG_ACTIVITY_MULTIPLE_TASK.toLong(),
            Intent.FLAG_ACTIVITY_NO_ANIMATION.toLong(),
            Intent.FLAG_ACTIVITY_NO_USER_ACTION.toLong(),
            Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP.toLong(),
            Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED.toLong(),
            Intent.FLAG_ACTIVITY_REORDER_TO_FRONT.toLong(),
            Intent.FLAG_ACTIVITY_TASK_ON_HOME.toLong(),
            Intent.FLAG_RECEIVER_REGISTERED_ONLY.toLong())
    @Retention(RetentionPolicy.SOURCE)
    annotation class FlagInt

    fun withFlags(@FlagInt flag: Int): Postcard {
        this.flags = flag
        return this
    }

    fun withObject(key: String?, value: Any?): Postcard {
        serializationService = SFRouterManager.navigation(SerializationService::class.java)
        extras?.putString(key, serializationService?.object2Json(value!!))
        return this
    }

    // Follow api copy from #{Bundle}

    fun withString(key: String?, value: String?): Postcard {
        extras!!.putString(key, value)
        return this
    }

    fun withBoolean(key: String?, value: Boolean): Postcard {
        extras!!.putBoolean(key, value)
        return this
    }

    fun withShort(key: String?, value: Short): Postcard {
        extras!!.putShort(key, value)
        return this
    }

    fun withInt(key: String?, value: Int): Postcard {
        extras!!.putInt(key, value)
        return this
    }

    fun withLong(key: String?, value: Long): Postcard {
        extras!!.putLong(key, value)
        return this
    }

    fun withDouble(key: String?, value: Double): Postcard {
        extras!!.putDouble(key, value)
        return this
    }

    fun withByte(key: String?, value: Byte): Postcard {
        extras!!.putByte(key, value)
        return this
    }

    fun withChar(key: String?, value: Char): Postcard {
        extras!!.putChar(key, value)
        return this
    }

    fun withFloat(key: String?, value: Float): Postcard {
        extras!!.putFloat(key, value)
        return this
    }

    fun withCharSequence(key: String?, value: CharSequence?): Postcard {
        extras!!.putCharSequence(key, value)
        return this
    }

    fun withParcelable(key: String?, value: Parcelable?): Postcard {
        extras!!.putParcelable(key, value)
        return this
    }

    fun withParcelableArray(key: String?, value: Array<Parcelable>?): Postcard {
        extras!!.putParcelableArray(key, value)
        return this
    }

    fun withParcelableArrayList(key: String?, value: ArrayList<out Parcelable>?): Postcard {
        extras!!.putParcelableArrayList(key, value)
        return this
    }

    fun withSparseParcelableArray(key: String?, value: SparseArray<out Parcelable>?): Postcard {
        extras!!.putSparseParcelableArray(key, value)
        return this
    }

    fun withIntegerArrayList(key: String?, value: ArrayList<Int>?): Postcard {
        extras!!.putIntegerArrayList(key, value)
        return this
    }

    fun withStringArrayList(key: String?, value: ArrayList<String>?): Postcard {
        extras!!.putStringArrayList(key, value)
        return this
    }

    fun withCharSequenceArrayList(key: String?, value: ArrayList<CharSequence>?): Postcard {
        extras!!.putCharSequenceArrayList(key, value)
        return this
    }

    fun withSerializable(key: String?, value: Serializable?): Postcard {
        extras!!.putSerializable(key, value)
        return this
    }

    fun withByteArray(key: String?, value: ByteArray?): Postcard {
        extras!!.putByteArray(key, value)
        return this
    }

    fun withShortArray(key: String?, value: ShortArray?): Postcard {
        extras!!.putShortArray(key, value)
        return this
    }

    fun withCharArray(key: String?, value: CharArray?): Postcard {
        extras!!.putCharArray(key, value)
        return this
    }

    fun withFloatArray(key: String?, value: FloatArray?): Postcard {
        extras!!.putFloatArray(key, value)
        return this
    }

    fun withCharSequenceArray(key: String?, value: Array<CharSequence>?): Postcard {
        extras!!.putCharSequenceArray(key, value)
        return this
    }

    fun withBundle(key: String?, value: Bundle?): Postcard {
        extras!!.putBundle(key, value)
        return this
    }

    fun withTransition(enterAnim: Int, exitAnim: Int): Postcard {
        this.enterAnim = enterAnim
        this.exitAnim = exitAnim
        return this
    }

    override fun toString(): String {
        return "Postcard{" +
                "uri=" + uri +
                ", tag=" + tag +
                ", mBundle=" + extras +
                ", flags=" + flags +
                ", timeout=" + timeout +
                ", provider=" + provider +
                ", greenChannel=" + isGreenChannel +
                ", optionsCompat=" + optionsBundle +
                ", enterAnim=" + enterAnim +
                ", exitAnim=" + exitAnim +
                "}\n" +
                super.toString()
    }
}
