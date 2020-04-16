package com.sop.cacapp.Persistence;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.sop.cacapp.Classes.Symptom;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SymptomPersistent {

    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private FirebaseFirestore mDataBase;
    private CollectionReference symptomsRef;

    public SymptomPersistent() {
        this.auth = FirebaseAuth.getInstance();
        this.currentUser = auth.getCurrentUser();
        this.mDataBase = FirebaseFirestore.getInstance();

        this.symptomsRef = mDataBase.collection("users")
                .document(currentUser.getUid())
                .collection("business_data")
                .document("symptomap")
                .collection("symptoms");
    }

    public CollectionReference getSymptomsRef() {
        return symptomsRef;
    }

    public void addSymptom(Symptom symptom, final View view) {
        /*symptomsRef.add(symptom)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(view.getContext(), "Sintoma agregado exitosamente", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d("addSymptom", "Sintoma no pudo ser agregado");
                            Log.w("addSymptom", task.getException().getLocalizedMessage());
                            Log.w("addSymptomMsg", task.getException().getMessage());
                            Log.w("addSymptomStrg", task.getException().toString());
                        }
                    }
                });*/
        symptomsRef.document(symptom.getId())
                .set(symptom)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(view.getContext(), "Sintoma agregado exitosamente", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d("addSymptom", "Sintoma no pudo ser agregado");
                            Log.w("addSymptom", task.getException().getLocalizedMessage());
                        }
                    }
                });
    }

    public void deleteSymptoms(List<Symptom> symptoms, final View view) {
        WriteBatch batch = mDataBase.batch();
        for (Symptom symptom : symptoms) {
            String docId = symptom.getId();
            DocumentReference docRef = symptomsRef.document(docId);
            batch.delete(docRef);
        }
        batch.commit()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isComplete() && task.isSuccessful()) {
                            Toast.makeText(view.getContext(), "Sintomas eliminados", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(view.getContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void updateSymptom(Symptom symptom, final OnSymptomPersistentCallback callback) {

        if (symptom != null) {

            Map<String, Object> updates = new HashMap<>();
            updates.put("intensity", symptom.getIntensity());
            updates.put("description", symptom.getDescription());

            DocumentReference docRefUpdatedSymptom = symptomsRef.document(symptom.getId());
            docRefUpdatedSymptom.update(updates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful() && task.isComplete()) {
                                callback.onSuccess();
                            } else {
                                callback.onFailure();
                            }
                        }
                    });
        }

    }

    public void getSymptomById(String id, final OnGetSymptomByIdCallback callback) {
        DocumentReference docRefSymptom = symptomsRef.document(id);
        docRefSymptom.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isComplete() && task.isSuccessful()) {
                            Symptom queryResult = null;
                            if (task.getResult() != null) {
                                queryResult = task.getResult().toObject(Symptom.class);
                                callback.onSuccess(queryResult);
                            } else {
                                callback.onFailure(queryResult, "Query result is null");
                            }

                        } else {
                            callback.onFailure(null, task.getException().getMessage());
                        }
                    }
                });
    }

    public interface OnGetSymptomByIdCallback {
        public void onSuccess(Symptom symptom);
        public void onFailure(Symptom symptom, String failureMessage);
    }

    public interface OnSymptomPersistentCallback {
        public void onSuccess();
        public void onFailure();
    }
}
