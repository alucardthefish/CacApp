package com.sop.cacapp.Persistence;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sop.cacapp.Object.Profile;

public class ProfilePersistent {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore mDataBase;

    private DocumentReference profileReference;

    public ProfilePersistent() {
        this.mAuth = FirebaseAuth.getInstance();
        this.currentUser = mAuth.getCurrentUser();
        this.mDataBase = FirebaseFirestore.getInstance();

        this.profileReference = mDataBase.collection("users")
                .document(currentUser.getUid())
                .collection("data")
                .document("profile");
    }

    public void createProfile(Profile profile, final OnCreateProfileListener listener) {
        profileReference
                .set(profile)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        listener.onCallBack(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onCallBack(false);
                    }
                });
    }

    public void GetProfile(final MyCallback myCallback) {
        profileReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Profile profile = documentSnapshot.toObject(Profile.class);
                        myCallback.onCallBack(true, profile);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        myCallback.onCallBack(false, null);
                    }
                });
    }

    public interface MyCallback {
        void onCallBack(boolean isSuccess, Profile profile);
    }

    public interface OnCreateProfileListener {
        void onCallBack(boolean isSuccess);
    }
}
