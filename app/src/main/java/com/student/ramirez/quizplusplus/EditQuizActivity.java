package com.student.ramirez.quizplusplus;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditQuizActivity extends AppCompatActivity {
    DatabaseReference db;
    private ArrayAdapter<CharSequence> timerAdapter;
    private TextView txtID, txtType, txtTimer;
    private EditText etQuizTitle, etQuizDesc;
    private Button btnFinish, btnCancel, btnEditQuestion;
    private Spinner cbTimerLimit;
    private String time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_quiz);
        db = FirebaseDatabase.getInstance().getReference().child("Quizzes");
        etQuizTitle = (EditText) findViewById(R.id.etQuizTitle);
        etQuizDesc = (EditText) findViewById(R.id.etQuizDesc);
        txtID = (TextView) findViewById(R.id.txtID);
        txtType = (TextView) findViewById(R.id.txtType);
        txtTimer = (TextView) findViewById(R.id.txtTimer);
        btnFinish = (Button) findViewById(R.id.btnFinish);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnEditQuestion = (Button) findViewById(R.id.btnEditQuestions);
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            final String id = extras.getString("QUIZID");
            String type = extras.getString("QUIZTYPE");
            String title = extras.getString("QUIZTITLE");
            String desc = extras.getString("QUIZDESC");
            time = extras.getString("QUIZTIMER");
            txtID.setText(id);
            txtType.setText(type);
            etQuizTitle.setText(title);
            etQuizDesc.setText(desc);
            txtTimer.setText(time);
            btnFinish.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String newTitle = etQuizTitle.getText().toString().trim();
                            String newDesc = etQuizDesc.getText().toString().trim();
                            String newtimer = txtTimer.getText().toString();

                            if (!newTitle.isEmpty() && !newDesc.isEmpty()){
                                db.child(id).child("quiz_title").setValue(newTitle);
                                db.child(id).child("quiz_desc").setValue(newDesc);
                                db.child(id).child("quiz_timer").setValue(newtimer);
                                Toast.makeText(EditQuizActivity.this, "Quiz Updated Successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(EditQuizActivity.this, MyQuizzesActivity.class));
                                finish();
                            }
                        }
                    }
            );

            // Timer
            cbTimerLimit = (Spinner) findViewById(R.id.cbTimerLimit);
            timerAdapter = ArrayAdapter.createFromResource(this, R.array.timers, android.R.layout.simple_spinner_item);
            timerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            cbTimerLimit.setAdapter(timerAdapter);
            cbTimerLimit.setSelection(timerAdapter.getPosition(txtTimer.getText().toString()));
            cbTimerLimit.setOnItemSelectedListener(
                    new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            //Toast.makeText(CreateQuizActivity.this, parent.getItemIdAtPosition(position) + "is selected.", Toast.LENGTH_SHORT).show();
                            ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE); // Make the textColor white
                            txtTimer.setText(cbTimerLimit.getSelectedItem().toString());
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    }
            );
            btnEditQuestion.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(EditQuizActivity.this, EditQuestionActivity.class);
                            intent.putExtra("QUIZTITLE", etQuizTitle.getText().toString());
                            intent.putExtra("QUIZID", txtID.getText().toString());
                            intent.putExtra("QUIZDESC", etQuizDesc.getText().toString());
                            intent.putExtra("QUIZTIMER", txtTimer.getText().toString());
                            switch (txtType.getText().toString()){
                                case "True or False":
                                    intent.putExtra("QUIZTYPE", 1);
                                    break;
                                case "Multiple Choice":
                                    intent.putExtra("QUIZTYPE", 2);
                                    break;
                                case "Identification":
                                    intent.putExtra("QUIZTYPE", 3);
                                    break;
                            }
                            intent.putExtra("USERFROM", "EDIT");
                            startActivity(intent);
                        }
                    }
            );
            btnCancel.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(EditQuizActivity.this, MyQuizzesActivity.class));
                            finish();
                        }
                    }
            );
        }
    }
}
