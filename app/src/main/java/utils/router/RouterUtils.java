package utils.router;

import com.therouter.TheRouter;
import com.tristana.sandroid.R;

/**
 * @author koala
 * @version 1.0
 * @date 2022/4/30 14:50
 * @description
 */
public class RouterUtils {
    public static void route(String path) {
        TheRouter
                .build(path)
                .navigation();
    }

    public static void routeWithDirect(String path, String direct) {
        TheRouter
                .build(path)
                .withString("direct", direct)
                .navigation();
    }
}
