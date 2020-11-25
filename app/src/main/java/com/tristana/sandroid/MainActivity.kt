package com.tristana.sandroid

import android.content.Intent
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
import com.tristana.sandroid.customInterface.IOnBackPressedInterface
import com.tristana.sandroid.tools.array.ArrayUtils
import com.tristana.sandroid.tools.file.FileUtils
import com.tristana.sandroid.tools.log.Timber
import com.tristana.sandroid.tools.toast.ToastUtils
import com.tristana.sandroid.ui.webView.X5WebViewFragment
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
                R.id.nav_login,
                R.id.nav_feedback,
                R.id.nav_trafficManager,
                R.id.nav_illegalManager
        )
                .setOpenableLayout(drawer)
                .build()
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration!!)
        NavigationUI.setupWithNavController(navigationView, navController)
        initNavigationOnChangeListener(navController)
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

    @Suppress("UNCHECKED_CAST")
    fun <F : Fragment> AppCompatActivity.getFragment(fragmentClass: Class<F>): F? {
        val navHostFragment = this.supportFragmentManager.fragments.first() as NavHostFragment
        navHostFragment.childFragmentManager.fragments.forEach {
            if (fragmentClass.isAssignableFrom(it.javaClass)) {
                return it as F
            }
        }
        return null
    }

}