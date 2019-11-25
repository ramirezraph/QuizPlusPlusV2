package com.student.ramirez.quizplusplus;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class RecordActivity extends AppCompatActivity {

    RecyclerView recyclerViewRecords;
    FirebaseRecyclerAdapter<Record, RecordActivity.RecordViewHolder> firebaseRecyclerAdapter;

    DatabaseReference mDatabase;
    FirebaseAuth mFirebaseAuth;

    // Widgets
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Records");

        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(RecordActivity.this, MainActivity.class));
                        finish();
                    }
                }
        );

        // Creating a Recycler View for Quizzes
        recyclerViewRecords = (RecyclerView) findViewById(R.id.recyclerViewStudentRecords);
        recyclerViewRecords.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerViewRecords.setLayoutManager(linearLayoutManager);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseLoadRecords();
    }
    public void FirebaseLoadRecords(){

        // Select Query
        Query firebaseSearchQuery = mDatabase.orderByChild("room_owner").equalTo(mFirebaseAuth.getCurrentUser().getEmail());
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Record, RecordActivity.RecordViewHolder>(
                Record.class,
                R.layout.room_record_list,
                RecordActivity.RecordViewHolder.class,
                firebaseSearchQuery
        ) {
            @Override
            protected void populateViewHolder(final RecordActivity.RecordViewHolder viewHolder, final Record model, final int position) {
                //final String key = getRef(position).getKey();
                viewHolder.setRoomId(model.getRoom_id());
                viewHolder.setRoomTitle(model.getRoom_title());
                viewHolder.setQuizTitle(model.getQuiz_title());

                viewHolder.mView.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(RecordActivity.this, RoomResultRecordActivity.class);
                                intent.putExtra("QUIZID", model.getQuiz_id());
                                startActivity(intent);
                            }
                        }
                );
                Button btnDeleteThis = viewHolder.mView.findViewById(R.id.btnDeleteThis);
                btnDeleteThis.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RecordActivity.this);
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
            }
        };
        recyclerViewRecords.setAdapter(firebaseRecyclerAdapter);
    }
    public static class RecordViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public RecordViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setRoomId(String id){
            TextView RoomId = (TextView) mView.findViewById(R.id.txtRoomID);
            RoomId.setText(id);

        }
        public void setRoomTitle(String title){
            TextView RoomTitle = (TextView) mView.findViewById(R.id.txtRoomTitle);
            RoomTitle.setText(title);
        }
        public void setQuizTitle(String title){
            TextView QuizTitle = (TextView) mView.findViewById(R.id.txtScore);
            QuizTitle.setText(title);
        }
    }
}
