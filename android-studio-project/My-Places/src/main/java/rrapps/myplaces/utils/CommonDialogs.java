package rrapps.myplaces.utils;

import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import rrapps.myplaces.R;
import rrapps.myplaces.view.fragments.dialogs.ThemedInfoDialog;

/**
 * Created by abhishek
 * on 10/12/16.
 */

public class CommonDialogs {
    public static void showLocationSettingRedirectDialog(final AppCompatActivity context) {
        if(context == null) {
            return;
        }

        ThemedInfoDialog dialog
                = ThemedInfoDialog.newInstance("", context.getString(R.string.please_turn_gps_on),
                null, null, false);
        dialog.setMPositiveClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContextUtils.redirectToLocationSettings(context);
            }
        });
        dialog.show(context.getSupportFragmentManager(), "");
    }
}
