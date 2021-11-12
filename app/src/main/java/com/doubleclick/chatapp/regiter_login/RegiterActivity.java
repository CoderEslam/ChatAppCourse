package com.doubleclick.chatapp.regiter_login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.doubleclick.chatapp.HomeActivity;
import com.doubleclick.chatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegiterActivity extends AppCompatActivity {


    private EditText name, email, password, confarm_password;
    private FirebaseAuth auth;
    private String UserId;
    private Button register;
    private DatabaseReference reference;
    private LottieAnimationView loading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regiter);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email_regist);
        password = findViewById(R.id.password_reg);
        confarm_password = findViewById(R.id.conferm_password);
        register = findViewById(R.id.register);
        auth = FirebaseAuth.getInstance();
        loading = findViewById(R.id.loading);



        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loading.setVisibility(View.VISIBLE);
                String Name = name.getText().toString();
                String Email = email.getText().toString();
                String Password = password.getText().toString();
                String C_Password = confarm_password.getText().toString();
                if (Name != null && Email != null && Password != null) {
                    if (Password.equals(C_Password)) {
                        CreatUser(Name, Email, Password);
                    } else {
                        Toast.makeText(RegiterActivity.this, "Password id wrong", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(RegiterActivity.this, "Name & Email & Password is required", Toast.LENGTH_LONG).show();

                }

            }
        });


    }

    private void CreatUser(String name, String email, String password) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    UserId = auth.getCurrentUser().getUid();
                    reference = FirebaseDatabase.getInstance().getReference().child("Users").child(UserId);
                    Map<String, Object> mapUser = new HashMap<>();
                    mapUser.put("Name", name);
                    mapUser.put("Email", email);
                    mapUser.put("Password", password);
                    mapUser.put("Image", "");
                    mapUser.put("Status", "offline");
                    mapUser.put("UserId", UserId);

                    reference.updateChildren(mapUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            loading.setVisibility(View.GONE);
                            Toast.makeText(RegiterActivity.this,"Done",Toast.LENGTH_LONG).show();
                            Intent intent =new Intent(RegiterActivity.this, HomeActivity.class);
                            startActivity(intent);

                        }
                    });

                }

            }
        });

    }
}