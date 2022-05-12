package com.soukaina.sweetpapers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class signUpPageActivity extends AppCompatActivity {

    private Button signUpBtn;
    private EditText username,email,password;
    private TextView backToLogin;
    private ImageView imgProfile;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private ProgressBar progressBar;

    static int PReqCode=1;
    static int REQUESCODE=1;
    Uri pickedImg;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);

        signUpBtn=findViewById(R.id.signUpBtn);
        username=findViewById(R.id.userName);
        email=findViewById(R.id.editEmailsignUp);
        password=findViewById(R.id.editPasswordSignUp);
        backToLogin=findViewById(R.id.backToLogin);
        imgProfile=findViewById(R.id.profileImg);
        progressBar=findViewById(R.id.progressBarLoading);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        //databaseReference=database.getReference(USER);

        progressBar.setVisibility(View.INVISIBLE);
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >=22){
                   checkAndRequestForPermission();
                }else{
                    openGallery();
                }
            }
        });


        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpBtn.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                final String usernameinput=username.getText().toString();
                final String emailinput=email.getText().toString();
                final String pass=password.getText().toString();
                if (usernameinput.isEmpty() || emailinput.isEmpty() || pass.isEmpty()){
                    //we need to display an error message
                    showMessage("Please fill the blanks");
                    signUpBtn.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }else{
                    // everything is ok now we can start creating the user account
                    CreateUserAccount(usernameinput,emailinput,pass);
                }
            }
        });

        getWindow().setStatusBarColor(ContextCompat.getColor(signUpPageActivity.this,R.color.black));
        backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(signUpPageActivity.this,loginPageActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    //create user account methods
    private void CreateUserAccount(String usernameinput, String emailinput, String pass) {

        firebaseAuth.createUserWithEmailAndPassword(emailinput,pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //user account created successfully
                    //showMessage("Account created successfully");
                    //after we created user account we need to update his profile and name
                    //now we check if the picked image is null or not
                    if(pickedImg !=null){
                        updateUserInfo(usernameinput,pickedImg,firebaseAuth.getCurrentUser());
                    }else{
                        updateUserInfoWithoutPhoto(usernameinput,firebaseAuth.getCurrentUser());
                    }
                }else{
                    //account creation failed
                    showMessage("something goes wrong! try again"+task.getException().getMessage().toString());
                    signUpBtn.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    //update user photo and name
    private void updateUserInfo(String usernameinput, Uri pickedImg, FirebaseUser currentUser) {
        //first we need to upload user photo to firebase storage and get url
        StorageReference mStorage= FirebaseStorage.getInstance().getReference().child("user_photos/"+firebaseAuth.getCurrentUser().getUid()+"/profile");
        mStorage.putFile(pickedImg).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //image uploaded successfully
                //now we can get our image url
                mStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //uri contain user image url
                        UserProfileChangeRequest profileUpdate=new UserProfileChangeRequest.Builder()
                                .setDisplayName(usernameinput)
                                .setPhotoUri(uri)
                                .build();

                        currentUser.updateProfile(profileUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            //user info updated successfully
                                            SendEmailVerification();
                                            Toast.makeText(signUpPageActivity.this, "Register Complete! verify your email and log in", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                        });
                    }
                });
            }
        });
    }

    private void updateUserInfoWithoutPhoto(String usernameinput,FirebaseUser currentUser) {
        //first we need to upload user photo to firebase storage and get url
        UserProfileChangeRequest profileUpdate=new UserProfileChangeRequest.Builder()
                .setDisplayName(usernameinput)
                .build();

        currentUser.updateProfile(profileUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //user info updated successfully
                    SendEmailVerification();
                    Toast.makeText(signUpPageActivity.this, "Register Complete! verify your email and log in", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    //simple method to show toast message
    private void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @SuppressWarnings("deprecation")
    private void openGallery() {
        //TODO: open gallery intent and wait for user to picked image
        Intent galleryIntent=new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQUESCODE);
    }


    private void checkAndRequestForPermission() {
        if(ContextCompat.checkSelfPermission(signUpPageActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(signUpPageActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText(this, "Please accept for required permission", Toast.LENGTH_SHORT).show();
            }else{
                ActivityCompat.requestPermissions(signUpPageActivity.this,
                        new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE},
                       PReqCode);
            }
        }
        else {
            openGallery();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK && requestCode==REQUESCODE && data !=null){
            //the user has successfully picked an image
            //we need to save its reference to a Uri variables
            pickedImg=data.getData();
            imgProfile.setImageURI(pickedImg);

        }
    }

    //Send Email verification
    private void SendEmailVerification(){
        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
        if(firebaseUser!=null){
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    //Toast.makeText(signUpPageActivity.this, "Verification email is sent, Verify and log in again", Toast.LENGTH_LONG).show();
                    firebaseAuth.signOut();
                    finish();
                    startActivity(new Intent(signUpPageActivity.this,loginPageActivity.class));
                }
            });
        }
        else{
            Toast.makeText(this, "Failed to send verification email, try again", Toast.LENGTH_SHORT).show();
        }
    }
}

