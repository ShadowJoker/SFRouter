package com.sfexpress.sfrouter.service

import android.content.Context
import android.util.LruCache
import com.sfexpress.sfrouter.annotation.Route
import com.sfexpress.sfrouter.template.IInjector
import com.sfexpress.sfrouter.utils.Consts.SUFFIX_AUTOBIND

/**
 * AutoBind服务的实现
 * Created by sf-zhangpeng on 2018/3/14.
 */
@Route(path = "/sfrouter/service/autobind")
class AutowiredServiceImpl : AutoBindService {

    private var classCache = LruCache<String, IInjector>(50)
    private var blackList = mutableListOf<String>()

    override fun init(context: Context) {
    }

    override fun autoBind(instance: Any) {
        val className = instance.javaClass.name
        try {
            if (!blackList.contains(className)) {
                var autoBindHelper: IInjector? = classCache.get(className)
                if (null == autoBindHelper) {  // 没有缓冲的时候通过反射创建一个helper
                    autoBindHelper = Class.forName(instance.javaClass.name + SUFFIX_AUTOBIND).getConstructor().newInstance() as IInjector
                }
                autoBindHelper.inject(instance)
                classCache.put(className, autoBindHelper)
            }
        } catch (ex: Exception) {
            blackList.add(className)  // 出现异常说明这个类没有需要自动绑定的参数，加入黑名单，跳过后续处理
        }
    }
}
