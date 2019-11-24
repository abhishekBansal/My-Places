package rrapps.myplaces.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * Created by abhishek on 01/07/15.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {

    List<FragmentTabItem> mFragmentTabItems;
    public ViewPagerAdapter(FragmentManager fm,
                            List<FragmentTabItem> fragmentTabItems) {
        super(fm);
        mFragmentTabItems = fragmentTabItems;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentTabItems.get(position % mFragmentTabItems.size()).getFragment();
    }

    @Override
    public int getCount() {
        if(mFragmentTabItems != null)
            return mFragmentTabItems.size();
        else
            return 0;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTabItems.get(position % mFragmentTabItems.size()).getTitle();
    }

    public static class FragmentTabItem {
        @Getter
        @Setter Fragment fragment;
        @Getter @Setter
        String title;

        public FragmentTabItem(Fragment fragment, String title) {
            this.fragment = fragment;
            this.title = title;
        }
    }
}
