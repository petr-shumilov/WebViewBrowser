package com.example.petr.myapplication;

;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ShareCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by petr on 24.06.17.
 */

public class Tab extends Fragment {

    private String tabUrl;
    public WebView tabWebView;
    private int tabID;
    private String tabTag;
    TabsSharedInfo sharedInfo;
    public boolean OnProgress = false;

    private void init(int id, String url, TabsSharedInfo sharedInfo) {

        this.tabUrl = url;
        this.tabID = id;
        this.tabTag = "tab" + tabID;
        this.sharedInfo = sharedInfo;
        sharedInfo.TabsIDs.add((Integer) tabID);
        this.addTabMenuItem(sharedInfo);
        this.sharedInfo.urlBar.setText("");
    }

    static Tab addTab(int id, String url, TabsSharedInfo tabsSharedInfo) {
        Tab tab = new Tab();

        tab.init(id, url, tabsSharedInfo);
        FragmentTransaction ft = tab.sharedInfo.tabsManager.beginTransaction();
        ft.add(R.id.content_frame, tab, tab.GetTabTag());
        ft.commit();

        // force transaction
        tab.sharedInfo.tabsManager.executePendingTransactions();

        return tab;
    }

    public void removeTab()
    {
        if (sharedInfo.TabsIDs.size() >  1){

            // switching politics
            int removingTabIndex = sharedInfo.TabsIDs.indexOf(this.tabID); // TODO: bad proximity - need some tree like structure

            int switchToIndex = ((removingTabIndex == 0) ? 1 : (removingTabIndex - 1));

            Tab removingTab = (Tab) sharedInfo.tabsManager.findFragmentByTag("tab" + this.tabID);
            Tab switchToTab = (Tab) sharedInfo.tabsManager.findFragmentByTag("tab" + sharedInfo.TabsIDs.get(switchToIndex));

            if (removingTab != null && switchToTab != null) {

                removingTab.removeMenuItem();
                sharedInfo.TabsIDs.remove((int)removingTabIndex);

                FragmentTransaction ft = sharedInfo.tabsManager.beginTransaction();
                ft.remove(removingTab);
                ft.show(switchToTab);
                ft.commit();
                sharedInfo.tabsManager.executePendingTransactions();

                sharedInfo.currentTab = switchToTab;
                switchToTab.UpdateUrlBar();
                switchToTab.SetTabMenuItemChecked();

            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        tabWebView.saveState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tabWebView.restoreState(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.tab, container, false);

        tabWebView = (WebView) view.findViewById(R.id.tab_webview);
        tabWebView.getSettings().setJavaScriptEnabled(true);
        tabWebView.clearCache(true);

        tabWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                OnProgress = true;
                tabUrl = url;
                //SetUrlBar(url);
                //if (this.equals(sharedInfo.currentTab)) {
                    UpdateUrlBar();
                    SetVisibleProgress(true);
                //}
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                OnProgress = false;
                tabUrl = url;
                //SetUrlBar();
                SetTabTitle(view.getTitle());

                // TODO: update shared resources
                //if (this.equals(sharedInfo.currentTab)) {
                UpdateUrlBar();
                SetVisibleProgress(false);
                //}
            }
        });

        tabWebView.loadUrl(this.tabUrl);

        return view;
    }

    private void addTabMenuItem(TabsSharedInfo sharedInfo)
    {
        MenuItem newTabMenuItem = sharedInfo.tabsMenu.add(R.id.tabs, this.tabID, Menu.NONE, "New tab");
        newTabMenuItem.setChecked(true);
        newTabMenuItem.setCheckable(true);
        newTabMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        newTabMenuItem.setActionView(R.layout.menu_tab_item);

        final ImageButton removeTab = (ImageButton) newTabMenuItem.getActionView().findViewById(R.id.remove_tab_button);
        removeTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeTab();
            }
        });
    }

    private void removeMenuItem() {
        this.sharedInfo.tabsMenu.removeItem(this.tabID);
    }

    public void SetTabMenuItemChecked() {
        this.sharedInfo.tabsMenu.findItem(this.tabID).setChecked(true);
    }

    public int GetTabID() {
        return tabID;
    }

    public String GetTabTag() {
        return tabTag;
    }

    public void SetTabTitle(String newTitle) {
        sharedInfo.tabsMenu.findItem(tabID).setTitle(newTitle);
    }

    public String GetUrl() {
        return this.tabUrl;
    }

    public void LoadUrl(String url) {
        this.tabUrl = url;
        tabWebView.loadUrl(this.tabUrl);
    }

    public void  SetUrlBar(String url){
        sharedInfo.urlBar.setText(url);
    }

    public void UpdateUrlBar() {
        sharedInfo.urlBar.setText(this.tabWebView.getUrl());
    }

    public void SetVisibleProgress(boolean visible){
        sharedInfo.progressBar.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }
}

