package rrapps.myplaces.view.fragments;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.maps.model.LatLng;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rrapps.myplaces.R;
import rrapps.myplaces.asyncTasks.SaveLocationAsyncTask;
import rrapps.myplaces.model.MPLocation;
import rrapps.myplaces.utils.ContextUtils;
import rrapps.myplaces.view.activities.LocationDetailsActivity;
import rrapps.myplaces.view.widgets.EditableTextView;

public class LocationDetailsFragment extends Fragment {

    @BindView(R.id.map_container)
    FrameLayout mMapContainer;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.tv_address)
    EditableTextView mAddressTv;

    @BindView(R.id.tv_notes)
    EditableTextView mNotesTv;

    @BindView(R.id.tv_name)
    EditableTextView mNameTv;

    @BindView(R.id.collapsing_layout)
    CollapsingToolbarLayout mCollapsingLayout;

    private MPLocation mLocation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mLocation = getActivity().getIntent().getParcelableExtra(LocationDetailsActivity.LOCATION_KEY);
        if (mLocation == null) {
            throw new IllegalArgumentException("Location is compulsory in LocationDetailsFragment");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_location_details, container, false);
        ButterKnife.bind(this, rootView);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        if (mLocation != null) {
            mToolbar.setTitle(mLocation.getName());
            getActivity().setTitle(mLocation.getName());
            LatLng latLng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
            DynamicMapFragment fragment = DynamicMapFragment.newInstance(latLng, DynamicMapFragment.DEFAULT_ZOOM, true);
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.map_container, fragment)
                    .commit();

            setupView();
        }

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.location_detail_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                Intent intent = ContextUtils.shareLocationIntent(mLocation);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupView() {
        setupAddress();
        setupName();
        setupNotes();

        mAddressTv.setOnTextChangedListener(new EditableTextView.OnTextChangedListener() {
            @Override
            public void onTextChanged(String text) {
                if (mLocation != null) {
                    mLocation.setAddress(text);
                    new SaveLocationAsyncTask().execute(mLocation);
                }
            }
        });

        mNotesTv.setOnTextChangedListener(new EditableTextView.OnTextChangedListener() {
            @Override
            public void onTextChanged(String text) {
                if (mLocation != null) {
                    mLocation.setNotes(text);
                    new SaveLocationAsyncTask().execute(mLocation);
                }
            }
        });

        mNameTv.setOnTextChangedListener(new EditableTextView.OnTextChangedListener() {
            @Override
            public void onTextChanged(String text) {
                if (mLocation != null) {
                    mLocation.setName(text);
                    new SaveLocationAsyncTask().execute(mLocation);
                    mCollapsingLayout.setTitle(mLocation.getName());
                }
            }
        });
    }

    private void setupAddress() {
        if (!TextUtils.isEmpty(mLocation.getAddress())) {
            mAddressTv.setText(mLocation.getAddress());
        } else {
            mAddressTv.setText(getString(R.string.no_address_added));
        }
    }

    private void setupNotes() {
        if (!TextUtils.isEmpty(mLocation.getNotes())) {
            mNotesTv.setText(mLocation.getNotes());
        } else {
            mNotesTv.setText(getString(R.string.no_notes_added));
        }
    }

    private void setupName() {
        if (!TextUtils.isEmpty(mLocation.getName())) {
            mNameTv.setText(mLocation.getName());
        }
    }

    @OnClick(R.id.fab_navigate)
    public void onClick() {
        ContextUtils.navigateToLocation(mLocation, getActivity());
    }
}
