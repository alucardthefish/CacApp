package com.sop.cacapp.Fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sop.cacapp.Adapters.SymptomOccurrenceAdapter;
import com.sop.cacapp.Classes.Symptom;
import com.sop.cacapp.CustomViewClasses.CustomRecyclerView;
import com.sop.cacapp.R;

import java.util.ArrayList;


public class SymptomsMainFragment extends Fragment {

    private SymptomOccurrenceAdapter symptomAdapter;
    private CustomRecyclerView customRecyclerView;
    private ArrayList<Symptom> symptomArrayList;
    private TextView emptyView;

    public SymptomsMainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_symptoms_main, container, false);

        emptyView = view.findViewById(R.id.tvNoSymptoms);

        customRecyclerView = view.findViewById(R.id.customRecyclerView);
        customRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        customRecyclerView.setEmptyView(emptyView);

        symptomArrayList = new ArrayList<>();

        symptomAdapter = new SymptomOccurrenceAdapter(getContext(), symptomArrayList);

        customRecyclerView.setAdapter(symptomAdapter);
        feedRecycler();

        return view;
    }

    private void feedRecycler() {
        Symptom s1 = new Symptom("Hola mundo este es mi primer sintoma vamos a ver como nos va con la app", 2.0f);
        Symptom s2 = new Symptom("Como esta mi pierna hoy muy llevada pues me la tronche como un putas", 2.5f);
        Symptom s3 = new Symptom("Hola mundo no te digo mucho hoy", 4.0f);
        Symptom s4 = new Symptom("Hola mundo3", 4.0f);
        symptomArrayList.add(s1);
        symptomArrayList.add(s2);
        symptomArrayList.add(s3);
        symptomArrayList.add(s4);

        symptomAdapter = new SymptomOccurrenceAdapter(getContext(), symptomArrayList);
        customRecyclerView.setAdapter(symptomAdapter);
    }
}
