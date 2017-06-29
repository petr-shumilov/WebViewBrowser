package com.example.petr.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TabsSharedResources sharedResources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // init actionbar
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // init navigation
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        // initialize shared resources
        sharedResources = new TabsSharedResources(
                new TabsList(getSupportFragmentManager()),           // tabs storage
                navigationView.getMenu(),                            // tabs menu
                (AutoCompleteTextView)findViewById(R.id.url_text),   // textedit for url
                (ProgressBar) findViewById(R.id.progress_bar)        // progress bar
        );

        // configuring autocomplete
        setupUrlBar();

        // create new tab
        Tab.addTab(Config.HOME_PAGE_URL, sharedResources);

    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        WebView webView = sharedResources.GetTabsList().GetCurrentTab().tabWebView;

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if (webView.canGoBack()) {
            webView.goBack();
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
            TabsList tabsList = sharedResources.GetTabsList();
            if (tabsList.СanGetCurrentTab()){
                tabsList.GetCurrentTab().RefreshPage();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        sharedResources.GetUrlBar().clearFocus();

        switch (id) {
            case R.id.add_new_tab:
                Tab.addTab(Config.HOME_PAGE_URL, sharedResources);
                break;
            default:
                this.selectTab(id);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    private void selectTab(int id){
        // find new tab by id of item
        Tab newTab =  (Tab) sharedResources.GetTabsList().GetTabByTag(Config.TAG_PREFIX + id);

        // current tab
        Tab oldTab = sharedResources.GetTabsList().GetCurrentTab();

        // switching
        if (newTab != null && oldTab != null) {
            // switch between tabs
            sharedResources.SwitchTabs(oldTab, newTab);

            // switch resources
            sharedResources.SwitchTabResources(newTab);
        }
    }

    private void setupUrlBar(){

        sharedResources.GetUrlBar().setThreshold(Config.AUTOCOMPLETE_THRESHOLD);
        sharedResources.GetUrlBar().setAdapter(new SearchAutoCompleteAdapter(this, android.R.layout.simple_expandable_list_item_1));
        sharedResources.GetUrlBar().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String suggestion = (String) adapterView.getItemAtPosition(position);

                String url = Config.SEARCH_API_UPL + suggestion;

                if (sharedResources.GetTabsList().СanGetCurrentTab()) {
                    sharedResources.GetTabsList().GetCurrentTab().LoadUrl(url);
                }
                sharedResources.GetUrlBar().clearFocus();
            }
        });
        sharedResources.GetUrlBar().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (i) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:

                            String request = sharedResources.GetUrlBar().getText().toString();

                            if (request.isEmpty())
                                return true;

                            if (!Patterns.WEB_URL.matcher(request).matches()) {
                                request = Config.SEARCH_API_UPL  + request;
                            }

                            if (sharedResources.GetTabsList().СanGetCurrentTab())
                                sharedResources.GetTabsList().GetCurrentTab().LoadUrl(request);

                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
    }

}
