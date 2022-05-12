package com.soukaina.sweetpapers;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CategoryFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    ImageView kawaii_img,anime_img,illustration_img,patterns_img,quotes_img;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle("Categories");

        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_category, container, false);
        kawaii_img=view.findViewById(R.id.img_category1);
        anime_img=view.findViewById(R.id.img_category2);
        illustration_img=view.findViewById(R.id.img_category3);
        patterns_img=view.findViewById(R.id.img_category4);
        quotes_img=view.findViewById(R.id.img_category5);

        kawaii_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),kawaiiActivity.class);
                startActivity(intent);
            }
        });

        anime_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),AnimeActivity.class);
                startActivity(intent);
            }
        });

        illustration_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),IllustrationActivity.class);
                startActivity(intent);
            }
        });

        patterns_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),PatternsActivity.class);
                startActivity(intent);
            }
        });

        quotes_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),QuotesActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}