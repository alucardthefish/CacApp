package com.sop.cacapp.Fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.sop.cacapp.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditProfileFragment extends Fragment {

    private View rootView;

    private Timestamp dateOfBirth;

    private String pattern;
    private Locale current;

    private TextInputEditText etUserNameEdition;
    private TextInputEditText etUserHeightEdition;
    private TextInputEditText etUserWeightEdition;
    // gender here
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

        initViews();
        initListeners();

        return rootView;
    }

    private void initViews() {
        pattern = "dd MMMM yyyy";
        current = rootView.getResources().getConfiguration().locale;

        etUserNameEdition = rootView.findViewById(R.id.etUserNameEdition);
        etUserHeightEdition = rootView.findViewById(R.id.etUserHeightEdition);
        etUserWeightEdition = rootView.findViewById(R.id.etUserWeightEdition);
        // gender here
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
            etUserBirthDateEdition.setText(new SimpleDateFormat(pattern, current).format(bDate));
        }
    }
}
