package com.student.ramirez.quizplusplus;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class RoomResultRecordActivity extends AppCompatActivity {

    RecyclerView recyclerViewStudentRecords;
    FirebaseRecyclerAdapter<StudentRecord, RoomResultRecordActivity.StudentRecordViewHolder> firebaseRecyclerAdapter;

    DatabaseReference mDatabase;
    FirebaseAuth mFirebaseAuth;

    private String QUIZID;

    // Widgets
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_result_record);

        mFirebaseAuth = FirebaseAuth.getInstance();
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            QUIZID = extras.getString("QUIZID");
            mDatabase = FirebaseDatabase.getInstance().getReference().child("Scores").child(String.valueOf(QUIZID));
            Log.e("QUIZID", QUIZID);
        }

        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(RoomResultRecordActivity.this, RecordActivity.class));
                    }
                }
        );

        // Creating a Recycler View for Quizzes
        recyclerViewStudentRecords = (RecyclerView) findViewById(R.id.recyclerViewStudentRecords);
        recyclerViewStudentRecords.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerViewStudentRecords.setLayoutManager(linearLayoutManager);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseLoadRecords();
    }
    public void FirebaseLoadRecords(){
        // Select Query
        Query query = mDatabase.orderByChild("roomOwner").equalTo(mFirebaseAuth.getCurrentUser().getEmail());
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<StudentRecord, RoomResultRecordActivity.StudentRecordViewHolder>(
                StudentRecord.class,
                R.layout.student_record_list,
                RoomResultRecordActivity.StudentRecordViewHolder.class,
                query
        ) {
            @Override
            protected void populateViewHolder(final RoomResultRecordActivity.StudentRecordViewHolder viewHolder, StudentRecord model, final int position) {

                viewHolder.setStudentName(model.getStudentname());
                viewHolder.setScore(model.getScore());
                TextView btnDelete = (TextView) viewHolder.mView.findViewById(R.id.btnDelete);
                btnDelete.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RoomResultRecordActivity.this);
                                builder.setTitle("Delete");
                                builder.setMessage("This action cannot be undone, continue?");
                                builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Delete
                                        firebaseRecyclerAdapter.getRef(position).removeValue();
                                    }
                                });
                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.show();
                            }
                        }
                );
                //ImageButton btnEditInformation = (ImageButton) viewHolder.mView.findViewById(R.id.btnEditInformation);
            }
        };
        recyclerViewStudentRecords.setAdapter(firebaseRecyclerAdapter);
    }
    public static class StudentRecordViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public StudentRecordViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setStudentName(String name){
            TextView txtStudentName = (TextView) mView.findViewById(R.id.txtName);
            txtStudentName.setText(name);

        }
        public void setScore(String score){
            TextView txtScore = (TextView) mView.findViewById(R.id.txtScore);
            txtScore.setText(score);
        }

    }
}
