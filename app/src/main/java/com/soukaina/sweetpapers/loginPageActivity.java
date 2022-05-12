package com.soukaina.sweetpapers;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class loginPageActivity extends AppCompatActivity {

    TextView goToSignUp,forgotPassword;
    Button login;
    EditText email,password;
    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBarlogin;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        getWindow().setStatusBarColor(ContextCompat.getColor(loginPageActivity.this,R.color.black));

        goToSignUp=findViewById(R.id.goToSignUp);
        forgotPassword=findViewById(R.id.forgotpsw);
        email=findViewById(R.id.editEmail);
        password=findViewById(R.id.editPassword);
        login=findViewById(R.id.loginbtn);
        progressBarlogin=findViewById(R.id.progressBarLogin);
        firebaseAuth=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
        // it means that is the user is already login then we don't want the user to login again and again
        if(firebaseUser!=null){
            finish();
            startActivity(new Intent(loginPageActivity.this,MainActivity.class));
        }

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(loginPageActivity.this,forgotPasswordActivity.class);
                startActivity(intent);
                finish();
            }
        });

        goToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(loginPageActivity.this,signUpPageActivity.class);
                startActivity(intent);
                finish();
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBarlogin.setVisibility(View.VISIBLE);
                login.setVisibility(View.INVISIBLE);
                String mail=email.getText().toString().trim();
                String passw=password.getText().toString().trim();
                if(mail.isEmpty() || passw.isEmpty()){
                    progressBarlogin.setVisibility(View.INVISIBLE);
                    login.setVisibility(View.VISIBLE);
                    Toast.makeText(loginPageActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                }else{
                    // login the user
                    progressBarlogin.setVisibility(View.VISIBLE);
                    login.setVisibility(View.INVISIBLE);

                    firebaseAuth.signInWithEmailAndPassword(mail,passw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                checkMailVerification();

                            }else{
                                progressBarlogin.setVisibility(View.INVISIBLE);
                                login.setVisibility(View.VISIBLE);
                                Toast.makeText(loginPageActivity.this, "Account does not Exist", Toast.LENGTH_SHORT).show();
                            }
                        }

                        private void checkMailVerification() {
                            FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();

                            if (firebaseUser.isEmailVerified()==true){
                                Toast.makeText(loginPageActivity.this, "Logged In", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(loginPageActivity.this,MainActivity.class));
                            }else{
                                progressBarlogin.setVisibility(View.INVISIBLE);
                                login.setVisibility(View.VISIBLE);
                                Toast.makeText(loginPageActivity.this, "Verify your Email", Toast.LENGTH_SHORT).show();
                                firebaseAuth.signOut();
                            }
                        }
                    });
                }
            }
        });

    }
}