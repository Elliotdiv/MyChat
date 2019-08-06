package com.example.myapplication;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabAcessoradapter extends FragmentPagerAdapter {

    public TabAcessoradapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                ChatFragment chatFragment = new ChatFragment();
                return chatFragment;
            case 1:
                GroupFragment groupFragment = new GroupFragment();
                return groupFragment;
            case 2:
                ContactsFragment contactsFragment = new ContactsFragment();
                return contactsFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "My Chats";
            case 1:
                return "Groups Chat";
            case 2:
                return "Contacts Info";
            default:
                return null;
        }
    }
}

