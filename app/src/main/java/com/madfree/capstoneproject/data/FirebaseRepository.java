package com.madfree.capstoneproject.data;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

import androidx.annotation.NonNull;

import timber.log.Timber;

public class FirebaseRepository {

    private OnFirebaseTaskComplete onFirebaseTaskComplete;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();

    private DatabaseReference triviaReference = firebaseDatabase.getReference().child(
            "trivia");
    public static final DatabaseReference userReference =
            FirebaseDatabase.getInstance().getReference("users");

    public FirebaseRepository(OnFirebaseTaskComplete onFirebaseTaskComplete) {
        this.onFirebaseTaskComplete = onFirebaseTaskComplete;
    }

    public void getQuizData(String category) {
        Query categoryQuery = triviaReference.orderByChild("category").equalTo(category);
        categoryQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                onFirebaseTaskComplete.QuizDataAdded(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Timber.e("Firebase loading problem: %s", databaseError);
            }
        });
    }

    public void getUserData() {
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                onFirebaseTaskComplete.QuizDataAdded(dataSnapshot);
                Timber.d("Returning player data from Firebase");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Timber.e("Firebase loading problem: %s", databaseError);
            }
        });

    }

    public File getImageData(String imageUrl) {
//        StorageReference triviaImageRef = mFirebaseStorage.getReference().child("trivia_images");
        StorageReference imageRef = mFirebaseStorage.getReferenceFromUrl(imageUrl);
        File localTriviaImage = null;
        try {
            localTriviaImage = File.createTempFile("images", "jpeg");
            imageRef.getFile(localTriviaImage).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Timber.d("Successfully loaded image from Firebase Storage");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Timber.d("Image download failed");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return localTriviaImage;
    }

    public interface OnFirebaseTaskComplete {
        void QuizDataAdded(DataSnapshot dataSnapshot);
    }

}
