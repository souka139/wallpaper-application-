package com.soukaina.sweetpapers;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.WallpaperManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class WallpaperPagerActivity extends AppCompatActivity {

    int pos;
    ArrayList<String> allImageList=new ArrayList<>();
    ViewPager viewPager;
    secondPage_fragment secondPage_fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper_pager);
        viewPager=findViewById(R.id.viewPager);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        pos=getIntent().getIntExtra("pos",0);
        allImageList=getIntent().getStringArrayListExtra("list");
        viewPager=findViewById(R.id.viewPager);

        pager_adapter pager_adapter=new pager_adapter(WallpaperPagerActivity.this,allImageList);
        viewPager.setAdapter(pager_adapter);
        viewPager.setCurrentItem(pos);

    }

}