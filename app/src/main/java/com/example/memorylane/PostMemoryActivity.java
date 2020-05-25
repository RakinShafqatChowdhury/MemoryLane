package com.example.memorylane;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;

import model.Memory;
import utils.MemoryApi;

public class PostMemoryActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int GALLERY_CODE = 1;
    private ImageView addImage;
    private ImageView memoryImage;
    private TextView userMemory;
    private TextView memoryDate;
    private EditText memoryTitle;
    private EditText memoryDesc;
    private ProgressBar progressBar;
    private Button saveBtn;
    private Button skipBtn;
    private Uri imageURI;
    private String userId;
    private String userName;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private CollectionReference collectionReference = db.collection("Memories");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_memory);

        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        addImage = findViewById(R.id.addImage);
        memoryImage = findViewById(R.id.memoryImageView);
        userMemory = findViewById(R.id.memoryUserTV);
        memoryDate = findViewById(R.id.memory_date);
        memoryTitle = findViewById(R.id.memory_titleET);
        memoryDesc = findViewById(R.id.memory_descET);
        progressBar = findViewById(R.id.memory_save_progressbar);

        progressBar.setVisibility(View.INVISIBLE);

        saveBtn = findViewById(R.id.memory_saveBtn);
        skipBtn = findViewById(R.id.skipBtn);
        skipBtn.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
        addImage.setOnClickListener(this);

        if (MemoryApi.getInstace() != null) {
            userId = MemoryApi.getInstace().getUserid();
             userName = MemoryApi.getInstace().getUsername();

            userMemory.setText(userName);
        }

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();

                if (user != null) {

                } else {

                }
            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();
        user = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuth != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addImage:
                Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
                gallery.setType("image/*");
                startActivityForResult(gallery, GALLERY_CODE);

                break;
            case R.id.memory_saveBtn:
                saveMemory();
                break;

            case R.id.skipBtn:
                startActivity(new Intent(PostMemoryActivity.this, MemoryListActivity.class));
                finish();
                break;

        }
    }

    private void saveMemory() {
        final String title = memoryTitle.getText().toString().trim();
        final String desc = memoryDesc.getText().toString().trim();

        progressBar.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(desc) && imageURI != null) {

            final StorageReference path = storageReference.child("memory_images").child("memories" + Timestamp.now().getSeconds());
            path.putFile(imageURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                    path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            String imageUrl = uri.toString();
                            Memory memory = new Memory();
                            memory.setTitle(title);
                            memory.setDesc(desc);
                            memory.setTimeAdded(new Timestamp(new Date()));
                            memory.setUserName(userName);
                            memory.setUserId(userId);
                            memory.setImageUrl(imageUrl);

                            collectionReference.add(memory).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    startActivity(new Intent(PostMemoryActivity.this, MemoryListActivity.class));
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Log.e("journal creation failed", "onFailure: " + e.getLocalizedMessage());

                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });

        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                imageURI = data.getData();

                memoryImage.setImageURI(imageURI);
            }
        }

    }
}
