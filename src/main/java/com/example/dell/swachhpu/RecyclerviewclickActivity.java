package com.example.dell.swachhpu;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecyclerviewclickActivity extends AppCompatActivity {
    private ImageView back_button;
    private ImageView setting_button;
    private TextView give_fb;
    private Spinner spinner;
    private DatabaseReference mdatabase;
    private FirebaseAuth firebaseAuth;
    private String pId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerviewclick);

        getSupportActionBar().hide();
        statusbarcolor();

        firebaseAuth = FirebaseAuth.getInstance();
        mdatabase = FirebaseDatabase.getInstance().getReference();

        give_fb = (TextView) findViewById(R.id.give_fb);
        spinner = (Spinner) findViewById(R.id.spinner_fb);
        give_fb.setVisibility(View.GONE);
        spinner.setVisibility(View.GONE);

        String stat = getIntent().getStringExtra("status");
        pId = getIntent().getStringExtra("push_id");
        if (stat.equals("2") || stat.equals("-1")){

            give_fb.setVisibility(View.VISIBLE);
            spinner.setVisibility(View.VISIBLE);
            String[] Feedback = new String[]{
                    "Select an Option...",
                    "Complaint not resolved"
            };

            final List<String> fbList = new ArrayList<>(Arrays.asList(Feedback));
            select_item(fbList,spinner);
        }

        back_button = (ImageView) findViewById(R.id.back);
        setting_button = (ImageView) findViewById(R.id.setting_option);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setting_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //creating a popup menu
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        //inflating menu from xml resource
        popup.inflate(R.menu.settingsmenu);
        //adding click listener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(android.view.MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.reopen:
                        //handle menu1 click
                        mdatabase.child("Users").child(firebaseAuth.getCurrentUser().getUid()).child(pId)
                                     .child("complaint data").child("Status")
                                     .setValue("0").addOnSuccessListener(new OnSuccessListener<Void>() {
                                 @Override
                                 public void onSuccess(Void aVoid) {
                                     Toast.makeText(RecyclerviewclickActivity.this,"Complaint Re-opened successfully",Toast.LENGTH_SHORT).show();
                                     finish();
                                     startActivity(new Intent(RecyclerviewclickActivity.this, UserProf.class));
                                 }
                             });

                        return true;
                    case R.id.delete:
                         Toast.makeText(RecyclerviewclickActivity.this,"Complaint deleted successfully",Toast.LENGTH_LONG).show();
                         mdatabase.child("Users").child(firebaseAuth.getCurrentUser().getUid()).child(pId).removeValue();
                         finish();
                        return true;
                    default:
                        return false;
                }
            }
        });
        //displaying the popup
        popup.show();
            }
        });

        getIncomingIntent();
    }
    private void getIncomingIntent(){
        if(getIntent().hasExtra("img_url") && getIntent().hasExtra("desc") && getIntent().hasExtra("status") && getIntent().hasExtra("address") && getIntent().hasExtra("floor") && getIntent().hasExtra("dept")){
            String img = getIntent().getStringExtra("img_url");
            String description = getIntent().getStringExtra("desc");
            String status = getIntent().getStringExtra("status");
            String location = getIntent().getStringExtra("address");
            String floor = getIntent().getStringExtra("floor");
            String dept = getIntent().getStringExtra("dept");
            setcontent(img, description, status, location, floor, dept);
        }
    }
    private void setcontent(String img, String description, String status, String location, String floor, String dept){
        TextView desc = (TextView) findViewById(R.id.desc);
        desc.setText(description);

        byte[] imageBytes = Base64.decode(img,Base64.DEFAULT);
        final Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        ImageView image = (ImageView) findViewById(R.id.complaint_image);
        //image.setImageBitmap(Bitmap.createScaledBitmap(decodedImage,100, 100, false));
        image.setImageBitmap(decodedImage);

        TextView stat = (TextView) findViewById(R.id.click_status);
        if (status.equals("0")) {
            stat.setText(" Recieved ");
            stat.setTextColor(Color.parseColor("#FFFF8800"));
        } else if (status.equals("1")) {
            stat.setText(" Resloving ");
            stat.setTextColor(Color.parseColor("#00AAE4"));

        } else if (status.equals("2")) {
            stat.setText(" Resolved ");
            stat.setTextColor(Color.parseColor("#039d00"));

        } else if (status.equals("-1")) {
            stat.setText(" Rejected ");
            stat.setTextColor(Color.RED);

        }

        TextView department = (TextView) findViewById(R.id.click_dept);
        department.setText(dept);

        TextView loc = (TextView) findViewById(R.id.click_loc);
        loc.setText(floor + ", " + location);

    }

    public void select_item(List<String> spinner_list, final Spinner spinner){
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
                             mdatabase.child("Users").child(firebaseAuth.getCurrentUser().getUid()).child(pId)
                                     .child("complaint data").child("Feedback")
                                     .setValue(spinner.getSelectedItem().toString().trim()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                 @Override
                                 public void onSuccess(Void aVoid) {
                                     Toast.makeText(RecyclerviewclickActivity.this,"feedback uploaded successfully",Toast.LENGTH_SHORT).show();
                                 }
                             });
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