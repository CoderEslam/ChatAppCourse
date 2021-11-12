package com.doubleclick.chatapp.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.doubleclick.chatapp.ChatActivity;
import com.doubleclick.chatapp.R;
import com.doubleclick.chatapp.model.User;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.RecyclerViewHolder> {

    private ArrayList<User> users = new ArrayList<>();

    public UsersAdapter(ArrayList<User> users) {
        this.users = users;
    }

    @NonNull
    @Override
    public UsersAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_uesr, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersAdapter.RecyclerViewHolder holder, int position) {
        User user = users.get(position);
        holder.name.setText(user.getName());
        Glide.with(holder.itemView.getContext()).load(user.getImage()).placeholder(R.drawable.image3).into(holder.image_user);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.itemView.getContext(), ChatActivity.class);
                intent.putExtra("id",users.get(position).getUserId());
                intent.putExtra("image",users.get(position).getImage());
                intent.putExtra("name",users.get(position).getName());
                holder.itemView.getContext().startActivity(intent);
            }
        });



    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        CircleImageView image_user;
        TextView name;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            image_user = itemView.findViewById(R.id.image_user);
            name = itemView.findViewById(R.id.name);
        }
    }

}
