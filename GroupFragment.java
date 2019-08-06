package com.example.myapplication;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupFragment extends Fragment {
    private View GroupFragmentView;
    private ListView list_view;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> List_of_Groups =new ArrayList<>();
    private DatabaseReference GroupRef;


    public GroupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        GroupFragmentView= inflater.inflate(R.layout.fragment_group, container, false);
        GroupRef= FirebaseDatabase.getInstance().getReference().child("Groups");
        intiallizeFields();

        retriveandDisplaygroups();

        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String currentGroupName=parent.getItemAtPosition(position).toString();

                Intent groupchatintent = new Intent(getContext(),GroupchatActivity.class);
                groupchatintent.putExtra("groupName",currentGroupName);
                startActivity(groupchatintent);
            }
        });

        return GroupFragmentView;
    }


    private void intiallizeFields()
    {
        list_view=(ListView) GroupFragmentView.findViewById(R.id.list_view);
        arrayAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,List_of_Groups);
        list_view.setAdapter(arrayAdapter);
    }
    private void retriveandDisplaygroups()
    {
        GroupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                Set<String> set =new HashSet<>();
                Iterator iterator  =dataSnapshot.getChildren().iterator();
                while(iterator.hasNext())

                {
                    set.add(((DataSnapshot)iterator.next()).getKey());
                }
                List_of_Groups.clear();
                List_of_Groups.addAll(set);
                arrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}
