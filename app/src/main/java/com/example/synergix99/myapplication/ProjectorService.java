package com.example.synergix99.myapplication;

/**
 * Created by Synergix 99 on 6/23/2016.
 */

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.WindowManager;

import java.util.concurrent.atomic.AtomicReference;

public class ProjectorService extends Service {
    static final String EXTRA_RESULT_CODE="resultCode";
    static final String EXTRA_RESULT_INTENT="resultIntent";
    static final int VIRT_DISPLAY_FLAGS=
            DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY |
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;
    private MediaProjection projection;
    private VirtualDisplay vdisplay;
    final private HandlerThread handlerThread=new HandlerThread(getClass().getSimpleName(),
            android.os.Process.THREAD_PRIORITY_BACKGROUND);
    private Handler handler;
    private AtomicReference<byte[]> latestPng=new AtomicReference<byte[]>();
    private MediaProjectionManager mgr;
    private WindowManager wmgr;
    private ImageTransmogrifier it;

    @Override
    public void onCreate() {
        super.onCreate();

        mgr=(MediaProjectionManager)getSystemService(MEDIA_PROJECTION_SERVICE);
        wmgr=(WindowManager)getSystemService(WINDOW_SERVICE);

        handlerThread.start();
        handler=new Handler(handlerThread.getLooper());
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public int onStartCommand(Intent i, int flags, int startId) {
        projection=
                mgr.getMediaProjection(i.getIntExtra(EXTRA_RESULT_CODE, -1),
                        (Intent)i.getParcelableExtra(EXTRA_RESULT_INTENT));

        it=new ImageTransmogrifier(this);

        MediaProjection.Callback cb=new MediaProjection.Callback() {
            @Override
            public void onStop() {
                vdisplay.release();
            }
        };

        vdisplay=projection.createVirtualDisplay("andprojector",
                it.getWidth(), it.getHeight(),
                getResources().getDisplayMetrics().densityDpi,
                VIRT_DISPLAY_FLAGS, it.getSurface(), null, handler);
        projection.registerCallback(cb, handler);

        return(START_NOT_STICKY);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onDestroy() {
        projection.stop();

        super.onDestroy();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        ImageTransmogrifier newIt=new ImageTransmogrifier(this);

        if (newIt.getWidth()!=it.getWidth() ||
                newIt.getHeight()!=it.getHeight()) {
            ImageTransmogrifier oldIt=it;

            it=newIt;
            vdisplay.resize(it.getWidth(), it.getHeight(),
                    getResources().getDisplayMetrics().densityDpi);
            vdisplay.setSurface(it.getSurface());

            oldIt.close();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
/*
    @Override
    protected boolean configureRoutes(AsyncHttpServer server) {
        serveWebSockets("/ss", null);

        server.get(getRootPath()+"/screen/.*",
                new ScreenshotRequestCallback());

        return(true);
    }*/



    WindowManager getWindowManager() {
        return(wmgr);
    }

    Handler getHandler() {
        return(handler);
    }

/*    void updateImage(byte[] newPng) {
        latestPng.set(newPng);

        for (WebSocket socket : getWebSockets()) {
            socket.send("screen/"+Long.toString(SystemClock.uptimeMillis()));
        }
    }*/

    /*@Override
    protected void buildForegroundNotification(NotificationCompat.Builder b) {
        Intent iActivity=new Intent(this, MainActivity.class);
        PendingIntent piActivity=PendingIntent.getActivity(this, 0,
                iActivity, 0);

        b.setContentTitle(getString(R.string.app_name))
                .setContentIntent(piActivity)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker(getString(R.string.app_name));
    }*/
/*
    private class ScreenshotRequestCallback
            implements HttpServerRequestCallback {
        @Override
        public void onRequest(AsyncHttpServerRequest request,
                              AsyncHttpServerResponse response) {
            response.setContentType("image/png");

            byte[] png=latestPng.get();
            ByteArrayInputStream bais=new ByteArrayInputStream(png);

            response.sendStream(bais, png.length);
        }
    }*/
}
