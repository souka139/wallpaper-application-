package com.soukaina.sweetpapers;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter_quotes extends RecyclerView.Adapter<RecyclerViewAdapter_quotes.myClass> {
    QuotesActivity quotesActivity;
    ArrayList<String> imgArrayList=new ArrayList<>();

    public RecyclerViewAdapter_quotes(QuotesActivity quotesActivity, ArrayList<String> imgArrayList) {
        this.quotesActivity = quotesActivity;
        this.imgArrayList = imgArrayList;
    }


    @NonNull
    @Override
    public myClass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_images_item,parent,false);
        return new myClass(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myClass holder, int position) {
        Glide.with(quotesActivity).load(imgArrayList.get(position)).into(holder.imageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(quotesActivity,WallpaperPagerActivity.class);
                intent.putExtra("list",imgArrayList);
                intent.putExtra("pos",position);
                quotesActivity.startActivity(intent);
            }
        });
    }



    @Override
    public int getItemCount() {
        return imgArrayList.size();
    }

    class myClass extends  RecyclerView.ViewHolder{

        ImageView imageView;
        public myClass(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.img);
        }
    }
}
