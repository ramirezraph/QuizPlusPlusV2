package com.student.ramirez.quizplusplus;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizOnlineActivity extends AppCompatActivity {

    // Firebase
    DatabaseReference mDatabaseRoom;

    // Widgets
    private RecyclerView recyclerViewRoom;
    private EditText etSearch;
    private Button btnBack;

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference NameRef, TypeRef;
    private String name;
    private String type;
    private FloatingActionButton FabCreateRoom;
    private Dialog RoomPrivateDialog;

    private List<UserAtRoom> userAtRoomList;
    private int checknumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_online);

        mDatabaseRoom = FirebaseDatabase.getInstance().getReference().child("Room");

        userAtRoomList = new ArrayList<>();

        etSearch = (EditText) findViewById(R.id.etSearchRoom);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                FirebaseUserSearchRoom(s.toString());
            }
        });

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

        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(QuizOnlineActivity.this, MainActivity.class));
                    }
                }
        );

        // Creating a Recycler View for Rooms
        recyclerViewRoom = (RecyclerView) findViewById(R.id.recyclerViewRoom);
        recyclerViewRoom.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerViewRoom.setLayoutManager(linearLayoutManager);

        FabCreateRoom = (FloatingActionButton) findViewById(R.id.fab);
        FabCreateRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QuizOnlineActivity.this, CreateRoomActivity.class);
                startActivity(intent);
            }
        });

        TypeRef = FirebaseDatabase.getInstance().getReference().child("Accounts").child(user.getUid()).child("Type");
        TypeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                type = dataSnapshot.getValue().toString();
                if (type.equals("Admin")){
                    FabCreateRoom.setVisibility(View.VISIBLE);
                } else if (type.equals("Teacher")){
                    FabCreateRoom.setVisibility(View.VISIBLE);
                } else if (type.equals("Student")){
                    FabCreateRoom.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        RoomPrivateDialog = new Dialog(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUserSearchRoom("");
    }

    public void FirebaseUserSearchRoom(String searchText){
        // Search Query
        Query firebaseSearchQuery = mDatabaseRoom.orderByChild("roomTitle").startAt(searchText).endAt(searchText + "\uf8ff");

        FirebaseRecyclerAdapter<Room, QuizOnlineActivity.RoomViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Room, QuizOnlineActivity.RoomViewHolder>(
                Room.class,
                R.layout.room_list_item,
                QuizOnlineActivity.RoomViewHolder.class,
                firebaseSearchQuery
        ) {
            @Override
            protected void populateViewHolder(final RoomViewHolder viewHolder, final Room model, int position) {
                final String key = getRef(position).getKey();
                viewHolder.setRoomID(model.getRoomId());
                viewHolder.setRoomTitle(model.getRoomTitle());
                viewHolder.setRoomNumber(String.valueOf(model.getRoomNumberOfUser()), String.valueOf(model.getRoomSize()));

                viewHolder.mView.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String type = model.getPrivate().toString();
                                Log.e("GETTYPE", type);
                                switch (type){
                                    case "false":
                                        String key = mDatabaseRoom.push().getKey();
                                        UserAtRoom user = new UserAtRoom(name, key);
                                        getAllUserInRoom(model.getRoomId(), user, key);
                                        Intent intent = new Intent(QuizOnlineActivity.this, RoomActivity.class);
                                        intent.putExtra("ROOMID", model.getRoomId());
                                        intent.putExtra("USERKEY", model.getUserListID());
                                        intent.putExtra("FROMWHAT", "Lobby");
                                        intent.putExtra("USERLISTKEY", user.getKey());
                                        intent.putExtra("USERNUMBER", model.getRoomNumberOfUser());
                                        intent.putExtra("OWNERNAME", model.getOwner_name());
                                        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
                                        if (currentUser.getEmail().equals(model.getRoomOwner())){
                                            intent.putExtra("ISOWNER", "YES");
                                        } else {
                                            intent.putExtra("ISOWNER", "NO");
                                        }
                                        Log.e("NUMBEROFUSERATONLINE", String.valueOf(model.getRoomNumberOfUser()));
                                        startActivity(intent);
                                        break;
                                    case "true":
                                        //Toast.makeText(QuizOnlineActivity.this, "This room is private", Toast.LENGTH_SHORT).show();
                                        ShowRoomPrivateDialog(model, viewHolder);
                                        break;
                                }

                            }
                        }
                );
            }
        };
        recyclerViewRoom.setAdapter(firebaseRecyclerAdapter);
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public RoomViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setRoomID(String id){
            TextView roomId = (TextView) mView.findViewById(R.id.txtRoomNo);
            roomId.setText(id);

        }
        public void setRoomTitle(String title){
            TextView roomTitle = (TextView) mView.findViewById(R.id.txtRoomTitle);
            roomTitle.setText(title);
        }
        public void setRoomNumber(String number, String size){
            TextView roomNumber = (TextView) mView.findViewById(R.id.txtNumber);
            roomNumber.setText(number +"/" + size);
            roomNumber.setVisibility(View.INVISIBLE); // NOT WORKING
        }

    }

    public void getAllUserInRoom(final String roomId, UserAtRoom userAtRoom, String key){
        final List<UserAtRoom> userList = new ArrayList<>();
        mDatabaseRoom.child(roomId).child("UserList").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> useratroom = dataSnapshot.getChildren();
                        for (DataSnapshot child: useratroom) {
                            checknumber++;
                            UserAtRoom value = child.getValue(UserAtRoom.class);
                            userList.add(value);
                            Log.e("USERADDED", value != null ? value.getName() : null);
                            Log.e("USERSIZE", String.valueOf(userList.size()));
                            mDatabaseRoom.child(roomId).child("roomNumberOfUser").setValue(userList.size());
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(QuizOnlineActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        userList.add(userAtRoom);
        mDatabaseRoom.child(roomId).child("UserList").child(key).setValue(userAtRoom);
    }

    public void ShowRoomPrivateDialog(final Room model, final RoomViewHolder viewHolder){
        TextView txtDialogTitle;
        Button btnCancel, btnJoinRoom;
        final EditText etRoomPassword;

        RoomPrivateDialog.setContentView(R.layout.dialog_room);
        txtDialogTitle = (TextView) RoomPrivateDialog.findViewById(R.id.txtDialogTitle);
        btnCancel = (Button) RoomPrivateDialog.findViewById(R.id.btnCancel);
        btnJoinRoom = (Button) RoomPrivateDialog.findViewById(R.id.btnJoinThisRoom);
        etRoomPassword = (EditText) RoomPrivateDialog.findViewById(R.id.etRoomPassword);

        txtDialogTitle.setText("Join Room: " +model.getRoomId());

        btnCancel.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RoomPrivateDialog.dismiss();
                    }
                }
        );
        btnJoinRoom.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String password = etRoomPassword.getText().toString().trim();
                        if (password.equals(model.getRoomPassword())){
                            String key = mDatabaseRoom.push().getKey();
                            UserAtRoom user = new UserAtRoom(name, key);
                            getAllUserInRoom(model.getRoomId(), user, key);
                            Intent intent = new Intent(QuizOnlineActivity.this, RoomActivity.class);
                            intent.putExtra("ROOMID", model.getRoomId());
                            intent.putExtra("USERKEY", model.getUserListID());
                            intent.putExtra("FROMWHAT", "Lobby");
                            intent.putExtra("USERLISTKEY", user.getKey());
                            intent.putExtra("USERNUMBER", model.getRoomNumberOfUser());
                            intent.putExtra("OWNERNAME", model.getOwner_name());
                            FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
                            if (currentUser.getEmail().equals(model.getRoomOwner())){
                                intent.putExtra("ISOWNER", "YES");
                            } else {
                                intent.putExtra("ISOWNER", "NO");
                            }
                            Log.e("NUMBEROFUSERATONLINE", String.valueOf(model.getRoomNumberOfUser()));
                            startActivity(intent);
                        } else {
                            Toast.makeText(QuizOnlineActivity.this, "The password you have provided is incorrect", Toast.LENGTH_SHORT).show();
                            RoomPrivateDialog.dismiss();
                        }
                    }
                }
        );
        RoomPrivateDialog.show();
    }


}
