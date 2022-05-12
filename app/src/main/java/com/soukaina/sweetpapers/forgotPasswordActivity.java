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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class forgotPasswordActivity extends AppCompatActivity {

    private Button recoverPsw;
    private EditText email_frgt;
    private TextView backtologin_frgt;
    FirebaseAuth firebaseAuth;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        recoverPsw=findViewById(R.id.forgotBtn);
        email_frgt=findViewById(R.id.editEmailForgotpsw);
        backtologin_frgt=findViewById(R.id.backToLogin_frgt);
        firebaseAuth=FirebaseAuth.getInstance(); // because we want to recover the password of the current instance of the user


        getWindow().setStatusBarColor(ContextCompat.getColor(forgotPasswordActivity.this,R.color.black));
        backtologin_frgt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(forgotPasswordActivity.this,loginPageActivity.class);
                startActivity(intent);
                finish();
            }
        });

        recoverPsw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = email_frgt.getText().toString().trim(); // if the user enter his email then we have to store that email in a variable
                if (mail.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Email required !", Toast.LENGTH_SHORT).show();
                } else {

                    //we have to send password recover email
                    firebaseAuth.sendPasswordResetEmail(mail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(forgotPasswordActivity.this, "Mail sent, you can recover your password", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(forgotPasswordActivity.this, loginPageActivity.class));
                            } else {
                                Toast.makeText(forgotPasswordActivity.this, "This account is not exist", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}