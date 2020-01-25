package com.londonappbrewery.climapm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class changecity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_city_layout);
        final EditText editTextField= findViewById(R.id.queryET);
        ImageButton backbutton=findViewById(R.id.backButton);

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        editTextField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                String newcity=editTextField.getText().toString();
                Intent cityintent= new Intent(changecity.this,WeatherController.class);
                cityintent.putExtra("city",newcity);
                startActivity(cityintent);
                return false;
            }
        });
    }
}
