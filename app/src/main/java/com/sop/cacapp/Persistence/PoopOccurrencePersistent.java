package com.sop.cacapp.Persistence;

import android.util.Log;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.sop.cacapp.Classes.PoopOccurrence;

import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;

public class PoopOccurrencePersistent {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore mDataBase;
    private CollectionReference poopOccurrencesRef;
    private DocumentReference calculatedDataDocRef;
    private boolean status;

    public PoopOccurrencePersistent() {
        this.status = false;
        this.mAuth = FirebaseAuth.getInstance();
        this.currentUser = mAuth.getCurrentUser();

        this.mDataBase = FirebaseFirestore.getInstance();
        //mDataBase.setFirestoreSettings(settings);
        this.poopOccurrencesRef = mDataBase.collection("users")
                .document(currentUser.getUid())
                .collection("business_data")
                .document("cacap")
                .collection("poop_occurrences");

        this.calculatedDataDocRef = mDataBase.collection("users")
                .document(currentUser.getUid())
                .collection("business_data")
                .document("cacap")
                .collection("interpreted_data")
                .document("global");
    }

    public DocumentReference getCalculatedDataDocRef() {
        return calculatedDataDocRef;
    }

    public CollectionReference getPoopOccurrencesRef() {
        return poopOccurrencesRef;
    }

    public PoopOccurrencePersistent(boolean stat) {
        this.status = stat;
    }

