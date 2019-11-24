package rrapps.myplaces.view.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.hudomju.swipe.SwipeToDismissTouchListener;
import com.hudomju.swipe.adapter.ListViewAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import rrapps.myplaces.MyPlacesApplication;
import rrapps.myplaces.PermissionUtils;
import rrapps.myplaces.R;
import rrapps.myplaces.adapters.LocationListAdapter;
import rrapps.myplaces.asyncTasks.GetPlacesAsyncTask;
import rrapps.myplaces.model.DaoSession;
import rrapps.myplaces.model.MPLocation;
import rrapps.myplaces.model.MPLocationDao;
import rrapps.myplaces.services.FetchAddressIntentService;
import rrapps.myplaces.utils.CommonDialogs;
import rrapps.myplaces.utils.ContextUtils;
import rrapps.myplaces.view.activities.LocationDetailsActivity;
import rrapps.myplaces.view.fragments.dialogs.AddPlaceDialogFragment;
import rrapps.myplaces.view.fragments.dialogs.ThemedInfoDialog;
import timber.log.Timber;


public class LocationListFragment extends Fragment
        implements GetPlacesAsyncTask.LocationsFetchedListener,
        DialogInterface.OnDismissListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 11;

    @BindView(R.id.lv_location)
    ListView mLocationListView;

    @BindView(R.id.empty_view)
    View mEmptyView;

    @BindView(R.id.fab_menu)
    FloatingActionMenu mFABMenu;

    private GoogleApiClient mGoogleApiClient;

    private LocationListAdapter mAdapter;

    private ProgressDialog mProgressDialog;

    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(FetchAddressIntentService.SUCCESS_ACTION_STRING)
                    && getActivity() != null) {
                refresh();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_location_list, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mGoogleApiClient == null
                && PermissionUtils.hasLocationPermission(getActivity())
                && ContextUtils.isLocationEnabled(getActivity())) {
            Timber.d("Permissions Checked, GPS Checked, Building API client");
            buildGoogleApiClient();
        }

        refresh();

        getActivity().registerReceiver(mBroadcastReceiver,
                new IntentFilter(FetchAddressIntentService.SUCCESS_ACTION_STRING));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new LocationListAdapter(getActivity(), mOnClickListener);
        mLocationListView.setAdapter(mAdapter);

        final SwipeToDismissTouchListener<ListViewAdapter> touchListener =
                new SwipeToDismissTouchListener<>(
                        new ListViewAdapter(mLocationListView),
                        new SwipeToDismissTouchListener.DismissCallbacks<ListViewAdapter>() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListViewAdapter viewAdapter, int i) {
                                archiveLocation(i);
                            }
                        });
        mLocationListView.setOnTouchListener(touchListener);
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        mLocationListView.setOnScrollListener((AbsListView.OnScrollListener) touchListener.makeScrollListener());
        mLocationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (touchListener.existPendingDismisses()) {
                    touchListener.undoPendingDismiss();
                } else {
                    openLocationDetails(position);
                }
            }
        });

        if(!PermissionUtils.hasLocationPermission(getActivity())) {
            Timber.i("Requesting for location permission");
            requestForLocationPermission();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }

    private void requestForLocationPermission() {
        // Here, thisActivity is the current activity
        if (!PermissionUtils.hasLocationPermission(getActivity())) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                ThemedInfoDialog dialog = ThemedInfoDialog.newInstance("",
                        getString(R.string.location_permission_rationale),
                        getString(R.string.ok), "", false);
                dialog.setMPositiveClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // No explanation needed, we can request the permission.
                        PermissionUtils.requestPermission(Manifest.permission.ACCESS_FINE_LOCATION,
                                MY_PERMISSIONS_REQUEST_LOCATION, getActivity());
                    }
                });
                dialog.show(getFragmentManager(), "");
            } else {
                // No explanation needed, we can request the permission.
                PermissionUtils.requestPermission(Manifest.permission.ACCESS_FINE_LOCATION,
                        MY_PERMISSIONS_REQUEST_LOCATION, getActivity());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Timber.i("Yay ! User granted location permission, lets check if GPS is available");
                    checkGPS();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void checkGPS() {
        // check if we have permission first
        if (getActivity() != null) {
            if(!PermissionUtils.hasLocationPermission(getActivity())) {
                Snackbar.make(mEmptyView, R.string.no_location_permission,
                        Snackbar.LENGTH_LONG).show();
            } else {
                // see if GPS is on
                if (!ContextUtils.isLocationEnabled(getActivity())) {
                    CommonDialogs.showLocationSettingRedirectDialog((AppCompatActivity) getActivity());
                } else {
                    // go on and build API client if everything is fine
                    buildGoogleApiClient();
                }
            }
        }
    }

    private void archiveLocation(int i) {
        MPLocation place = mAdapter.getItem(i);

        DaoSession session = MyPlacesApplication.getInstance().getDaoSession();
        MPLocationDao dbPlaceDao = session.getMPLocationDao();
        dbPlaceDao.deleteByKey(place.getId());

        mAdapter.remove(place);

        if(mAdapter.getCount() == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
            mLocationListView.setVisibility(View.GONE);
        }
    }

    private void refresh() {
        new GetPlacesAsyncTask(this).execute();
    }

    @OnItemClick(R.id.lv_location)
    public void openLocationDetails(int index) {
        MPLocation location = mAdapter.getItem(index);
        Intent intent = new Intent(getActivity(), LocationDetailsActivity.class);
        intent.putExtra(LocationDetailsActivity.LOCATION_KEY, location);
        startActivity(intent);
    }

    @Override
    public void onLocationsFetched(List<MPLocation> MPLocations) {
        if(getActivity() != null && MPLocations.size() > 0) {
            mLocationListView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
            mAdapter.clear();
            mAdapter.addAll(MPLocations);
        } else {
            Timber.w("No Locations found in database");
            mEmptyView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        refresh();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
        mProgressDialog = ProgressDialog.show(getActivity(), null,
                getString(R.string.fetching_location), true, false);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Timber.i("Connected to location services successfully");
        hideProgressDialog();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Timber.w( "Connection to location service was suspended");
        hideProgressDialog();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(getActivity(), R.string.could_not_fetch_location,
                Toast.LENGTH_LONG).show();
        Timber.e( "Connection to location service was failed");
        hideProgressDialog();
    }

    @OnClick(R.id.fab_add_location)
    void addNewLocation() {
        if(ContextUtils.isLocationEnabled(getActivity())) {
            try {
                if(mGoogleApiClient != null) {
                    Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    if (lastLocation == null) {
                        Snackbar.make(mEmptyView, R.string.try_after_few_seconds,
                                Snackbar.LENGTH_LONG).show();
                        return;
                    }

                    Bundle bundle = new Bundle();
                    bundle.putParcelable(AddPlaceDialogFragment.LOCATION_KEY, lastLocation);

                    AddPlaceDialogFragment addDF = new AddPlaceDialogFragment();
                    addDF.setArguments(bundle);
                    addDF.setOnDismissListener(this);
                    addDF.show(getFragmentManager(), "AddLocationDF");
                }
            } catch (SecurityException e) {
                Snackbar.make(mEmptyView, R.string.please_provide_location_permission, Snackbar.LENGTH_LONG)
                        .show();
                requestForLocationPermission();
            }
        } else {
            CommonDialogs.showLocationSettingRedirectDialog((AppCompatActivity) getActivity());
        }

        mFABMenu.close(false);
    }

    @OnClick(R.id.fab_add_car)
    void parkCar() {
        if(ContextUtils.isLocationEnabled(getActivity())) {
            try {
                if(mGoogleApiClient != null) {
                    Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    if (lastLocation == null) {
                        Snackbar.make(mEmptyView, R.string.try_after_few_seconds, Snackbar.LENGTH_LONG)
                                .show();
                        return;
                    }

                    MPLocation carLocation = new MPLocation();
                    carLocation.setName(getString(R.string.my_parked_car));
                    carLocation.setParkedCar(true);
                    carLocation.setLatitude(lastLocation.getLatitude());
                    carLocation.setLongitude(lastLocation.getLongitude());

                    DaoSession session = MyPlacesApplication.getInstance().getDaoSession();
                    MPLocationDao locationDao = session.getMPLocationDao();
                    long key = locationDao.insertOrReplace(carLocation);

                    ContextUtils.startFetchAddressService(key, carLocation.getLatitude(), carLocation.getLongitude(), getActivity());

                    refresh();
                }
            } catch (SecurityException e) {
                Snackbar.make(mEmptyView, R.string.please_provide_location_permission, Snackbar.LENGTH_LONG)
                        .show();
                requestForLocationPermission();
            }
        } else {
            CommonDialogs.showLocationSettingRedirectDialog((AppCompatActivity) getActivity());
        }

        mFABMenu.close(false);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button_share: {
                    MPLocation place = (MPLocation) v.getTag();
                    Intent intent = ContextUtils.shareLocationIntent(place);
                    startActivity(intent);
                    break;
                }
                case R.id.button_navigate: {
                    MPLocation place = (MPLocation) v.getTag();
                    ContextUtils.navigateToLocation(place, getActivity());
                    break;
                }
                default:
                    break;
            }
        }
    };

    private void hideProgressDialog() {
        if(mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
