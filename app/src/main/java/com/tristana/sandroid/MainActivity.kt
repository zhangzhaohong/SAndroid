package com.tristana.sandroid

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.KeyEvent
import android.view.Menu
import android.view.MotionEvent
import androidx.activity.OnBackPressedCallback
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.event.tracker.ws.Constants
import com.event.tracker.ws.Constants.EVENT_ON_OPENED_ACTIVITY
import com.event.tracker.ws.model.AppInfoDataModel
import com.event.tracker.ws.model.DeviceInfoDataModel
import com.event.tracker.ws.model.EventTrackerDataModel
import com.event.tracker.ws.model.InfoEventDataModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.tencent.smtt.export.external.TbsCoreSettings
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.TbsDownloader
import com.tencent.smtt.sdk.TbsListener
import com.therouter.TheRouter
import com.therouter.router.Autowired
import com.therouter.router.Route
import com.tristana.library.tools.keyboard.KeyboardsUtils
import com.tristana.library.tools.sharedPreferences.SpUtils
import com.tristana.library.tools.watcher.HomeWatcher.OnHomePressedListener
import com.tristana.sandroid.customizeInterface.IOnBackPressedInterface
import com.tristana.sandroid.dataModel.data.DataModel
import com.tristana.sandroid.ui.webview.X5WebViewFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.reflect.Field
import java.util.*


@Route(path = MainActivity.ROUTE)
class MainActivity : AppCompatActivity() {

    companion object {
        const val ROUTE = "/app/activity/main"
    }

    @Autowired
    @JvmField
    var direct: String? = null

    @Autowired
    @JvmField
    var extra: String? = null

    private var mExitTime: Long = 0

    private var onHomePressedListener = object : OnHomePressedListener {
        override fun onHomeLongPressed() {
            LogUtils.i("onHomeLongPressed")
        }

        override fun onHomePressed() {
            LogUtils.i("onHomePressed")
        }
    }

    private var mAppBarConfiguration: AppBarConfiguration? = null
    private var menu: Menu? = null

