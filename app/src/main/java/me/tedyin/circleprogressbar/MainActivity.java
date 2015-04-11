package me.tedyin.circleprogressbar;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import me.tedyin.circleprogressbarlib.CircleProgressBar;


public class MainActivity extends Activity {

    CircleProgressBar bar1, bar2, bar3,bar4;
    static int current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bar1 = (CircleProgressBar) findViewById(R.id.bar1);
        bar2 = (CircleProgressBar) findViewById(R.id.bar2);
        bar3 = (CircleProgressBar) findViewById(R.id.bar3);
        bar4 = (CircleProgressBar) findViewById(R.id.bar4);

        bar2.setColorScheme(Color.GREEN, Color.YELLOW, Color.RED);
        bar4.setColorScheme(Color.GREEN, Color.YELLOW, Color.RED);

        bar2.setLoadingCallBack(new CircleProgressBar.LoadingCallBack() {
            @Override
            public void loadingComplete(View v) {
                Toast.makeText(MainActivity.this, "Loading Complete ", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.btn).setOnClickListener(new ClickL());
    }

    void delay() {
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class ClickL implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            current = 0;
            bar2.requestLayout();
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    while (current <= 100) {
                        current++;
                        bar1.setProgress(current);
                        bar2.setProgress(current);
                        bar3.setProgress(current);
                        bar4.setProgress(current);
                        delay();
                    }
                }
            }.start();
        }
    }
}
