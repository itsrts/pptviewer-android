package com.itsrts;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.olivephone.office.TempFileManager;
import com.olivephone.office.powerpoint.DocumentSession;
import com.olivephone.office.powerpoint.DocumentSessionBuilder;
import com.olivephone.office.powerpoint.DocumentSessionStatusListener;
import com.olivephone.office.powerpoint.android.AndroidMessageProvider;
import com.olivephone.office.powerpoint.android.AndroidSystemColorProvider;
import com.olivephone.office.powerpoint.android.AndroidTempFileStorageProvider;
import com.olivephone.office.powerpoint.view.PersentationView;
import com.olivephone.office.powerpoint.view.SlideShowNavigator;
import com.olivephone.office.powerpoint.view.SlideView;

import java.io.File;

public class AppCompactPPTViewer extends RelativeLayout implements DocumentSessionStatusListener, View.OnTouchListener {

    public static final int SLIDE_ID = 23762378;

    LayoutParams params;
    String path;
    Context ctx;
    AppCompatActivity act;
    boolean maintainZoomLevel = false;
    double zoomlevel = 20;
    double zoomfactor = 0.5;
    PPTViewerListener listener;
    boolean isLoaded, pinchZoomEnabled = true, swipeToChangeEnabled = true;
    private DocumentSession session;
    private SlideShowNavigator navitator;
    private PersentationView slide;
    private View progressView;
    private int currentSlideNumber = -1;
    private ScaleGestureDetector mScaleDetector;
    private GestureDetector mGestureDetector;

