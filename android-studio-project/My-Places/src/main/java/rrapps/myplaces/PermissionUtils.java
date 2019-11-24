package rrapps.myplaces;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * Created by abhishek
 * on 04/12/16.
 */

public class PermissionUtils {
    public static void requestPermission(final String permission,
                                         final int requestCode,
                                         final Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[]{permission},
                requestCode);
    }

    public static boolean hasLocationPermission(final Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }
}
