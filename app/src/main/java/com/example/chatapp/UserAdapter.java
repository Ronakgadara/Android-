package com.example.chatapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.chatapp.R;
import com.example.chatapp.User;

import java.util.List;

public class UserAdapter extends BaseAdapter {

    private Context context;
    private List<User> userList;
    private LayoutInflater inflater;

    public UserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int position) {
        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        TextView username;
        TextView email;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.user_item, parent, false);
            holder = new ViewHolder();
            holder.username = convertView.findViewById(R.id.textUserName);
            holder.email = convertView.findViewById(R.id.textUserEmail);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        User user = userList.get(position);
        holder.username.setText(user.getFirstName() + " " + user.getLastName());
        holder.email.setText(user.getEmail());

        return convertView;
    }
}
