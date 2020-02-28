package com.sop.cacapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sop.cacapp.Persistence.PoopOccurrencePersistent;
import com.sop.cacapp.R;


public class MainFragment extends Fragment {

    private Button btnAddPoop;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.main_fragment, container, false);
        btnAddPoop = view.findViewById(R.id.btnAddPoopRecord);
        btnAddPoop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(view.getContext(), "Creando deposición", Toast.LENGTH_SHORT).show();
                PoopOccurrencePersistent dbAccessor = new PoopOccurrencePersistent();
                dbAccessor.CreatePoopOccurrence(view);
                /*if (dbAccessor.CreatePoopOccurrence()) {
                    Toast.makeText(view.getContext(), "Exitosamente agregado a poop diary", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(view.getContext(), "No se pudo agregar a poop diary", Toast.LENGTH_LONG).show();
                }*/
            }
        });
        return view;
    }
}
