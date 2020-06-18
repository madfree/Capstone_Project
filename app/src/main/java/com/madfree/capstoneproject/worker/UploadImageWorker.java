package com.madfree.capstoneproject.worker;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.madfree.capstoneproject.util.Constants;

import java.util.concurrent.CountDownLatch;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import timber.log.Timber;

public class UploadImageWorker extends Worker {

    private FirebaseStorage mFirebaseStorage;
    private StorageReference mTriviaImageStorageReference;

    private Data mOutputData;

    public UploadImageWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        mFirebaseStorage = FirebaseStorage.getInstance();
        mTriviaImageStorageReference = mFirebaseStorage.getReference().child("trivia_images");

        CountDownLatch countDown = new CountDownLatch(2);
        Uri imageUri = Uri.parse(getInputData().getString(Constants.KEY_IMAGE_URI));

        try {

        // get the image reference
        final StorageReference imageRef = mTriviaImageStorageReference.child(imageUri.getLastPathSegment());
        Timber.d("This is the Firebase Storage reference to the image :%s", imageRef);

        // upload the image to Firebase
        imageRef.putFile(imageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                countDown.countDown();
                return imageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Timber.d("Image was successfully uploaded to Firebase");
                    Uri downloadUri = task.getResult();
                    // set the mImageUrl Variable to the downloadUri from Firebase Storage
                    String imageUrl = downloadUri.toString();
                    Timber.d(("URl of the image is: " + imageUrl));
                    mOutputData = new Data.Builder()
                            .putString(Constants.KEY_FIREBASE_IMAGE_URL, imageUrl)
                            .build();
                    countDown.countDown();
                } else {
                    Toast.makeText(getApplicationContext(), "upload failed", Toast.LENGTH_SHORT).show();
                    countDown.countDown();
                }
            }
        });
        // If there were no errors, return SUCCESS and the image URL in Firebase as outPutData
        countDown.await();
        return Result.success(mOutputData);

        } catch (Throwable throwable) {
            Timber.e(throwable, "Error uploading image");
            return Result.failure();
        }
    }
}
