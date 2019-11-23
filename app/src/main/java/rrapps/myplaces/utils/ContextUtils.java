package rrapps.myplaces.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.widget.Toast;

import rrapps.myplaces.R;
import rrapps.myplaces.model.MPLocation;
import rrapps.myplaces.services.FetchAddressIntentService;
import timber.log.Timber;

/**
 * Created by abhishek
 * on 08/12/16.
 */

public class ContextUtils {

    public static boolean isLocationEnabled(Context context) {
        int locationMode;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    public static void redirectToLocationSettings(final Context context) {
        context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    }

    // TODO: 17/12/16 return error and show it to user
    public static void navigateToLocation(MPLocation place, final Context context) {
        String uri = "geo:" + place.getLatitude() + ","
                + place.getLongitude()
                + "?q=" + place.getLatitude()
                + "," + place.getLongitude() + "(" + place.getName()+ ")";
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse(uri)));
        } catch (ActivityNotFoundException e) {
            Timber.e("Could not find any map installed application to view this location");
        }
    }

    public static Intent shareLocationIntent(MPLocation place) {
        String geoUri = "http://maps.google.com/maps?q=loc:"
                + place.getLatitude() + ","
                + place.getLongitude() + "(" + Uri.encode(place.getName()) + ")";
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                place.getName() + "\n" + geoUri + "\n\nSent Via MyPlaces Android App\nhttp://ow.ly/6A7E307dxKF");;
        sendIntent.setType("text/plain");

        return sendIntent;
    }

    public static Intent
    startFetchAddressService(long key, double latitude, double longitude, final Context context) {
        Intent intent = new Intent(context, FetchAddressIntentService.class);
        intent.putExtra(FetchAddressIntentService.LOCATION_PARCELABLE_ID_KEY, key);
        intent.putExtra(FetchAddressIntentService.LOCATION_LATITUDE_DATA_EXTRA, latitude);
        intent.putExtra(FetchAddressIntentService.LOCATION_LONGITUDE_DATA_EXTRA, longitude);
        context.startService(intent);
        return intent;
    }
}

