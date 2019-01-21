package com.example.elliotsymons.positioningtestbed;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class FingerprintingMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprinting_menu);
    }


    public void gridMethodSelected(View view) {
        Toast.makeText(this, "Grid method not yet implemented", Toast.LENGTH_SHORT).show();
    }
    public void placementMethodSelected(View view) {
        Toast.makeText(this, "Placement method not yet implemented", Toast.LENGTH_SHORT).show();
    }
}
