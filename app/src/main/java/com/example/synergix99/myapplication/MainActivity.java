package com.example.synergix99.myapplication;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
    private static final int REQUEST_SCREENSHOT=59706;
   // private MenuItem start, stop;
    private MediaProjectionManager mgr;
    boolean isRunning = false;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        /*Window window=getWindow();

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(
                getResources().getColor(R.color.colorPrimary));*/

        mgr=(MediaProjectionManager)getSystemService(MEDIA_PROJECTION_SERVICE);

        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isRunning){
                    button.setText("Stop");
                    startActivityForResult(mgr.createScreenCaptureIntent(),
                            REQUEST_SCREENSHOT);
                } else {
                    button.setText("Start");
                    stopService(new Intent(MainActivity.this, ProjectorService.class));
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

       // EventBus.getDefault().registerSticky(this);
    }

    @Override
    protected void onPause() {
        //EventBus.getDefault().unregister(this);

        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       /* getMenuInflater().inflate(R.menu.actions, menu);

        start=menu.findItem(R.id.start);
        stop=menu.findItem(R.id.stop);

        WebServerService.ServerStartedEvent event=
                EventBus.getDefault().getStickyEvent(WebServerService.ServerStartedEvent.class);

        if (event!=null) {
            handleStartEvent(event);
        }*/

        return(super.onCreateOptionsMenu(menu));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       /* if (item.getItemId()==R.id.start) {
            startActivityForResult(mgr.createScreenCaptureIntent(),
                    REQUEST_SCREENSHOT);
        }
        else {
            stopService(new Intent(this, ProjectorService.class));
        }
*/
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode==REQUEST_SCREENSHOT) {
            if (resultCode==RESULT_OK) {
                Intent i=
                        new Intent(this, ProjectorService.class)
                                .putExtra(ProjectorService.EXTRA_RESULT_CODE,
                                        resultCode)
                                .putExtra(ProjectorService.EXTRA_RESULT_INTENT,
                                        data);
                startService(i);
                finish();
                overridePendingTransition(R.anim.zoom_out_left_animator,R.anim.zoom_out_left_animator);
            }
        }
    }

    /*public void onEventMainThread(WebServerService.ServerStartedEvent event) {
        if (start!=null) {
            handleStartEvent(event);
        }
    }

    public void onEventMainThread(WebServerService.ServerStoppedEvent event) {
        if (start!=null) {
            start.setVisible(true);
            stop.setVisible(false);
            setListAdapter(null);
        }
    }

    private void handleStartEvent(WebServerService.ServerStartedEvent event) {
        start.setVisible(false);
        stop.setVisible(true);

        setListAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, event.getUrls()));
    }*/
}
