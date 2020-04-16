package com.sop.cacapp.Fragments;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.sop.cacapp.Classes.Symptom;
import com.sop.cacapp.LoaderDialog;
import com.sop.cacapp.Persistence.SymptomPersistent;
import com.sop.cacapp.R;


public class EditSymptomFragment extends Fragment {

    private View rootView;
    private ActionMode actionMode;

    private RatingBar rbEditSymptom;
    private EditText etEditSymptom;
    private LoaderDialog loaderDialog;

    private Symptom symptomToEdit;

    public EditSymptomFragment() {
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
        rootView = inflater.inflate(R.layout.fragment_edit_symptom, container, false);

        initActionMode();
        initViews();
        loadBundle();

        return rootView;
    }

    private void initActionMode() {
        AppCompatActivity mainActivity = ((AppCompatActivity) getActivity());
        actionMode = mainActivity.startSupportActionMode(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.edit_symptom_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                if (item.getItemId() == R.id.action_ok) {
                    updateSymptom();
                    mode.finish();
                    return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                getActivity().onBackPressed();
            }
        });
        actionMode.setTitle("Editar");
        actionMode.setSubtitle("sintoma");
    }

    private void initViews() {
        rbEditSymptom = rootView.findViewById(R.id.rbEditSymptom);
        etEditSymptom = rootView.findViewById(R.id.etEditSymptom);
        symptomToEdit = null;
        loaderDialog = new LoaderDialog(rootView.getContext());
    }

    private void loadBundle() {
        loaderDialog.startLoading();
        if (getArguments() != null) {
            Bundle message = getArguments();
            rbEditSymptom.setRating(message.getFloat("symptomIntensity"));
            etEditSymptom.setText(message.getString("symptomDesc"));
            String symptomId = message.getString("symptomId");

            SymptomPersistent symptomPersistent = new SymptomPersistent();
            symptomPersistent.getSymptomById(symptomId, new SymptomPersistent.OnGetSymptomByIdCallback() {
                @Override
                public void onSuccess(Symptom symptom) {
                    symptomToEdit = symptom;
                    Log.d("EditSymptomFragment", symptomToEdit.toString());
                    loaderDialog.stopLoading();
                }

                @Override
                public void onFailure(Symptom symptom, String failureMessage) {
                    Toast.makeText(rootView.getContext(), failureMessage, Toast.LENGTH_LONG).show();
                    loaderDialog.stopLoading();
                }
            });
        }
    }

    private boolean hasDataChanged() {
        return (rbEditSymptom.getRating() != symptomToEdit.getIntensity() || !etEditSymptom.getText().toString().equals(symptomToEdit.getDescription()));
    }

    private void updateSymptom() {
        if (hasDataChanged()) {
            Toast.makeText(rootView.getContext(), "Los datos no han cambiado", Toast.LENGTH_LONG).show();
            symptomToEdit.setDescription(etEditSymptom.getText().toString());
            symptomToEdit.setIntensity(rbEditSymptom.getRating());
            SymptomPersistent symptomPersistent = new SymptomPersistent();
            symptomPersistent.updateSymptom(symptomToEdit, new SymptomPersistent.OnSymptomPersistentCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(rootView.getContext(), "Sintoma actualizado", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure() {
                    Toast.makeText(rootView.getContext(), "Ocurrio un error al intentar actualizar", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(rootView.getContext(), "Ningun cambio registrado", Toast.LENGTH_LONG).show();
        }
    }
}
