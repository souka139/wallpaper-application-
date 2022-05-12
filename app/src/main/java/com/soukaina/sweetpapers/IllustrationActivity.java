package com.soukaina.sweetpapers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class IllustrationActivity extends AppCompatActivity {

    RecyclerView rcvMain;
    ArrayList<String> imgArrayList;
    DatabaseReference reference;
    ProgressBar progressBar;
    FloatingActionButton fab_top;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_imgs_page);
        getSupportActionBar().setTitle("Illustration wallpapers");

        reference= FirebaseDatabase.getInstance().getReference().child("illustration_imgs");
        rcvMain=findViewById(R.id.recyclerViewMain_imgs);
        progressBar=findViewById(R.id.progressbar);
        fab_top=findViewById(R.id.fab_back_toTop);
        fab_top.setSize(FloatingActionButton.SIZE_MINI);


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.GONE);
                imgArrayList=new ArrayList<>();
                for (DataSnapshot shot : snapshot.getChildren()){

                    String data=shot.getValue().toString();
                    imgArrayList.add(data);

                }
                rcvMain.setLayoutManager(new GridLayoutManager(IllustrationActivity.this,3));
                RecyclerViewAdapter_illustration recyclerViewAdapter_illustration=new RecyclerViewAdapter_illustration(IllustrationActivity.this,imgArrayList);
                rcvMain.setAdapter(recyclerViewAdapter_illustration);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
            }
        });

        fab_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rcvMain.smoothScrollToPosition(0);

            }
        });

        rcvMain.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if(dy>0){ //scrolling down
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            fab_top.setVisibility(View.GONE);
                        }
                    },3000);
                }else if(dy<0){ //scrolling up
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            fab_top.setVisibility(View.VISIBLE);
                        }
                    },3000); //delay of 3sc before hiding the fab
                }
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if(newState==RecyclerView.SCROLL_STATE_IDLE){ // No scrolling
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //fab_top.setVisibility(View.GONE);
                        }
                    },3000);
                }
            }
        });
    }
}