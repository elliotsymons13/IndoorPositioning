package com.example.elliotsymons.positioningtestbed;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;

import com.example.elliotsymons.positioningtestbed.MapManagement.MapManager;

import java.util.HashSet;
import java.util.Set;

import static com.example.elliotsymons.positioningtestbed.MapViewFragment.GENERIC_DOT;

public class MyMapView extends AppCompatImageView {
    private static final String TAG = "MyMapView";
    private Preferences prefs;
    private MapManager mapManager;

    //Map
    private Bitmap mapBackground;
    private Rect displayRect;
    private int MAP_WIDTH;
    private int MAP_HEIGHT;
    private int ORIGIN_IN_X;
    private int ORIGIN_IN_Y;


    private Paint PERSISTENT_DOT_PAINT;

    private Set<Point> persistentDots;
    private Set<NavDot> navigationDots;

    //Default constructor, calls through
    public MyMapView(Context context) {
        this(context, null);
    }

    public MyMapView(Context context, AttributeSet attributeSet) {
        super(context);
        prefs = Preferences.getInstance(getContext());
        mapManager = MapManager.getInstance(getContext());

        //Import map image resource
        String mapURI = prefs.getMapURI();
        mapBackground = mapManager.decodeImageFromURIString(mapURI);

        //Size display
        Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        Point displaySize = new Point();
        display.getSize(displaySize);
        int dispWidth = displaySize.x;
        int dispHeight = displaySize.y; //FIXME

        //Calculate dimensions for image
        final int MAP_WIDTH_ORIGINAL = mapBackground.getWidth();
        final int MAP_HEIGHT_ORIGINAL = mapBackground.getHeight();
        Log.d(TAG, "MyMapView: ORI_WIDTH: " + MAP_WIDTH_ORIGINAL);
        Log.d(TAG, "MyMapView: ORI_HEIGHT: " + MAP_HEIGHT_ORIGINAL);

        MAP_WIDTH = dispWidth;
        final double SF = (double) MAP_WIDTH / MAP_WIDTH_ORIGINAL;
        Log.d(TAG, "MyMapView: SF = " + SF);
        MAP_HEIGHT = (int) (MAP_HEIGHT_ORIGINAL * SF);

        // Check this will fit in half of the screen
        int maxHeight = (int) (dispHeight/2);
        if (MAP_HEIGHT > maxHeight) {
            double SF2 = (double) maxHeight / MAP_HEIGHT;
            MAP_HEIGHT = maxHeight;
            MAP_WIDTH = (int) (MAP_WIDTH * SF2);
            Log.d(TAG, "MyMapView: Scaled due to being too tall ");
        }

        Log.d(TAG, "MyMapView: measured height: " + getRootView().getMeasuredHeight());
        Log.d(TAG, "MyMapView: measured width: " + getRootView().getMeasuredWidth());

        //Construct rectangle container for background sizing
        ORIGIN_IN_X = (dispWidth-MAP_WIDTH)/2;
        ORIGIN_IN_Y = 0;
        displayRect = new Rect(ORIGIN_IN_X, ORIGIN_IN_Y, ( dispWidth - ORIGIN_IN_X ), MAP_HEIGHT);

        //Setup Paints
        PERSISTENT_DOT_PAINT = new Paint();
        PERSISTENT_DOT_PAINT.setStyle(Paint.Style.FILL);
        PERSISTENT_DOT_PAINT.setColor(getResources().getColor(R.color.colorPersistentDot));

        navigationDots = new HashSet<>();
        persistentDots = new HashSet<>();
    }



