package com.example.myapplication;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class findfriendsActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView FindFriendsRecyclerList;
    private DatabaseReference userref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findfriends);


        userref = FirebaseDatabase.getInstance().getReference().child("Users");
        FindFriendsRecyclerList = (RecyclerView) findViewById(R.id.find_friends_recycler_list);
        FindFriendsRecyclerList.setLayoutManager(new LinearLayoutManager(this));


        mToolbar = (Toolbar) findViewById(R.id.find_friends_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Find Friends");
        chodut();
    }

    protected void chodut() {

       // super.onStart();

        FirebaseRecyclerOptions<Contacts> options=
                new FirebaseRecyclerOptions.Builder<Contacts>()
                        .setQuery(userref,Contacts.class)
                        .build();

        FirebaseRecyclerAdapter<Contacts,FindFriendsViewHolder> adapter=
                new FirebaseRecyclerAdapter<Contacts, FindFriendsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull FindFriendsViewHolder holder, int position, @NonNull Contacts model)
                    {
                        holder.UserName.setText(model.getName());
                        holder.UserStaus.setText(model.getStatus());
                        Picasso.get().load(model.getImage()).into(holder.profileimage);
                    }

                    @NonNull
                    @Override
                    public FindFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
                    {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_display, viewGroup,false);
                        FindFriendsViewHolder viewHolder= new FindFriendsViewHolder(view);
                        return viewHolder;
                    }
                };
        FindFriendsRecyclerList.setAdapter(adapter);
        adapter.startListening();
    }



    public static class FindFriendsViewHolder extends RecyclerView.ViewHolder
    {
        TextView UserName, UserStaus;
        CircleImageView profileimage;


        public FindFriendsViewHolder(@NonNull View itemView)
        {
            super(itemView);

            UserName = itemView.findViewById(R.id.user_profie_name);
            UserStaus = itemView.findViewById(R.id.user_profie_status);
            profileimage = itemView.findViewById(R.id.user_profile_image);

        }
    }

}
