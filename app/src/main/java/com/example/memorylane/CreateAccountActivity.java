package com.example.memorylane;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import utils.MemoryApi;

public class CreateAccountActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private EditText userName;
    private EditText createEmail;
    private EditText createPass;
    private Button createAccBtn;
    private ProgressBar progressBar;

    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        firebaseAuth = FirebaseAuth.getInstance();


        userName = findViewById(R.id.createAcc_username);
        createEmail = findViewById(R.id.createAcc_email);
        createPass = findViewById(R.id.createAcc_pass);
        createAccBtn = findViewById(R.id.createAccBtn);
        progressBar = findViewById(R.id.createAcc_loading_bar);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();

                if(currentUser!=null){

                }else{

                }
            }
        };

        createAccBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(createEmail.getText().toString())
                && !TextUtils.isEmpty(createPass.getText().toString())
                && !TextUtils.isEmpty(userName.getText().toString())){

                    String email = createEmail.getText().toString().trim();
                    String password = createPass.getText().toString().trim();
                    String username = userName.getText().toString().trim();
                    createAccount(email, password, username);
                }else{
                    Toast.makeText(CreateAccountActivity.this, "Empty Fields", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

        public void createAccount(String email, String password, final String username){

        if(!TextUtils.isEmpty(email)&&!TextUtils.isEmpty(password)&&!TextUtils.isEmpty(username)){
            progressBar.setVisibility(View.VISIBLE);

            firebaseAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull final Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                currentUser = firebaseAuth.getCurrentUser();

                                final String currentUserId = currentUser.getUid();
                                Map<String,String> userData = new HashMap<>();

                                userData.put("UserID",currentUserId);
                                userData.put("Username",username);

                                collectionReference.add(userData)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                documentReference.get()
                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                if((task.getResult()).exists()){
                                                                    progressBar.setVisibility(View.INVISIBLE);

                                                                    String name = task.getResult().getString("Username");

                                                                    MemoryApi memoryApi = MemoryApi.getInstace();
                                                                    memoryApi.setUserid(currentUserId);
                                                                    memoryApi.setUsername(name);

                                                                    Intent intent = new Intent(CreateAccountActivity.this,PostMemoryActivity.class);
                                                                    intent.putExtra("Username",name);
                                                                    intent.putExtra("UserID",currentUserId);
                                                                    startActivity(intent);

                                                                }else{

                                                                }

                                                            }
                                                        });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });


                            }else{

                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });

        }else{

        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);

    }
}
