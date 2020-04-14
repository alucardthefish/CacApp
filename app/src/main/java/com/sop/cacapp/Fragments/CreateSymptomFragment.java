package com.sop.cacapp.Fragments;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sop.cacapp.Classes.Symptom;
import com.sop.cacapp.Persistence.SymptomPersistent;
import com.sop.cacapp.R;


public class CreateSymptomFragment extends Fragment {

    private View view;
    private ActionMode actionMode;

    private RatingBar rbCreateSymptomIntensity;
    private EditText etCreateSymptomDesc;

    private float symptomIntensity;
    private String symptomDescription;

    public CreateSymptomFragment() {
        // Required empty public constructor
        Log.d("CreateSymptomFragment", "Constructor");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("CreateSymptomFragment", "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("CreateSymptomFragment", "onCreateView");
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_create_symptom, container, false);

        initViews();
        initActionMode();

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("CreateSymptomFragment", "onDestroy");
    }

    private void initViews() {
        rbCreateSymptomIntensity = view.findViewById(R.id.rbCreateSymptomIntensity);
        etCreateSymptomDesc = view.findViewById(R.id.etCreateSymptomDesc);
    }

    private void initActionMode() {
        AppCompatActivity mainActivity = ((AppCompatActivity) getActivity());
        actionMode = mainActivity.startSupportActionMode(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.create_symptom_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                boolean flag = false;
                int id = item.getItemId();
                switch (id) {
                    case R.id.action_create:
                        createSymptom();
                        flag = true;
                        break;
                    default:
                        break;
                }
                return flag;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                getActivity().onBackPressed();
            }
        });
    }

    private void createSymptom() {
        symptomIntensity = rbCreateSymptomIntensity.getRating();
        symptomDescription = etCreateSymptomDesc.getText().toString();
        if (isValidData(symptomIntensity, symptomDescription)) {
            Symptom symptom = new Symptom(symptomDescription, symptomIntensity);
            SymptomPersistent symptomPersistent = new SymptomPersistent();
            symptomPersistent.addSymptom(symptom, view);
            actionMode.finish();
        }
    }

    private boolean isValidData(float intensity, String desc) {
        boolean flag = true;
        String msg = "";
        if (intensity == 0) {
            msg += "La intensidad es requerida. \n";
            flag = false;
        }
        if (desc.length() < 61) {
            msg += "La descripción de tu síntoma debe ser almenos de 60 characteres";
            flag = false;
        }
        if (msg.length() > 0) {
            Toast.makeText(view.getContext(), msg, Toast.LENGTH_LONG).show();
        }
        return flag;
    }

}
