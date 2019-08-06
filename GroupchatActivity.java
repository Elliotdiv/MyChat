package com.example.myapplication;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class GroupchatActivity extends AppCompatActivity {

    private Toolbar mtoolbar;
    private ImageButton sendMessageButton;
    private EditText usermessageinput;
    private ScrollView mscrollview;
    private TextView displaytextmessage;
    private String currentGroupName, currentuserid, currentusername,currentdate, currenttime;
    private FirebaseAuth mauth;
    private DatabaseReference userref, groupnameref, grouprefmessagekey;



    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupchat);

        currentGroupName = getIntent().getExtras().get("groupName").toString();
        Toast.makeText(GroupchatActivity.this,currentGroupName, Toast.LENGTH_SHORT).show();

        mauth=FirebaseAuth.getInstance();
        currentuserid = mauth.getCurrentUser().getUid();
        userref=FirebaseDatabase.getInstance().getReference().child("Users");
        groupnameref=FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName);


        intializationField();

        getuserinfo();

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savemessageinfotodatabase();
                usermessageinput.setText("");
                mscrollview.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        groupnameref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                if(dataSnapshot.exists())
                {
                    Displaymessage(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                if(dataSnapshot.exists())
                {
                    Displaymessage(dataSnapshot);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }


    private void intializationField()
    {
        mtoolbar = (Toolbar) findViewById(R.id.Group_chat_app_bar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle(currentGroupName);

        sendMessageButton = (ImageButton) findViewById(R.id.send_message_button);
        usermessageinput = (EditText) findViewById(R.id.input_group_message);
        displaytextmessage = (TextView) findViewById(R.id.Group_chat_Text_display);
        mscrollview = (ScrollView) findViewById(R.id.my_scroll_view);
    }

    private void getuserinfo()
    {
        userref.child(currentuserid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    currentusername=dataSnapshot.child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void savemessageinfotodatabase()
    {

        String message = usermessageinput.getText().toString();
        String messageKEY= groupnameref.push().getKey();
        if(TextUtils.isEmpty(message))
        {
            Toast.makeText(this, "PLease Write message", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Calendar calfordate = Calendar.getInstance();
            SimpleDateFormat currentdateformat = new SimpleDateFormat("MMM, DD, YYYY");
            currentdate = currentdateformat.format(calfordate.getTime());

            Calendar calfortime= Calendar.getInstance();
            SimpleDateFormat currenttimeformat = new SimpleDateFormat("HH:MM a");
            currenttime = currenttimeformat.format(calfortime.getTime());


            HashMap<String,Object> Groupmessagekey = new HashMap<>();
            groupnameref.updateChildren(Groupmessagekey);
            grouprefmessagekey=groupnameref.child(messageKEY);

            HashMap<String, Object> messageinfomap = new HashMap<>();
                messageinfomap.put("name",currentusername);
                messageinfomap.put("message",message);
                messageinfomap.put("date",currentdate);
                messageinfomap.put("time",currenttime);
            grouprefmessagekey.updateChildren(messageinfomap);


        }

    }
    private void Displaymessage(DataSnapshot dataSnapshot)
    {
        Iterator iterator= dataSnapshot.getChildren().iterator();

        while(iterator.hasNext())
        {
            String chatdate = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatMessage = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatName = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatTime = (String) ((DataSnapshot)iterator.next()).getValue();

            displaytextmessage.append(chatName + "\n" + chatMessage + "\n" + chatTime + "   " + chatdate +"\n\n\n");
            mscrollview.fullScroll(ScrollView.FOCUS_DOWN);
        }

    }


}
