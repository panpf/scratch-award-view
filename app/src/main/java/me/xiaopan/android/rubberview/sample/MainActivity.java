package me.xiaopan.android.rubberview.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import me.xiaopan.android.rubberview.R;
import me.xiaopan.android.rubberview.RubberView;

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
                            Toast.makeText(getBaseContext(), "点击了", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
