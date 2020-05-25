package com.example.memorylane;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import model.Memory;
import ui.MemoryRecyclerAdapter;
import utils.MemoryApi;

public class MemoryListActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;

    private List<Memory> memoryList;
    private RecyclerView recyclerView;
    private MemoryRecyclerAdapter memoryRecyclerAdapter;
    private TextView noMemory;
    private CollectionReference collectionReference =  db.collection("Memories");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_list);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        recyclerView = findViewById(R.id.recyclerView);
        noMemory = findViewById(R.id.noMemoryTV);
        memoryList = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bar, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.add_memory:
                if(user!=null && firebaseAuth!=null){
                    startActivity(new Intent(MemoryListActivity.this,PostMemoryActivity.class));
                }
                break;
            case R.id.sign_out:
                if(user!=null && firebaseAuth!=null){
                    firebaseAuth.signOut();
                    startActivity(new Intent(MemoryListActivity.this,MainActivity.class));
                }
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        collectionReference.whereEqualTo("userId", firebaseAuth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(!queryDocumentSnapshots.isEmpty()){
                            for(QueryDocumentSnapshot memories : queryDocumentSnapshots){
                                Memory memory = memories.toObject(Memory.class);

                                memoryList.add(memory);
                            }

                            memoryRecyclerAdapter = new MemoryRecyclerAdapter(MemoryListActivity.this,
                                    memoryList);
                            recyclerView.setAdapter(memoryRecyclerAdapter);
                            memoryRecyclerAdapter.notifyDataSetChanged();

                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                noMemory.setVisibility(View.VISIBLE);
            }
        });
    }
}
