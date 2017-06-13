package com.zwb.touchpullview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.zwb.touchpullview.view.TouchPullView;

public class MainActivity extends AppCompatActivity {
    private TouchPullView tpv;
    private int mMaxHeight = 800;
    private float downY = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tpv = (TouchPullView) findViewById(R.id.tpv);
        findViewById(R.id.activity_main).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float touchY = event.getY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downY = touchY;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float dy = touchY - downY;
                        dy = dy > mMaxHeight ? mMaxHeight : dy;
                        dy = dy < 0 ? 0 : dy;
                        float radio = dy / mMaxHeight;
                        tpv.setmRatio(radio);
                        break;
                }
                return true;
            }
        });
    }
}