    public AppCompactPPTViewer(Context ctx, AttributeSet attr) {
        super(ctx, attr);
        this.ctx = ctx;
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(CENTER_IN_PARENT);
        slide = new PersentationView(ctx, attr);
        slide.setId(SLIDE_ID);
        mScaleDetector = new ScaleGestureDetector(ctx, new ScaleListener());
        mGestureDetector = new GestureDetector(ctx, new GestureListener());
        slide.setOnTouchListener(this);
        slide.setVisibility(INVISIBLE);
        this.addView(slide, params);

        progressView = new View(ctx);
        progressView.setBackgroundColor(Color.WHITE);
        this.addView(slide, params);
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public boolean isSwipeToChangeEnabled() {
        return swipeToChangeEnabled;
    }

    public AppCompactPPTViewer setSwipeToChangeEnabled(boolean swipeToChangeEnabled) {
        this.swipeToChangeEnabled = swipeToChangeEnabled;
        return this;
    }

    public boolean isPinchZoomEnabled() {
        return pinchZoomEnabled;
    }

    public AppCompactPPTViewer setPinchZoomEnabled(boolean pinchZoomEnabled) {
        this.pinchZoomEnabled = pinchZoomEnabled;
        return this;
    }

    public double getZoomfactor() {
        return zoomfactor;
    }

    public AppCompactPPTViewer setZoomfactor(double zoomfactor) {
        this.zoomfactor = zoomfactor;
        return this;
    }

    public boolean isMaintainZoomLevel() {
        return maintainZoomLevel;
    }

    public AppCompactPPTViewer setMaintainZoomLevel(boolean maintainZoomLevel) {
        this.maintainZoomLevel = maintainZoomLevel;
        return this;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mScaleDetector.onTouchEvent(event);
        mGestureDetector.onTouchEvent(event);
        return false;
    }

    public AppCompactPPTViewer setPPTViewerListner(PPTViewerListener listner) {
        this.listener = listner;
        return this;
    }

    public int getCurrentSlideNumber() {
        return currentSlideNumber;
    }

    public int getTotalSlides() {
        return this.session != null && this.session.getPPTContext() != null ? this.navitator.getSlideCount() : -1;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void loadPPT(AppCompatActivity act, String path) {
        this.setPath(path);
        this.act = act;
        try {
            AndroidMessageProvider e = new AndroidMessageProvider(this.ctx);
            TempFileManager tmpFileManager = new TempFileManager(new AndroidTempFileStorageProvider(this.ctx));
            AndroidSystemColorProvider sysColorProvider = new AndroidSystemColorProvider();
            this.session = (new DocumentSessionBuilder(new File(this.path))).setMessageProvider(e).setTempFileManager(tmpFileManager).setSystemColorProvider(sysColorProvider).setSessionStatusListener(this).build();
            this.session.startSession();
        } catch (Exception var5) {
            var5.printStackTrace();
        }
    }

    int dpToPx(int dp) {
        return (int) ((float) dp * Resources.getSystem().getDisplayMetrics().density);
    }

    int pxToDp(int px) {
        return (int) ((float) px / Resources.getSystem().getDisplayMetrics().density);
    }

    public void onDocumentException(Exception e) {
        if (listener != null) {
            listener.onSlideLoadError(e);
        }
    }

    public void onDocumentReady() {
        this.act.runOnUiThread(new Runnable() {
            public void run() {
                slide.setVisibility(VISIBLE);
                isLoaded = true;
                AppCompactPPTViewer.this.navitator = new SlideShowNavigator(AppCompactPPTViewer.this.session.getPPTContext());
                currentSlideNumber = navitator.getFirstSlideNumber() - 1;
                progressView.setVisibility(GONE);
                AppCompactPPTViewer.this.next();
                if (listener != null) {
                    listener.onSlidesLoaded();
                }
            }
        });
    }

    public void onSessionEnded() {
    }

    public void onSessionStarted() {
    }

    public void zoom(double zoomlevel) {
        if (zoomlevel < 5) {
            return;
        }
        if (this.slide != null) {
            this.slide.notifyScale(zoomlevel / 100.0D);
        }
    }

    public void zoomIn() {
        zoomlevel += zoomfactor;
        zoom(zoomlevel);
    }

    public void zoomOut() {
        zoomlevel -= zoomfactor;
        zoom(zoomlevel);
    }

    public boolean navigateTo(int slideNumber) {
        if (slideNumber < navitator.getFirstSlideNumber() || slideNumber > navitator.getFirstSlideNumber() + navitator.getSlideCount()) {
            return false;
        }
        SlideView slideShow = this.navitator.navigateToSlide(this.slide.getGraphicsContext(), slideNumber);
//        slide.notifyScale(zoomlevel);
        this.slide.setContentView(slideShow);
        if (listener != null) {
            listener.onSlideChanged(slideNumber);
        }
        return true;
    }

    public boolean next() {
        if (this.navitator != null) {
            if (this.navitator.getFirstSlideNumber() + this.navitator.getSlideCount() - 1 > this.currentSlideNumber) {
                return this.navigateTo(++this.currentSlideNumber);
            }
        }
        return false;
    }

    public boolean previous() {
        if (this.navitator != null) {
            if (this.navitator.getFirstSlideNumber() < this.currentSlideNumber) {
                return this.navigateTo(--this.currentSlideNumber);
            }
        }
        return false;
    }

    public interface PPTViewerListener {

        void onSlideTapped();

        void onSlidesLoaded();

        void onSlideLoadError(Exception e);

        void onSlideChanged(int position);
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_MIN_DISTANCE = 120;
        private static final int SWIPE_MAX_OFF_PATH = 250;
        private static final int SWIPE_THRESHOLD_VELOCITY = 300;

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (listener != null) {
                listener.onSlideTapped();
            }
            return false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (!isSwipeToChangeEnabled()) {
                return false;
            }
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) {
                    return false;
                }
                // right to left swipe
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    next();
                }
                // left to right swipe
                else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    previous();
                }
            } catch (Exception e) {
                return false;
            }
            return true;
        }
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        float d = 1;

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            d = 0;
            return isPinchZoomEnabled();
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            if (!isPinchZoomEnabled()) {
                return false;
            }
            double deltaScale = detector.getScaleFactor();
            float x = detector.getFocusX();
            float y = detector.getFocusY();
            if (deltaScale > 1) {
                Log.e("scale : ", "zoomin");
                zoomIn();
            }
            if (deltaScale < 1) {
                Log.e("scale : ", "zoomout");
                zoomOut();
            }
//            Log.e("scale", deltaScale + " : " + x + " : " + a);
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            super.onScaleEnd(detector);
        }
    }
}
