package com.example.horsetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        TextView textView = (TextView) findViewById(R.id.log);

        Button startButton = (Button) findViewById(R.id.start);

        startButton.setOnClickListener(view -> {
            textView.append("Hello World\n");
        });


        Button stopButton = (Button) findViewById(R.id.stop);
        stopButton.setOnClickListener(view -> {
            if (textView.length() > 0) {
                CharSequence text = textView.getText();
                int i = textView.length() - 1;
                do {
                    i--;
                    System.out.println("i = " + i);
                }
                while ('\n' != textView.getText().charAt(i) && i > 0);
                CharSequence charSequence = text.subSequence(0, i);
                textView.setText(charSequence);
            }
        });

    }
}