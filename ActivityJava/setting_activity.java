package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class setting_activity extends AppCompatActivity {

    private Button update_Button;
    private EditText username, status;
    private CircleImageView userprofileimage;
    private String CurrentUserId;
    private FirebaseAuth mauth;
    private DatabaseReference rootref;

    public static final int gallerypic=1;
    private StorageReference userprofileimageref;
    private ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_activity);


        mauth=FirebaseAuth.getInstance();
        CurrentUserId=mauth.getCurrentUser().getUid();
        rootref= FirebaseDatabase.getInstance().getReference();
        userprofileimageref= FirebaseStorage.getInstance().getReference().child("profile images");

        Intiallizefield();
        username.setVisibility(View.INVISIBLE);

        update_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatesetting();
            }
        });

        retriveuserinfo();

        userprofileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryintent= new Intent();
                galleryintent.setAction(Intent.ACTION_GET_CONTENT);
                galleryintent.setType("image/*");
                startActivityForResult(galleryintent,gallerypic);
            }
        });
    }



    private void Intiallizefield()
    {
        update_Button = (Button) findViewById(R.id.set_update_Button);
        username = (EditText) findViewById(R.id.set_user_name);
        status = (EditText) findViewById(R.id.set_profile_status);
        userprofileimage = (CircleImageView) findViewById(R.id.set_profile_image);
        loadingbar= new ProgressDialog(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {

        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==gallerypic && resultCode==RESULT_OK && data!=null)
        {
            Uri ImageUri = data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result  = CropImage.getActivityResult(data);

            if(resultCode==RESULT_OK)
            {
                loadingbar.setTitle("Set Profile Image");
                loadingbar.setMessage("please wait");
                loadingbar.setCanceledOnTouchOutside(false);
                loadingbar.show();


                Uri resulturi= result.getUri();

                StorageReference filepath= userprofileimageref.child(CurrentUserId + ".jpg");
                filepath.putFile(resulturi).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
                    {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(setting_activity.this, "Image updated", Toast.LENGTH_SHORT).show();

                            final String downloadUrl = task.getResult().getDownloadUrl().toString();
                            rootref.child("Users").child(CurrentUserId).child("Image")
                                    .setValue(downloadUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                                loadingbar.dismiss();
                                                Toast.makeText(setting_activity.this, "Image saved in Database ", Toast.LENGTH_SHORT).show();
                                            }
                                            else
                                            {


                                                String message=task.getException().toString();
                                                Toast.makeText(setting_activity.this, "Error" + message , Toast.LENGTH_SHORT).show();
                                                loadingbar.dismiss();
                                            }
                                        }
                                    });
                        }
                        else
                        {
                            String message = task.getException().toString() ;
                            Toast.makeText(setting_activity.this, "Error" + message , Toast.LENGTH_SHORT).show();
                            loadingbar.dismiss();
                        }
                    }
                });

            }


        }
    }

    private void updatesetting()
    {
        String setusername=username.getText().toString();
        String setstatus=status.getText().toString();

        if(TextUtils.isEmpty(setusername))
        {
            Toast.makeText(this, "Please Give Username", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(setstatus))
        {
            Toast.makeText(this, "set the status first", Toast.LENGTH_SHORT).show();
        }
        else
        {
            HashMap<String, String> profilemap=new HashMap<>();
                profilemap.put("uid",CurrentUserId);
                profilemap.put("name",setusername);
                profilemap.put("status",setstatus);
            rootref.child("Users").child(CurrentUserId).setValue(profilemap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                SendUserToMainActivity();
                                Toast.makeText(setting_activity.this, "profile updated sucessfully", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                String message=task.getException().toString();
                                Toast.makeText(setting_activity.this, "Error" + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
    private void retriveuserinfo()
    {
        rootref.child("Users").child(CurrentUserId).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                       if((dataSnapshot.exists()) && (dataSnapshot.hasChild("name") && (dataSnapshot.hasChild("Image"))))
                       {
                           String retriveusername = dataSnapshot.child("name").getValue().toString();
                           String retriveuserstatus = dataSnapshot.child("status").getValue().toString();
                           String retriveprofileImage = dataSnapshot.child("Image").getValue().toString();

                           username.setText(retriveusername);
                           status.setText(retriveuserstatus);
                           Picasso.get().load(retriveprofileImage).into(userprofileimage);
                       }
                       else if((dataSnapshot.exists()) && (dataSnapshot.hasChild("name")))
                       {
                           String retriveusername = dataSnapshot.child("name").getValue().toString();
                           String retriveuserstatus = dataSnapshot.child("status").getValue().toString();

                           username.setText(retriveusername);
                           status.setText(retriveuserstatus);
                       }
                       else
                       {
                           username.setVisibility(View.VISIBLE);
                           Toast.makeText(setting_activity.this, "Please update your profile information", Toast.LENGTH_SHORT).show();
                       }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    private void SendUserToMainActivity() {
        Intent mainintent = new Intent(setting_activity.this,MainActivity.class);
        mainintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainintent);
        finish();
    }
}
