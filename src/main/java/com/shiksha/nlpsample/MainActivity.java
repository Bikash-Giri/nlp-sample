package com.shiksha.nlpsample;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.shiksha.nlp.loc.posloc.NetworkProvideManager;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "IchnaeaPreferences";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissions();
    }
    void requestPermissions(){
        if (!NetworkProvideManager.isLocationPermissionsGranted(this)) {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            String[] permissions = NetworkProvideManager.permissions;
            ActivityCompat.requestPermissions(this, permissions, 10);
        }
    }

    /**
     * Permission is granted or not . Continue the action or workflow in your
     *Explain to the user that the feature is unavailable because the
     * features requires a permission that the user has denied. At the
     *same time, respect the user's decision. Don't link to system
     *   settings in an effort to convince the user to change their
     *decision.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==10){
            if (NetworkProvideManager.isLocationPermissionsGranted(this)){
                Toast.makeText(this,"permissions are granted",Toast.LENGTH_LONG).show();

            }else {
                Toast.makeText(this,"permissions are not granted",Toast.LENGTH_LONG).show();
            }

        }
    }
    @Override
    protected void onPause() {
        super.onPause();
    }
}