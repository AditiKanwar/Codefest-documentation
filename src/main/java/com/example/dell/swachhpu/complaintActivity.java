package com.example.dell.swachhpu;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class complaintActivity extends AppCompatActivity {

    private Button mUploadBtn;
    private ImageView back_button;
    private ImageView mImageLabel;
    private static final int CAMERA_REQUEST_CODE = 1;
    private ProgressDialog mProgress;
    private ProgressDialog mProgress2;
    private String pId;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference name_Database;
    private Button compl_upload;
    private double latitude;
    private double longitude;
    private String imageEncoded = "0";
    private Spinner spinner_desc;
    private Spinner spinner_dept;
    private Spinner spinner_floor;
    private String spinner_check = "Select an Option...";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint);

        askPermission(android.Manifest.permission.CAMERA, CAMERA_REQUEST_CODE);

        getSupportActionBar().hide();
        getWindow().setBackgroundDrawableResource(R.color.white);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        statusbarcolor();

        MyLocation.LocationResult locationResult = new MyLocation.LocationResult(){
            @Override
            public void gotLocation(Location location){
                //Got the location!
                latitude = location.getLatitude();
                longitude = location.getLongitude();

                //textView.setText("latitude: "+ latitude+"\n"+"longitude: "+ longitude);

            }
        };
        MyLocation myLocation = new MyLocation();
        myLocation.getLocation(complaintActivity.this, locationResult);

        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        pId = mDatabase.child(user.getUid()).push().getKey();


        compl_upload = (Button) findViewById(R.id.upload_compl);
        mUploadBtn = (Button) findViewById(R.id.capture_img);
        back_button = (ImageView) findViewById(R.id.back_button);
        mImageLabel = (ImageView) findViewById(R.id.img);
        mProgress = new ProgressDialog(this);
        mProgress2 = new ProgressDialog(this);
        spinner_desc = (Spinner) findViewById(R.id.spinner_desc);
        spinner_dept = (Spinner) findViewById(R.id.spinner_dept);


        String[] Desc = new String[]{
                "Select an Option...",
                "Garbage dump",
                "Dustbins not cleaned",
                "Sweeping not done",
                "Dead animals",
                "Public toilet(s) cleaning",
                "No water supply in toilets"
        };

        String[] Dept = new String[]{
                "Select an Option...",
                "Nation"
        };

        final List<String> DescList = new ArrayList<>(Arrays.asList(Desc));
        select_item(DescList,spinner_desc);

        final List<String> DeptList = new ArrayList<>(Arrays.asList(Dept));
        select_item(DeptList,spinner_dept);

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mUploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,CAMERA_REQUEST_CODE);
            }
        });

        compl_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               saveUserInformation();
               savedata();
            }
        });
    }

    private void askPermission(String permissions, int requestCode ){
        if(ContextCompat.checkSelfPermission(this, permissions)!= PackageManager.PERMISSION_GRANTED){
            //Code for what happens next
            ActivityCompat.requestPermissions(this, new String[]{permissions}, requestCode);
        }
        else{
            //Dekhna hai abhi
        }
    }
    private void savedata() {
        if ((spinner_desc.getSelectedItem().toString() != spinner_check && spinner_dept.getSelectedItem().toString() != spinner_check  && imageEncoded != "0" )) {
            mProgress2.setMessage("Uploading data");
            mProgress2.setCancelable(false);
            mProgress2.show();
            final String description = spinner_desc.getSelectedItem().toString().trim();
            final String department = spinner_dept.getSelectedItem().toString().trim();
            final String floor = "0";
            final String date = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(Calendar.getInstance().getTime());
            final String status = "0";
            name_Database = FirebaseDatabase.getInstance().getReference().child("User names").child(firebaseAuth.getCurrentUser().getUid());
            name_Database.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                   String name = dataSnapshot.getValue(String.class);
                   UserData ckd = new UserData(description, department, floor, status, date, name);
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    mDatabase.child("Users").child(user.getUid()).child(pId).child("complaint data").setValue(ckd).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mProgress2.dismiss();
                            startActivity(new Intent(complaintActivity.this,UserProf.class));
                            finish();
                        }
                    });
                    //Toast.makeText(loginactivity.this,name,Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } else {
            Toast.makeText(this, "Please enter all details.", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case CAMERA_REQUEST_CODE:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                    //Toast.makeText(this, "Location", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(this, "Grant Permission to use Camera.", Toast.LENGTH_SHORT).show();

        }
    }


    @Override
    public void onBackPressed(){
        if((spinner_floor.getSelectedItem().toString() == spinner_check || spinner_desc.getSelectedItem().toString() == spinner_check || spinner_dept.getSelectedItem().toString() == spinner_check || imageEncoded == "0" )) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            mDatabase.child("Users").child(user.getUid()).child(pId).removeValue();
            super.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            mProgress.setMessage("Uploading Image...");
            mProgress.setCancelable(false);
            mProgress.show();
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mImageLabel.setImageBitmap(imageBitmap);
            encodeBitmapAndSaveToFirebase(imageBitmap);
        }

    }
    public void encodeBitmapAndSaveToFirebase(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(pId).child("Image").child("image");
        ref.setValue(imageEncoded).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mProgress.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mProgress.dismiss();
                Toast.makeText(complaintActivity.this,"Uploading failed. Please check your internet connection.",Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void saveUserInformation() {
        if((latitude !=0 && longitude !=0 && spinner_desc.getSelectedItem().toString() != spinner_check && spinner_dept.getSelectedItem().toString() != spinner_check  && imageEncoded != "0")) {
            mProgress.setMessage("Setting Location...");
            mProgress.setCancelable(false);
            mProgress.show();
            UserLocation userLocation = new UserLocation(latitude, longitude);
            FirebaseUser user = firebaseAuth.getCurrentUser();
            mDatabase.child("Users").child(user.getUid()).child(pId).child("Location").setValue(userLocation).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    mProgress.dismiss();
                }
            });
        }
        else {
            Toast.makeText(complaintActivity.this, "Please enter all data", Toast.LENGTH_LONG).show();
        }

    }

    public void select_item(List<String> spinner_list, Spinner spinner){
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this,R.layout.spinner_item,spinner_list){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                }
                else
                {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(spinnerArrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint
                if(position > 0){
                    // Notify the selected item text
                    Toast.makeText
                            (getApplicationContext(), "Selected : " + selectedItemText, Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void statusbarcolor() {

        android.view.Window window = this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(android.support.v4.content.ContextCompat.getColor(this,R.color.colorPrimaryDark));

    }

}