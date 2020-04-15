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
import android.widget.TextView;

import com.sop.cacapp.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ViewSymptomFragment extends Fragment {

    private View view;
    private ActionMode actionMode;

    private TextView tvDate;
    private TextView tvTime;
    private TextView tvIntensity;
    private TextView tvDescription;

    public ViewSymptomFragment() {
        // Required empty public constructor
        Log.d("ViewSymptomFragment", "on Constructor");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("ViewSymptomFragment", "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("ViewSymptomFragment", "onCreateView");
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_view_symptom, container, false);

        initActionMode();
        initViews();
        loadData();



        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("ViewSymptomFragment", "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("ViewSymptomFragment", "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("ViewSymptomFragment", "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("ViewSymptomFragment", "onStop");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.d("ViewSymptomFragment", "onLowMemory");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("ViewSymptomFragment", "onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("ViewSymptomFragment", "onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("ViewSymptomFragment", "onDetach");
    }

    @Override
    public void onDestroyOptionsMenu() {
        super.onDestroyOptionsMenu();
        Log.d("ViewSymptomFragment", "onDestroyOptionsMenu");
    }

    private void initViews() {
        tvDate = view.findViewById(R.id.tvViewDate);
        tvTime = view.findViewById(R.id.tvViewTime);
        tvIntensity = view.findViewById(R.id.tvIntensity);
        tvDescription = view.findViewById(R.id.tvDescription);
    }

    private void loadData() {
        Bundle bundle = getArguments();
        float intensity = bundle.getFloat("symptomIntensity");
        long symptomDateLong = bundle.getLong("symptomDateLong");
        Date symptomDate = new Date(symptomDateLong);
        String patternOne = "dd MMMM yyyy";
        String patternTwo = "hh:mm aa";
        Locale current = view.getContext().getResources().getConfiguration().locale;
        tvDate.setText(new SimpleDateFormat(patternOne, current).format(symptomDate));
        tvTime.setText(new SimpleDateFormat(patternTwo, current).format(symptomDate));
        String desc = bundle.getString("symptomDesc");
        String strIntensity = String.format(current, "Intensidad %.1f de 5", intensity);
        tvIntensity.setText(strIntensity);
        tvDescription.setText(desc);
    }

    private void initActionMode() {
        AppCompatActivity mainActivity = ((AppCompatActivity) getActivity());
        actionMode = mainActivity.startSupportActionMode(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                Log.d("ViewSympFragment", "Me destruyeron");
                getActivity().onBackPressed();
            }
        });
    }
}
