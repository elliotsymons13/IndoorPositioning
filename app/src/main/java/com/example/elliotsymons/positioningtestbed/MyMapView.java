package com.example.elliotsymons.positioningtestbed;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.Display;

public class MyMapView extends AppCompatImageView {

    private Canvas mapCanvas;

    //Map
    private Bitmap mapBackground;
    private Rect displayRect;
    private int dispWidth, dispHeight, MAP_WIDTH, MAP_HEIGHT;

    //Dots on map
    private final int DEFAULT_PERSISTENT_DOT_RADIUS = 10;
    private Paint BLUE_DOT_PAINT;
    private Paint PERSISTENT_DOT_PAINT;

    private int blueDot_x = 0;
    private int blueDot_y = 0;
    private int blueDot_r = 20;


    //Default constructor, calls through
    public MyMapView(Context context) {
        this(context, null);
    }

    public MyMapView(Context context, AttributeSet attributeSet) {
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
        final int MAP_WIDTH_ORIGINAL = 1241;
        final int MAP_HEIGHT_ORIGINAL = 1080;

        MAP_WIDTH = dispWidth;
        final double SF = (double) MAP_WIDTH / MAP_WIDTH_ORIGINAL;
        MAP_HEIGHT = (int) (MAP_HEIGHT_ORIGINAL * SF);

        //Construct rectangle container for background sizing
        displayRect = new Rect(0, 0, MAP_WIDTH, MAP_HEIGHT);

        //Setup Paints
        BLUE_DOT_PAINT = new Paint();
        BLUE_DOT_PAINT.setColor(Color.parseColor("#4285f4")); //'Google maps dot blue'
        BLUE_DOT_PAINT.setStyle(Paint.Style.FILL);

    }



    //Also called on startup (as view is inflated from zero size when created).
    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);

        // this.invalidate(); //TODO uncomment
    }


    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mapCanvas = canvas;
        canvas.drawBitmap(mapBackground, null, displayRect, null);
        canvas.drawCircle(blueDot_x, blueDot_y, blueDot_r, BLUE_DOT_PAINT);

        //invalidate();
    }



    /**
     * Allows for the location of the 'blue dot' representing the user's location to be changed.
     *
     * @param x pixel coordinate of center of dot horizontally
     * @param y pixel coordinate of center of dot vertically
     * @param r radius of dot to draw.
     */
    public void updateBlueDot(int x, int y, int r) {
        //TODO
        blueDot_x = x;
        blueDot_y = y;
        blueDot_r = r;

        invalidate(); //redraw view
    }

    public void updateBlueDot(int x, int y) {
        updateBlueDot(x, y, blueDot_r);
    }

    public void setBlueDotColour(Paint colour) {
        //TODO?
    }

    /**
     * Adds a graphical dot to the map representing a fingerprinted point.
     * This is so the user knows which locations have already been fingerprinted.
     *
     * @param x pixel coordinate of center of dot horizontally
     * @param y pixel coordinate of center of dot vertically
     * @param r radius of dot to draw.
     */
    public void addPersistentDot(int x, int y, int r) {
        //TODO
    }

    public void addPerisistentDot(int x, int y) {
        addPersistentDot(x, y, DEFAULT_PERSISTENT_DOT_RADIUS);
    }

    public void setPersistentDotColour() {
        //TODO?
    }


    public int getMapWidth() {
        return MAP_WIDTH;
    }

    public int getMapHeight() {
        return MAP_HEIGHT;
    }

}

