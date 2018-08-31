package com.example.dell.swachhpu;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RegisterActivity extends AppCompatActivity {

    private ImageView img4;
    private ImageView img3;
    private AutoCompleteTextView username;
    private AutoCompleteTextView userpassword;
    private AutoCompleteTextView useremail;
    private Button regbutton;
    private TextView userlogin;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().hide();
        statusbarcolor();

        getWindow().setBackgroundDrawableResource(R.drawable.swachhb);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        img3 = (ImageView)findViewById(R.id.imageView3);
        img4 = (ImageView)findViewById(R.id.imageView4);

        Animation animation3 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade);
        img3.startAnimation(animation3);
        Animation animation4 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade);
        img4.startAnimation(animation4);

        username = (AutoCompleteTextView) findViewById(R.id.Username);
        userpassword = (AutoCompleteTextView)findViewById(R.id.Userpassword);
        useremail = (AutoCompleteTextView)findViewById(R.id.Useremail);
        regbutton = (Button)findViewById(R.id.regbutton);
        userlogin = (TextView)findViewById(R.id.signin);

        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        regbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                android.view.inputmethod.InputMethodManager inputManager = (android.view.inputmethod.InputMethodManager)
                getSystemService(android.content.Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS);

                if(validate()){

                    String user_email = useremail.getText().toString().trim();
                    String user_password = userpassword.getText().toString().trim();

                    firebaseAuth.createUserWithEmailAndPassword(user_email, user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                           if(task.isSuccessful()){
                               FirebaseUser user = firebaseAuth.getCurrentUser();
                               String name = username.getText().toString().trim();
                               mDatabase.child("User names").child(user.getUid()).setValue(name);
                               Toast.makeText(RegisterActivity.this,"Resgistration Successful",Toast.LENGTH_SHORT).show();
                               startActivity(new Intent(RegisterActivity.this,UserProf.class));
                               finish();

                           } else {

                               Toast.makeText(RegisterActivity.this,"Resgistration Failed.",Toast.LENGTH_SHORT).show();
                           }
                        }
                    });
                }
            }
        });

        userlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
    }
    private Boolean validate(){
        Boolean result = false;
        String name = username.getText().toString();
        String email = useremail.getText().toString();
        String password = userpassword.getText().toString();
        if(name.isEmpty() || email.isEmpty() || password.isEmpty()){
            Toast.makeText(this,"Please enter all details",Toast.LENGTH_SHORT).show();
        } else {
            result = true;
        }
        return result;
    }
    public void statusbarcolor() {

            android.view.Window window = this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
            window.setStatusBarColor(android.support.v4.content.ContextCompat.getColor(this,R.color.colorAccent));

    }
}
