package com.example.petr.myapplication;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;

import java.util.List;

public class TabsSharedResources  {

    private TabsList tabsList;
    private Menu tabsMenu;
    private AutoCompleteTextView urlBar;
    private ProgressBar progressBar;

    public TabsSharedResources (TabsList tabsList, Menu tabsMenu, AutoCompleteTextView urlBar, ProgressBar progressBar) {
        this.tabsList = tabsList;
        this.tabsMenu = tabsMenu;
        this.urlBar = urlBar;
        this.progressBar = progressBar;

    }

    public TabsList GetTabsList(){
        return this.tabsList;
    }

    public Menu GetTabsMenu(){
        return this.tabsMenu;
    }

    public AutoCompleteTextView GetUrlBar(){
        return this.urlBar;
    }

    public ProgressBar GetProgressBar(){
        return this.progressBar;
    }

    public Integer AddTabResources() {
        this.urlBar.setText("");
        Integer idNewTab = this.tabsList.Add();
        return idNewTab;
    }

    public void SwitchTabResources(Tab tab){
        // set new current id
        this.tabsList.SetCurrentTabID(tab.GetTabID()); // -- very important !

        // other useful things
        tab.UpdateUrlBar();

        tab.SetTabMenuItemChecked();
        this.progressBar.setVisibility(tab.GetOnProgress() ? View.VISIBLE : View.INVISIBLE);
    }

    public void RemoveTabResources(Tab tab, int index) {
        tab.removeMenuItem();
        this.tabsList.Remove(index);
    }

    public void SwitchTabs(Tab from, Tab to) {
        // init transaction
        FragmentTransaction ft = this.tabsList.GetTabsManager().beginTransaction();
        ft.hide(from);
        ft.show(to);
        ft.commit();

        // force transaction
        this.tabsList.GetTabsManager().executePendingTransactions();
    }

    public void RemoveAndSwitchTab(Tab from, Tab to) {
        // init transaction
        FragmentTransaction ft = this.tabsList.GetTabsManager().beginTransaction();
        ft.remove(from);
        ft.show(to);
        ft.commit();

        // force transaction
        this.tabsList.GetTabsManager().executePendingTransactions();
    }
}


