package com.example.memorylane;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import utils.MemoryApi;

public class MainActivity extends AppCompatActivity {

    private Button startButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();
                if(currentUser!=null){
                    currentUser = firebaseAuth.getCurrentUser();
                    String currentuserId = currentUser.getUid();

                    collectionReference.whereEqualTo("UserID",currentuserId)
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                                    @Nullable FirebaseFirestoreException e) {

                                    if(e!=null){
                                        return;
                                    }
                                    String name;
                                    if(!queryDocumentSnapshots.isEmpty()){
                                        for(QueryDocumentSnapshot snapshot : queryDocumentSnapshots){
                                            MemoryApi memoryApi = MemoryApi.getInstace();
                                            memoryApi.setUserid(snapshot.getString("UserID"));
                                            memoryApi.setUsername(snapshot.getString("Username"));

                                            startActivity(new Intent(MainActivity.this,MemoryListActivity.class));
                                            finish();

                                        }
                                    }

                                }
                            });
                }
                else{

                }
            }
        };
        //getSupportActionBar().setElevation(0);
        startButton = findViewById(R.id.startBtn);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(firebaseAuth!=null){
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}
