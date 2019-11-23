package rrapps.myplaces.view.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import rrapps.myplaces.R;

public class LocationListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
