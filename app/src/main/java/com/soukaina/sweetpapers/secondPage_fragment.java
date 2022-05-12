package com.soukaina.sweetpapers;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

public class secondPage_fragment extends Fragment {

    // TODO: Rename and change types of parameters
    RecyclerView rcvMain;
    ArrayList<String> imgArrayList;
    DatabaseReference reference;
    ProgressBar progressBar;
    RecyclerViewAdapter adapter;
    FloatingActionButton fab_top;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_second_page_fragment, container, false);
        reference= FirebaseDatabase.getInstance().getReference().child("images");
        rcvMain=view.findViewById(R.id.recyclerViewMain);
        progressBar=view.findViewById(R.id.progressbar);
        fab_top=view.findViewById(R.id.fab_back_toTop);

        getActivity().setTitle("All Wallpapers");
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
                rcvMain.setLayoutManager(new GridLayoutManager(getContext(),3));
                RecyclerViewAdapter recyclerViewAdapter=new RecyclerViewAdapter(secondPage_fragment.this,imgArrayList);
                rcvMain.setAdapter(recyclerViewAdapter);
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

        return view;
    }

}