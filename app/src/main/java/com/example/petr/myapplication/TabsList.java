package com.example.petr.myapplication;

import android.support.v4.app.FragmentManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class TabsList {

    // id of current opens tab
    private Integer currentTabID;

    // list of all tabs
    private List<Integer> arrayID;

    // last created tad id
    private Integer tabIDIterator;

    // container of tabs
    private FragmentManager tabsManager;


    public TabsList(FragmentManager tabsManager) {
        this.tabsManager = tabsManager;
        this.currentTabID = 0;
        this.tabIDIterator = 0;
        this.arrayID = new ArrayList<Integer>();
    }

    public Integer Add() {
        // set new current id
        this.currentTabID = this.tabIDIterator;

        arrayID.add(this.tabIDIterator);

        this.tabIDIterator++;

        return this.currentTabID;
    }

    public void Remove(int index) {
        this.arrayID.remove((int)index);
    }

    public void SetCurrentTabID(Integer id) {
        this.currentTabID = id;
    }

    public Integer GetCurrentTabID() {
        return this.currentTabID;
    }

    public boolean СanGetCurrentTab() {
        return (this.tabsManager.findFragmentByTag(Config.TAG_PREFIX + this.currentTabID) != null);
    }

    public Tab GetCurrentTab() {
        return (Tab) this.tabsManager.findFragmentByTag(Config.TAG_PREFIX + this.currentTabID);
    }

    public Tab GetTabByTag(String tag) {
        return (Tab) this.tabsManager.findFragmentByTag(tag);
    }

    public FragmentManager GetTabsManager() {
        return this.tabsManager;
    }

    public List<Integer> GetArrayID() {
        return this.arrayID;
    }

    public boolean CanRemoveTab() {
        return (this.arrayID.size() > 1);
    }

    public Integer FindTabIndex(Integer id) {
        return this.arrayID.indexOf(id);
    }

    public Tab GetTabByIndex(Integer index) {
        return (Tab) this.tabsManager.findFragmentByTag(Config.TAG_PREFIX + this.arrayID.get(index));
    }
}

