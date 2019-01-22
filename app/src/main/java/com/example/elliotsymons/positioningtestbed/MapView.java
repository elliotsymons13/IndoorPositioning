package com.example.elliotsymons.positioningtestbed;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.Display;

public class MapView extends AppCompatImageView {

    private Canvas mapCanvas;

    private Bitmap mapBackground;
    private Rect displayRect;
    private int dispWidth, dispHeight;

    private final int MAP_WIDTH_ORIGNAL = 1241;
    private final int MAP_HEIGHT_ORIGINAL = 1080;
    private int MAP_WIDTH;
    private int MAP_HEIGHT;




    //Default constructor, calls through
    public MapView(Context context) {
        this(context, null);
    }

    public MapView(Context context, AttributeSet attributeSet) {
        super(context);

        //Import map image resource
        mapBackground = BitmapFactory.decodeResource(getResources(), R.drawable.floor_plan);

        //Size display
        Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        Point displaySize = new Point();
        display.getSize(displaySize);
        dispWidth = displaySize.x;
        dispHeight = displaySize.y;

        //Calculate dimensions for image
        MAP_WIDTH = dispWidth;
        final double SF = MAP_WIDTH / MAP_WIDTH_ORIGNAL;
        MAP_HEIGHT = (int) (MAP_HEIGHT_ORIGINAL * SF);

        //Construct rectangle container for background sizing
        displayRect = new Rect(0, 0, dispWidth, (int) dispHeight/2);

    }

    //Also called on startup (as view is inflated from zero size when created).
    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);

        this.invalidate();
    }


    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(mapBackground, null, displayRect, null);
    }

}

