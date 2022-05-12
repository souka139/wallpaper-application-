package com.soukaina.sweetpapers;

import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

public class pager_adapter extends PagerAdapter {
    WallpaperPagerActivity wallpaperPagerActivity;
    ArrayList<String> allImageList;
    BottomSheetDialog sheetDialog;
    ImageView home_screen,lock_screen,home_lock_screen;
    boolean clicked=false; //by default it is false

    public pager_adapter(WallpaperPagerActivity wallpaperPagerActivity, ArrayList<String> allImageList) {
        this.allImageList=allImageList;
        this.wallpaperPagerActivity=wallpaperPagerActivity;
    }

    @Override
    public int getCount() {
        return allImageList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view= LayoutInflater.from(wallpaperPagerActivity).inflate(R.layout.pager_adapter_item,container,false);
        ImageView imageView;
        FloatingActionButton shareBtn,expandBtn,setWallpaperBtn,favoritesBtn;
        Animation rotateOpen, rotateClose, fromBottom, toBottom;

        imageView=view.findViewById(R.id.fullImg);
        shareBtn=view.findViewById(R.id.shareBtnn);
        expandBtn=view.findViewById(R.id.expandBtn);
        setWallpaperBtn=view.findViewById(R.id.setWallpaper);
        favoritesBtn=view.findViewById(R.id.setToFavorites);

        // animations
        rotateOpen= AnimationUtils.loadAnimation(wallpaperPagerActivity,R.anim.rotate_open_anime);
        rotateClose=AnimationUtils.loadAnimation(wallpaperPagerActivity,R.anim.rotate_close_anime);
        fromBottom=AnimationUtils.loadAnimation(wallpaperPagerActivity,R.anim.from_bottom_anime);
        toBottom=AnimationUtils.loadAnimation(wallpaperPagerActivity,R.anim.to_bottom_anime);

        //set the click listener on the Main fab
        expandBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddbtnClicked();
            }

            private void onAddbtnClicked() {
                setVisibility(clicked);
                setAnimation(clicked);
                setClickable(clicked);
                if (!clicked){
                    clicked=true;
                }else{
                    clicked=false;
                }
            }

            private void setVisibility(boolean clicked) {
                if (!clicked){
                    shareBtn.setVisibility(View.VISIBLE);
                    setWallpaperBtn.setVisibility(View.VISIBLE);
                    favoritesBtn.setVisibility(View.VISIBLE);
                }else{
                    shareBtn.setVisibility(View.INVISIBLE);
                    setWallpaperBtn.setVisibility(View.INVISIBLE);
                    favoritesBtn.setVisibility(View.INVISIBLE);
                }
            }

            private void setAnimation(boolean clicked) {
                if (!clicked){
                    shareBtn.startAnimation(fromBottom);
                    expandBtn.startAnimation(rotateOpen);
                    setWallpaperBtn.startAnimation(fromBottom);
                    favoritesBtn.startAnimation(fromBottom);
                }else{
                    shareBtn.startAnimation(toBottom);
                    expandBtn.startAnimation(rotateClose);
                    setWallpaperBtn.startAnimation(toBottom);
                    favoritesBtn.startAnimation(toBottom);
                }
            }

