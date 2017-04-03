package com.jayavijayjayavelu.stockwatch;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by jayavijayjayavelu on 3/22/17.
 */

public class DialogActivity extends Activity {
    TextView textView1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_activity);
        Bundle extras = getIntent().getExtras();
        if(extras==null){

        }else{

            setTitle(extras.getString("header"));
            textView1 =(TextView) findViewById(R.id.dialog1);
            textView1.setText(extras.getString("body"));
            textView1.setPadding(40,0,0,20);
            textView1.setTextSize(15);
            textView1.setTextColor(Color.BLACK);
        }
    }
}
