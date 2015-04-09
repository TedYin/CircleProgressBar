package me.tedyin.circleprogressbar;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import me.tedyin.circleprogressbarlib.CircleProgressBar;


public class MainActivity extends Activity {

    CircleProgressBar bar1, bar2;
    TextView text;
    static int current;
    boolean isRestart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bar1 = (CircleProgressBar) findViewById(R.id.bar1);
        bar2 = (CircleProgressBar) findViewById(R.id.bar2);
        text = (TextView) findViewById(R.id.text);
        findViewById(R.id.btn).setOnClickListener(new ClickL());
    }

    void delay() {
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class ClickL implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            current = 0;
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    while (current < 80) {
                        current += 1;
                        bar1.setProgress(current);
                        bar2.setProgress(current);
                        delay();
                    }
                }
            }.start();
        }
    }
}
