package com.example.lenovo.drawer;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;


/**
 * Created by 郭攀峰 on 2015/10/31.
 */
public class VerticalDrawerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verticaldrawer);

        findViewById(R.id.click).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(VerticalDrawerActivity.this, "clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
