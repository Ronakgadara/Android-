package com.example.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;

public class ChatUserList extends AppCompatActivity {

    private ListView userListView;
    private ArrayList<User> userList;
    private UserAdapter userAdapter;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_user_list);

        userListView = findViewById(R.id.userListView);
        userList = new ArrayList<>();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        Toolbar toolbar = findViewById(R.id.chatToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow); // Custom icon
        }

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("User");

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    String uid = dataSnapshot.getKey();

                    if (user != null && uid != null && !uid.equals(currentUser.getUid())) {
                        user.setUid(uid);
                        user.setName(user.getFirstName() + " " + user.getLastName());
                        userList.add(user);
                    }
                }

                userAdapter = new UserAdapter(ChatUserList.this, userList);
                userListView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatUserList.this, "Failed to load users: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        userListView.setOnItemClickListener((parent, view, position, id) -> {
            User selectedUser = userList.get(position);
            Intent intent = new Intent(ChatUserList.this, Chat.class);
            intent.putExtra("receiverUid", selectedUser.getUid());
            intent.putExtra("receiverName", selectedUser.getName());
            startActivity(intent);
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }    
}