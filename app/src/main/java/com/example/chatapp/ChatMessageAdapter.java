package com.example.chatapp;

import android.app.AlertDialog;
import android.content.Context;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.*;

import java.util.List;

public class ChatMessageAdapter extends ArrayAdapter<ChatMessage> {

    private final Context context;
    private final List<ChatMessage> messages;
    private final String currentUid;

    public ChatMessageAdapter(Context context, List<ChatMessage> messages, String currentUid) {
        super(context, 0, messages);
        this.context = context;
        this.messages = messages;
        this.currentUid = currentUid;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMessage msg = messages.get(position);
        boolean isSentByMe = msg.getSender().equals(currentUid);
        int layoutId = isSentByMe ? R.layout.chat_item_sent : R.layout.chat_item_received;
        convertView = LayoutInflater.from(context).inflate(layoutId, parent, false);

        TextView msgText = convertView.findViewById(R.id.messageText);
        msgText.setText(msg.getMessage());

        convertView.setOnLongClickListener(v -> {
            showDeleteOptions(msg, position);
            return true;
        });

        return convertView;
    }

    private void showDeleteOptions(ChatMessage message, int position) {
        boolean isSentByMe = message.getSender().equals(currentUid);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Message");

        if (isSentByMe) {
            builder.setItems(new CharSequence[]{"Delete for Me", "Delete for Everyone", "Cancel"}, (dialog, which) -> {
                switch (which) {
                    case 0:
                        deleteForMe(position);
                        break;
                    case 1:
                        deleteForEveryone(message.getMessageId());
                        break;
                    case 2:
                        dialog.dismiss();
                        break;
                }
            });
        } else {
            builder.setItems(new CharSequence[]{"Delete for Me", "Cancel"}, (dialog, which) -> {
                switch (which) {
                    case 0:
                        deleteForMe(position);
                        break;
                    case 1:
                        dialog.dismiss();
                        break;
                }
            });
        }

        builder.show();
    }

    private void deleteForMe(int position) {
        messages.remove(position);
        notifyDataSetChanged();
        Toast.makeText(context, "Message deleted for you", Toast.LENGTH_SHORT).show();
    }

    private void deleteForEveryone(String messageId) {
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chats");
        chatRef.child(messageId).removeValue()
                .addOnSuccessListener(unused -> {
                    Toast.makeText(context, "Message deleted for everyone", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Delete failed", Toast.LENGTH_SHORT).show();
                });
    }
}