    public void CreatePoopOccurrence(final View view, float satisfaction) {
        final PoopOccurrence depositionDateTime = new PoopOccurrence(satisfaction);
        poopOccurrencesRef.add(depositionDateTime)
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

    public void addPoopOccurrence(final View view, final PoopOccurrence poopOccurrence) {

        calculatedDataDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {

                    long depositionCounter = documentSnapshot.getLong("deposition_counter");
                    Map<String, Object> data = new HashMap<>();
                    data.put("deposition_counter", FieldValue.increment(1));

                    if (depositionCounter > 0) {
                        Timestamp first_date = documentSnapshot.getTimestamp("first_deposition_date");
                        long firstDepositionTime = first_date.toDate().getTime();
                        long totalTime = poopOccurrence.getOccurrenceTime().toDate().getTime() - firstDepositionTime;
                        long deposition_mean_frequency = totalTime / (depositionCounter);
                        data.put("last_deposition_date", poopOccurrence.getOccurrenceTime());
                        data.put("deposition_mean_frequency", deposition_mean_frequency);

                        CreateDepositionAndUpdateCalculatedData(poopOccurrence, data, view);

                    } else {
                        // Initial: enters once
                        data.put("last_deposition_date", poopOccurrence.getOccurrenceTime());
                        data.put("first_deposition_date", poopOccurrence.getOccurrenceTime());
                        CreateDepositionAndUpdateCalculatedData(poopOccurrence, data, view);
                    }
                } else {
                    Log.d("onSuccess", "Document does not exist");
                }
            }
        });


    }

    public void CreateDepositionAndUpdateCalculatedData(PoopOccurrence poopOccurrence, Map<String, Object> calculatedObject, final View view) {

        WriteBatch batch = mDataBase.batch();
        //Generate new id for the new occurrence
        String occurrenceId = UUID.randomUUID().toString();
        // Set the new occurrence to batch
        batch.set(poopOccurrencesRef.document(occurrenceId), poopOccurrence);

        // Update calculated data
        batch.update(calculatedDataDocRef, calculatedObject);

        // Commit all the transactions
        batch.commit()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(view.getContext(), "Deposición guardada", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d("onComplete", "CreateDepositionAndUpdateCalculatedData task failed");
                        }
                    }
                });
    }

    public void CreateCalculatedData() {
        Map<String, Object> data = new HashMap<>();
        data.put("deposition_counter", 0);
        data.put("last_deposition_date", "NoDate");
        data.put("first_deposition_date", "NoDate");
        data.put("deposition_mean_frequency", 0);
        calculatedDataDocRef.set(data);
    }

    public void UpdateCalculatedData(final Timestamp current_date) {
        calculatedDataDocRef
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            int deposition_counter =  documentSnapshot.getDouble("deposition_counter").intValue();
                            long diff = 0;
                            if (documentSnapshot.get("last_deposition_date") instanceof String) {
                                updateByDepositionCounter(deposition_counter,
                                        diff,
                                        current_date);
                            } else {
                                Timestamp first_date = documentSnapshot.getTimestamp("first_deposition_date");
                                diff = current_date.toDate().getTime() - first_date.toDate().getTime();
                                updateByDepositionCounter(deposition_counter,
                                        diff,
                                        current_date);
                            }
                        }
                    }
                });
    }

    private void updateByDepositionCounter(int counter, long timeDiff, Timestamp current_date) {
        Map<String, Object> data = new HashMap<>();
        data.put("deposition_counter", FieldValue.increment(1));
        data.put("last_deposition_date", current_date);
        if (counter > 0) {
            double deposition_mean_frequency = timeDiff / (counter);
            data.put("deposition_mean_frequency", deposition_mean_frequency);
        } else {
            data.put("first_deposition_date", current_date);
        }
        calculatedDataDocRef
                .update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("ByDepositionCounter", "Successfully updated");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("ByDepositionCounter", "Fail to update");
                    }
                });
    }

    public void GetCalculatedData(final MyCallback myCallback) {
        calculatedDataDocRef.get()
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

    public void getMyPoopData(final PoopDataCallback callback) {
        poopOccurrencesRef
                .orderBy("occurrenceTime", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            ArrayList<Date> depositionDates = new ArrayList<>();
                            for (DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()) {
                                //depositionDates.add(ds.getTimestamp("occurrenceTime").toDate());
                                depositionDates.add(ds.getDate("occurrenceTime"));
                            }
                            ArrayList lineChartDataDict = extractChartData(depositionDates);
                            callback.onCallback(lineChartDataDict);
                        }
                    }
                });
    }

    private ArrayList extractChartData(ArrayList<Date> depositionDates) {
        Dictionary mDictDates = new Hashtable();
        ArrayList<Integer> arrayOfKeys = new ArrayList<>();
        for (Date d : depositionDates) {
            int month = d.getMonth() + 1;
            int year = d.getYear() + 1900;
            int key = year + month;
            if (mDictDates.get(key) != null) {
                mDictDates.put(key, (int) mDictDates.get(key) + 1);
            } else {
                mDictDates.put(key, 1);
                arrayOfKeys.add(key);
            }
        }
        ArrayList retArray = new ArrayList();
        retArray.add(mDictDates);
        retArray.add(arrayOfKeys);
        return retArray;
    }

    public interface PoopDataCallback {
        void onCallback(ArrayList allData);
    }

    public void getMyPoopSatisfactionData(final PoopSatisfactionDataCallback callback) {
        poopOccurrencesRef
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            ArrayList<Double> satisfactionValues = new ArrayList<>();
                            for (DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()) {
                                satisfactionValues.add(ds.getDouble("satisfaction"));
                            }
                            Map<Double, Integer> satisfactionData = extractSatisfactionData(satisfactionValues);
                            callback.onCallback(satisfactionData);
                        }
                    }
                });
    }

    private Map<Double, Integer> extractSatisfactionData(ArrayList<Double> satisfactions) {
        Map<Double, Integer> data = new HashMap<Double, Integer>() {
            {
                put(1.0, 0);
                put(2.0, 0);
                put(3.0, 0);
                put(4.0, 0);
                put(5.0, 0);
            }
        };

        for (double satisfaction : satisfactions) {
            if (data.containsKey(satisfaction)) {
                data.put(satisfaction, data.get(satisfaction) + 1);
            } else {
                double key = satisfaction + 0.5;
                data.put(key, data.get(key) + 1);
            }
        }

        return data;
    }

    public interface PoopSatisfactionDataCallback {
        void onCallback(Map<Double, Integer> satisfactionData);
    }
}
