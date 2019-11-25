package com.student.ramirez.quizplusplus;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

public class CreateQuizActivity extends AppCompatActivity {

    private Button btnCancel, btnNext;
    private EditText etQuizTitle, etQuizDesc;
    private RadioGroup rgQuizType;
    private RadioButton rbQuizType;

    private Spinner cbTimerLimit;
    private ArrayAdapter<CharSequence> timerAdapter;

    public static Activity createQuizActivity;
    private int quiz_type = 0;

    // From Room Activity
    private String RoomID;
    private String RoomTitle;
    private String RoomType;
    private String RoomPassword;
    private int RoomSize;
    private boolean fromRoomCreate = false;

    private String quizTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quiz);

        createQuizActivity = this;

        etQuizTitle = (EditText) findViewById(R.id.etQuizTitle);
        etQuizDesc = (EditText) findViewById(R.id.etQuizDesc);
        rgQuizType = (RadioGroup) findViewById(R.id.rgQuizType);

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            // User from Create Room
            RoomID = extras.getString("ROOMID");
            RoomTitle = extras.getString("ROOMTITLE");
            RoomType = extras.getString("ROOMTYPE");
            RoomPassword = extras.getString("ROOMPASSWORD");
            RoomSize = extras.getInt("ROOMSIZE");
            fromRoomCreate = extras.getBoolean("fromRoomCreate");

        }
        // Timer
        cbTimerLimit = (Spinner) findViewById(R.id.cbTimerLimit);
        timerAdapter = ArrayAdapter.createFromResource(this, R.array.timers, android.R.layout.simple_spinner_item);
        timerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cbTimerLimit.setAdapter(timerAdapter);
        cbTimerLimit.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        //Toast.makeText(CreateQuizActivity.this, parent.getItemIdAtPosition(position) + "is selected.", Toast.LENGTH_SHORT).show();
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE); // Make the textColor white
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                }
        );
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }
        );
        btnNext = (Button) findViewById(R.id.btnNextCreateQuiz);
        btnNext.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String quiz_title = etQuizTitle.getText().toString().trim();
                        String quiz_desc = etQuizDesc.getText().toString().trim();
                        quizTimer = cbTimerLimit.getSelectedItem().toString();

                        if (quiz_title.equals("") || quiz_desc.equals("")){
                            Toast.makeText(CreateQuizActivity.this, "Please complete the form to continue.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // get selected quiz type
                        int selectedType = rgQuizType.getCheckedRadioButtonId();
                        rbQuizType = (RadioButton) findViewById(selectedType);
                        if (selectedType == -1){
                            Toast.makeText(getApplicationContext(), "Type of Quiz is required.", Toast.LENGTH_SHORT).show();
                            return; // stop the function if empty
                        }
                        String strQuizType = rbQuizType.getText().toString();
                        switch (strQuizType){
                            case "True / False":
                                quiz_type = 1;
                                break;
                            case "Multiple Choice":
                                quiz_type = 2;
                                break;
                            case "Identification":
                                quiz_type = 3;
                                break;
                        }
                        if (!fromRoomCreate){
                            // Quiz
                            Intent intent = new Intent(CreateQuizActivity.this, CreateQuestionActivity.class);
                            intent.putExtra("QUIZ_TITLE", String.valueOf(quiz_title));
                            intent.putExtra("QUIZ_DESC", String.valueOf(quiz_desc));
                            intent.putExtra("QUIZ_TYPE", Integer.valueOf(quiz_type));
                            intent.putExtra("QUIZ_TIMER", quizTimer);
                            intent.putExtra("FROM", "QuizActivity");
                            startActivity(intent);
                        } else if (fromRoomCreate){
                            // Quiz
                            Intent intent = new Intent(CreateQuizActivity.this, CreateQuestionActivity.class);
                            intent.putExtra("QUIZ_TITLE", String.valueOf(quiz_title));
                            intent.putExtra("QUIZ_DESC", String.valueOf(quiz_desc));
                            intent.putExtra("QUIZ_TYPE", Integer.valueOf(quiz_type));
                            intent.putExtra("QUIZ_TIMER", quizTimer);
                            // Room
                            intent.putExtra("ROOMID", RoomID);
                            intent.putExtra("ROOMTITLE", RoomTitle);
                            intent.putExtra("ROOMTYPE", RoomType);
                            intent.putExtra("ROOMPASSWORD", RoomPassword);
                            intent.putExtra("ROOMSIZE", RoomSize);
                            intent.putExtra("FROM", "RoomActivity");
                            startActivity(intent);
                        }

                    }
                }
        );
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        createQuizActivity = null;
    }
}
