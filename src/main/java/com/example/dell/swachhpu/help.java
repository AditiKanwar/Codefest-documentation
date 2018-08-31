package com.example.dell.swachhpu;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class help extends AppCompatActivity {

    TextView text1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        getSupportActionBar().hide();

        ImageView back = (ImageView) findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        text1 = (TextView) findViewById(R.id.email1);
        text1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:puathority@gmail.com"));
                intent.putExtra(Intent.EXTRA_SUBJECT,"Help ");
                intent.putExtra(Intent.EXTRA_TEXT, "Hi");
                if(intent.resolveActivity(getPackageManager()) != null)
                    startActivity(intent);
            }
        });

    }
}
