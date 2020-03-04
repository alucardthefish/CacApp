package com.sop.cacapp.Persistence;

import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sop.cacapp.Object.PoopOccurrence;

import java.util.HashMap;
import java.util.Map;

public class PoopOccurrencePersistent {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore mDataBase;
    private CollectionReference mReference;
    private DocumentReference mCollectedDataReference;
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

        this.mCollectedDataReference = mDataBase.collection("users")
                .document(currentUser.getUid())
                .collection("business_data")
                .document("cacap")
                .collection("interpreted_data")
                .document("global");

        // Old:
        //java.util.Date date = snapshot.getDate("created_at");
        // New:
        //Timestamp timestamp = snapshot.getTimestamp("created_at");
        //java.util.Date date = timestamp.toDate();
    }

    public DocumentReference getmCollectedDataReference() {
        return mCollectedDataReference;
    }

    public CollectionReference getmReference() {
        return mReference;
    }

    public PoopOccurrencePersistent(boolean stat) {
        this.status = stat;
    }

    public void CreatePoopOccurrence(final View view) {
        final PoopOccurrence depositionDateTime = new PoopOccurrence();
        mReference.add(depositionDateTime)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        UpdateCalculatedData(depositionDateTime.getOccurrenceTime());
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

    public void CreatePoopOccurrenceTwo(final View view, final OnCreatePoopOccurrenceListener listenerCallback) {
        final PoopOccurrence depositionDateTime = new PoopOccurrence();
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
        mCollectedDataReference
                .update(
                        "deposition_counter", FieldValue.increment(1),
                        "last_deposition_date", depositionDateTime.getOccurrenceTime())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            mCollectedDataReference.get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            listenerCallback.onCallBack(documentSnapshot.getData());
                                        }
                                    });
                        } else {
                            Toast.makeText(view.getContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public interface OnCreatePoopOccurrenceListener {
        void onCallBack(Map<String, Object> data);
    }

    public void CreateCalculatedData() {
        Map<String, Object> data = new HashMap<>();
        data.put("deposition_counter", 0);
        data.put("last_deposition_date", "NoDate");
        mCollectedDataReference.set(data);
    }

    public void UpdateCalculatedData(Timestamp last_date) {
        mCollectedDataReference
                .update(
                        "deposition_counter", FieldValue.increment(1),
                        "last_deposition_date", last_date);
    }

    public void GetCalculatedData(final MyCallback myCallback) {
        mCollectedDataReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        myCallback.onCallBack(documentSnapshot.getData());
                    }
                });
    }

    public interface MyCallback {
        void onCallBack(Map<String, Object> data);
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}