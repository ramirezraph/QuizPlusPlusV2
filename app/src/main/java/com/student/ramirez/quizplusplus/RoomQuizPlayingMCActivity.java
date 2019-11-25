package com.student.ramirez.quizplusplus;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class RoomQuizPlayingMCActivity extends AppCompatActivity {

    private Button btnEndQuiz, btnShowMyAnswer;
    private TextView txtQuizTitle, txtQuizID, txtCountdown, txtChoices;
    private TextView txtQuestionNumber, txtQuestion;
    private ProgressDialog progressDialog;
    private Button btnA, btnB, btnC, btnD;

    // Firebase Database
    DatabaseReference db;
    DatabaseReference mRecord;

    private List<Question> questionList;
    private CountDownTimer timer;
    private List<String> quizResult;
    private static int currentQuestionNumber = 0;
    private static int totalScore = 0;
    private int checkreadycount = 0;
    private String strTimer;

    // Dialogs
    private Dialog QuizDoneDialog;

    private String QUIZOWNER;

    // Room
    private DatabaseReference mRoom;
    private String ROOMID;
    private String USERKEY;
    private String QUIZKEY;
    private String FROMWHAT;
    private String ISOWNER;
    private String yourkey;
    private String PARTICIPANTNAME;
    private String OWNERNAME;
    private String ROOMTITLE;
    private int numberOfUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_playing_mc);

        // Holds questions
        questionList = new ArrayList<Question>();
        // Holds the Result
        quizResult = new ArrayList<>();

        btnShowMyAnswer = (Button) findViewById(R.id.btnShowAnswer);
        btnEndQuiz = (Button) findViewById(R.id.btnEndQuiz);

        txtQuizTitle = (TextView) findViewById(R.id.txtScore);
        txtQuizID = (TextView) findViewById(R.id.txtQuizID);
        txtCountdown = (TextView) findViewById(R.id.txtCountdown);
        txtChoices = (TextView) findViewById(R.id.txtQuizDesc);

        btnA = (Button) findViewById(R.id.btnA);
        btnB = (Button) findViewById(R.id.btnB);
        btnC = (Button) findViewById(R.id.btnC);
        btnD = (Button) findViewById(R.id.btnD);

        // Setup Dialog End Quiz
        QuizDoneDialog = new Dialog(this);

        txtQuestionNumber = (TextView) findViewById(R.id.txtQuestionNumber);
        txtQuestion = (TextView) findViewById(R.id.txtQuestion);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Setting up .. Please Wait ...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            String quiz_title = extras.get("QUIZTITLE").toString();
            String quiz_id = extras.get("QUIZKEY").toString();

            ROOMID = extras.getString("ROOMID");
            USERKEY = extras.getString("USERKEY");
            QUIZKEY = extras.getString("USERKEY");
            yourkey = extras.getString("USERLISTKEY");
            FROMWHAT = extras.getString("FROMWHAT");
            ISOWNER = extras.getString("ISOWNER");
            OWNERNAME = extras.getString("OWNERNAME");
            numberOfUser = extras.getInt("USERNUMBER");
            PARTICIPANTNAME = extras.getString("PARTICIPANTNAME");
            QUIZOWNER = extras.getString("QUIZOWNER");
            ROOMTITLE = extras.getString("ROOMTITLE");

            txtQuizTitle.setText(quiz_title);
            txtQuizID.setText(quiz_id);

            db = FirebaseDatabase.getInstance().getReference().child("OnlineQuizzes").child(quiz_id);
            mRoom = FirebaseDatabase.getInstance().getReference().child("Records").child(QUIZKEY);
            mRecord = FirebaseDatabase.getInstance().getReference().child("Records");
        } else {
            Toast.makeText(this, "Error Occurred.", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference timerRef = db.child("quiz_timer");
        timerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    Toast.makeText(RoomQuizPlayingMCActivity.this, "Timer not found", Toast.LENGTH_SHORT).show();
                    return;
                }
                strTimer = dataSnapshot.getValue().toString();
                switch (strTimer){
                    case "10 secs":
                        // Set Countdown
                        long timerten = 10000;
                        timer = new CountDownTimer(timerten, 1000){
                            @SuppressLint("ResourceAsColor")
                            @Override
                            public void onTick(long millisUntilFinished) {
                                txtCountdown.setTextColor(Color.BLACK);
                                if ((millisUntilFinished / 1000) <= 3){
                                    txtCountdown.setTextColor(Color.RED);
                                }
                                txtCountdown.setText(String.valueOf(millisUntilFinished / 1000));
                            }

                            @Override
                            public void onFinish() {
                                checkQuestion(currentQuestionNumber, "No Answer");
                                // Setup Question
                                currentQuestionNumber++;
                                if (currentQuestionNumber < questionList.size()){
                                    String question = questionList.get(currentQuestionNumber).getQuestion();
                                    String a = questionList.get(currentQuestionNumber).getChoiceA();
                                    String b = questionList.get(currentQuestionNumber).getChoiceB();
                                    String c = questionList.get(currentQuestionNumber).getChoiceC();
                                    String d = questionList.get(currentQuestionNumber).getChoiceD();
                                    setQuestion(currentQuestionNumber, question, a, b, c, d);
                                    timer.start();
                                } else {
                                    timer.cancel();
                                    //ShowQuizDoneMessage();
                                    DisplayQuizResult();
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        public void run() {
                                            FinishingQuiz();
                                        }
                                    }, 2000);   //2 second
                                }
                            }
                        };
                        break;
                    case "20 secs":
                        // Set Countdown
                        long timertwenty = 20000;
                        timer = new CountDownTimer(timertwenty, 1000){
                            @SuppressLint("ResourceAsColor")
                            @Override
                            public void onTick(long millisUntilFinished) {
                                txtCountdown.setTextColor(Color.BLACK);
                                if ((millisUntilFinished / 1000) <= 3){
                                    txtCountdown.setTextColor(Color.RED);
                                }
                                txtCountdown.setText(String.valueOf(millisUntilFinished / 1000));
                            }

                            @Override
                            public void onFinish() {
                                checkQuestion(currentQuestionNumber, "No Answer");
                                // Setup Question
                                currentQuestionNumber++;
                                if (currentQuestionNumber < questionList.size()){
                                    String question = questionList.get(currentQuestionNumber).getQuestion();
                                    String a = questionList.get(currentQuestionNumber).getChoiceA();
                                    String b = questionList.get(currentQuestionNumber).getChoiceB();
                                    String c = questionList.get(currentQuestionNumber).getChoiceC();
                                    String d = questionList.get(currentQuestionNumber).getChoiceD();
                                    setQuestion(currentQuestionNumber, question, a, b, c, d);
                                    timer.start();
                                } else {
                                    timer.cancel();
                                    //ShowQuizDoneMessage();
                                    DisplayQuizResult();
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        public void run() {
                                            FinishingQuiz();
                                        }
                                    }, 2000);   //2 second
                                }
                            }
                        };
                        break;
                    case "30 secs":
                        // Set Countdown
                        long timerthirty = 30000;
                        timer = new CountDownTimer(timerthirty, 1000){
                            @SuppressLint("ResourceAsColor")
                            @Override
                            public void onTick(long millisUntilFinished) {
                                txtCountdown.setTextColor(Color.BLACK);
                                if ((millisUntilFinished / 1000) <= 3){
                                    txtCountdown.setTextColor(Color.RED);
                                }
                                txtCountdown.setText(String.valueOf(millisUntilFinished / 1000));
                            }

                            @Override
                            public void onFinish() {
                                checkQuestion(currentQuestionNumber, "No Answer");
                                // Setup Question
                                currentQuestionNumber++;
                                if (currentQuestionNumber < questionList.size()){
                                    String question = questionList.get(currentQuestionNumber).getQuestion();
                                    String a = questionList.get(currentQuestionNumber).getChoiceA();
                                    String b = questionList.get(currentQuestionNumber).getChoiceB();
                                    String c = questionList.get(currentQuestionNumber).getChoiceC();
                                    String d = questionList.get(currentQuestionNumber).getChoiceD();
                                    setQuestion(currentQuestionNumber, question, a, b, c, d);
                                    timer.start();
                                } else {
                                    timer.cancel();
                                    //ShowQuizDoneMessage();
                                    DisplayQuizResult();
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        public void run() {
                                            FinishingQuiz();
                                        }
                                    }, 2000);   //2 second
                                }
                            }
                        };
                        break;
                    case "40 secs":
                        // Set Countdown
                        long timerforty = 40000;
                        timer = new CountDownTimer(timerforty, 1000){
                            @SuppressLint("ResourceAsColor")
                            @Override
                            public void onTick(long millisUntilFinished) {
                                txtCountdown.setTextColor(Color.BLACK);
                                if ((millisUntilFinished / 1000) <= 3){
                                    txtCountdown.setTextColor(Color.RED);
                                }
                                txtCountdown.setText(String.valueOf(millisUntilFinished / 1000));
                            }

                            @Override
                            public void onFinish() {
                                checkQuestion(currentQuestionNumber, "No Answer");
                                // Setup Question
                                currentQuestionNumber++;
                                if (currentQuestionNumber < questionList.size()){
                                    String question = questionList.get(currentQuestionNumber).getQuestion();
                                    String a = questionList.get(currentQuestionNumber).getChoiceA();
                                    String b = questionList.get(currentQuestionNumber).getChoiceB();
                                    String c = questionList.get(currentQuestionNumber).getChoiceC();
                                    String d = questionList.get(currentQuestionNumber).getChoiceD();
                                    setQuestion(currentQuestionNumber, question, a, b, c, d);
                                    timer.start();
                                } else {
                                    timer.cancel();
                                    //ShowQuizDoneMessage();
                                    DisplayQuizResult();
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        public void run() {
                                            FinishingQuiz();
                                        }
                                    }, 2000);   //2 second
                                }
                            }
                        };
                        break;
                    case "60 secs":
                        // Set Countdown
                        long timersixty = 60000;
                        timer = new CountDownTimer(timersixty, 1000){
                            @SuppressLint("ResourceAsColor")
                            @Override
                            public void onTick(long millisUntilFinished) {
                                txtCountdown.setTextColor(Color.BLACK);
                                if ((millisUntilFinished / 1000) <= 3){
                                    txtCountdown.setTextColor(Color.RED);
                                }
                                txtCountdown.setText(String.valueOf(millisUntilFinished / 1000));
                            }

                            @Override
                            public void onFinish() {
                                checkQuestion(currentQuestionNumber, "No Answer");
                                // Setup Question
                                currentQuestionNumber++;
                                if (currentQuestionNumber < questionList.size()){
                                    String question = questionList.get(currentQuestionNumber).getQuestion();
                                    String a = questionList.get(currentQuestionNumber).getChoiceA();
                                    String b = questionList.get(currentQuestionNumber).getChoiceB();
                                    String c = questionList.get(currentQuestionNumber).getChoiceC();
                                    String d = questionList.get(currentQuestionNumber).getChoiceD();
                                    setQuestion(currentQuestionNumber, question, a, b, c, d);
                                    timer.start();
                                } else {
                                    timer.cancel();
                                    //ShowQuizDoneMessage();
                                    DisplayQuizResult();
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        public void run() {
                                            FinishingQuiz();
                                        }
                                    }, 2000);   //2 second
                                }
                            }
                        };
                        break;
                    case "90 secs":
                        // Set Countdown
                        long timernine = 90000;
                        // Set Countdown
                        timer = new CountDownTimer(timernine, 1000){
                            @SuppressLint("ResourceAsColor")
                            @Override
                            public void onTick(long millisUntilFinished) {
                                txtCountdown.setTextColor(Color.BLACK);
                                if ((millisUntilFinished / 1000) <= 3){
                                    txtCountdown.setTextColor(Color.RED);
                                }
                                txtCountdown.setText(String.valueOf(millisUntilFinished / 1000));
                            }

                            @Override
                            public void onFinish() {
                                checkQuestion(currentQuestionNumber, "No Answer");
                                // Setup Question
                                currentQuestionNumber++;
                                if (currentQuestionNumber < questionList.size()){
                                    String question = questionList.get(currentQuestionNumber).getQuestion();
                                    String a = questionList.get(currentQuestionNumber).getChoiceA();
                                    String b = questionList.get(currentQuestionNumber).getChoiceB();
                                    String c = questionList.get(currentQuestionNumber).getChoiceC();
                                    String d = questionList.get(currentQuestionNumber).getChoiceD();
                                    setQuestion(currentQuestionNumber, question, a, b, c, d);
                                    timer.start();
                                } else {
                                    timer.cancel();
                                    //ShowQuizDoneMessage();
                                    DisplayQuizResult();
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        public void run() {
                                            FinishingQuiz();
                                        }
                                    }, 2000);   //2 second
                                }
                            }
                        };
                        break;
                    case "120 secs":
                        // Set Countdown
                        long timeronetwenty = 120000;
                        // Set Countdown
                        timer = new CountDownTimer(timeronetwenty, 1000){
                            @SuppressLint("ResourceAsColor")
                            @Override
                            public void onTick(long millisUntilFinished) {
                                txtCountdown.setTextColor(Color.BLACK);
                                if ((millisUntilFinished / 1000) <= 3){
                                    txtCountdown.setTextColor(Color.RED);
                                }
                                txtCountdown.setText(String.valueOf(millisUntilFinished / 1000));
                            }

                            @Override
                            public void onFinish() {
                                checkQuestion(currentQuestionNumber, "No Answer");
                                // Setup Question
                                currentQuestionNumber++;
                                if (currentQuestionNumber < questionList.size()){
                                    String question = questionList.get(currentQuestionNumber).getQuestion();
                                    String a = questionList.get(currentQuestionNumber).getChoiceA();
                                    String b = questionList.get(currentQuestionNumber).getChoiceB();
                                    String c = questionList.get(currentQuestionNumber).getChoiceC();
                                    String d = questionList.get(currentQuestionNumber).getChoiceD();
                                    setQuestion(currentQuestionNumber, question, a, b, c, d);
                                    timer.start();
                                } else {
                                    timer.cancel();
                                    //ShowQuizDoneMessage();
                                    DisplayQuizResult();
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        public void run() {
                                            FinishingQuiz();
                                        }
                                    }, 2000);   //2 second
                                }
                            }
                        };
                        break;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Starts quiz when ready
        ReadyQuiz();

        btnA.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btnA.setEnabled(false);
                        btnB.setEnabled(false);
                        btnC.setEnabled(false);
                        btnD.setEnabled(false);
                        if (currentQuestionNumber < questionList.size()){
                            String answer = btnA.getText().toString().substring(3,btnA.getText().length());
                            Log.e("SHOWANSWER", answer);
                            checkQuestion(currentQuestionNumber, answer);

                            currentQuestionNumber++;
                            if (currentQuestionNumber < questionList.size()){
                                String question = questionList.get(currentQuestionNumber).getQuestion();
                                String a = questionList.get(currentQuestionNumber).getChoiceA();
                                String b = questionList.get(currentQuestionNumber).getChoiceB();
                                String c = questionList.get(currentQuestionNumber).getChoiceC();
                                String d = questionList.get(currentQuestionNumber).getChoiceD();
                                setQuestion(currentQuestionNumber, question, a, b, c, d);
                                btnA.setEnabled(true);
                                btnB.setEnabled(true);
                                btnC.setEnabled(true);
                                btnD.setEnabled(true);
                                timer.start();
                            } else {
                                // Finish Quiz
                                timer.cancel();
                                //ShowQuizDoneMessage();
                                DisplayQuizResult();
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    public void run() {
                                        FinishingQuiz();
                                    }
                                }, 2000);   //2 second

                            }
                        }
                    }
                }
        );
        btnB.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btnA.setEnabled(false);
                        btnB.setEnabled(false);
                        btnC.setEnabled(false);
                        btnD.setEnabled(false);
                        if (currentQuestionNumber < questionList.size()){
                            String answer = btnB.getText().toString().substring(3,btnB.getText().length());
                            Log.e("SHOWANSWER", answer);
                            checkQuestion(currentQuestionNumber, answer);

                            currentQuestionNumber++;
                            if (currentQuestionNumber < questionList.size()){
                                String question = questionList.get(currentQuestionNumber).getQuestion();
                                String a = questionList.get(currentQuestionNumber).getChoiceA();
                                String b = questionList.get(currentQuestionNumber).getChoiceB();
                                String c = questionList.get(currentQuestionNumber).getChoiceC();
                                String d = questionList.get(currentQuestionNumber).getChoiceD();
                                setQuestion(currentQuestionNumber, question, a, b, c, d);
                                btnA.setEnabled(true);
                                btnB.setEnabled(true);
                                btnC.setEnabled(true);
                                btnD.setEnabled(true);
                                timer.start();
                            } else {
                                // Finish Quiz
                                timer.cancel();
                                //ShowQuizDoneMessage();
                                DisplayQuizResult();
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    public void run() {
                                        FinishingQuiz();
                                    }
                                }, 2000);   //2 second

                            }
                        }
                    }
                }
        );
        btnC.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btnA.setEnabled(false);
                        btnB.setEnabled(false);
                        btnC.setEnabled(false);
                        btnD.setEnabled(false);
                        if (currentQuestionNumber < questionList.size()){
                            String answer = btnC.getText().toString().substring(3,btnC.getText().length());
                            Log.e("SHOWANSWER", answer);
                            checkQuestion(currentQuestionNumber, answer);

                            currentQuestionNumber++;
                            if (currentQuestionNumber < questionList.size()){
                                String question = questionList.get(currentQuestionNumber).getQuestion();
                                String a = questionList.get(currentQuestionNumber).getChoiceA();
                                String b = questionList.get(currentQuestionNumber).getChoiceB();
                                String c = questionList.get(currentQuestionNumber).getChoiceC();
                                String d = questionList.get(currentQuestionNumber).getChoiceD();
                                setQuestion(currentQuestionNumber, question, a, b, c, d);
                                btnA.setEnabled(true);
                                btnB.setEnabled(true);
                                btnC.setEnabled(true);
                                btnD.setEnabled(true);
                                timer.start();
                            } else {
                                // Finish Quiz
                                timer.cancel();
                                //ShowQuizDoneMessage();
                                DisplayQuizResult();
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    public void run() {
                                        FinishingQuiz();
                                    }
                                }, 2000);   //2 second

                            }
                        }
                    }
                }
        );
        btnD.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btnA.setEnabled(false);
                        btnB.setEnabled(false);
                        btnC.setEnabled(false);
                        btnD.setEnabled(false);
                        if (currentQuestionNumber < questionList.size()){
                            String answer = btnD.getText().toString().substring(3,btnD.getText().length());
                            Log.e("SHOWANSWER", answer);
                            checkQuestion(currentQuestionNumber, answer);

                            currentQuestionNumber++;
                            if (currentQuestionNumber < questionList.size()){
                                String question = questionList.get(currentQuestionNumber).getQuestion();
                                String a = questionList.get(currentQuestionNumber).getChoiceA();
                                String b = questionList.get(currentQuestionNumber).getChoiceB();
                                String c = questionList.get(currentQuestionNumber).getChoiceC();
                                String d = questionList.get(currentQuestionNumber).getChoiceD();
                                setQuestion(currentQuestionNumber, question, a, b, c, d);
                                btnA.setEnabled(true);
                                btnB.setEnabled(true);
                                btnC.setEnabled(true);
                                btnD.setEnabled(true);
                                timer.start();
                            } else {
                                // Finish Quiz
                                timer.cancel();
                                //ShowQuizDoneMessage();
                                DisplayQuizResult();
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    public void run() {
                                        FinishingQuiz();
                                    }
                                }, 2000);   //2 second

                            }
                        }
                    }
                }
        );

        btnShowMyAnswer.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DisplayQuizResult();
                    }
                }
        );
        btnEndQuiz.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        timer.cancel();
                        Intent intent = new Intent(RoomQuizPlayingMCActivity.this, RoomActivity.class);
                        // Room
                        intent.putExtra("ROOMID", ROOMID);
                        intent.putExtra("USERKEY", USERKEY);
                        intent.putExtra("FROMWHAT", "RoomQuiz");
                        intent.putExtra("USERLISTKEY", yourkey);
                        intent.putExtra("USERNUMBER", numberOfUser);
                        intent.putExtra("ISOWNER", ISOWNER);
                        intent.putExtra("OWNERNAME", OWNERNAME);
                        startActivity(intent);
                        finish();
                    }
                }
        );

    }
    public void getAllQuestion(){
        db.child("Questions").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> question = dataSnapshot.getChildren();
                        for (DataSnapshot child: question) {
                            checkreadycount++;
                            Question value = child.getValue(Question.class);
                            questionList.add(value);
                            Log.e("QUESTIONCHILD", value != null ? value.getQuestion() : null);

                            Log.e("QUESTIONSIZE", String.valueOf(questionList.size()));
                        }
                        Collections.shuffle(questionList);
                        // Wait
                        if (checkreadycount >= questionList.size()){
                            //Done Loading
                            Log.e("DONELOADING", "Done Loading Questions");
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    String question = questionList.get(currentQuestionNumber).getQuestion();
                                    String a = questionList.get(currentQuestionNumber).getChoiceA();
                                    String b = questionList.get(currentQuestionNumber).getChoiceB();
                                    String c = questionList.get(currentQuestionNumber).getChoiceC();
                                    String d = questionList.get(currentQuestionNumber).getChoiceD();
                                    setQuestion(currentQuestionNumber, question, a, b, c, d);
                                    progressDialog.dismiss();
                                    timer.start();
                                }
                            }, 3000);   //3 seconds
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(RoomQuizPlayingMCActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
    public void checkQuestion(int number, String answer){
        if (number < questionList.size()){
            int questionNumber = number + 1;
            String correctAnswer = questionList.get(number).getAnswer();
            if (Objects.equals(correctAnswer, answer)){
                quizResult.add("Question #"+questionNumber+":");
                quizResult.add("Correct Answer = "+questionList.get(currentQuestionNumber).getAnswer()+"");
                quizResult.add("Your Answer = "+answer+" = CORRECT");
                quizResult.add("--------------------------------------------");
                totalScore++;
            } else {
                quizResult.add("Question #"+questionNumber+":");
                quizResult.add("Correct Answer = "+questionList.get(currentQuestionNumber).getAnswer()+"");
                quizResult.add("Your Answer = "+answer+" = WRONG");
                quizResult.add("--------------------------------------------");
            }

        } else {
            timer.cancel();
        }
    }
    public void DisplayQuizResult(){
        Log.e("QUIZRESULTSIZE", String.valueOf(quizResult.size()));
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("QUIZ RESULT: \n\n");
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        for (int i = 0; i < quizResult.size(); i++){
            stringBuilder.append(quizResult.get(i) + "\n");
        }
        stringBuilder.append("\n");
        stringBuilder.append("Total Score: "+totalScore+"/"+questionList.size()+" \n");
        builder.setMessage(stringBuilder);
        builder.setCancelable(true);
        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

        StudentRecord studentRecord = new StudentRecord(PARTICIPANTNAME, totalScore +"/"+questionList.size() ,PARTICIPANTNAME);
        SaveResultToRecords(studentRecord, QUIZOWNER, ROOMTITLE, QUIZKEY, ROOMID, txtQuizTitle.getText().toString());
    }

    private void SaveResultToRecords(StudentRecord studentRecord,String quizowner, String roomTitle, String quizId, String roomid, String quiz_title) {
        // Save Records
        Record record = new Record();
        record.setRoom_id(roomid);
        record.setRoom_title(roomTitle);
        record.setRoom_owner(quizowner);
        record.setQuiz_id(quizId);
        record.setQuiz_title(quiz_title);
        mRoom.setValue(record);
        // Individual Record
        HashMap<String, String> map = new HashMap<>();
        map.put("studentname", studentRecord.getStudentname());
        map.put("score", studentRecord.getScore());
        map.put("key", studentRecord.getStudentname());

        // Save
        DatabaseReference data = FirebaseDatabase.getInstance().getReference().child("Scores").child(String.valueOf(QUIZKEY)).child(studentRecord.getStudentname());
        data.setValue(map);
        data.child("roomOwner").setValue(quizowner);
    }

    @SuppressLint("SetTextI18n")
    public void setQuestion(int number, String question, String a, String b, String c, String d){
        int questionNumber = number + 1;
        txtQuestionNumber.setText("Question #" +questionNumber);
        txtQuestion.setText(question);
        btnA.setText("A. " +a);
        btnB.setText("B. " +b);
        btnC.setText("C. " +c);
        btnD.setText("D. " +d);
    }

    public void FinishingQuiz(){
        txtChoices.setVisibility(View.INVISIBLE);
        txtQuestionNumber.setVisibility(View.INVISIBLE);
        txtQuestion.setVisibility(View.INVISIBLE);
        btnA.setVisibility(View.INVISIBLE);
        btnB.setVisibility(View.INVISIBLE);
        btnC.setVisibility(View.INVISIBLE);
        btnD.setVisibility(View.INVISIBLE);
    }

    public void ReadyQuiz(){
        currentQuestionNumber = 0;
        totalScore = 0;

        txtChoices.setVisibility(View.VISIBLE);
        txtQuestionNumber.setVisibility(View.VISIBLE);
        txtQuestion.setVisibility(View.VISIBLE);
        btnA.setVisibility(View.VISIBLE);
        btnB.setVisibility(View.VISIBLE);
        btnC.setVisibility(View.VISIBLE);
        btnD.setVisibility(View.VISIBLE);

        getAllQuestion();
    }
    public void ShowQuizDoneMessage(){
        QuizDoneDialog.setContentView(R.layout.dialog_endofquiz);
        QuizDoneDialog.setCancelable(false);
        QuizDoneDialog.show();
        Button btnSeeResult = (Button) QuizDoneDialog.findViewById(R.id.btnSeeResult);
        btnSeeResult.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        QuizDoneDialog.dismiss();
                        DisplayQuizResult();
                    }
                }
        );

        Button btnClose = (Button) QuizDoneDialog.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        QuizDoneDialog.dismiss();
                    }
                }
        );
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

}
