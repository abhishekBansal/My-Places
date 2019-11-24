package rrapps.myplaces.view.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import rrapps.myplaces.R;

public class LocationDetailsActivity extends AppCompatActivity {

    public static final String LOCATION_KEY = "LOCATION_OBJECT_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_details);
    }
}
