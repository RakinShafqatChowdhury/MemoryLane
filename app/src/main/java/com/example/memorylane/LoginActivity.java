package com.example.memorylane;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import utils.MemoryApi;

public class LoginActivity extends AppCompatActivity {

    private AutoCompleteTextView email;
    private TextInputEditText password;

    private Button login;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.email_login);
        password = findViewById(R.id.password_login);
        login = findViewById(R.id.loginBtn);
        progressBar = findViewById(R.id.loading_bar);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email.getText().equals(null)){
                    email.setError("Please enter email address");
                    return;
                }
                if(password.getText().equals(null)){
                    password.setError("Please enter password");
                    return;
                }
                logInWithEmailandPassword(email.getText().toString().trim(),password.getText().toString().trim());
            }
        });


    }

    private void logInWithEmailandPassword(String email, String pass) {
        progressBar.setVisibility(View.VISIBLE);
        if(!TextUtils.isEmpty(email)&&!TextUtils.isEmpty(pass)){
            firebaseAuth.signInWithEmailAndPassword(email,pass)
                  .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                      @Override
                      public void onComplete(@NonNull Task<AuthResult> task) {
                          user = firebaseAuth.getCurrentUser();


                          final String userid = user.getUid();

                          collectionReference.whereEqualTo("UserID",userid)
                                  .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                      @Override
                                      public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                                          @Nullable FirebaseFirestoreException e) {
                                          if(e!=null){

                                          }
                                          if(!queryDocumentSnapshots.isEmpty()){
                                              progressBar.setVisibility(View.VISIBLE);
                                              for(QueryDocumentSnapshot snapshot: queryDocumentSnapshots){
                                                  MemoryApi memoryApi = MemoryApi.getInstace();
                                                  memoryApi.setUserid(snapshot.getString("UserID"));
                                                  memoryApi.setUsername(snapshot.getString("Username"));

                                                  startActivity(new Intent(LoginActivity.this,PostMemoryActivity.class));
                                              }


                                          }

                                      }
                                  });

                      }
                  }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.VISIBLE);
                }
            });

        }else{
            progressBar.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
        }

    }

    public void onClick(View view) {
        startActivity(new Intent(LoginActivity.this,CreateAccountActivity.class));
    }



}
