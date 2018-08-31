package com.example.dell.swachhpu;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserProf extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth firebaseAuth;
    private final int LOCATION_REQUEST_CODE = 2;
    private DatabaseReference myref ;
    private int test = 0;
    private ProgressDialog progressDialog;
    List<Retrievedata> retrievedataList ;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_prof);

        askPermission(android.Manifest.permission.ACCESS_FINE_LOCATION, LOCATION_REQUEST_CODE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View v) {isLocationEnabled(UserProf.this);

                                   }
                               });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        final ImageView bg_empty = (ImageView) findViewById(R.id.bg_empty);
        progressDialog = new ProgressDialog(this);

        final String mUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        retrievedataList = new ArrayList<>();
        adapter = new ProductAdapter(this,retrievedataList);
        recyclerView.setAdapter(adapter);
        progressDialog.setMessage("Loading your previous complaints...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        myref = FirebaseDatabase.getInstance().getReference() ;
        myref.child("Users").child(mUid).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                retrievedataList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if(dataSnapshot.hasChild("complaint data") && dataSnapshot.hasChild("Image") && dataSnapshot.hasChild("Location")) {

                        //for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        //String name = dataSnapshot.child("complaint data").child("name").getValue(String.class);
                        // Log.e("name", name);

                        String description = dataSnapshot.child("complaint data").child("Description").getValue(String.class);
                        String Status = dataSnapshot.child("complaint data").child("Status").getValue(String.class);
                        String date = dataSnapshot.child("complaint data").child("Date").getValue(String.class);
                        String name = dataSnapshot.child("complaint data").child("Name").getValue(String.class);
                        String floor = dataSnapshot.child("complaint data").child("Floor").getValue(String.class);
                        String dept = dataSnapshot.child("complaint data").child("Department").getValue(String.class);

                        String image = dataSnapshot.child("Image").child("image").getValue(String.class);

                        Double latitude = dataSnapshot.child("Location").child("latitude").getValue(Double.class);
                        Double longitude = dataSnapshot.child("Location").child("longitude").getValue(Double.class);

                        Retrievedata retrievedata = new Retrievedata();

                        retrievedata.name = name;
                        retrievedata.Description = description;
                        retrievedata.Image = image;
                        retrievedata.Status = Status;
                        retrievedata.Date = date;
                        retrievedata.latitude = latitude;
                        retrievedata.longitude = longitude;
                        retrievedata.Floor = floor;
                        retrievedata.Department = dept;
                        retrievedata.pId = dataSnapshot.getKey();

                        retrievedataList.add(retrievedata);
                    } else {
                        String id = dataSnapshot.getKey();
                        if (test == 0) {
                            myref.child("Users").child(mUid).child(id).removeValue();
                        }
                    }

                    // Log.e("musers" ,retrievedata.name + "" + retrievedata.address) ;
                    //}
                }
                adapter = new ProductAdapter(UserProf.this, retrievedataList);
                if (adapter.getItemCount() != 0){
                    bg_empty.setVisibility(View.GONE);
                }else {
                    bg_empty.setImageResource(R.drawable.bg_empty);
                }

                recyclerView.setAdapter(adapter);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();

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
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case LOCATION_REQUEST_CODE:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                    //Toast.makeText(this, "Location", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(this, "Grant Permission to use Location.", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            onexitPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_prof, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.report){
            startActivity(new Intent(UserProf.this, complaintActivity.class));
        }
        else if (id == R.id.help) {
            startActivity(new Intent(UserProf.this, help.class));
        } else if (id == R.id.exit) {
            onexitPressed();
        } else if (id == R.id.logout) {
            firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.signOut();
            startActivity(new android.content.Intent(UserProf.this, LoginActivity.class));
        } else if (id == R.id.about) {
            startActivity(new android.content.Intent(UserProf.this, aboutUs.class));
        } else if (id == R.id.contact) {
            startActivity(new android.content.Intent(UserProf.this, contactUs.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onexitPressed(){
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_alert)
                .setTitle("Exit")
                .setMessage("Are you sure you want to quit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        moveTaskToBack(true);
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);

                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
    public void isLocationEnabled(final Context context){
        LocationManager locationManager = (LocationManager)  context.getSystemService(Context.LOCATION_SERVICE);
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setIcon(R.drawable.ic_alert);
            builder.setTitle(R.string.gps_not_found_title);
            builder.setMessage(R.string.gps_not_found_message);
            builder.setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(myIntent);
                }
            });
            builder.setNegativeButton(R.string.NO, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(UserProf.this,"Please enable location.",Toast.LENGTH_SHORT).show();
                }
            });
            builder.create().show();
            return;
        } else {
            test = 1;
            startActivity(new Intent(UserProf.this, complaintActivity.class));
        }
    }
}