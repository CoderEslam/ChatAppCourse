package com.doubleclick.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private EditText massege;
    private ImageView send;
    private String name, id, image;
    private CircleImageView imageFriend;
    private TextView nameFriend;
    private ImageView camre;
    private final int request_code = 100;
    private Uri imageUri;
    private LottieAnimationView loading;
    private StorageTask task;
    private StorageReference referenceImages;
    private DatabaseReference RefChat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        loading = findViewById(R.id.loading);
        massege = findViewById(R.id.massege);
        send = findViewById(R.id.send);
        camre = findViewById(R.id.image);
        referenceImages = FirebaseStorage.getInstance().getReference().child("UploadsImage");
        RefChat = FirebaseDatabase.getInstance().getReference().child("Chats");
        camre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        Toolbar toolbar = findViewById(R.id.toolbar);
        setTitle("");
        setSupportActionBar(toolbar);
        nameFriend = findViewById(R.id.nameFriend);
        imageFriend = findViewById(R.id.imageFriend);
        name = getIntent().getStringExtra("name");
        id = getIntent().getStringExtra("id");
        image = getIntent().getStringExtra("image");
        if (!name.isEmpty() && !image.isEmpty()) {
            nameFriend.setText(name);
            Glide.with(ChatActivity.this).load(image).placeholder(R.drawable.image3).into(imageFriend);
        }


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });

    }

    private void openGellery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, request_code);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == request_code && resultCode == RESULT_OK && data.getData() != null) {
            imageUri = data.getData();
            uploadImage();
        }

    }

    private void uploadImage() {
        if (imageUri != null) {
            loading.setVisibility(View.VISIBLE);
            String pushid = RefChat.push().getKey().toString();
            StorageReference reference = referenceImages.child(pushid + ".jpg");
            task = reference.putFile(imageUri);
            task.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (task.isSuccessful()) {
                        return reference.getDownloadUrl();
                    }
                    return null;
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Uri uri = task.getResult();
                    String image = uri.toString();
                    Map<String, Object> map = new HashMap<>();
                    map.put("Type", "image");
                    map.put("message", image);
                    RefChat.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            loading.setVisibility(View.GONE);
                        }
                    });

                }
            });

        }

    }

}