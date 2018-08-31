package com.example.dell.swachhpu;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.annotation.NonNull;
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


public class LoginActivity extends AppCompatActivity {

    ImageView img4;
    ImageView img3;
    private TextView new_acct;
    private AutoCompleteTextView useremail;
    private AutoCompleteTextView userpassword;
    private Button logbutton;
    FirebaseAuth firebaseAuth;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();
        statusbarcolor();

        getWindow().setBackgroundDrawableResource(R.drawable.swachhb);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        img3 = (ImageView)findViewById(R.id.imageView3);
        img4 = (ImageView)findViewById(R.id.imageView4);
        useremail = (AutoCompleteTextView)findViewById(R.id.logemail);
        userpassword = (AutoCompleteTextView)findViewById(R.id.logpassword);
        logbutton = (Button)findViewById(R.id.logbutton);
        mProgress = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user!= null){
            startActivity(new Intent(LoginActivity.this,UserProf.class));
            finish();
        }



        new_acct = (TextView)findViewById(R.id.text1);
        new_acct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
        Animation animation3 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade);
        img3.startAnimation(animation3);
        Animation animation4 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade);
        img4.startAnimation(animation4);

        logbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                android.view.inputmethod.InputMethodManager inputManager = (android.view.inputmethod.InputMethodManager)
                getSystemService(android.content.Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS);

                if (useremail.getText().toString().length() == 0 || userpassword.getText().toString().length() == 0){

                    Toast.makeText(LoginActivity.this, "Please enter all details", Toast.LENGTH_SHORT).show();
                } else {
                mProgress.setMessage("Logging In...");
                mProgress.setCancelable(false);
                //mProgress.setCanceledOnTouchOutside(false);
                mProgress.show();
                validate();
                validate(useremail.getText().toString(),userpassword.getText().toString());
                }

            }
        });
    }

    public void validate(String name, String password){

        firebaseAuth.signInWithEmailAndPassword(name, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        mProgress.dismiss();
                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, UserProf.class));
                        finish();

                    } else {
                        Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                        mProgress.dismiss();
                    }
                }

        });
    }

    private Boolean validate(){
        Boolean result = false;
        String email = useremail.getText().toString();
        String password = userpassword.getText().toString();
        if( email.isEmpty() || password.isEmpty()){
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
