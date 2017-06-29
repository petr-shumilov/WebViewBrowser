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


public class Tab extends Fragment {

    // id
    private int tabID;
    private String tabTag;

    // current tab url
    private String tabUrl;

    // tab webview instance
    public WebView tabWebView;

    // needed resources
    TabsSharedResources sharedResources;

    // status
    private boolean onProgress = false;

    // thats why can't override constructor
    private void init(Integer id, String url, TabsSharedResources sharedResources) {

        this.tabUrl = url;
        this.tabID = id;
        this.tabTag = Config.TAG_PREFIX + tabID;
        this.sharedResources = sharedResources;

        this.addTabMenuItem(sharedResources);
    }

    // Static method for more simplicity in MainActivity
    static void addTab(String url, TabsSharedResources tabsSharedInfo) {

        Integer id = tabsSharedInfo.AddTabResources();

        Tab tab = new Tab();
        tab.init(id, url, tabsSharedInfo);

        // adding in tabManager
        FragmentTransaction ft = tabsSharedInfo.GetTabsList().GetTabsManager().beginTransaction();
        ft.add(R.id.content_frame, tab, tab.GetTabTag());
        ft.commit();

        // force transaction
        tabsSharedInfo.GetTabsList().GetTabsManager().executePendingTransactions();
    }

    public void removeTab()
    {
        // check ability of removing. For ex if >1 we can
        if (sharedResources.GetTabsList().CanRemoveTab()){

            int removingTabIndex = sharedResources.GetTabsList().FindTabIndex(this.tabID);
            Tab removingTab = sharedResources.GetTabsList().GetTabByIndex(removingTabIndex);

            // switching politics
            int switchToIndex = ((removingTabIndex == 0) ? 1 : (removingTabIndex - 1));
            Tab switchToTab = sharedResources.GetTabsList().GetTabByIndex(switchToIndex);

            if (removingTab != null && switchToTab != null) {
                sharedResources.RemoveTabResources(removingTab, removingTabIndex);

                sharedResources.RemoveAndSwitchTab(removingTab, switchToTab);

                sharedResources.SwitchTabResources(switchToTab);
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

        // handlers for starting and finishing loading of page
        tabWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                SetOnProgress(true);
                tabUrl = url;

                // have access or no
                if (tabID == sharedResources.GetTabsList().GetCurrentTabID()) {
                    UpdateUrlBar();
                    SetVisibleProgress(true);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                SetOnProgress(false);
                
                tabUrl = url;
                
                SetTabTitle(view.getTitle());
                
                if (tabID == sharedResources.GetTabsList().GetCurrentTabID()) {
                    UpdateUrlBar();
                    SetVisibleProgress(false);
                }
            }
        });

        tabWebView.loadUrl(this.tabUrl);

        return view;
    }

    private void addTabMenuItem(TabsSharedResources sharedResources)
    {
        MenuItem newTabMenuItem = sharedResources.GetTabsMenu().add(R.id.tabs, this.tabID, Menu.NONE, R.string.new_tab_title);
        newTabMenuItem.setCheckable(true).setChecked(true);
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

    public void removeMenuItem() {
        this.sharedResources.GetTabsMenu().removeItem(this.tabID);
    }

    public void SetTabMenuItemChecked() {
        this.sharedResources.GetTabsMenu().findItem(this.tabID).setChecked(true);
    }

    public int GetTabID() {
        return tabID;
    }

    public String GetTabTag() {
        return tabTag;
    }

    public void SetTabTitle(String newTitle) {
        sharedResources.GetTabsMenu().findItem(tabID).setTitle(newTitle);
    }

    public String GetUrl() {
        return this.tabUrl;
    }

    public void LoadUrl(String url) {
        this.tabUrl = url;
        tabWebView.loadUrl(this.tabUrl);
    }

    public void  SetUrlBar(String url){
        sharedResources.GetUrlBar().setText(url);
    }

    public void UpdateUrlBar() {
        sharedResources.GetUrlBar().setText(this.tabWebView.getUrl());
    }

    public void SetVisibleProgress(boolean visible){
        sharedResources.GetProgressBar().setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    public void RefreshPage(){
        WebView view = sharedResources.GetTabsList().GetCurrentTab().tabWebView;
        view.loadUrl(view.getUrl());
    }

    public void SetOnProgress(boolean onProgress) {
        this.onProgress = onProgress;
    }

    public boolean GetOnProgress(){
        return this.onProgress;
    }
}

