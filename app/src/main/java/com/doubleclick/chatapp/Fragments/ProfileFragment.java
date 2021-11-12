package com.doubleclick.chatapp.Fragments;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.doubleclick.chatapp.HomeActivity;
import com.doubleclick.chatapp.MainActivity;
import com.doubleclick.chatapp.R;
import com.doubleclick.chatapp.model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        // Required empty public constructor
    }

    private CircleImageView image;
    private TextView name;
    private final int request_code = 1;
    private Uri imageUri;
    private StorageReference referenceImages;
    private FirebaseAuth mAuth;
    private String id;
    private StorageTask task;
    private DatabaseReference RefUsers;
    private LottieAnimationView loading;




    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        referenceImages = FirebaseStorage.getInstance().getReference().child("Images");
        mAuth = FirebaseAuth.getInstance();
        id = mAuth.getCurrentUser().getUid().toString();
        RefUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(id);
        image = view.findViewById(R.id.image);
        name = view.findViewById(R.id.name);
        loading = view.findViewById(R.id.loading);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImage();
            }
        });

        RefUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                name.setText(user.getName());
                if (!user.getImage().equals("")){
                    Glide.with(getContext()).load(user.getImage()).placeholder(R.drawable.image3).into(image);
                }else {
//                    image.setImageResource(getContext().getResources().getDrawable(R.drawable.image3));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        return view;
    }

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, request_code);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == request_code && resultCode == RESULT_OK  && data.getData() != null) {
            imageUri = data.getData();
            image.setImageURI(imageUri);
            uploadImage();
        }
    }


    private void uploadImage() {
        if (imageUri!=null){
            loading.setVisibility(View.VISIBLE);
            StorageReference reference = referenceImages.child(id+".jpg");
            task = reference.putFile(imageUri);
            task.continueWithTask(new Continuation<UploadTask.TaskSnapshot,Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (task.isSuccessful()){
                        return reference.getDownloadUrl();
                    }
                    return null;
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Uri uri = task.getResult();
                    String image = uri.toString();
                    Map<String,Object> map = new HashMap<>();
                    map.put("Image",image);
                    RefUsers.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
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