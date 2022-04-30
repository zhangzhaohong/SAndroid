package utils.arouter;

import com.alibaba.android.arouter.launcher.ARouter;
import com.tristana.sandroid.R;

/**
 * @author koala
 * @version 1.0
 * @date 2022/4/30 14:50
 * @description
 */
public class ARouterUtils {
    public static void route(String path) {
        ARouter
                .getInstance()
                .build(path)
                .withTransition(R.anim.common_slide_in_left, R.anim.common_slide_out_right)
                .navigation();
    }
}
