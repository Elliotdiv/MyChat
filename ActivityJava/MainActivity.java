package com.example.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private Toolbar mtoolbar;
    private ViewPager mypager;
    private TabLayout mytablayout;
    private TabAcessoradapter mytabAcessoradapter;
    private FirebaseUser currentuser;
    private FirebaseAuth mauth;
    private DatabaseReference rootref;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mauth=FirebaseAuth.getInstance();
        currentuser=mauth.getCurrentUser();
        rootref = FirebaseDatabase.getInstance().getReference();
        mtoolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("MyChat");

        mypager = (ViewPager) findViewById(R.id.main_pager);
        mytabAcessoradapter = new TabAcessoradapter(getSupportFragmentManager());
        mypager.setAdapter(mytabAcessoradapter);

        mytablayout = (TabLayout) findViewById(R.id.main_tabs);
        mytablayout.setupWithViewPager(mypager);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(currentuser==null)
        {
            SendUserToLoginActivity();
        }
        else
        {
            verifyuserexistance();
        }
    }

    private void verifyuserexistance()
    {
        String currentuserid =mauth.getCurrentUser().getUid();
        rootref.child("Users").child(currentuserid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if((dataSnapshot.child("name").exists()))
                {
                    Toast.makeText(MainActivity.this, "Hi!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    SendUserToSettingActivity();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {

        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);


        if(item.getItemId()==R.id.main_logout_option)
        {
            mauth.signOut();
            SendUserToLoginActivity();
        }
        if(item.getItemId()==R.id.main_setting_option)
        {
            SendUserToSettingActivity();
        }
        if(item.getItemId()==R.id.main_group_option)
        {
            requestforgroup();
        }
        if(item.getItemId()==R.id.main_find_friend_option)
        {
            SendUserToFindFriendsActivity();
        }
        return true;
    }

    private void requestforgroup()
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this,R.style.AlertDialog);
        builder.setTitle("Enter the group name:");
        final EditText Groupnamefield= new EditText(MainActivity.this);
        Groupnamefield.setHint("e.g. Divyanshu Friends");
        builder.setView(Groupnamefield);

        builder.setPositiveButton("create", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            String Groupname= Groupnamefield.getText().toString();
            if(TextUtils.isEmpty(Groupname))
            {
                Toast.makeText(MainActivity.this, "Plaese Enter Group name", Toast.LENGTH_SHORT).show();
            }
            else
            {
                createnewgroup(Groupname);
            }
        }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void createnewgroup(final String groupname)
    {
        rootref.child("Groups").child(groupname).setValue("").
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(MainActivity.this, groupname + "Group is created Sucessfully", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void SendUserToLoginActivity() {
        Intent loginintent = new Intent(MainActivity.this,loginactivity.class);
        loginintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginintent);
        finish();
    }

    private void SendUserToSettingActivity() {
        Intent settingintent = new Intent(MainActivity.this,setting_activity.class);
        settingintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(settingintent);
        finish();
    }

    private void SendUserToFindFriendsActivity() {
        Intent findfriendintent = new Intent(MainActivity.this,findfriendsActivity.class);

        startActivity(findfriendintent);

    }

}
