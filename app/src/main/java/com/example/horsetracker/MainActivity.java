package com.example.horsetracker;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.horsetracker.database.DatabaseHelper;
import com.example.horsetracker.database.model.LogLine;

import java.time.Instant;
import java.util.stream.Collectors;


public class MainActivity extends AppCompatActivity {


    private DatabaseHelper databaseHelper = new DatabaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        TextView textView = findViewById(R.id.log);
        refreshLines(textView);


        Button startButton = findViewById(R.id.start);
        startButton.setOnClickListener(view -> {
            databaseHelper
                    .insertLogLine(-25, Instant.now().toString(), "aa:123");
            refreshLines(textView);
        });


        Button stopButton = findViewById(R.id.stop);
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

    private void refreshLines(TextView textView) {
        String lines = databaseHelper.getAllLogLines().stream()
                .map(LogLine::toDisplayString)
                .collect(Collectors.joining("\n"));

        textView.setText(lines);
    }
}