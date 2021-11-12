package com.doubleclick.chatapp.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.doubleclick.chatapp.Fragments.ChatFragment;
import com.doubleclick.chatapp.Fragments.ProfileFragment;
import com.doubleclick.chatapp.Fragments.UsersFragment;


public class Pager_Adapter extends FragmentStatePagerAdapter{


    int number;
    public Pager_Adapter(@NonNull FragmentManager fm,int number) {
        super(fm);
        this.number = number;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                ChatFragment chatFragment = new ChatFragment();
                return chatFragment;
            case 1:
                UsersFragment usersFragment  =new UsersFragment();
                return usersFragment;
            case 2:
                ProfileFragment profileFragment = new ProfileFragment();
                return profileFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return number;
    }
}
