package com.example.petr.myapplication;

/**
 * Created by petr on 27.06.17.
 */

import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;

import java.util.List;

public class TabsSharedInfo {

    public FragmentManager tabsManager;
    public List<Integer> TabsIDs;
    public Menu tabsMenu;
    public Tab currentTab;
    public AutoCompleteTextView urlBar;
    public ProgressBar progressBar;

    public TabsSharedInfo(FragmentManager tabsManager, List<Integer> TabsIDs, Tab currentTab, Menu tabsMenu, AutoCompleteTextView urlBar) {
        this.tabsManager = tabsManager;
        this.TabsIDs = TabsIDs;
        this.tabsMenu = tabsMenu;
        this.currentTab= currentTab;
        this.urlBar = urlBar;


    }
    public TabsSharedInfo(FragmentManager tabsManager, List<Integer> TabsIDs, Tab currentTab, Menu tabsMenu, AutoCompleteTextView urlBar, ProgressBar progressBar) {
        this.tabsManager = tabsManager;
        this.TabsIDs = TabsIDs;
        this.tabsMenu = tabsMenu;
        this.currentTab= currentTab;
        this.urlBar = urlBar;
        this.progressBar = progressBar;

    }
}
