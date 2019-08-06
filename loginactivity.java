package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class loginactivity extends AppCompatActivity {


    //private FirebaseUser currentuser;
    private FirebaseAuth mauth;
    private ProgressDialog loadingbar;
    private Button LoginButton, PhoneLoginButton;
    private EditText Useremail, Userpassword;
    private TextView Neednewaccountlink, Forgetpasswordlink;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginactivity);

        mauth=FirebaseAuth.getInstance();
       // currentuser=mauth.getCurrentUser();



        InitializeFields();
        Neednewaccountlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToRegisterActivity();
            }
        });

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allowusertologin();
            }
        });

        PhoneLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent phoneloginintent= new Intent(loginactivity.this, phoneloginActivity.class);
                startActivity(phoneloginintent);
            }
        });

    }

    private void allowusertologin() {
        String email= Useremail.getText().toString();
        String password= Userpassword.getText().toString();

        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingbar.setTitle("Logging in into account");
            loadingbar.setMessage("please wait! while we are Logging into your account");
            loadingbar.setCanceledOnTouchOutside(true);
            loadingbar.show();


            mauth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                SendUserToMainActivity();
                                Toast.makeText(loginactivity.this, "Logged in sucessfully", Toast.LENGTH_SHORT).show();
                                loadingbar.dismiss();
                            }
                            else
                            {
                                String message=task.getException().toString();
                                Toast.makeText(loginactivity.this, "Error "+ message, Toast.LENGTH_SHORT).show();
                                loadingbar.dismiss();
                            }
                        }
                    });
        }
    }

    private void InitializeFields()
    {
        LoginButton=(Button)findViewById(R.id.login_button);
        PhoneLoginButton=(Button)findViewById(R.id.login_phone);
        Useremail=(EditText)findViewById(R.id.login_email);
        Userpassword=(EditText)findViewById(R.id.login_password);
        Neednewaccountlink=(TextView)findViewById(R.id.need_new_account);
        Forgetpasswordlink=(TextView)findViewById(R.id.forgot_link);
        loadingbar= new ProgressDialog(this);
    }

   /* @Override
    protected void onStart() {
        super.onStart();
        if(currentuser!=null)
        {
            SendUserToMainActivity();
        }
    }*/

    private void SendUserToMainActivity() {
        Intent mainintent = new Intent(loginactivity.this,MainActivity.class);
        mainintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainintent);
        finish();
    }
    private void SendUserToRegisterActivity() {
        Intent registerintent = new Intent(loginactivity.this,registeractivity.class);
        startActivity(registerintent);
    }

}
