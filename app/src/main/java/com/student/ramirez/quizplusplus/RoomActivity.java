package com.student.ramirez.quizplusplus;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class RoomActivity extends AppCompatActivity {

    private Button btnEnd, btnViewQuiz, btnRoomInfo;
    DatabaseReference mOnlineQuiz;
    DatabaseReference mDatabase;
    DatabaseReference mUserData;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference NameRef;

    private String name;

    private RecyclerView recyclerViewUserList;
    private String ROOMID;
    private String USERKEY;
    private String QUIZKEY;
    private String FROMWHAT;
    private String yourkey;
    private String ISOWNER;
    private String OWNERNAME;
    private int numberOfUser;

    // Room Info
    private String thisroomid = "";
    private String thisroomtitle;
    private String thisroomisPrivate;
    private String thisroomnumberofuser;
    private String thisroomowner;
    private String thisroomsize;
    private String thisroomquizid;

    // Quiz
    private Dialog RoomQuizDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        btnEnd = (Button) findViewById(R.id.btnEndRoom);
        btnViewQuiz = (Button) findViewById(R.id.btnViewQuiz);
        btnRoomInfo = (Button) findViewById(R.id.btnRoomInfo);

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            ROOMID = extras.getString("ROOMID");
            USERKEY = extras.getString("USERKEY");
            QUIZKEY = extras.getString("USERKEY");
            yourkey = extras.getString("USERLISTKEY");
            FROMWHAT = extras.getString("FROMWHAT");
            numberOfUser = extras.getInt("USERNUMBER");
            OWNERNAME = extras.getString("OWNERNAME");
            ISOWNER = extras.getString("ISOWNER");
            switch (ISOWNER){
                case "YES":
                    btnViewQuiz.setVisibility(View.VISIBLE);
                    btnViewQuiz.setText("Back to Lobby");
                    btnEnd.setText("End Quiz");
                    break;
                case "NO":
                    btnViewQuiz.setVisibility(View.VISIBLE);
                    btnEnd.setText("Leave");
                    break;
            }
            TextView txtRoomNumber = (TextView) findViewById(R.id.txtResult);
            txtRoomNumber.setText("Room #: "+ROOMID);
        }

        mOnlineQuiz = FirebaseDatabase.getInstance().getReference().child("OnlineQuizzes");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Room");
        mUserData = FirebaseDatabase.getInstance().getReference().child("Room").child(ROOMID).child("UserList");

        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        NameRef = FirebaseDatabase.getInstance().getReference().child("Accounts").child(user.getUid()).child("Name");
        NameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        switch (FROMWHAT){
            case "Lobby":
                numberOfUser++;
                mDatabase.child(ROOMID).child("roomNumberOfUser").setValue(numberOfUser);
                Log.e("NUMBEROFUSER", String.valueOf(numberOfUser));
                break;
            case "RoomQuiz":
                break;
        }

        mDatabase.child(ROOMID).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, String> map = (Map)dataSnapshot.getValue();
                        thisroomid = map.get("roomId");
                        thisroomtitle= map.get("roomTitle");
                        thisroomisPrivate = String.valueOf(map.get("private"));
                        thisroomnumberofuser = String.valueOf(map.get("roomNumberOfUser"));
                        thisroomowner = map.get("roomOwner");
                        thisroomsize= String.valueOf(map.get("roomSize"));
                        thisroomquizid= map.get("roomQuizID");
                        Log.e("thisroomid", thisroomid);
                        Log.e("thisroomtitle", thisroomtitle);
                        Log.e("thisroomisPrivate", thisroomisPrivate);
                        Log.e("thisroomnumberofuser", thisroomnumberofuser);
                        Log.e("thisroomowner", thisroomowner);
                        Log.e("thisroomsize", thisroomsize);
                        Log.e("thisroomquizid", thisroomquizid);

                        HandleViewClick(thisroomquizid);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );

        btnRoomInfo.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ShowDialogRoomInfo(thisroomtitle, thisroomid, thisroomowner, thisroomnumberofuser, thisroomsize, thisroomisPrivate);
                    }
                }
        );

        btnEnd.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String btnText = btnEnd.getText().toString();
                        switch (btnText){
                            case "Leave":
                                mUserData.addListenerForSingleValueEvent(
                                        new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()){
                                                    AlertDialog.Builder leavebuilder = new AlertDialog.Builder(RoomActivity.this);
                                                    leavebuilder.setTitle("Confirm");
                                                    leavebuilder.setMessage("Are you you want to leave?");
                                                    leavebuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            mUserData.child(yourkey).removeValue();
                                                            numberOfUser--;
                                                            Log.e("NUMBEROFUSERBEFORESAVE", String.valueOf(numberOfUser));
                                                            mDatabase.child(ROOMID).child("roomNumberOfUser").setValue(numberOfUser);
                                                            startActivity(new Intent(RoomActivity.this, QuizOnlineActivity.class));
                                                            finish();
                                                        }
                                                    });
                                                    leavebuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.dismiss();
                                                        }
                                                    });
                                                    leavebuilder.show();
                                                } else {
                                                    startActivity(new Intent(RoomActivity.this, QuizOnlineActivity.class));
                                                    finish();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        }
                                );
                                break;
                            case "End Quiz":
                                AlertDialog.Builder endbuilder = new AlertDialog.Builder(RoomActivity.this);
                                endbuilder.setTitle("Confirm");
                                endbuilder.setMessage("Are you sure you want to end and delete this room?");
                                endbuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mDatabase.child(ROOMID).removeValue();
                                        mOnlineQuiz.child(QUIZKEY).removeValue();
                                        startActivity(new Intent(RoomActivity.this, QuizOnlineActivity.class));
                                        finish();
                                    }
                                });
                                endbuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                endbuilder.show();
                                break;
                        }
                    }
                }
        );

        // Creating a Recycler View for Rooms
        recyclerViewUserList = (RecyclerView) findViewById(R.id.recyclerViewUserList);
        recyclerViewUserList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerViewUserList.setLayoutManager(linearLayoutManager);

        RoomQuizDialog = new Dialog(this);

    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUserSearchRoom("");
    }

    public void FirebaseUserSearchRoom(String searchText){
        // Search Query
        Query firebaseSearchQuery = mUserData.orderByChild("name").startAt(searchText).endAt(searchText + "\uf8ff");
        FirebaseRecyclerAdapter<UserAtRoom, RoomActivity.UserListViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<UserAtRoom, RoomActivity.UserListViewHolder>(
                UserAtRoom.class,
                R.layout.room_user_list,
                RoomActivity.UserListViewHolder.class,
                firebaseSearchQuery
        ) {
            @Override
            protected void populateViewHolder(UserListViewHolder viewHolder, UserAtRoom model, int position) {
                final String key = getRef(position).getKey();
                viewHolder.setUserName(model.getName());
                viewHolder.setVisibleButton(FROMWHAT);

            }
        };
        recyclerViewUserList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class UserListViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public UserListViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setUserName(String name){

            TextView userName = (TextView) mView.findViewById(R.id.txtUserName);
            userName.setText(name);
        }
        public void setVisibleButton(String FROMWHAT){
            Button btn = (Button) mView.findViewById(R.id.btnKick);
            switch (FROMWHAT){
                case "RoomQuiz":
                    btn.setVisibility(View.GONE);
                    break;
                case "Lobby":
                    btn.setVisibility(View.GONE);
                    break;
            }
        }
    }

    public void HandleViewClick(final String quizref){
        btnViewQuiz.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String state = btnViewQuiz.getText().toString();
                        switch (state){
                            case "View Quiz":
                                // show a dialog about the quiz and play button
                                mOnlineQuiz.child(quizref).addListenerForSingleValueEvent(
                                        new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()){
                                                    Map<String, String> map = (Map)dataSnapshot.getValue();
                                                    String quiztype = "";
                                                    String title = map.get("quiz_title");
                                                    String id = map.get("quiz_id");
                                                    String desc = map.get("quiz_desc");
                                                    String owner = map.get("quiz_owner");
                                                    String type = map.get("quiz_type");
                                                    String size = map.get("quiz_questionsize");
                                                    String timer = map.get("quiz_timer");
                                                    switch (type){
                                                        case "1":
                                                            quiztype = "True or False";
                                                            break;
                                                        case "2":
                                                            quiztype = "Multiple Choice";
                                                            break;
                                                        case "3":
                                                            quiztype = "Identification";
                                                            break;
                                                        default:
                                                            quiztype = "None";
                                                            break;
                                                    }
                                                    ShowRoomQuizDialog(title, id, desc, owner, quiztype, size, timer);
                                                } else {
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(RoomActivity.this);
                                                    builder.setTitle("Message");
                                                    builder.setMessage("Unable to view. The room and its quiz no longer exists");
                                                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.dismiss();
                                                        }
                                                    });
                                                    builder.show();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        }
                                );
                                break;
                            case "Back to Lobby":
                                AlertDialog.Builder leavebuilder = new AlertDialog.Builder(RoomActivity.this);
                                leavebuilder.setTitle("Confirm");
                                leavebuilder.setMessage("Are you you want to leave?");
                                leavebuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mUserData.child(yourkey).removeValue();
                                        numberOfUser--;
                                        Log.e("NUMBEROFUSERBEFORESAVE", String.valueOf(numberOfUser));
                                        mDatabase.child(ROOMID).child("roomNumberOfUser").setValue(numberOfUser);
                                        startActivity(new Intent(RoomActivity.this, QuizOnlineActivity.class));
                                        finish();
                                    }
                                });
                                leavebuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                leavebuilder.show();
                                break;
                        }

                    }
                }
        );
    }

    public void ShowDialogRoomInfo(String title, String id, String owner, String number, String size, String isprivate){
        StringBuilder builder = new StringBuilder();
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        builder.append("Room Name: ").append(title).append(" \n");
        builder.append("Room ID: ").append(id).append(" \n");
        builder.append("Created By: ").append(owner).append(" \n");
        builder.append("Current No. of User: ").append(number).append(" \n");
        builder.append("Room Size: ").append(size).append(" \n");
        String type = "";
        if (isprivate != null){
            switch (isprivate){
                case "true":
                    type = "Private";
                    break;
                case "false":
                    type = "Public";
                    break;
            }
        }
        builder.append("Room Type: ").append(type).append("\n");
        dialog.setTitle("Room Information:");
        dialog.setMessage(builder);
        dialog.setCancelable(true);
        dialog.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    // Set the Quiz Dialog
    public void ShowRoomQuizDialog(final String title, final String id, String desc, final String owner, final String type, String size, String timer){
        final TextView txtTitle, txtDesc, txtOwner, txtNoQuiz, txtType, txtQuizID, txtTimerLimit;
        Button btnPlay, btnClose;

        // Layout
        RoomQuizDialog.setContentView(R.layout.dialog_quiz_info);

        // Widgets
        txtTitle = (TextView) RoomQuizDialog.findViewById(R.id.txtTitle);
        txtDesc = (TextView) RoomQuizDialog.findViewById(R.id.txtQuizDesc);
        txtOwner = (TextView) RoomQuizDialog.findViewById(R.id.txtQuizCreator);
        txtNoQuiz = (TextView) RoomQuizDialog.findViewById(R.id.txtNoQuestion);
        txtType = (TextView) RoomQuizDialog.findViewById(R.id.txtQuizType);
        txtQuizID = (TextView) RoomQuizDialog.findViewById(R.id.txtQuizID);
        txtTimerLimit = (TextView) RoomQuizDialog.findViewById(R.id.txttimertimer);

        btnPlay = (Button) RoomQuizDialog.findViewById(R.id.btnPlayQuizNow);
        btnClose = (Button) RoomQuizDialog.findViewById(R.id.btnCloseDialog);

        // Events / Values
        txtTitle.setText(title);
        txtQuizID.setText(id);
        txtDesc.setText(desc);
        txtOwner.setText(owner);
        txtType.setText(type);
        txtNoQuiz.setText(size);
        txtTimerLimit.setText(timer);

        btnPlay.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Begin play = Intent
                        switch (type){
                            case "True or False":
                                Intent tfintent = new Intent(RoomActivity.this, RoomQuizPlayingTFActivity.class);
                                tfintent.putExtra("QUIZTITLE", title);
                                tfintent.putExtra("QUIZKEY", id);
                                tfintent.putExtra("QUIZOWNER", owner);
                                // Room
                                tfintent.putExtra("ROOMID", ROOMID);
                                tfintent.putExtra("USERKEY", USERKEY);
                                tfintent.putExtra("FROMWHAT", FROMWHAT);
                                tfintent.putExtra("USERLISTKEY", yourkey);
                                tfintent.putExtra("USERNUMBER", numberOfUser);
                                tfintent.putExtra("ISOWNER", ISOWNER);
                                tfintent.putExtra("PARTICIPANTNAME", name);
                                tfintent.putExtra("OWNERNAME", OWNERNAME);
                                tfintent.putExtra("ROOMTITLE", thisroomtitle);
                                startActivity(tfintent);
                                break;
                            case "Multiple Choice":
                                Intent mcintent = new Intent(RoomActivity.this, RoomQuizPlayingMCActivity.class);
                                mcintent.putExtra("QUIZTITLE", title);
                                mcintent.putExtra("QUIZKEY", id);
                                mcintent.putExtra("QUIZOWNER", owner);
                                // Room
                                mcintent.putExtra("ROOMID", ROOMID);
                                mcintent.putExtra("USERKEY", USERKEY);
                                mcintent.putExtra("FROMWHAT", FROMWHAT);
                                mcintent.putExtra("USERLISTKEY", yourkey);
                                mcintent.putExtra("USERNUMBER", numberOfUser);
                                mcintent.putExtra("ISOWNER", ISOWNER);
                                mcintent.putExtra("PARTICIPANTNAME", name);
                                mcintent.putExtra("OWNERNAME", OWNERNAME);
                                mcintent.putExtra("ROOMTITLE", thisroomtitle);
                                startActivity(mcintent);
                                break;
                            case "Identification":
                                break;
                            case "None":
                                Toast.makeText(RoomActivity.this, "Quiz Type not recognized", Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                }
        );

        btnClose.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Close the dialog
                        RoomQuizDialog.dismiss();
                    }
                }
        );

        RoomQuizDialog.show();

    }
}
