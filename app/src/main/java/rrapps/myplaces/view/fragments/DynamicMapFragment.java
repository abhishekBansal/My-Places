package rrapps.myplaces.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 *
 * Created by abhishek on 21/04/16.
 */
public class DynamicMapFragment
       extends SupportMapFragment
       implements OnMapReadyCallback,
                  GoogleMap.OnMapClickListener,
                  GoogleMap.OnMapLoadedCallback {

    private static final String LAT_LONG_KEY = "LatLongKey";

    public static final int DEFAULT_ZOOM = 16;
    private static final String ZOOM_LEVEL_KEY = "ZoomLevelKey";
    private static final String IS_READ_ONLY = "Is_read_only";

    private LatLng mLatLong;

    private boolean mIsReadOnly;

    private OnDynamicMapReadyListener mDynamicMapReadyListener;
    private OnDynamicMapLoadedCallback mDynamicMapLoadedCallback;
    private GoogleMap mGoogleMap;
    private int mZoomLevel = DEFAULT_ZOOM;

    public LatLng getSelectedLocation() {
        return mLatLong;
    }

    public interface OnDynamicMapReadyListener {
        void onMapReady(GoogleMap map);
    }

    public interface OnDynamicMapLoadedCallback {
        void onMapLoaded();
    }

    public DynamicMapFragment() {
        super();
    }

    public static DynamicMapFragment newInstance(LatLng latLng, int zoomLevel, boolean isReadOnly) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(LAT_LONG_KEY, latLng);
        bundle.putInt(ZOOM_LEVEL_KEY, zoomLevel);
        bundle.putBoolean(IS_READ_ONLY, isReadOnly);
        DynamicMapFragment fragment = new DynamicMapFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mLatLong = bundle.getParcelable(LAT_LONG_KEY);
            mZoomLevel = bundle.getInt(ZOOM_LEVEL_KEY, DEFAULT_ZOOM);
            mIsReadOnly = bundle.getBoolean(IS_READ_ONLY, false);
        }

        if (mLatLong == null) {
            mLatLong = new LatLng(0.0, 0.0);
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater arg0, ViewGroup arg1, Bundle arg2) {
        View v = super.onCreateView(arg0, arg1, arg2);


        getMapAsync(this);
        return v;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (googleMap != null) {
            // not calling this was causing crash in some of the cases
            // http://stackoverflow.com/questions/19541915/google-maps-cameraupdatefactory-not-initalized
            MapsInitializer.initialize(getActivity());

            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLatLong, mZoomLevel));
            googleMap.addMarker(new MarkerOptions()
                    .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                    .position(mLatLong));
            googleMap.setOnMapLoadedCallback(this);

            if (!mIsReadOnly) {
                googleMap.setOnMapClickListener(this);
            }

            mGoogleMap = googleMap;

            if (mDynamicMapReadyListener != null) {
                mDynamicMapReadyListener.onMapReady(mGoogleMap);
            }
        }
    }

    @Override
    public void onMapLoaded() {
        if(mDynamicMapLoadedCallback != null) {
            mDynamicMapLoadedCallback.onMapLoaded();
        }
    }

    public void setDynamicMapReadyListener(OnDynamicMapReadyListener listener) {
        mDynamicMapReadyListener = listener;
    }

    public void setDynamicMapLoadedCallback(OnDynamicMapLoadedCallback mDynamicMapLoadedCallback) {
        this.mDynamicMapLoadedCallback = mDynamicMapLoadedCallback;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (mGoogleMap != null) {
            mGoogleMap.clear();
            mGoogleMap.addMarker(new MarkerOptions()
                    .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                    .position(latLng));
            mLatLong = latLng;
        }
    }
}
