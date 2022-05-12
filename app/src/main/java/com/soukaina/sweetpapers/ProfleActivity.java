package com.soukaina.sweetpapers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ProfleActivity extends AppCompatActivity {

    private ImageView profile_img_info;
    private Button update_profile;
    private TextInputEditText username;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private StorageReference storageReference;
    Uri pickedImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profle);
        getSupportActionBar().setTitle("Profile");

        profile_img_info=findViewById(R.id.img_profile);
        update_profile=findViewById(R.id.update_profile);
        username=findViewById(R.id.userName_update_profile);
        progressBar=findViewById(R.id.progressBar_update_profile);
        storageReference=FirebaseStorage.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();

        ShowUserInfo();
        username.setSelection(firebaseUser.getDisplayName().length());



        update_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username_edit=username.getText().toString();
                if(username_edit.isEmpty()){
                    Toast.makeText(ProfleActivity.this, "username required !", Toast.LENGTH_SHORT).show();
                }else{
                    update_profile.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    if (pickedImg !=null){
                        updateUserInfo(pickedImg,mAuth.getCurrentUser());
                    }else{
                        updateUserInfoWithoutPhoto(username_edit,mAuth.getCurrentUser());
                    }

                }

            }
        });

        // update user profile image
        profile_img_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               OpenGallery();
            }
        });
    }

    @SuppressWarnings("deprecation")
    private void OpenGallery() {
        Intent openGallery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(openGallery,1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1000){
            if(resultCode== Activity.RESULT_OK){
                pickedImg=data.getData();
                profile_img_info.setImageURI(pickedImg);

            }
        }
    }

    private void UploadImageToFirebase(Uri pickedImg) {
        //upload image to firebase storage
        final StorageReference fileRef=FirebaseStorage.getInstance().getReference().child("user_photos/"+mAuth.getCurrentUser().getUid()+"/profile");
        fileRef.putFile(pickedImg).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getApplicationContext()).load(firebaseUser.getPhotoUrl()).into(profile_img_info);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Something went wrong !", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void updateUserInfoWithoutPhoto(String usernameinput,FirebaseUser currentUser) {
        String DISPLAY_NAME=username.getText().toString();
        UserProfileChangeRequest request= new UserProfileChangeRequest.Builder()
                .setDisplayName(DISPLAY_NAME)
                .build();
        firebaseUser.updateProfile(request)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        update_profile.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(ProfleActivity.this, "Successfully Updated profile", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        update_profile.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(ProfleActivity.this, "Something went wrong !", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUserInfo(Uri pickedImg, FirebaseUser currentUser) {
        String DISPLAY_NAME=username.getText().toString();
        UserProfileChangeRequest request= new UserProfileChangeRequest.Builder()
                .setDisplayName(DISPLAY_NAME)
                .setPhotoUri(pickedImg)
                .build();
        UploadImageToFirebase(pickedImg);
        firebaseUser.updateProfile(request)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        update_profile.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(ProfleActivity.this, "Successfully Updated profile", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        update_profile.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(ProfleActivity.this, "Something went wrong !", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void ShowUserInfo() {
        //new we will use Glide to load user image
        //first we need to need to import the library
        username.setText(firebaseUser.getDisplayName());
        progressBar.setVisibility(View.GONE);
        if(firebaseUser.getPhotoUrl() !=null){
            Glide.with(this).load(firebaseUser.getPhotoUrl()).into(profile_img_info);
        }
    }

    @Override
    public void onBackPressed() {
       Intent intent=new Intent(ProfleActivity.this,MainActivity.class);
       startActivity(intent);
       finish();

    }


}