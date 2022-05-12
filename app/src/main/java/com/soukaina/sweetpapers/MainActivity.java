package com.soukaina.sweetpapers;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.view.Window;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this,R.color.status_bar_color));

        // it means that is the user is already login then we don't want the user to login again and again
        if(firebaseUser!=null){
            finish();
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        }

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationView = findViewById(R.id.nav_menu);
        drawerLayout = findViewById(R.id.drawer);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        if (savedInstanceState==null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new secondPage_fragment()).commit();
            navigationView.setCheckedItem(R.id.menu_home);
        }

        navigationView.setNavigationItemSelectedListener(MainActivity.this);
        UpdateNavHeader();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new secondPage_fragment()).commit();
                break;
            case R.id.menu_categries:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new CategoryFragment()).commit();
                break;
            case R.id.menu_profile:
                startActivity(new Intent(this,ProfleActivity.class));
                break;
            case R.id.menu_Favorites:
                Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_about:
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setMessage("This application build with love by soukaina laaroussi I hope you like it ! ");
                alert.setCancelable(true);
                alert.setTitle("About");

                alert.setPositiveButton(
                        "Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                alert.create().show();
                break;

            case R.id.logout:
                firebaseAuth.signOut();
                finish();
                Intent intent=new Intent(this,loginPageActivity.class);
                startActivity(intent);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void UpdateNavHeader(){
        navigationView = findViewById(R.id.nav_menu);
        View headerView=navigationView.getHeaderView(0);
        TextView navUserName=headerView.findViewById(R.id.nav_user_name);
        TextView navUserEmail=headerView.findViewById(R.id.nav_user_email);
        ImageView navUserPhoto=headerView.findViewById(R.id.nav_profile_img);

        navUserName.setText(firebaseUser.getDisplayName());
        navUserEmail.setText(firebaseUser.getEmail());

        //new we will use Glide to load user image
        //first we need to need to import the library
        if(firebaseUser.getPhotoUrl() !=null){
            Glide.with(this).load(firebaseUser.getPhotoUrl()).into(navUserPhoto);
        }else{
            Glide.with(this).load(R.drawable.avatar1).into(navUserPhoto);
        }
    }
}