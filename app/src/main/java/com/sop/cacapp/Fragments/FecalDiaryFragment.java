package com.sop.cacapp.Fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.sop.cacapp.Adapters.PoopOccurrenceAdapter;
import com.sop.cacapp.Classes.PoopOccurrence;
import com.sop.cacapp.Persistence.PoopOccurrencePersistent;
import com.sop.cacapp.R;

import java.util.ArrayList;


public class FecalDiaryFragment extends Fragment {

    private PoopOccurrenceAdapter poopOccurrenceAdapter;
    private RecyclerView recyclerViewPoopOccurrence;
    private ArrayList<PoopOccurrence> poopOccurrenceArrayList;

    public FecalDiaryFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fecal_diary, container, false);
        recyclerViewPoopOccurrence = view.findViewById(R.id.recyclerViewDiary);
        recyclerViewPoopOccurrence.setLayoutManager(new LinearLayoutManager(getContext()));
        poopOccurrenceArrayList = new ArrayList<>();

        poopOccurrenceAdapter = new PoopOccurrenceAdapter(getContext(), poopOccurrenceArrayList);
        recyclerViewPoopOccurrence.setAdapter(poopOccurrenceAdapter);
        // Load list
        loadList();
        return view;
    }

    public void loadList() {
        CollectionReference poopOccurrencesReference = new PoopOccurrencePersistent().getPoopOccurrencesRef();
        poopOccurrencesReference
                .orderBy("occurrenceTimestamp", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.d("snaplistener", "Listen Failed on loadList method of FecalDiaryFragment.");
                            return;
                        }
                        if (!queryDocumentSnapshots.isEmpty()) {
                            Log.d("snaplistener", "La cantidad de datos son: " + queryDocumentSnapshots.size());
                            for (DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()) {
                                PoopOccurrence poopOccurrence = ds.toObject(PoopOccurrence.class);
                                poopOccurrenceArrayList.add(poopOccurrence);
                            }
                            // Show data
                            if (getContext() != null) {
                                showData();
                            }
                        }
                    }
                });
    }

    public void showData() {
        recyclerViewPoopOccurrence.setLayoutManager(new LinearLayoutManager(getContext()));
        poopOccurrenceAdapter = new PoopOccurrenceAdapter(getContext(), poopOccurrenceArrayList);
        recyclerViewPoopOccurrence.setAdapter(poopOccurrenceAdapter);
    }
}
