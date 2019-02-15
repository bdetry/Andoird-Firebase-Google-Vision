package com.gosecurity.guestmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.content.SharedPreferences;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseError;
import com.gosecurity.guestmanager.classes.AgentClass;
import com.gosecurity.guestmanager.classes.EventClass;


import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    public static ArrayList<AgentClass> agents = new ArrayList<AgentClass>();
    public static ArrayList<EventClass> events = new ArrayList<EventClass>();
    private Spinner agentSpinner;
    private Spinner agentSpinnerLister;
    private Spinner eventSpinner;
    private Spinner eventSpinnerLister;

    private Button logInButton;

    //Navigation
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            SharedPreferences sessionPref = getApplicationContext().getSharedPreferences("Session", MODE_PRIVATE);
            Boolean session = sessionPref.getBoolean("status" , false);

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    //mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    if(session){

                        Intent intentAdd = new Intent(MainActivity.this , AddActivity.class);
                        MainActivity.this.startActivity(intentAdd);

                    }else{
                        Toast.makeText( getApplicationContext() , "Aucun evenements en cours" , Toast.LENGTH_LONG ).show();
                    }
                    return true;
                case R.id.navigation_notifications:
                    if(session){

                        Intent intentInvetd = new Intent(MainActivity.this , InvetdActivity.class);
                        MainActivity.this.startActivity(intentInvetd);

                    }else{
                        Toast.makeText( getApplicationContext() , "Aucun evenements en cours" , Toast.LENGTH_LONG ).show();
                    }
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        SharedPreferences.Editor editor = getSharedPreferences("Session", MODE_PRIVATE).edit();
        editor.putBoolean("status", false);
        editor.apply();
        // GET FIREBASE USERS
        // Write a message to the database
        DatabaseReference agentsRef = database.getReference("agents");

        // Read from the database
        agentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                MainActivity.agents.clear();
                for(DataSnapshot dst : dataSnapshot.getChildren()){
                    MainActivity.agents.add(new AgentClass(dst.getKey() , dst.child("name").getValue().toString() , dst.child("last_name").getValue().toString()));
                }

                agentSpinner = (Spinner) findViewById(R.id.spinnerAgent);
                ArrayAdapter<String> adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_spinner_item , MainActivity.agents);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                agentSpinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("TAGTEST", "Failed to read value.", error.toException());
            }
        });


        // GET FIREBASE EVENTS
        DatabaseReference eventsRef = database.getReference("events");

        // Read from the database
        eventsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                MainActivity.events.clear();
                for(DataSnapshot dst : dataSnapshot.getChildren()){
                    MainActivity.events.add(new EventClass(dst.getKey() , dst.child("name").getValue().toString()));
                }

                eventSpinner = (Spinner) findViewById(R.id.spinnerEvent);
                ArrayAdapter<String> adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_spinner_item , MainActivity.events);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                eventSpinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("TAGTEST", "Failed to read value.", error.toException());
            }
        });


        //TEXT NAVIGATION
        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //SPINNER LISTNER AGENT
        agentSpinnerLister = (Spinner) findViewById(R.id.spinnerAgent);
        OnItemSelectedListener agentListner = new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> spinner, View container, int position, long id) {
                String item = spinner.getItemAtPosition(position).toString();
                SharedPreferences.Editor editor = getSharedPreferences("Users", MODE_PRIVATE).edit();
                editor.putString("name", item);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        };

        // Setting ItemClick Handler for Spinner Widget
        agentSpinnerLister.setOnItemSelectedListener(agentListner);

        //SPINNER LISTNER EVENTS
        eventSpinnerLister = (Spinner) findViewById(R.id.spinnerEvent);
        OnItemSelectedListener eventListner = new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> spinner, View container, int position, long id) {
                String item = spinner.getItemAtPosition(position).toString();
                SharedPreferences.Editor editor = getSharedPreferences("Events", MODE_PRIVATE).edit();
                editor.putString("name", item);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        };


        // Setting ItemClick Handler for Spinner Widget
        eventSpinnerLister.setOnItemSelectedListener(eventListner);


        logInButton = (Button) findViewById(R.id.buttonlogIn);
        logInButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
            SharedPreferences evntPref = getApplicationContext().getSharedPreferences("Events", MODE_PRIVATE);
            String event = evntPref.getString("name", null);

            SharedPreferences userPref = getApplicationContext().getSharedPreferences("Users", MODE_PRIVATE);
            String agent = userPref.getString("name", null);

                if(event!=null && agent!=null){
                    SharedPreferences.Editor editor = getSharedPreferences("Session", MODE_PRIVATE).edit();
                    editor.putBoolean("status", true);
                    editor.apply();

                    Toast.makeText( v.getContext() , "Bienvenue " + agent , Toast.LENGTH_LONG ).show();
                }
            }
        });
    }


}
