package com.example.elliotsymons.positioningtestbed;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class PlacementFingerprintingActivity extends AppCompatActivity {

    //For drawing on the map
    private Canvas mapCanvas;
    private Bitmap mapBitmap;
    private ImageView map;
    private Paint paint = new Paint();
    private Paint mPaintText = new Paint(Paint.UNDERLINE_TEXT_FLAG);

    private Rect rect1 = new Rect();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placement_fingerprinting);

        getSupportActionBar().setTitle("Fingerprint capture");

        paint.setColor(getColor(R.color.colorAccent));
        mPaintText.setColor(getColor((R.color.colorPrimaryDark)));
        mPaintText.setTextSize(70);

        map = (ImageView) findViewById(R.id.iv_map);
    }

    public void drawSomething(View view) {
        int height = view.getHeight();
        int width = view.getWidth();

        //Create bitmap
        mapBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565); // ARGB_8888 wider
        //Associate with image view
        map.setImageBitmap(mapBitmap);
        //create canvas with the bitmap
        mapCanvas = new Canvas(mapBitmap);

        //TESTING: draw background
        mapCanvas.drawColor(getColor(R.color.colorAccent));


        //Forces recalculation of values for next draw
        view.invalidate();
    }
}
