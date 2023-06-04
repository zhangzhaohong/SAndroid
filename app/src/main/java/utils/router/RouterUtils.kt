package utils.router

import com.therouter.TheRouter.build

/**
 * @author koala
 * @version 1.0
 * @date 2022/4/30 14:50
 * @description
 */
object RouterUtils {
    fun route(path: String?) {
        build(path)
            .navigation()
    }

    fun routeWithDirect(path: String?, direct: String?, extra: String? = null) {
        build(path)
            .withString("direct", direct)
            .withString("extra", extra)
            .navigation()
    }
}