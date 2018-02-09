package com.done.sharescreenclient.activity;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.done.sharescreenclient.R;
import com.done.sharescreenclient.fragment.LiveViewFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        LiveViewFragment fragment = new LiveViewFragment();
        transaction.replace(R.id.sample_content_fragment, fragment);
        transaction.commit();
    }
}
