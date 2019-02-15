package com.gosecurity.guestmanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gosecurity.guestmanager.classes.EventClass;
import com.gosecurity.guestmanager.classes.InvitedClass;

import java.util.ArrayList;

public class InvetdActivity extends AppCompatActivity {

    private TextView mTextMessage;

    public static ArrayList<String> inveted = new ArrayList<String>();

    public ListView listView;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Intent intentHome  = new Intent(InvetdActivity.this , MainActivity.class);
                    InvetdActivity.this.startActivity(intentHome);
                    return true;
                case R.id.navigation_dashboard:
                    Intent intentAdd  = new Intent(InvetdActivity.this , AddActivity.class);
                    InvetdActivity.this.startActivity(intentAdd);
                    return true;
                case R.id.navigation_notifications:
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invetd);

        // GET FIREBASE EVENTS
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference eventsRef = database.getReference("invited");

        // Read from the database
        eventsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                InvetdActivity.inveted.clear();
                SharedPreferences evntPref = getApplicationContext().getSharedPreferences("Events", MODE_PRIVATE);
                String event = evntPref.getString("name", null);
                for(DataSnapshot dst : dataSnapshot.getChildren()){

                    if( dst.child("event").getValue().toString().contains(event)){
                        InvetdActivity.inveted.add(new InvitedClass(dst.child("name").getValue().toString() , dst.child("last_name").getValue().toString() ,
                                dst.child("event").getValue().toString() ,  dst.child("invited_by").getValue().toString() ).toString());
                    }

                }

                listView = (ListView) findViewById(R.id.listInvited);
                ArrayAdapter<String> adapter = new ArrayAdapter(InvetdActivity.this,
                        android.R.layout.simple_list_item_1,
                        InvetdActivity.inveted);
                listView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("TAGTEST", "Failed to read value.", error.toException());
            }
        });

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