    // private lateinit var mHomeWatcher: HomeWatcher
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // mHomeWatcher = HomeWatcher(this)
        // mHomeWatcher.setOnHomePressedListener(onHomePressedListener)
        // mHomeWatcher.startWatch()
        TheRouter.inject(this)
        BarUtils.transparentStatusBar(this)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        // val title = toolbar.getChildAt(0) as AppCompatTextView
        // title.layoutParams.width = LinearLayoutCompat.LayoutParams.MATCH_PARENT
        // title.gravity = Gravity.CENTER_HORIZONTAL
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = AppBarConfiguration.Builder(
            R.id.nav_space
        )
            .setOpenableLayout(drawer)
            .build()
        // val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        //1、先拿NavHostFragment
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        //2、再拿NavController
        val navController: NavController = navHostFragment.navController
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration!!)
        NavigationUI.setupWithNavController(navigationView, navController)
        initNavigationOnChangeListener(navController)
        onBackPressedDispatcher.addCallback(
            this@MainActivity,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val fragment = getFragment(X5WebViewFragment::class.java)
                    if (fragment == null) {
                        navController.popBackStack()
                    } else {
                        if ((fragment as IOnBackPressedInterface?)!!.onBackPressed()) {
                            navController.popBackStack()
                            supportActionBar?.show()
                        }
                    }
                }
            })
        XXPermissions.with(this)
            .permission(Permission.READ_PHONE_STATE)
            .permission(Permission.WRITE_EXTERNAL_STORAGE)
            .permission(Permission.READ_MEDIA_IMAGES)
            .permission(Permission.READ_MEDIA_VIDEO)
            .permission(Permission.READ_MEDIA_AUDIO)
            // 设置权限请求拦截器（局部设置）
            //.interceptor(new PermissionInterceptor())
            // 设置不触发错误检测机制（局部设置）
            //.unchecked()
            .request(object : OnPermissionCallback {
                override fun onGranted(permissions: MutableList<String>, all: Boolean) {
                    MainScope().launch {
                        withContext(Dispatchers.IO) {
                            /* 设置允许移动网络下进行内核下载。默认不下载，会导致部分一直用移动网络的用户无法使用x5内核 */
                            QbSdk.setDownloadWithoutWifi(true)
                            // QbSdk.setNeedInitX5FirstTime(true)
                            // QbSdk.setTBSInstallingStatus(false)
                            // 在调用TBS初始化、创建WebView之前进行如下配置
                            val map: Map<String, Any> = mapOf(
                                TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER to true,
                                TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE to true
                            )
                            QbSdk.initTbsSettings(map)
                            // TbsDownloader.startDownload(MyApplication.instance);//手动开始下载，此时需要先判定网络是否符合
                            val callback: QbSdk.PreInitCallback = object : QbSdk.PreInitCallback {
                                override fun onViewInitFinished(arg0: Boolean) {
                                    // TODO Auto-generated method stub
                                    //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                                    LogUtils.i("onViewInitFinished: $arg0")
                                    if (!arg0) {
                                        //判断是否要自行下载内核
                                        //false表示下载完成 或者 下载了一部分  true表示完全没下载
                                        val needDownload = TbsDownloader.needDownload(
                                            applicationContext,
                                            TbsDownloader.DOWNLOAD_OVERSEA_TBS
                                        )
                                        LogUtils.i("X5", needDownload.toString() + "")
                                        //重置
                                        QbSdk.reset(applicationContext)
                                        // 启动下载
                                        TbsDownloader.startDownload(applicationContext)
                                    }
                                }

                                override fun onCoreInitFinished() {
                                    // TODO Auto-generated method stub
                                    LogUtils.i("onCoreInitFinished")
                                }
                            }

                            //x5内核初始化接口
                            QbSdk.initX5Environment(applicationContext, callback)
                            /* SDK内核初始化周期回调，包括 下载、安装、加载 */
                            QbSdk.setTbsListener(object : TbsListener {
                                /**
                                 * @param stateCode 110: 表示当前服务器认为该环境下不需要下载
                                 */
                                override fun onDownloadFinish(stateCode: Int) {
                                    LogUtils.d("onDownloadFinished: $stateCode")
                                }

                                /**
                                 * @param stateCode 200、232安装成功
                                 */
                                override fun onInstallFinish(stateCode: Int) {
                                    LogUtils.d("onInstallFinished: $stateCode")
                                }

                                /**
                                 * 首次安装应用，会触发内核下载，此时会有内核下载的进度回调。
                                 * @param progress 0 - 100
                                 */
                                override fun onDownloadProgress(progress: Int) {
                                    LogUtils.d("Core Downloading: $progress")
                                }
                            })
                        }
                    }
                }

                override fun onDenied(permissions: MutableList<String>, never: Boolean) {

                }
            })
        if (FragmentDirector.doDirect(navController, direct, extra)) {
            return
        }
        MyApplication.eventTrackerInstance?.sendEvent(
            EVENT_ON_OPENED_ACTIVITY,
            EventTrackerDataModel(ROUTE)
        )
        reportInfoEvent()
    }

    /**
     * 初始化页面监听
     * 页面切换后使用
     */
    private fun initNavigationOnChangeListener(navController: NavController) {
        navController.addOnDestinationChangedListener { _, _, _ -> updateMenu() }
    }

    /**
     * 更新menu
     * 对于有些页面需要使用的menu进行处理
     */
    private fun updateMenu() {
        if (menu != null) {
            val networkSetting = menu!!.findItem(R.id.network_setting)
            val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
            val label = Objects.requireNonNull(navController.currentDestination!!.label).toString()
            networkSetting.isVisible = label == getString(R.string.menu_login)
            networkSetting.setOnMenuItemClickListener {
                startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
                true
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        this.menu = menu
        initTestProject()
        updateMenu()
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        val actionSettings = menu.findItem(R.id.action_settings)
        actionSettings.setOnMenuItemClickListener {
            navController.navigate(R.id.nav_setting)
            true
        }
        val actionLab = menu.findItem(R.id.action_settings_lab)
        if (SpUtils.get(applicationContext, DataModel.ENABLE_SHOW_LAB_SP, false) as Boolean) {
            actionLab.isVisible = true
            actionLab.setOnMenuItemClickListener {
                navController.navigate(R.id.nav_setting_lab)
                true
            }
        } else {
            actionLab.isVisible = false
        }
        val actionDownloadManager = menu.findItem(R.id.action_download_manager)
        actionDownloadManager.setOnMenuItemClickListener {
            navController.navigate(R.id.nav_download_manager)
            true
        }
        return true
    }

    private fun initTestProject() {
//        val menuRead = menu!!.findItem(R.id.action_read)
//        menuRead.setOnMenuItemClickListener {
//            true
//        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        return (NavigationUI.navigateUp(navController, mAppBarConfiguration!!)
                || super.onSupportNavigateUp())
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        try {
            var navHostFragment: NavHostFragment? = null
            this.supportFragmentManager.fragments.first()?.let {
                if (it is NavHostFragment) {
                    navHostFragment = it
                } else {
                    return super.onKeyDown(keyCode, event)
                }
                val navController: NavController? = navHostFragment?.navController
                navController?.currentBackStackEntry?.destination?.label?.let { label ->
                    if (label != this.resources.getString(R.string.menu_space)) {
                        return super.onKeyDown(keyCode, event)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return super.onKeyDown(keyCode, event)
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                ToastUtils.showShort("再按一次退出APP")
                //System.currentTimeMillis()系统当前时间
                mExitTime = System.currentTimeMillis()
            } else {
                onExit()
                ActivityUtils.finishAllActivities(true)
                finish()
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    @CallSuper
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val view = currentFocus
            if (KeyboardsUtils.isShouldHideKeyBord(view, ev)) {
                KeyboardsUtils.hintKeyBoards(view)
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun onExit() {
        // if (ObjectUtils.isNotEmpty(mHomeWatcher)) {
        //     mHomeWatcher.stopWatch()
        // }
        MyApplication.fetch?.close()
        MyApplication.eventTrackerInstance?.stopTracker()
        AppUtils.unregisterAppStatusChangedListener(MyApplication.appStatusChangeListener)
    }

    private fun <F : Fragment> getFragment(fragmentClass: Class<F>): F? {
        try {
            val navHostFragment = this.supportFragmentManager.fragments.first() as NavHostFragment
            navHostFragment.childFragmentManager.fragments.forEach {
                if (fragmentClass.isAssignableFrom(it.javaClass)) {
                    @Suppress("UNCHECKED_CAST")
                    return it as F
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun reportInfoEvent() {
        MainScope().launch {
            withContext(Dispatchers.IO) {
                MyApplication.eventTrackerInstance?.sendEvent(
                    Constants.EVENT_APPLICATION_INFO, EventTrackerDataModel(
                        null, EventTrackerDataModel(
                            null,
                            InfoEventDataModel(
                                AppInfoDataModel(
                                    AppUtils.getAppName(),
                                    AppUtils.getAppPackageName(),
                                    AppUtils.getAppVersionName(),
                                    AppUtils.getAppVersionCode(),
                                    getBuildConfigValue("MAIN_VERSION_NAME"),
                                    getBuildConfigValue("MAIN_VERSION_CODE"),
                                    getBuildConfigValue("EXPAND_VERSION_NAME"),
                                    getBuildConfigValue("EXPAND_VERSION_CODE"),
                                    getBuildConfigValue("APP_VERSION_CODE"),
                                    getBuildConfigValue("GIT_COMMIT_ID"),
                                    getBuildConfigValue("BUILD_TIME"),
                                    AppUtils.isAppRoot(),
                                    AppUtils.isAppDebug(),
                                    AppUtils.isAppSystem()

                                ), DeviceInfoDataModel(
                                    DeviceUtils.isDeviceRooted(),
                                    DeviceUtils.isAdbEnabled(),
                                    DeviceUtils.getSDKVersionName(),
                                    DeviceUtils.getSDKVersionCode(),
                                    Build.DISPLAY,
                                    DeviceUtils.getAndroidID(),
                                    DeviceUtils.getMacAddress().toString(),
                                    DeviceUtils.getManufacturer(),
                                    DeviceUtils.getModel(),
                                    DeviceUtils.getABIs().toString(),
                                    DeviceUtils.isTablet(),
                                    DeviceUtils.isEmulator(),
                                    DeviceUtils.getUniqueDeviceId().toString()
                                )
                            )
                        )
                    )
                )
            }
        }
    }

    private fun getBuildConfigValue(fieldName: String?): String? {
        fieldName?.let {
            try {
                val packageName = AppUtils.getAppPackageName()
                val clazz = Class.forName("$packageName.BuildConfig")
                val field: Field = clazz.getField(it)
                return field.get(null)?.toString()
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            } catch (e: NoSuchFieldException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        }
        return null
    }

}