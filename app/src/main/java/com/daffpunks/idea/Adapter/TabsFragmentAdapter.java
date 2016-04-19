package com.daffpunks.idea.Adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.daffpunks.idea.Fragment.AbstractTabFragment;
import com.daffpunks.idea.Fragment.NoteFragment;
import com.daffpunks.idea.Fragment.TaskFragment;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by User on 22.03.2016.
 */
public class TabsFragmentAdapter extends FragmentPagerAdapter{

    private Map<Integer, AbstractTabFragment> tabs;
    private Context context;

    public TabsFragmentAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;

        initTabsMap(context);
    }

    private void initTabsMap(Context context) {
        tabs = new HashMap<>();
        tabs.put(0, NoteFragment.getInstance(context));
        tabs.put(1, TaskFragment.getInstance(context));
    }


    @Override
    public Fragment getItem(int position) {
        return tabs.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabs.get(position).getTitle();
    }

    @Override
    public int getCount() {
        return tabs.size();
    }
}
