package com.gosecurity.guestmanager;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import android.util.SparseArray;

import android.content.pm.PackageManager;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;


import com.google.android.gms.vision.Detector;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.gosecurity.guestmanager.classes.InvitedClass;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class AddActivity extends AppCompatActivity {


    private TextView mTextMessage;
    public JSONObject user = new JSONObject();

    SurfaceView cameraView;
    CameraSource cameraSource;

    String name = null;
    String last_name = null;
    String id = null;

    Boolean runnableBialog = false;
    int numbersFinded = 0;

    final int RequestCameraPermissionID = 1001;
    private static final int requestPermissionID = 101;
    ImageView img ;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Intent intentAdd = new Intent(AddActivity.this , MainActivity.class);
                    AddActivity.this.startActivity(intentAdd);
                    return true;
                case R.id.navigation_dashboard:
                    return true;
                case R.id.navigation_notifications:
                    Intent intentInvited = new Intent(AddActivity.this, InvetdActivity.class);
                    AddActivity.this.startActivity(intentInvited);
                    return true;
            }
            return false;
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        cameraView = (SurfaceView) findViewById(R.id.surfaceView);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        startCameraSource();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != requestPermissionID) {
            Log.d("permission", "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            try {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                cameraSource.start(cameraView.getHolder());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void startCameraSource() {

        //Start text recognizer
        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if (!textRecognizer.isOperational()) {
            Log.d("MainActivity", "Detector dependencies are not yet available");
        } else {
            //camera start
            cameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setRequestedFps(2.0f)
                    .setAutoFocusEnabled(true)
                    .build();

            //camera fetch
            cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
               @Override
               public void surfaceCreated(SurfaceHolder surfaceHolder) {

                   try {
                       if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                           ActivityCompat.requestPermissions(AddActivity.this,
                                   new String[]{Manifest.permission.CAMERA},
                                   RequestCameraPermissionID);
                           return;
                       }
                       cameraSource.start(cameraView.getHolder());
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
               }

                @Override
                public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                    cameraSource.stop();
                }
         });

            //recognize
        textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                                        @Override
                                        public void release() {
                                        }

            @Override
            public void receiveDetections(Detector.Detections<TextBlock> detections) {
                final SparseArray<TextBlock> items = detections.getDetectedItems();

                if (items.size() != 0 ){
                    for(int i=0;i<items.size();i++){
                        TextBlock item = items.valueAt(i);
                        //SET ID
                        Log.d("item" , item.getValue());
                        if(item.getValue().matches("(\\d{12})$")){
                            if(numbersFinded==0 && id==null) {
                                Log.d("CARTEID", item.getValue());
                                id = item.getValue().trim();
                            }
                        }

                        //SET NAME
                        if(item.getValue().contains("Nom")){
                            if(item.getValue().replace(":", "").length() > 3){
                                int number = item.getValue().replace(":", "").indexOf("Nom");

                                if(item.getValue().charAt(number) == 'N' && item.getValue().charAt(number+2) == 'm'){
                                    if(!item.getValue().substring( item.getValue().indexOf("Nom")+3).replace(':', ' ').trim().contains(" ")&&
                                            item.getValue().trim() != ""){
                                        if(numbersFinded==0 && last_name == null) {
                                            last_name = item.getValue().substring(item.getValue().indexOf("Nom") + 3).replace(':', ' ').trim();
                                            Log.d("CARTENAME", item.getValue().substring(item.getValue().indexOf("Nom") + 3).replace(':', ' ').trim());
                                        }
                                    }

                                }
                            }
                        }

                        //SET LASTNAME
                        if(item.getValue().contains("Prénom(s)")){
                            if(!item.getValue().substring( item.getValue().indexOf("Prénom(s)")+11).contains("Sexe") &&
                                !item.getValue().substring( item.getValue().indexOf("Prénom(s)")+11).contains("Taille") &&
                                !item.getValue().substring( item.getValue().indexOf("Prénom(s)")+11).contains("Signature") &&
                                        item.getValue().trim() != "" ){
                                if(numbersFinded==0 && name == null){
                                    Log.d("PRENOM" , item.getValue().substring( item.getValue().indexOf("Prénom(s)")+11).trim());
                                    name = item.getValue().substring( item.getValue().indexOf("Prénom(s)")+11).trim();
                                }

                            }


                        }

                        //Verify if all good
                        if(last_name!=null && name!=null && id!=null && numbersFinded==0){
                            numbersFinded++;
                            veirfyInvit(name , last_name , id);
                            items.removeAtRange(0 , items.size());
                            break;
                        }
                    }
                }
            }
        });
    }
    } //end start camera

    //Veirfy the detected data
    public void veirfyInvit(final String name , final String last_name , final String id){

        Log.d("CONTINUE" ,last_name);
        Log.d("CONTINUE" ,name);
        Log.d("CONTINUE" ,id);

        if(runnableBialog==false && numbersFinded==1) {
            runnableBialog = true;

            AddActivity.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddActivity.this);
                    builder.setTitle(AddActivity.this.name + " " + AddActivity.this.last_name);
                    builder.setMessage("Numero : " + AddActivity.this.id);

                    builder.setPositiveButton("Valider", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d("CONTINUE2" ,"OK");
                            runnableBialog = false;
                            numbersFinded = 0;
                            AddActivity.this.last_name = null;
                            AddActivity.this.name = null;
                            AddActivity.this.id = null;
                            saveData(name , last_name , id);
                            dialog.cancel();
                        }
                    });

                    builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Log.d("CONTINUE2" ,"NO");
                            runnableBialog = false;
                            numbersFinded = 0;
                            AddActivity.this.last_name = null;
                            AddActivity.this.name = null;
                            AddActivity.this.id = null;
                            dialog.cancel();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
        }
    }

    //Save data
    public void saveData(String name , String last_name , String id){


        try {
            user.put("id", id);
            user.put("name", name);
            user.put("lastname", last_name);

        } catch (JSONException e) {
            Log.d("JSONEXEP" , e.toString());

            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("invited").child(id);

        SharedPreferences evntPref = getApplicationContext().getSharedPreferences("Events", MODE_PRIVATE);
        String event = evntPref.getString("name", null);

        SharedPreferences userPref = getApplicationContext().getSharedPreferences("Users", MODE_PRIVATE);
        String user = userPref.getString("name", null);

        myRef.setValue(new InvitedClass(name , last_name , event , user));

    }


}




