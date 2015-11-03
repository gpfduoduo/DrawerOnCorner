package com.example.lenovo.drawer;


import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.lenovo.drawerlibrary.DrawerLayout;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;


/**
 * Created by 郭攀峰 on 2015/10/31.
 */
public class VerticalDrawerActivity extends AppCompatActivity
{

    private static final String tag = VerticalDrawerActivity.class.getSimpleName();

    private DrawerLayout mDrawerLayout;
    private LinearLayout mNumberLayout;
    private LinearLayout mDrawerContent;
    private int mTranslationY = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d(tag, "activity onCreate function");

        setContentView(R.layout.activity_verticaldrawer);

        findViewById(R.id.click).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(VerticalDrawerActivity.this, "clicked", Toast.LENGTH_SHORT)
                        .show();
            }
        });

        mNumberLayout = (LinearLayout) findViewById(R.id.number_layout);

        mDrawerContent = (LinearLayout) findViewById(R.id.drawerContent);
        mDrawerContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(tag, "drawer content click");
            }
        });
        Button mDelBtn = (Button) findViewById(R.id.del_click);
        mDelBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d(tag, "clicked delete button");
            }
        });

        fillListView((ListView) findViewById(R.id.list_view));

        mDrawerLayout = (DrawerLayout) findViewById(R.id.dial_drawer);
        mDrawerLayout.setInitialState(DrawerLayout.State.Open); //set drawer initial state: open or close
        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener()
        {
            @Override
            public void drawerOpened()
            {
                //showNumberView();
            }

            @Override
            public void drawerClosed()
            {
                //hideNumberView();
            }
        });
    }

    private void fillListView(ListView view)
    {
        List<String> list = new ArrayList<String>(100);
        for (int i = 0; i < 100; i++)
        {
            list.add("Item " + i);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
            android.R.layout.simple_list_item_1, list);
        view.setAdapter(adapter);

        view.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                    long id)
            {
                Log.d(tag,"ListView item clicked");
            }
        });
    }

    private void showNumberView()
    {
        ViewHelper.setAlpha(mNumberLayout, 0);
        ViewHelper.setTranslationY(mNumberLayout,
            mTranslationY - mNumberLayout.getHeight());
        ViewPropertyAnimator.animate(mNumberLayout).alpha(1f).translationY(mTranslationY)
                .setInterpolator(new DecelerateInterpolator()).setDuration(300)
                .setStartDelay(0).start();
    }

    private void hideNumberView()
    {
        ViewPropertyAnimator.animate(mNumberLayout).alpha(0f)
                .translationY(-mNumberLayout.getHeight() + mTranslationY)
                .setInterpolator(new AccelerateInterpolator()).setDuration(300).start();

    }
}
