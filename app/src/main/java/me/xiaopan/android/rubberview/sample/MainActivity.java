package me.xiaopan.android.rubberview.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import me.xiaopan.android.rubberview.R;
import me.xiaopan.android.widget.RubberView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final RubberView rubberView = (RubberView) findViewById(R.id.rubberView_main);
        rubberView.enableAcrossMonitor(findViewById(R.id.text_main), new RubberView.OnAcrossHintViewListener() {
            private boolean across;
            @Override
            public void onAcrossHintView(View hintView) {
                if(!across){
                    across = true;
                    rubberView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    });
                }
            }
        });
    }
}
