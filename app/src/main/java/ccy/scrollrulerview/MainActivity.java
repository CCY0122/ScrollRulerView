package ccy.scrollrulerview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private ScrollRulerView s2;
    private ScrollRulerView s3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        s2 = (ScrollRulerView) findViewById(R.id.s2);
        s2.setCurrentValue(-2.0f);
        s3 = (ScrollRulerView) findViewById(R.id.s3);
        s3.setEndNum(60);
        s3.setMinGap(60);
        s3.setUnit("km/h");
        s3.setCurrentValue(35);
    }
}
