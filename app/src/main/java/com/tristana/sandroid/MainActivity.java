package com.tristana.sandroid;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.tristana.sandroid.tools.array.ArrayUtils;
import com.tristana.sandroid.tools.file.FileUtils;
import com.tristana.sandroid.tools.log.Timber;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private Menu menu;
    private Timber timber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timber = new Timber("MainActivity");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        AppCompatTextView title = (AppCompatTextView) toolbar.getChildAt(0);
        title.getLayoutParams().width = LinearLayoutCompat.LayoutParams.MATCH_PARENT;
        title.setGravity(Gravity.CENTER_HORIZONTAL);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,
                R.id.nav_gallery,
                R.id.nav_slideshow,
                R.id.nav_login,
                R.id.nav_feedback,
                R.id.nav_trafficManager,
                R.id.nav_illegalManager
        )
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        initNavigationOnChangeListener(navController);
    }

    /**
     * 初始化页面监听
     * 页面切换后使用
     */
    private void initNavigationOnChangeListener(NavController navController) {
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                updateMenu();
            }
        });
    }

    /**
     * 更新menu
     * 对于有些页面需要使用的menu进行处理
     */
    private void updateMenu() {
        if (menu != null) {
            MenuItem networkSetting = menu.findItem(R.id.network_setting);
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
            String label = Objects.requireNonNull(Objects.requireNonNull(navController.getCurrentDestination()).getLabel()).toString();
            if (!label.equals(getString(R.string.menu_login))) {
                networkSetting.setVisible(false);
            } else {
                networkSetting.setVisible(true);
            }
            networkSetting.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                    return true;
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        this.menu = menu;
        initTestProject();
        updateMenu();
        return true;
    }

    private void initTestProject() {
        MenuItem menu_read = menu.findItem(R.id.action_read);
        MenuItem menu_write = menu.findItem(R.id.action_write);
        MenuItem menu_textToArray = menu.findItem(R.id.action_textToArray);
        MenuItem menu_arrayToText = menu.findItem(R.id.action_arrayToText);
        menu_read.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                ArrayList<String> data = new FileUtils().readLine(MainActivity.this, "data_TEST");
                for (int i = 0; i < data.size(); i++) {
                    timber.d("Data_read[" + i + "] " + data.get(i));
                }
                return true;
            }
        });
        menu_write.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                new FileUtils().writeData(MainActivity.this, "data_TEST", "This is the data!" + System.currentTimeMillis());
                return true;
            }
        });
        menu_textToArray.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                String data = "111,222,333,444,555";
                ArrayList<Object> result = new ArrayUtils().textToArrayList(data);
                for (int i = 0; i < result.size(); i++) {
                    timber.d("Data_textToArray[" + i + "] " + result.get(i));
                }
                return true;
            }
        });
        menu_arrayToText.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                ArrayList<String> data = new ArrayList<>();
                data.add("111");
                data.add("222");
                data.add("333");
                data.add("444");
                data.add("555");
                data.add("666");
                String result = new ArrayUtils().arrayListToString(data);
                timber.d("Data_arrayToText: " + result);
                return true;
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}