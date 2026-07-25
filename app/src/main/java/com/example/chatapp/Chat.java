package com.example.chatapp;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import org.json.JSONArray;
import org.json.JSONObject;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.*;

public class Chat extends AppCompatActivity {
    private TextView chatTitle;
    private EditText messageBox;
    private Button sendButton;
    private ListView chatListView;
    private String senderUid;
    private String senderName;
    private String receiverUid;
    private String receiverName;
    private final ArrayList<ChatMessage> messages = new ArrayList<>();
    private ChatMessageAdapter adapter;
    private DatabaseReference chatRef;
    private final OkHttpClient okHttp = new OkHttpClient();
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String ONESIGNAL_APP_ID = "YOUR_KEY";
    private static final String ONE_SIGNAL_REST_ID = "YOUR_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatTitle = findViewById(R.id.chatTitle);
        messageBox = findViewById(R.id.messageBox);
        sendButton = findViewById(R.id.sendButton);
        chatListView = findViewById(R.id.chatListView);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.chatToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow); // Custom icon
        }

        // Get UIDs from intent or deep link
        Uri data = getIntent().getData();
        if (data != null) {
            receiverUid = data.getQueryParameter("receiverUid");
            receiverName = data.getQueryParameter("receiverName");
        } else {
            receiverUid = getIntent().getStringExtra("receiverUid");
            receiverName = getIntent().getStringExtra("receiverName");
        }

        // Firebase current user UID
        senderUid = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : "";

        // Set title text
        chatTitle.setText("Chat with " + (receiverName != null ? receiverName : "user"));

        // Chat setup
        adapter = new ChatMessageAdapter(this, messages, senderUid);
        chatListView.setAdapter(adapter);
        chatRef = FirebaseDatabase.getInstance().getReference("Chats");

        // Disable send until sender name loads
        sendButton.setEnabled(false);
        loadSenderName();

        sendButton.setOnClickListener(v -> {
            String msg = messageBox.getText().toString().trim();
            if (!msg.isEmpty()) {
                sendMessage(msg);
                sendPushNotification(receiverUid, msg);
                messageBox.setText("");
            }
        });

        listenForMessages();
    }

    private void loadSenderName() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User").child(senderUid);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snap) {
                String first = snap.child("firstName").getValue(String.class);
                String last = snap.child("lastName").getValue(String.class);
                senderName = (first != null ? first : "") + (last != null ? " " + last : "");
                if (senderName.trim().isEmpty()) senderName = "New message";
                sendButton.setEnabled(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError e) {
                senderName = "New message";
                sendButton.setEnabled(true);
            }
        });
    }

    private void sendMessage(String message) {
        String key = chatRef.push().getKey();
        if (key == null || senderUid.isEmpty()) return;

        HashMap<String, Object> data = new HashMap<>();
        data.put("messageId", key);
        data.put("sender", senderUid);
        data.put("receiver", receiverUid);
        data.put("message", message);
        data.put("timestamp", System.currentTimeMillis());

        chatRef.child(key).setValue(data);
    }

    private void listenForMessages() {
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messages.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ChatMessage msg = ds.getValue(ChatMessage.class);
                    if (msg == null) continue;

                    boolean imSender = senderUid.equals(msg.getSender()) && receiverUid.equals(msg.getReceiver());
                    boolean imReceiver = senderUid.equals(msg.getReceiver()) && receiverUid.equals(msg.getSender());
                    if (imSender || imReceiver) messages.add(msg);
                }
                adapter.notifyDataSetChanged();
                chatListView.setSelection(messages.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError e) {
                Toast.makeText(Chat.this, "Failed to load messages", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendPushNotification(String targetUid, String message) {
        try {
            JSONObject root = new JSONObject();
            root.put("app_id", ONESIGNAL_APP_ID);

            JSONArray arr = new JSONArray();
            arr.put(targetUid);
            root.put("include_external_user_ids", arr);

            JSONObject headings = new JSONObject();
            headings.put("en", senderName);
            root.put("headings", headings);

            JSONObject contents = new JSONObject();
            contents.put("en", message);
            root.put("contents", contents);

            JSONObject data = new JSONObject();
            data.put("senderUid", senderUid);
            data.put("senderName", senderName);
            root.put("data", data);

            String launchUrl = "chatapp://chat?receiverUid=" + targetUid + "&receiverName=" + receiverName;
            root.put("android_launch_url", launchUrl);

            RequestBody body = RequestBody.create(root.toString(), JSON);
            Request request = new Request.Builder()
                    .url("https://onesignal.com/api/v1/notifications")
                    .addHeader("Authorization", "Basic " + ONE_SIGNAL_REST_ID)
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();

            okHttp.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call c, @NonNull IOException e) {
                    Log.e("PushError", "Notification Failed", e);
                }

                @Override
                public void onResponse(@NonNull Call c, @NonNull Response r) throws IOException {
                    Log.d("PushSuccess", "Notification Sent: " + r.body().string());
                }
            });
        } catch (Exception e) {
            Log.e("PushError", "JSON build error", e);
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}