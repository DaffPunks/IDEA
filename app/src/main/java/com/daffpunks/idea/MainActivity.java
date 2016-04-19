package com.daffpunks.idea;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.AttrRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.daffpunks.idea.Adapter.TabsFragmentAdapter;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;


public class MainActivity extends AppCompatActivity {

    private Toolbar     toolbar;
    private ViewPager   viewPager;
    TabsFragmentAdapter adapter;
    ActionMode          actionMode;

    private BroadcastReceiver mReceiver   = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getStringExtra("action")) {
                case "SELECTIONMODE TRUE":
                    selectCount = 0;
                    actionMode = toolbar.startActionMode(actionModeCallback);
                    break;
                case "SELECTIONMODE FALSE":
                    selectCount = 0;
                    if (actionMode != null)
                        actionMode.finish();
                    break;
                case "LISTITEM SELECTED":
                    selectCount += 1;
                    actionMode.setTitle(selectCount + " Selected");
                    break;
                case "LISTITEM UNSELECTED":
                    selectCount -= 1;
                    actionMode.setTitle(selectCount + " Selected");
                    break;
            }
        }
    };

    private int selectCount = 0;
    private static final int LAYOUT = R.layout.main_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppDefault);
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        initToolbar();
        initNavigatonbar();
        initTabs();

        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter("NOTE"));
    }


    private void initTabs() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        adapter = new TabsFragmentAdapter(getApplicationContext(), getSupportFragmentManager());

        viewPager.setAdapter(adapter);


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
        });
    }


    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });
    }


    private void initNavigatonbar() {
        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggleAnimated(true)
                .withSelectedItem(-1)
                .addDrawerItems(
                        new PrimaryDrawerItem()
                                .withName(R.string.nav_menu_item_home)
                                .withIcon(GoogleMaterial.Icon.gmd_home),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem()
                                .withName(R.string.nav_menu_item_settings)
                                .withIcon(GoogleMaterial.Icon.gmd_settings)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch (position) {
                            case 0:
                                Toast.makeText(MainActivity.this, "Home Screen" , Toast.LENGTH_SHORT).show();
                                break;
                            case 2:
                                //Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                                //startActivity(intent);
                                Toast.makeText(MainActivity.this, "TODO: Make settings" , Toast.LENGTH_SHORT).show();
                                break;

                        }
                        return false;
                    }
                })
                .build();
    }


    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_list_callback, menu);

            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
            }
            return true; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.delete:
                    Intent sendIntent = new Intent("TIMELIST");
                    sendIntent.putExtra("action", "DELETE SELECTED");
                    LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(sendIntent);
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            Intent sendIntent = new Intent("TIMELIST");
            sendIntent.putExtra("action", "NOTE ADDED");
            LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(sendIntent);
        }
    };
}
