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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class registeractivity extends AppCompatActivity {
    private Button createaccount;
    private EditText Useremail, Userpassword;
    private TextView AlreadyHaveaccount;

    private FirebaseAuth mauth;
    private DatabaseReference rootref;
    private ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeractivity);

        mauth=FirebaseAuth.getInstance();
        rootref= FirebaseDatabase.getInstance().getReference();


        InitializeFields();
        AlreadyHaveaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToLoginActivity();
            }
        });

        createaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createnewaccount();
            }
        });
    }

    private void createnewaccount()
    {
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
            loadingbar.setTitle("creating new account");
            loadingbar.setMessage("please wait! while we are creating new account for you");
            loadingbar.setCanceledOnTouchOutside(true);
            loadingbar.show();

            mauth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if(task.isSuccessful())
                            {
                                String currentuserid=mauth.getCurrentUser().getUid();
                                rootref.child("user").child(currentuserid).setValue("");


                                SendUserToMainActivity();
                                Toast.makeText(registeractivity.this, "account created sucessfully", Toast.LENGTH_SHORT).show();
                                loadingbar.dismiss();
                            }
                            else
                            {
                                String message=task.getException().toString();
                                Toast.makeText(registeractivity.this, "Error "+ message, Toast.LENGTH_SHORT).show();
                                loadingbar.dismiss();
                            }
                        }
                    });
        }
    }

    private void InitializeFields() {
        createaccount=(Button)findViewById(R.id.register_login_button);
        Useremail=(EditText)findViewById(R.id.register_email);
        Userpassword=(EditText)findViewById(R.id.register_password);
        AlreadyHaveaccount=(TextView)findViewById(R.id.already_have_account);
        loadingbar= new ProgressDialog(this);
    }

    private void SendUserToLoginActivity() {
        Intent loginintent = new Intent(registeractivity.this,loginactivity.class);
        startActivity(loginintent);
    }

    private void SendUserToMainActivity() {
        Intent mainintent = new Intent(registeractivity.this,MainActivity.class);
        mainintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainintent);
        finish();
    }
}
