package com.sop.cacapp.Persistence;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

    public Task<Void> CreateProfile(Profile profile) {
        return profileReference.set(profile);
    }

    public void GetProfile(final MyCallback myCallback) {
        profileReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Profile profile = documentSnapshot.toObject(Profile.class);
                        myCallback.onCallBack(profile);
                    }
                });
    }

    public interface MyCallback {
        void onCallBack(Profile profile);
    }
}
