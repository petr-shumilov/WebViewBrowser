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
import android.util.Patterns;
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
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AutoCompleteTextView urlBarView;
    private Menu menu;
    private int tabIDIterator;
    private Tab currentTab;

    private FragmentManager tabsManager;
    private List<Integer> listOfTabsID;
    private ProgressBar progressBar;

    // TODO: SHARED RESOURCES

    // TODO: concurrency URLBAR AND PROGRESS

    // TODO: delete  MAGIC constants

    // TODO: locallization
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
        menu = navigationView.getMenu();
        tabsManager = getSupportFragmentManager();
        listOfTabsID = new ArrayList<Integer>();
        tabIDIterator = 0;
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        // URL BAR
        urlBarView = (AutoCompleteTextView)findViewById(R.id.url_text);
        urlBarView.setThreshold(2);
        urlBarView.setAdapter(new SearchAutoCompleteAdapter(this, android.R.layout.simple_expandable_list_item_1));
        urlBarView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String suggestion = (String) adapterView.getItemAtPosition(position);
                String url = "https://yandex.ru/search/?text=" + suggestion;
                currentTab.LoadUrl(url);
                urlBarView.clearFocus();
            }
        });
        urlBarView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (i) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:

                            String url = urlBarView.getText().toString();

                            if ( Patterns.WEB_URL.matcher(url).matches()) {
                                currentTab.LoadUrl(url);
                            }
                            else {
                                url = "https://yandex.ru/search/?text=" + url;
                                currentTab.LoadUrl(url);
                            }

                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
        // URL BAR


        // create new tab
        addNewTab("http://ya.ru/");

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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.refresh_page) {
            currentTab.RefreshPage();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        urlBarView.clearFocus();
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
        Tab tab = Tab.addTab(tabIDIterator, url, new TabsSharedInfo(tabsManager, listOfTabsID, currentTab, menu, urlBarView, progressBar));

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

            // set new current tab
            currentTab = newTab;

            // TODO: onSelect methdod
            // update url bar
            currentTab.UpdateUrlBar();
            progressBar.setVisibility(currentTab.OnProgress ? View.VISIBLE : View.INVISIBLE);

        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
    }


}
