package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class phoneloginActivity extends AppCompatActivity {

    private Button sendverficationcodebutton, verifybuttoon;
    private EditText inputphonenumber, inputverificationcode;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private String mVerificationId;
    private ProgressDialog loadingbar;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phonelogin);

        mAuth = FirebaseAuth.getInstance();

        sendverficationcodebutton = (Button) findViewById(R.id.Send_verfication_code);
        verifybuttoon = (Button) findViewById(R.id.verify_button);
        inputphonenumber = (EditText) findViewById(R.id.phone_no_input);
        inputverificationcode = (EditText) findViewById(R.id.verification_code_input);
        loadingbar= new ProgressDialog(this);


        sendverficationcodebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String phoneNumber = inputphonenumber.getText().toString();

                if(TextUtils.isEmpty(phoneNumber))
                {
                    Toast.makeText(phoneloginActivity.this, "Please Enter The Phone Number First..", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    loadingbar.setTitle("Phone Verification");
                    loadingbar.setMessage("PLease Wait!");
                    loadingbar.setCanceledOnTouchOutside(false);
                    loadingbar.show();


                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            phoneloginActivity.this,               // Activity (for callback binding)
                            callbacks);        // OnVerificationStateChangedCallbacks

                }

            }
        });


        verifybuttoon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendverficationcodebutton.setVisibility(View.INVISIBLE);
                inputphonenumber.setVisibility(View.INVISIBLE);

                String verificationcode=inputverificationcode.getText().toString();

                if(TextUtils.isEmpty(verificationcode))
                {
                    Toast.makeText(phoneloginActivity.this, "PLease write the Verification Code first", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    loadingbar.setTitle("Code Verification");
                    loadingbar.setMessage("PLease Wait!");
                    loadingbar.setCanceledOnTouchOutside(false);
                    loadingbar.show();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationcode);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential)
            {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e)
            {
                loadingbar.dismiss();
                Toast.makeText(phoneloginActivity.this, "please enter valid phone number with country code", Toast.LENGTH_SHORT).show();
                sendverficationcodebutton.setVisibility(View.VISIBLE);
                inputphonenumber.setVisibility(View.VISIBLE);


                inputverificationcode.setVisibility(View.INVISIBLE);
                verifybuttoon.setVisibility(View.INVISIBLE);



            }
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                mVerificationId = verificationId;
                mResendToken = token;
                loadingbar.dismiss();
                Toast.makeText(phoneloginActivity.this, "Code has be Sent Please check..", Toast.LENGTH_SHORT).show();

                sendverficationcodebutton.setVisibility(View.INVISIBLE);
                inputphonenumber.setVisibility(View.INVISIBLE);


                inputverificationcode.setVisibility(View.VISIBLE);
                verifybuttoon.setVisibility(View.VISIBLE);


            }
        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            loadingbar.dismiss();
                            Toast.makeText(phoneloginActivity.this, "You are sucessfully logged in!", Toast.LENGTH_SHORT).show();
                            sendusertomainActivity();
                        }
                        else
                        {
                            String message = task.getException().toString();
                            Toast.makeText(phoneloginActivity.this, "Error " + message , Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void sendusertomainActivity()
    {
        Intent mainintent=new Intent(phoneloginActivity.this,MainActivity.class);
        startActivity(mainintent);
        finish();
    }

}
