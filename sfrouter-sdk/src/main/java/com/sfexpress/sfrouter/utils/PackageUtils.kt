package com.sfexpress.sfrouter.utils

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import com.sfexpress.sfrouter.launcher.SFRouterManager
import com.sfexpress.sfrouter.utils.Consts.LAST_VERSION_CODE
import com.sfexpress.sfrouter.utils.Consts.LAST_VERSION_NAME
import com.sfexpress.sfrouter.utils.Consts.SFROUTER_SP_CACHE_KEY

/**
 * 包管理工具类
 * Created by sf-zhangpeng on 2018/3/13.
 */
object PackageUtils {
    private var NEW_VERSION_NAME: String? = null
    private var NEW_VERSION_CODE: Int = 0

    fun isNewVersion(context: Context): Boolean {
        val packageInfo = getPackageInfo(context)
        if (null != packageInfo) {
            val versionName = packageInfo.versionName
            val versionCode = packageInfo.versionCode
            val sp = context.getSharedPreferences(SFROUTER_SP_CACHE_KEY, Context.MODE_PRIVATE)
            return if (versionName != sp.getString(LAST_VERSION_NAME, null)
                    || versionCode != sp.getInt(LAST_VERSION_CODE, -1)) {
                // new version
                NEW_VERSION_NAME = versionName
                NEW_VERSION_CODE = versionCode
                true
            } else {
                false
            }
        } else {
            return true
        }
    }

    fun updateVersion(context: Context) {
        if (!android.text.TextUtils.isEmpty(NEW_VERSION_NAME) && NEW_VERSION_CODE != 0) {
            val sp = context.getSharedPreferences(SFROUTER_SP_CACHE_KEY, Context.MODE_PRIVATE)
            sp.edit().putString(LAST_VERSION_NAME, NEW_VERSION_NAME).putInt(LAST_VERSION_CODE, NEW_VERSION_CODE).apply()
        }
    }

    private fun getPackageInfo(context: Context): PackageInfo? {
        var packageInfo: PackageInfo? = null
        try {
            packageInfo = context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_CONFIGURATIONS)
        } catch (ex: Exception) {
            SFRouterManager.logger.error(Consts.TAG, "Get package info error.")
        }

        return packageInfo
    }
}
