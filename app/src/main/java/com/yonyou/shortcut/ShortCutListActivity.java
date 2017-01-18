package com.yonyou.shortcut;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

public class ShortCutListActivity extends AppCompatActivity {

    private FrameLayout mFrameLayout;
    private FloatingActionButton mFloatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_short_cut_list);
        getSupportFragmentManager().beginTransaction().add(R.id.shortcut, new ActionFragment()).commit();
        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ShortCutListActivity.this, ShortCutAddActivity.class));
            }
        });
    }
}
