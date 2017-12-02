package com.jwx.animationandcustomview.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

import com.jwx.animationandcustomview.widget.tablayout.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jwx on 2017/12/2
 */

public class ChangeColorPagerAdapter extends FragmentStatePagerAdapter {

    private List<String> titleList;
    private List<Fragment> fragmentList;

    public ChangeColorPagerAdapter(FragmentManager fm) {
        super(fm);
        titleList = new ArrayList<>();
        fragmentList = new ArrayList<>();

    }

    public ChangeColorPagerAdapter(FragmentManager fm, List<String> titleList, List<Fragment> fragmentList) {
        super(fm);
        this.titleList = titleList;
        this.fragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    public void setData(ArrayList<Fragment> fragmentList, ArrayList<String> titleList) {
        this.fragmentList.clear();
        this.titleList.clear();
        this.fragmentList = fragmentList;
        this.titleList = titleList;
        notifyDataSetChanged();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position);
    }

    public void clear() {
        try {
            for (Fragment fragment : fragmentList) {
                FragmentTransaction transaction = fragment.getFragmentManager().beginTransaction();
                transaction.remove(fragment).commitAllowingStateLoss();
            }

            fragmentList.clear();
        } catch (Exception e) {
            // do nothing
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);


        if (position <= getCount()) {
            FragmentManager manager = ((Fragment) object).getFragmentManager();
            FragmentTransaction trans = manager.beginTransaction();
            trans.remove((Fragment) object);
            trans.commitAllowingStateLoss();
        }
    }
}
