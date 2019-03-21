package com.zm.skin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;

import com.zm.skin.skin.SkinFactory;


public class TwoActivity extends AppCompatActivity implements View.OnClickListener {


    private SkinFactory mSkinFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mSkinFactory = new SkinFactory(getDelegate());
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        layoutInflater.setFactory2(mSkinFactory);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSkinFactory.setSkin(getIntent().getStringExtra("path"), this);
    }

    @Override
    public void onClick(View v) {

    }
}