            private void setClickable(boolean clicked){
                if(!clicked){
                    shareBtn.setClickable(true);
                    setWallpaperBtn.setClickable(true);
                    favoritesBtn.setClickable(true);

                }else{
                    shareBtn.setClickable(false);
                    setWallpaperBtn.setClickable(false);
                    favoritesBtn.setClickable(false);
                }
            }
        });


        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new shareImage().execute(allImageList.get(position));
            }
        });


        setWallpaperBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sheetDialog=new BottomSheetDialog(wallpaperPagerActivity,R.style.BottomSheetStyle);
                View view=LayoutInflater.from(wallpaperPagerActivity).inflate(R.layout.bottom_sheet_dialog,(LinearLayout) sheetDialog.findViewById(R.id.sheet));
                sheetDialog.setContentView(view);
                sheetDialog.show();

                //click listener to buttons inside bottom sheet dialog
                home_screen=view.findViewById(R.id.homeScreen);
                lock_screen=view.findViewById(R.id.lockScreen);
                home_lock_screen=view.findViewById(R.id.homeLockScreen);
                home_screen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new setWallPaperHomeScreen().execute(allImageList.get(position));
                        sheetDialog.hide();
                    }
                });
                lock_screen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new setWallPaperLockScreen().execute(allImageList.get(position));
                        sheetDialog.hide();
                    }
                });
                home_lock_screen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new setWallpaperHomeLockScreen().execute(allImageList.get(position));
                        sheetDialog.hide();
                    }
                });

            }
        });

        Glide.with(wallpaperPagerActivity).load(allImageList.get(position)).into(imageView);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    /*class DownloadImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            Toast.makeText(wallpaperPagerActivity, "Wallpaper download Successfully", Toast.LENGTH_SHORT).show();

        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {

                URL url = new URL(urls[0]);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.connect();

                InputStream inputStream = connection.getInputStream();

                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);


                Bitmap icon = myBitmap;

                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                icon.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                int time = (int) (System.currentTimeMillis());
                File f = new File(Environment.getExternalStorageDirectory() + "/SweetPapers/" + time + "myimage" + ".jpg");
                try {
                    f.createNewFile();
                    FileOutputStream fo = new FileOutputStream(f);
                    fo.write(bytes.toByteArray());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(wallpaperPagerActivity, "Download successfully", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {

                e.printStackTrace();

            }
            return null;
        }
    }*/

    class shareImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);


                Bitmap b = myBitmap;
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/jpeg");
                share.setType("text/html");
                share.putExtra(Intent.EXTRA_TEXT, "img");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                b.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                String path = MediaStore.Images.Media.insertImage(wallpaperPagerActivity.getContentResolver(),
                        b, "Title", null);
                Uri imageUri = Uri.parse(path);
                share.putExtra(Intent.EXTRA_STREAM, imageUri);
                wallpaperPagerActivity.startActivity(Intent.createChooser(share, "Select"));


            } catch (Exception e) {

                e.printStackTrace();

            }

            return null;
        }
    }

    class setWallPaperHomeScreen extends AsyncTask<String, Void, Bitmap> {

        Random random;
        int var;

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            Toast.makeText(wallpaperPagerActivity, "Wallpaper applied to home screen", Toast.LENGTH_SHORT).show();

        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Bitmap doInBackground(String... urls) {

            random = new Random();
            var = random.nextInt(10000);

            try {

                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);
                WallpaperManager myWallpaperManager
                        = WallpaperManager.getInstance(wallpaperPagerActivity.getApplicationContext());
                try {
                    myWallpaperManager.setBitmap(myBitmap,null,true,WallpaperManager.FLAG_SYSTEM);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


            } catch (Exception e) {

                e.printStackTrace();

            }
            return null;
        }
    }

    class setWallPaperLockScreen extends AsyncTask<String, Void, Bitmap> {

        Random random;
        int var;

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            Toast.makeText(wallpaperPagerActivity, "Wallpaper applied to lock screen", Toast.LENGTH_SHORT).show();

        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Bitmap doInBackground(String... urls) {

            random = new Random();
            var = random.nextInt(10000);

            try {

                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);
                WallpaperManager myWallpaperManager
                        = WallpaperManager.getInstance(wallpaperPagerActivity.getApplicationContext());
                try {
                    myWallpaperManager.setBitmap(myBitmap,null,true,WallpaperManager.FLAG_LOCK);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


            } catch (Exception e) {

                e.printStackTrace();

            }
            return null;
        }
    }

    class setWallpaperHomeLockScreen extends AsyncTask<String, Void, Bitmap> {

        Random random;
        int var;

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            Toast.makeText(wallpaperPagerActivity, "Wallpaper applied to home and lock screens", Toast.LENGTH_SHORT).show();

        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Bitmap doInBackground(String... urls) {

            random = new Random();
            var = random.nextInt(10000);

            try {

                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);
                WallpaperManager myWallpaperManager
                        = WallpaperManager.getInstance(wallpaperPagerActivity.getApplicationContext());
                try {
                    myWallpaperManager.setBitmap(myBitmap);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


            } catch (Exception e) {

                e.printStackTrace();

            }
            return null;
        }
    }
}
