package com.student.ramirez.quizplusplus;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EditQuestionActivity extends AppCompatActivity {

    List<Question> questionList;
    List<Question> editquestionList;
    private Button btnAddQuestion, btnBack, btnFinish;
    private TextView txtQuizTitle, txtQN;
    private int quizType;
    private String QuizTypeEdit;
    private EditQuestionAdapter questionAdapter;
    private ProgressDialog progressDialog;
    private DatabaseReference QuizRef, QuestionRef;
    private FirebaseAuth mFirebaseAuth;
    protected static int numberquestion = 0;
    private String descExtra;
    private String timeExtra;
    // Dialogs
    private Dialog MultipleChoiceInstruction;
    private Dialog TrueFalseInstruction;
    // Room
    private String RoomID;
    private String RoomTitle;
    private String RoomType;
    private String RoomPassword;
    private int RoomSize;
    private boolean fromRoomCreate;
    private int checkreadycount = 0;
    RecyclerView recyclerViewQuestion;
    private String ID;
    public EditQuestionActivity() {
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_question);
        // Firebase
        QuizRef = FirebaseDatabase.getInstance().getReference().child("Quizzes");
        QuestionRef = FirebaseDatabase.getInstance().getReference().child("Quizzes");
        mFirebaseAuth = FirebaseAuth.getInstance();
        // Widgets
        txtQuizTitle = findViewById(R.id.txtScore);
        btnBack = findViewById(R.id.btnBack);
        txtQN = findViewById(R.id.txtQuestion);
        btnFinish = findViewById(R.id.btnFinish);
        progressDialog = new ProgressDialog(this);
        // Setup Dialog Instruction
        MultipleChoiceInstruction = new Dialog(this);
        TrueFalseInstruction = new Dialog(this);
        // Add Question
        questionList = new ArrayList<>();
        editquestionList = new ArrayList<>();
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            String from = extras.getString("FROM");
            String titleExtra;
            fromRoomCreate = false;
            ID = extras.getString("QUIZID");
            quizType = extras.getInt("QUIZTYPE");
            titleExtra = extras.getString("QUIZTITLE");
            descExtra = extras.getString("QUIZDESC");
            timeExtra = extras.getString("QUIZTIMER");
            txtQuizTitle.setText(String.valueOf(titleExtra));
            switch (quizType){
                case 1:
                    ShowTrueFalseInstruction();
                    break;
                case 2:
                    ShowMultipleChoiceInstruction();
                    break;
                case 3:
                    break;
            }
            QuestionRef.child(ID).child("Questions").addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Iterable<DataSnapshot> question = dataSnapshot.getChildren();
                            for (DataSnapshot child: question) {
                                checkreadycount++;
                                Question value = child.getValue(Question.class);
                                editquestionList.add(value);
                                Log.e("QUESTIONCHILD", value != null ? value.getQuestion() : null);
                                Log.e("QUESTIONSIZE", String.valueOf(editquestionList.size()));
                            }
                            DisplayQuestions();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(EditQuestionActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                        }

                    }
            );

        }
        recyclerViewQuestion = (RecyclerView) findViewById(R.id.recyclerViewQuestion);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerViewQuestion.setItemAnimator(new DefaultItemAnimator());
        recyclerViewQuestion.setHasFixedSize(true);
        recyclerViewQuestion.setLayoutManager(layoutManager);
        questionAdapter = new EditQuestionAdapter(questionList, new EditQuestionAdapter.QuestionAdapterListener() {
            @Override
            public void DeleteButtonOnClicked(View view, int position) {
                Log.e("WhichButtonDelete", "Button at position " +position );
                questionAdapter.removeAt(position);
                numberquestion = numberquestion - 1;
                txtQN.setText(String.valueOf(questionList.size()));
            }
        });
        recyclerViewQuestion.setAdapter(questionAdapter);
        // Add
        btnAddQuestion = findViewById(R.id.btnAddQuestion);
        btnAddQuestion.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (quizType){
                            case 1: // True or False
                                String choiceA = "True";
                                String choiceB = "False";
                                AddQuestion(questionAdapter.getItemCount(), "", "", quizType, choiceA, choiceB, "", "");
                                break;
                            case 2: // Multiple Choice
                                AddQuestion(questionAdapter.getItemCount(), "", "", quizType, "", "", "", "");
                                break;
                            case 3: // Identification
                                break;
                        }
                        recyclerViewQuestion.smoothScrollToPosition(questionAdapter.getItemCount());
                        recyclerViewQuestion.setItemViewCacheSize(questionAdapter.getItemCount()); // This single line of code saves this entire project
                    }
                }
        );
        btnBack.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                }
        );
        btnFinish.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isSuccess = true;
                        if (questionAdapter.getItemCount() != 0){
                            for (int i = 0; i < questionList.size(); i++){
                                if (questionList.get(i).getAnswer().isEmpty() || questionList.get(i).getQuestion().isEmpty() || questionList.get(i).getAnswer().equals("None")){
                                    isSuccess = false;
                                }
                            }
                            if (isSuccess){
                                progressDialog.setMessage("Finishing ..");
                                progressDialog.setCancelable(true);
                                progressDialog.show();
                                final FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
                                HashMap<String, String> QuizInformation = new HashMap<String, String>();
                                QuizInformation.put("quiz_title", txtQuizTitle.getText().toString());
                                QuizInformation.put("quiz_desc", descExtra);
                                QuizInformation.put("quiz_owner", currentUser.getEmail());
                                QuizInformation.put("quiz_type", String.valueOf(quizType));
                                QuizInformation.put("quiz_id", ID);
                                QuizInformation.put("quiz_questionsize", String.valueOf(questionList.size()));
                                QuizInformation.put("quiz_timer", timeExtra);
                                if (!fromRoomCreate){
                                    // User from Quiz
                                    QuizRef.child(ID).setValue(QuizInformation).addOnCompleteListener(
                                            new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        for (int i = 0; i < questionList.size(); i++)
                                                        {
                                                            Log.e("FinishEvent", String.valueOf(i));
                                                            Question question = questionList.get(i);
                                                            QuestionRef.child(ID).child("Questions").child(String.valueOf(i)).child("answer").setValue(question.getAnswer());
                                                            QuestionRef.child(ID).child("Questions").child(String.valueOf(i)).child("question").setValue(question.getQuestion());
                                                            QuestionRef.child(ID).child("Questions").child(String.valueOf(i)).child("quiz_id").setValue(i);
                                                            QuestionRef.child(ID).child("Questions").child(String.valueOf(i)).child("type").setValue(question.getType());
                                                            QuestionRef.child(ID).child("Questions").child(String.valueOf(i)).child("choiceA").setValue(question.getChoiceA());
                                                            QuestionRef.child(ID).child("Questions").child(String.valueOf(i)).child("choiceB").setValue(question.getChoiceB());
                                                            QuestionRef.child(ID).child("Questions").child(String.valueOf(i)).child("choiceC").setValue(question.getChoiceC());
                                                            QuestionRef.child(ID).child("Questions").child(String.valueOf(i)).child("choiceD").setValue(question.getChoiceD());
                                                        }
                                                        progressDialog.dismiss();
                                                        //QuestionRef.child(uniqueKey).child("Questions").setValue(questionList);
                                                        //CreateQuizActivity.createQuizActivity.finish();
                                                        finish();
                                                        Intent intent = new Intent(EditQuestionActivity.this, MyQuizzesActivity.class);
                                                        startActivity(intent);
                                                        Toast.makeText(EditQuestionActivity.this, "Quiz edited successfully", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(EditQuestionActivity.this, "Quiz edited Failed", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }
                                    );
                                }
                            } else {
                                Toast.makeText(EditQuestionActivity.this, "Red Question Found.", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(EditQuestionActivity.this, "No Question Found", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );

    }

    private void DisplayQuestions(){
        for (int i = 0; i < editquestionList.size(); i++){
            DisQuestion(i, editquestionList.get(i).getQuestion(), editquestionList.get(i).getAnswer(),editquestionList.get(i).getType(),editquestionList.get(i).getChoiceA(), editquestionList.get(i).getChoiceB(), editquestionList.get(i).getChoiceC(), editquestionList.get(i).getChoiceD());
        }
    }
    private void DisQuestion(int id, String question, String answer, int type, String choiceA, String choiceB, String choiceC, String choiceD){
        questionList.add(
                new Question(
                        id,
                        question,
                        answer,
                        type,
                        choiceA,
                        choiceB,
                        choiceC,
                        choiceD
                ));
        numberquestion++;
        txtQN.setText(String.valueOf(questionList.size()));
    }

    private void AddQuestion(int id, String question, String answer, int type, String choiceA, String choiceB, String choiceC, String choiceD){
        questionList.add(
                new Question(
                        id,
                        question,
                        answer,
                        type,
                        choiceA,
                        choiceB,
                        choiceC,
                        choiceD
                ));
        numberquestion++;
        txtQN.setText(String.valueOf(questionList.size()));
        Toast.makeText(this, "Question Added", Toast.LENGTH_SHORT).show();
    }
    public void ShowMultipleChoiceInstruction(){
        MultipleChoiceInstruction.setContentView(R.layout.dialog_instruction_mc);
        MultipleChoiceInstruction.show();
        MultipleChoiceInstruction.setCancelable(true);

        TextView btnHide = (TextView) MultipleChoiceInstruction.findViewById(R.id.btnHideInstruction);
        btnHide.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MultipleChoiceInstruction.dismiss();
                    }
                }
        );
    }
    public void ShowTrueFalseInstruction(){
        TrueFalseInstruction.setContentView(R.layout.dialog_instruction_tf);
        TrueFalseInstruction.show();
        TrueFalseInstruction.setCancelable(true);

        TextView btnHide = (TextView) TrueFalseInstruction.findViewById(R.id.btnHideInstruction);
        btnHide.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TrueFalseInstruction.dismiss();
                    }
                }
        );
    }

    @Override
    public void onBackPressed() {
        if (questionList.size() != 0){
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Confirmation");
            builder.setMessage("This will reset all the changes you've done, Do you still want to continue?");
            builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    EditQuestionActivity.super.onBackPressed();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        } else {
            super.onBackPressed();
        }
    }
}