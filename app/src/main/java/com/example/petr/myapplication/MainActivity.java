package com.example.petr.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabItem;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Layout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private AutoCompleteTextView urlBarView;
    private Menu menu;
    private int tabIDIterator;
    private Tab currentTab;

    private FragmentManager tabsManager;
    private List<Integer> listOfTabsID;

    private AutoCompleteTextView mAutoCompleteTextView; 


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        // initialize global objects
        urlBarView = (AutoCompleteTextView)findViewById(R.id.url_text);
        menu = navigationView.getMenu();
        tabsManager = getSupportFragmentManager();
        listOfTabsID = new ArrayList<Integer>();
        tabIDIterator = 0;
        mAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.url_text);

        // create new tab
        addNewTab("http://ya.ru/");
        //



        // URL BAR
        urlBarView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (i)
                    {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            //addCourseFromTextBox();

                            String url = urlBarView.getText().toString();

                            if (URLUtil.isValidUrl(url)) {
                                currentTab.LoadUrl(url);
                            }
                            else {
                                url = "http://" + url;
                                currentTab.LoadUrl(url);
                            }

                            //Log.i("KeyPress", urlBarView.getText().toString());
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
        // URL BAR





    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (currentTab.tabWebView.canGoBack()) {
            currentTab.tabWebView.goBack();
            return;
        }
        else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.refresh_page) {

            currentTab.tabWebView.loadUrl(currentTab.tabWebView.getUrl());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id)
        {
            case R.id.add_new_tab:
                addNewTab("http://ya.ru/");
                break;
            default:
                switchTabById(id);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void addNewTab(String url)
    {
        Tab tab = Tab.addTab(tabIDIterator, url, new TabsSharedInfo(tabsManager, listOfTabsID, currentTab, menu, urlBarView));

        currentTab = tab;

        tabIDIterator++;
    }

    private void switchTabById(int id){
        // find new tab by id of item
        Tab newTab =  (Tab) tabsManager.findFragmentByTag("tab" + id); // TODO: wrap "tab" + id on method creteTag(id)

        // switching
        if (newTab != null) {
            // init transaction
            FragmentTransaction ft = tabsManager.beginTransaction();
            ft.hide(currentTab);
            ft.show(newTab);
            ft.commit();

            // force transaction
            tabsManager.executePendingTransactions();

            // update url bar
            newTab.UpdateUrlBar();

            // set new current tab
            currentTab = newTab;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
    }


}
