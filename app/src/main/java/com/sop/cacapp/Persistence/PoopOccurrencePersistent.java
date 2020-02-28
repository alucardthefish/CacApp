package com.sop.cacapp.Persistence;

import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sop.cacapp.Object.PoopOccurrence;

public class PoopOccurrencePersistent {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore mDataBase;
    private CollectionReference mReference;
    private boolean status;

    public PoopOccurrencePersistent() {
        this.status = false;
        this.mAuth = FirebaseAuth.getInstance();
        //FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
        //        .setTimestampsInSnapshotsEnabled(true)
        //        .build();
        this.currentUser = mAuth.getCurrentUser();
        this.mDataBase = FirebaseFirestore.getInstance();
        //mDataBase.setFirestoreSettings(settings);
        this.mReference = mDataBase.collection("users")
                .document(currentUser.getUid())
                .collection("business_data")
                .document("cacap")
                .collection("poop_occurrences");

        // Old:
        //java.util.Date date = snapshot.getDate("created_at");
        // New:
        //Timestamp timestamp = snapshot.getTimestamp("created_at");
        //java.util.Date date = timestamp.toDate();
    }

    public PoopOccurrencePersistent(boolean stat) {
        this.status = stat;
    }

    public void CreatePoopOccurrence(final View view) {
        PoopOccurrence depositionDateTime = new PoopOccurrence();
        mReference.add(depositionDateTime)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(view.getContext(), "En hora buena", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(view.getContext(), "Paila mi so", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
