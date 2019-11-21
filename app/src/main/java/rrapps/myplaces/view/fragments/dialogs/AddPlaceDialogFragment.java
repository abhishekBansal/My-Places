package rrapps.myplaces.view.fragments.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rrapps.myplaces.MyPlacesApplication;
import rrapps.myplaces.R;
import rrapps.myplaces.model.DaoSession;
import rrapps.myplaces.model.MPLocation;
import rrapps.myplaces.model.MPLocationDao;
import timber.log.Timber;

/**
 *
 * Created by abhishek on 29/06/15.
 */
public class AddPlaceDialogFragment extends DialogFragment {

    public static final String LOCATION_KEY = "bundle_location_key";
    @BindView(R.id.et_location)
    EditText mLocationEt;


    private DialogInterface.OnDismissListener mOnDismissListener;
    private Location mLastLocation;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null) {
            mLastLocation = getArguments().getParcelable(LOCATION_KEY);
            if (mLastLocation == null) {
                Timber.e("Location should not be null here.");
                Snackbar.make(mLocationEt, R.string.could_not_fetch_location, Snackbar.LENGTH_LONG)
                        .show();
            }
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_add_place, container);
        ButterKnife.bind(this, view);
        Timber.d( "onCreateView");

        return view;
    }

    /**
     * add a place using current location
     */
    @OnClick(R.id.button_cur_loc)
    public void addCurrentLocation() {
        String location = mLocationEt.getText().toString();
        if(!TextUtils.isEmpty(location) && mLastLocation != null) {
            MPLocation mpLocation = new MPLocation();
            mpLocation.setName(mLocationEt.getText().toString());
            mpLocation.setLatitude(mLastLocation.getLatitude());
            mpLocation.setLongitude(mLastLocation.getLongitude());
            DaoSession session = MyPlacesApplication.getInstance().getDaoSession();
            MPLocationDao locationDao = session.getMPLocationDao();
            locationDao.insertOrReplace(mpLocation);

            mOnDismissListener.onDismiss(getDialog());
            dismiss();
        } else {
            Toast.makeText(getActivity(), R.string.please_enter_location_name, Toast.LENGTH_LONG).show();
        }
    }

    @OnClick(R.id.button_cancel)
    public void cancel() {
        dismiss();
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener dismissListener) {
        mOnDismissListener = dismissListener;
    }
}