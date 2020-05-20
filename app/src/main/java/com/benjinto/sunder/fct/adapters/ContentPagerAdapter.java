package com.benjinto.sunder.fct.adapters;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.benjinto.sunder.fct.views.ContentScreenSlideFragment;
import com.benjinto.sunder.fct.misc.Note;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ContentPagerAdapter extends FragmentStatePagerAdapter {

    private final static String ARG_NOTE = "NOTE";
    private GetList mGetList;

    public ContentPagerAdapter(FragmentManager fm, GetList getList) {
        super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mGetList = getList;
    }

    @NotNull
    @Override
    public Fragment getItem(int i) {
        Fragment fragment = new ContentScreenSlideFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_NOTE,mGetList.getList().get(i));
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getCount() {
        return mGetList.getList().size();
    }

    public interface GetList{
        List<Note> getList();
    }
}
