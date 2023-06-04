package com.tristana.sandroid

import android.os.Bundle
import androidx.navigation.NavController
import com.blankj.utilcode.util.LogUtils
import com.tristana.sandroid.ui.ad.AdWebViewFragment
import com.tristana.sandroid.ui.setting.LabFragment
import com.tristana.sandroid.ui.setting.SettingFragment

/**
 * @author koala
 * @version 1.0
 * @date 2023/6/3 12:16
 * @description
 */
class FragmentDirector {
    companion object {
        fun doDirect(navController: NavController, direct: String?, extra: String?): Boolean {
            direct?.let {
                LogUtils.i("found direct: $it, extra: $extra")
                when (direct) {
                    SettingFragment.ROUTE -> {
                        navController.navigate(R.id.nav_setting)
                        return true
                    }

                    LabFragment.ROUTE -> {
                        navController.navigate(R.id.nav_setting_lab)
                        return true
                    }

                    AdWebViewFragment.ROUTE -> {
                        val bundle = Bundle()
                        bundle.putString(
                            "url", if (extra.isNullOrEmpty()) {
                                "about:blank"
                            } else {
                                extra
                            }
                        )
                        navController.navigate(R.id.nav_ad_browser, bundle)
                        return true
                    }

                    else -> {
                        LogUtils.i("unsupported direct: $direct")
                    }
                }
            }
            return false
        }
    }
}