    //Also called on startup (as view is inflated from zero size when created).
    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
    }


    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Canvas mapCanvas = canvas;
        canvas.drawBitmap(mapBackground, null, displayRect, null);
        for (Point p : persistentDots) {
            //Dots on map
            int DEFAULT_PERSISTENT_DOT_RADIUS = 10;
            canvas.drawCircle(p.x, p.y, DEFAULT_PERSISTENT_DOT_RADIUS, PERSISTENT_DOT_PAINT);
        }
        for (NavDot p : navigationDots) {
            if (p.isVisible()) {
                canvas.drawCircle(p.getX(), p.getY(), p.getR(), p.getPaint());
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        for (NavDot p : navigationDots) {
            if (p.getID() == GENERIC_DOT && p.isVisible() && !p.isLocked()) {
                int touchX = (int) event.getX();
                int touchY = (int) event.getY();

                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        updateNavDot(GENERIC_DOT, touchX, touchY);
                        return true;
                    default:
                        return false;
                }
            }
        }
        return false;
    }

    public void setMapBackground(Bitmap bitmap) {
        mapBackground = bitmap;
        invalidate();
    }

    /**
     * Adds a graphical dot to the map representing a users mapBitmap,
     * or the placed mapBitmap for fingerprint capture.
     *
     * @param x pixel coordinate of center of dot horizontally
     * @param y pixel coordinate of center of dot vertically
     */
    public void addNavDot(int ID, int x, int y, int colourResource) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(getResources().getColor(colourResource));
        navigationDots.add(new NavDot(ID, x, y, paint));
        invalidate();
    }

    public void setNavDotRadius(int ID, int r) {
        for (NavDot p : navigationDots) {
            if (p.getID() == ID) {
                p.setR(r);
            }
        }
    }

    /**
     * Allows for the mapBitmap of the 'blue dot' representing the user's mapBitmap to be changed.
     *
     * @param x pixel coordinate of center of dot horizontally
     * @param y pixel coordinate of center of dot vertically
     */
    public void updateNavDot(int ID, int x, int y) {
        Log.d(TAG, "setDotY: newY: " + y);
        if (x <= ORIGIN_IN_X || x > (ORIGIN_IN_X + MAP_WIDTH))
            return;
        if (y <= ORIGIN_IN_Y || y > (ORIGIN_IN_Y + MAP_HEIGHT))
            return;
        for (NavDot p : navigationDots) {
            if (p.getID() == ID) {
                p.setX(x);
                p.setY(y);
            }
        }
        invalidate(); //redraw view
    }

    public int getDotX(int ID) {
        for (NavDot p : navigationDots) {
            if (p.getID() == ID) {
                return p.getX();
            }
        }
        return -1;
    }

    public int getDotY(int ID) {
        for (NavDot p : navigationDots) {
            if (p.getID() == ID) {
                return p.getY();
            }
        }
        return -1;
    }

    public void setDotX(int ID, int newX) {
        if (newX < ORIGIN_IN_X || newX > (ORIGIN_IN_X + MAP_WIDTH))
            return;
        for (NavDot p : navigationDots) {
            if (p.getID() == ID) {
                p.setX(newX);
            }
        }
        invalidate();
    }

    public void setDotY(int ID, int newY) {
        Log.d(TAG, "setDotY: newY: " + newY);
        Log.d(TAG, "setDotY: map height: " + MAP_HEIGHT);
        if (newY < ORIGIN_IN_Y || newY > (ORIGIN_IN_Y + MAP_HEIGHT))
            return;
        for (NavDot p : navigationDots) {
            if (p.getID() == ID) {
                p.setY(newY);
            }
        }
        invalidate();
    }

    public void hideNavDot(int ID) {
        for (NavDot p : navigationDots) {
            if (p.getID() == ID) {
                p.setVisible(false);
            }
        }
        invalidate();
    }

    public void showNavDot(int ID) {
        for (NavDot p : navigationDots) {
            if (p.getID() == ID) {
                p.setVisible(true);
            }
        }
        invalidate();
    }

    public void lockNavDot(int ID) {
        for (NavDot p : navigationDots) {
            if (p.getID() == ID) {
                p.setLocked(true);
            }
        }
    }

    public void unlockNavDot(int ID) {
        for (NavDot p : navigationDots) {
            if (p.getID() == ID) {
                p.setLocked(false);
            }
        }
    }

    /**
     * Adds a graphical dot to the map representing a fingerprinted point.
     * This is so the user knows which locations have already been fingerprinted.
     *
     * @param x pixel coordinate of center of dot horizontally
     * @param y pixel coordinate of center of dot vertically
     */
    public void addPersistentDot(int x, int y) {
        persistentDots.add(new Point(x, y));
        invalidate();
    }

    public void removeAllPeristentDots() {
        persistentDots = new HashSet<>();
        invalidate();
    }

    public int getMapWidth() {
        return MAP_WIDTH;
    }

    public int getMapHeight() {
        return MAP_HEIGHT;
    }

}

