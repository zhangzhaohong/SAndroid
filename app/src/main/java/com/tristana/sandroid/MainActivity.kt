package com.tristana.sandroid

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
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
import com.tristana.sandroid.customInterface.IOnBackPressedInterface
import com.tristana.sandroid.tools.array.ArrayUtils
import com.tristana.sandroid.tools.file.FileUtils
import com.tristana.sandroid.tools.log.Timber
import com.tristana.sandroid.tools.toast.ToastUtils
import com.tristana.sandroid.ui.webView.X5WebViewFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class MainActivity : AppCompatActivity() {
    private var mAppBarConfiguration: AppBarConfiguration? = null
    private var menu: Menu? = null
    private var timber: Timber? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        timber = Timber("MainActivity")
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val title = toolbar.getChildAt(0) as AppCompatTextView
        title.layoutParams.width = LinearLayoutCompat.LayoutParams.MATCH_PARENT
        title.gravity = Gravity.CENTER_HORIZONTAL
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
                R.id.nav_home,
                R.id.nav_gallery,
                R.id.nav_slideshow,
                R.id.nav_login
        )
                .setOpenableLayout(drawer)
                .build()
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration!!)
        NavigationUI.setupWithNavController(navigationView, navController)
        initNavigationOnChangeListener(navController)
        XXPermissions.with(this)
            .permission(Permission.READ_PHONE_STATE)
            .permission(Permission.WRITE_EXTERNAL_STORAGE)
            .permission(Permission.READ_EXTERNAL_STORAGE)
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
                            timber?.i("VERSION: " + Build.VERSION.SDK_INT);
                            // TbsDownloader.startDownload(MyApplication.instance);//手动开始下载，此时需要先判定网络是否符合
                            val callback: QbSdk.PreInitCallback = object : QbSdk.PreInitCallback {
                                override fun onViewInitFinished(arg0: Boolean) {
                                    // TODO Auto-generated method stub
                                    //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                                    timber?.i("onViewInitFinished: $arg0")
                                }

                                override fun onCoreInitFinished() {
                                    // TODO Auto-generated method stub
                                    timber?.i("onCoreInitFinished")
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
                                    timber?.i("onDownloadFinished: $stateCode")
                                }

                                /**
                                 * @param stateCode 200、232安装成功
                                 */
                                override fun onInstallFinish(stateCode: Int) {
                                    timber?.i("onInstallFinished: $stateCode")
                                }

                                /**
                                 * 首次安装应用，会触发内核下载，此时会有内核下载的进度回调。
                                 * @param progress 0 - 100
                                 */
                                override fun onDownloadProgress(progress: Int) {
                                    timber?.i("Core Downloading: $progress")
                                }
                            })
                        }
                    }
                }

                override fun onDenied(permissions: MutableList<String>, never: Boolean) {

                }
            })
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
        val actionSettings = menu.findItem(R.id.action_settings)
        actionSettings.setOnMenuItemClickListener {
            val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
            navController.navigate(R.id.nav_setting)
            true
        }
        return true
    }

    private fun initTestProject() {
        val menuRead = menu!!.findItem(R.id.action_read)
        val menuWrite = menu!!.findItem(R.id.action_write)
        val menuTextToArray = menu!!.findItem(R.id.action_textToArray)
        val menuArrayToText = menu!!.findItem(R.id.action_arrayToText)
        val menuDelFile = menu!!.findItem(R.id.action_delFile)
        menuRead.setOnMenuItemClickListener {
            val data = FileUtils().readLine(this@MainActivity, "data_TEST")
            for (i in data.indices) {
                timber!!.d("Data_read[" + i + "] " + data[i])
            }
            true
        }
        menuWrite.setOnMenuItemClickListener {
            FileUtils().writeData(this@MainActivity, "data_TEST", "This is the data!" + System.currentTimeMillis())
            true
        }
        menuTextToArray.setOnMenuItemClickListener {
            val data = "111,222,333,444,555"
            val result = ArrayUtils().textToArrayList(data)
            for (i in result.indices) {
                timber!!.d("Data_textToArray[" + i + "] " + result[i])
            }
            true
        }
        menuArrayToText.setOnMenuItemClickListener {
            val data = ArrayList<String>()
            data.add("111")
            data.add("222")
            data.add("333")
            data.add("444")
            data.add("555")
            data.add("666")
            val result = ArrayUtils().arrayListToString(data)
            timber!!.d("Data_arrayToText: $result")
            true
        }
        menuDelFile.setOnMenuItemClickListener {
            if (FileUtils().deleteFile(this@MainActivity, "data_TEST")) {
                ToastUtils.showToast(this@MainActivity, "删除成功！")
            } else {
                ToastUtils.showToast(this@MainActivity, "删除失败！")
            }
            true
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        return (NavigationUI.navigateUp(navController, mAppBarConfiguration!!)
                || super.onSupportNavigateUp())
    }

    override fun onBackPressed() {
        val fragment = getFragment(X5WebViewFragment::class.java)
        if (fragment == null) {
            super.onBackPressed()
        } else {
            if ((fragment as IOnBackPressedInterface?)!!.onBackPressed()) {
                super.onBackPressed()
                this.supportActionBar?.show()
            }
        }

    }

    private fun <F : Fragment> getFragment(fragmentClass: Class<F>): F? {
        val navHostFragment = this.supportFragmentManager.fragments.first() as NavHostFragment
        navHostFragment.childFragmentManager.fragments.forEach {
            if (fragmentClass.isAssignableFrom(it.javaClass)) {
                @Suppress("UNCHECKED_CAST")
                return it as F
            }
        }
        return null
    }

}