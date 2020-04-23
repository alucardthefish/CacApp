package com.sop.cacapp.Fragments;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.sop.cacapp.Classes.HealthBackground;
import com.sop.cacapp.Persistence.ProfilePersistent;
import com.sop.cacapp.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EditProfileFragment extends Fragment {

    private View rootView;
    private ActionMode actionMode;

    private Timestamp dateOfBirth;
    private String gender;

    private String pattern;
    private Locale current;

    private TextInputEditText etUserNameEdition;
    private TextInputEditText etUserHeightEdition;
    private TextInputEditText etUserWeightEdition;
    private RadioGroup rgGender;
    private TextInputEditText etUserBirthDateEdition;
    private TextInputEditText etUserAllergiesEdition;
    private TextInputEditText etUserConditionsEdition;
    private TextInputEditText etUserSurgeriesEdition;
    private TextInputEditText etUserFamilyEdition;


    public EditProfileFragment() {
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
        rootView = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        initActionMode();
        initViews();
        loadProfileBundle();
        initListeners();

        return rootView;
    }

    private void initViews() {
        pattern = "dd MMMM yyyy";
        current = rootView.getResources().getConfiguration().locale;

        etUserNameEdition = rootView.findViewById(R.id.etUserNameEdition);
        etUserHeightEdition = rootView.findViewById(R.id.etUserHeightEdition);
        etUserWeightEdition = rootView.findViewById(R.id.etUserWeightEdition);
        rgGender = rootView.findViewById(R.id.genderEditOptions);
        etUserBirthDateEdition = rootView.findViewById(R.id.etUserBirthDateEdition);
        etUserAllergiesEdition = rootView.findViewById(R.id.etUserAllergiesEdition);
        etUserConditionsEdition = rootView.findViewById(R.id.etUserConditionsEdition);
        etUserSurgeriesEdition = rootView.findViewById(R.id.etUserSurgeriesEdition);
        etUserFamilyEdition = rootView.findViewById(R.id.etUserFamilyEdition);
    }

    private void initListeners() {
        etUserBirthDateEdition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
                int currentMonth = calendar.get(Calendar.MONTH);
                int currentYear = calendar.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog = new DatePickerDialog(rootView.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, month, dayOfMonth);
                        dateOfBirth = new Timestamp(newDate.getTime());
                        etUserBirthDateEdition.setText(new SimpleDateFormat(pattern, current).format(newDate.getTime()));
                    }
                }, currentYear, currentMonth, currentDay);
                datePickerDialog.show();
            }
        });
    }

    private void loadProfileBundle() {
        if (getArguments() != null) {
            Bundle profileBundle = getArguments();
            Date bDate = new Date(profileBundle.getLong("birthdayInLong"));
            dateOfBirth = new Timestamp(bDate);
            etUserBirthDateEdition.setText(new SimpleDateFormat(pattern, current).format(bDate));
            if (profileBundle.getString("gender").equals("female")) {
                rgGender.check(R.id.rbEditFemale);
            } else {
                rgGender.check(R.id.rbEditMale);
            }
            etUserNameEdition.setText(profileBundle.getString("name"));
            int height = profileBundle.getInt("height");
            double weight = profileBundle.getDouble("weight");
            etUserHeightEdition.setText(Integer.toString(height));
            etUserWeightEdition.setText(Double.toString(weight));

            String allergies = profileBundle.getString("allergies");
            String surgicals = profileBundle.getString("surgicals");
            String conditions = profileBundle.getString("conditions");
            String family = profileBundle.getString("family");

            if (!allergies.equals("Ninguna")) {
                etUserAllergiesEdition.setText(allergies);
            }
            if (!surgicals.equals("Ninguna")) {
                etUserSurgeriesEdition.setText(surgicals);
            }
            if (!conditions.equals("Ninguna")) {
                etUserConditionsEdition.setText(conditions);
            }
            if (!family.equals("Ninguna")) {
                etUserFamilyEdition.setText(family);
            }
        }
    }

    private Map<String, Object> captureDataProfile() {
        Bundle bundie = getArguments();
        String name = etUserNameEdition.getText().toString();
        int height = Integer.parseInt(etUserHeightEdition.getText().toString());
        double weight = Double.parseDouble(etUserWeightEdition.getText().toString());
        if (rgGender.getCheckedRadioButtonId() == R.id.rbEditFemale) {
            gender = "female";
        } else {
            gender = "male";
        }
        Timestamp bDateBundle = new Timestamp(new Date(bundie.getLong("birthdayInLong")));
        String allergies = etUserAllergiesEdition.getText().toString();
        String healthConditions = etUserConditionsEdition.getText().toString();
        String surgeries = etUserSurgeriesEdition.getText().toString();
        String family = etUserFamilyEdition.getText().toString();

        HealthBackground hb = new HealthBackground();
        hb.setAlergies(allergies);
        hb.setConditionsOrIllnessesDiagnosed(healthConditions);
        hb.setSurgicalProcedures(surgeries);
        hb.setFamilyBackground(family);

        Map<String, Object> dataUpdatedMap = new HashMap<>();

        if (dateOfBirth != bDateBundle) {
            dataUpdatedMap.put("birthDate", dateOfBirth);
        }
        if (!name.equals(bundie.getString("name"))) {
            dataUpdatedMap.put("name", name);
        }
        if (height != bundie.getInt("height")) {
            dataUpdatedMap.put("height", height);
        }
        if (weight != bundie.getDouble("weight")) {
            dataUpdatedMap.put("weight", weight);
        }
        if (!gender.equals(bundie.getString("gender"))) {
            dataUpdatedMap.put("gender", gender);
        }
        dataUpdatedMap.put("healthBackground", hb);

        return dataUpdatedMap;

    }

    private void updateProfile() {
        Map<String, Object> data = captureDataProfile();
        ProfilePersistent profilePersistent = new ProfilePersistent();
        profilePersistent.updateProfile(data, new ProfilePersistent.Callback() {
            @Override
            public void onCallback(boolean isSuccess) {
                if (isSuccess) {
                    Toast.makeText(rootView.getContext(), "Actualizado exitosamente", Toast.LENGTH_SHORT).show();
                    actionMode.finish();
                } else {
                    Log.d("updateProfile", "No se pudo actualizar");
                }
            }
        });
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
                    Toast.makeText(rootView.getContext(), "Enviar actualizacion", Toast.LENGTH_SHORT).show();
                    updateProfile();
                    //mode.finish();
                    return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                getActivity().onBackPressed();
            }
        });
    }
}
