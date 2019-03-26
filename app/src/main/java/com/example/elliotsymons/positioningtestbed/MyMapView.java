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
import android.preference.Preference;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;

import static com.example.elliotsymons.positioningtestbed.MapViewFragment.GENERIC_DOT;

public class MyMapView extends AppCompatImageView {
    private static final String TAG = "MyMapView";
    private Preferences prefs;

    private Canvas mapCanvas;

    //Map
    private Bitmap mapBackground;
    private Rect displayRect;
    private int dispWidth, dispHeight, MAP_WIDTH, MAP_HEIGHT;

    //Dots on map
    private final int DEFAULT_PERSISTENT_DOT_RADIUS = 10;
    private Paint PERSISTENT_DOT_PAINT;

    private Set<Point> persistentDots;
    private Set<NavDot> navigationDots;

    MapViewFragment.LocationPassListener locationPassListener;


    //Default constructor, calls through
    public MyMapView(Context context) {
        this(context, null);
    }

    public MyMapView(Context context, AttributeSet attributeSet) {
        super(context);
        prefs = Preferences.getInstance(getContext());

        //Import map image resource
        int mapID = prefs.getMapID();
        mapBackground = BitmapFactory.decodeResource(getResources(), mapID);

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
        PERSISTENT_DOT_PAINT = new Paint();
        PERSISTENT_DOT_PAINT.setStyle(Paint.Style.FILL);
        PERSISTENT_DOT_PAINT.setColor(getResources().getColor(R.color.colorPersistentDot));

        navigationDots = new HashSet<>();
        persistentDots = new HashSet<>();

        //make sure the required interfaces are implemented by the parent activity
        try {
            locationPassListener = (MapViewFragment.LocationPassListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement MapViewLocationListener");
        }

    }



    //Also called on startup (as view is inflated from zero size when created).
    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
    }


    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mapCanvas = canvas;
        canvas.drawBitmap(mapBackground, null, displayRect, null);
        for (Point p : persistentDots) {
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

    public void setMapBackground(int mapResourceID) {
        mapBackground = BitmapFactory.decodeResource(getResources(), mapResourceID);
        invalidate();
    }

    /**
     * Adds a graphical dot to the map representing a users location,
     * or the placed location for fingerprint capture.
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
     * Allows for the location of the 'blue dot' representing the user's location to be changed.
     *
     * @param x pixel coordinate of center of dot horizontally
     * @param y pixel coordinate of center of dot vertically
     */
    public void updateNavDot(int ID, int x, int y) {
        for (NavDot p : navigationDots) {
            if (p.getID() == ID) {
                p.setX(x);
                p.setY(y);
            }
        }
        locationPassListener.passLocation(x, y);

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
        for (NavDot p : navigationDots) {
            if (p.getID() == ID) {
                p.setX(newX);
            }
        }
        invalidate();
    }

    public void setDotY(int ID, int newY) {
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

    public int getMapWidth() {
        return MAP_WIDTH;
    }

    public int getMapHeight() {
        return MAP_HEIGHT;
    }

}

