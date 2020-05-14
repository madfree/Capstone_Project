package com.madfree.capstoneproject;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.concurrent.CountDownLatch;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import timber.log.Timber;

public class UploadImageWorker extends Worker {

    public static final String KEY_IMAGE_URI = "image_uri";
    public static final String KEY_FIREBASE_IMAGE_URL = "firebase_image_url";

    private FirebaseStorage mFirebaseStorage;
    private StorageReference mTriviaImageStorageReference;

    private String mImageUrl;

    public UploadImageWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

    }

    @NonNull
    @Override
    public Result doWork() {
        mFirebaseStorage = mFirebaseStorage.getInstance();
        mTriviaImageStorageReference = mFirebaseStorage.getReference().child("trivia_images");

        Uri imageUri = Uri.parse(getInputData().getString(KEY_IMAGE_URI));

        try {
            if (TextUtils.isEmpty(imageUri.toString())) {
            Timber.e("Invalid input uri");
            throw new IllegalArgumentException("Invalid input uri");
        }

        // get the image reference
        final StorageReference imageRef = mTriviaImageStorageReference.child(imageUri.getLastPathSegment());
        // upload the image to Firebase
        imageRef.putFile(imageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return imageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "image successfully uploaded", Toast.LENGTH_SHORT).show();
                    Timber.d("Image was successfully uploaded to Firebase");
                    Uri downloadUri = task.getResult();
                    // set the mImageUrl Variable to the downloadUri from Firebase Storage
                    // TODO: Worker won't wait for Firebase to finish upload. Adapt method to wait for continueWithTask to finish, before finishing Worker
                    mImageUrl = downloadUri.toString();
                    Timber.d(("URl of the image is: " + mImageUrl));
                } else {
                    Toast.makeText(getApplicationContext(), "upload failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Data outputData = new Data.Builder()
                .putString(KEY_FIREBASE_IMAGE_URL, mImageUrl)
                .build();

        // If there were no errors, return SUCCESS and the image URL in Firebase as outPutData
        return Result.success(outputData);

        } catch (Throwable throwable) {
            Timber.e(throwable, "Error uploading image");
            return Result.failure();
        }
    }
}
