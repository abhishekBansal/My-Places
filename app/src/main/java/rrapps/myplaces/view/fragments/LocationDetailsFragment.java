package rrapps.myplaces.view.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import rrapps.myplaces.R;
import rrapps.myplaces.adapters.ViewPagerAdapter;
import rrapps.myplaces.model.MPLocation;
import rrapps.myplaces.view.activities.LocationDetailsActivity;

public class LocationDetailsFragment extends Fragment {

//    @BindView(R.id.viewpager_my_properties)
//    ViewPager mMyPropertiesViewPager;
//
//    @BindView(R.id.sliding_tabs_header)
//    PagerSlidingTabStrip mSlidingTabsHeader;

    private MPLocation mLocation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocation = getActivity().getIntent().getParcelableExtra(LocationDetailsActivity.LOCATION_KEY);
        if(mLocation == null) {
            throw new IllegalArgumentException("Location is compulsory in LocationDetailsFragment");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_location_details, container, false);
        ButterKnife.bind(this, rootView);

        List<ViewPagerAdapter.FragmentTabItem> fragmentList = new ArrayList<>();

//        fragmentList.add(new ViewPagerAdapter.FragmentTabItem(LocationContactsFragment.newInstance(),
//                getString(R.string.contacts)));
//        fragmentList.add(new ViewPagerAdapter.FragmentTabItem(LocationWifiFragment.newInstance(),
//                getString(R.string.Wifi)));

        ViewPagerAdapter adapter
                = new ViewPagerAdapter(getChildFragmentManager() , fragmentList);
//        mMyPropertiesViewPager.setAdapter(adapter);
//
//        mSlidingTabsHeader.setViewPager(mMyPropertiesViewPager);

        getActivity().setTitle(mLocation.getName());

        return rootView;
}
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        List<Fragment> fragments = getChildFragmentManager().getFragments();
//        if (fragments != null) {
//            for (Fragment fragment : fragments) {
//                //TODO fragment was coming null at times which was causing crash crashing
//                //TODO not sure why !
//                if (fragment != null) {
//                    fragment.onActivityResult(requestCode, resultCode, data);
//                }
//            }
//        }
//    }

    /*@Override
    public void onWifiFetched(List<RWifi> rWifi) {
        if(getActivity() != null && rWifi.size() > 0) {
            //mLocationListView.setVisibility(View.VISIBLE);
            //mEmptyView.setVisibility(View.GONE);
            mWifiAdapter.clear();
            mWifiAdapter.addAll(rWifi);
        } else {
            Log.w(TAG, "No Wifi found in database");
        }
    }*/
}
