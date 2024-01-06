package com.tristana.sandroid

import android.os.Bundle
import androidx.navigation.NavController
import com.blankj.utilcode.util.LogUtils
import com.tristana.sandroid.ui.ad.AdWebViewFragment
import com.tristana.sandroid.ui.downloader.DownloadManagerFragment
import com.tristana.sandroid.ui.music.area.operation.MusicSearchOperationFragment
import com.tristana.sandroid.ui.setting.LabFragment
import com.tristana.sandroid.ui.setting.SettingFragment
import com.tristana.sandroid.ui.webview.X5WebViewFragment

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
                    MusicSearchOperationFragment.ROUTE -> {
                        navController.navigate(R.id.nav_music_search_operation)
                        return true
                    }
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

                    X5WebViewFragment.ROUTE -> {
                        val bundle = Bundle()
                        bundle.putString(
                            "url", if (extra.isNullOrEmpty()) {
                                "about:blank"
                            } else {
                                extra
                            }
                        )
                        navController.navigate(R.id.nav_browser, bundle)
                        return true
                    }

                    DownloadManagerFragment.ROUTE -> {
                        navController.navigate(R.id.nav_download_manager)
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