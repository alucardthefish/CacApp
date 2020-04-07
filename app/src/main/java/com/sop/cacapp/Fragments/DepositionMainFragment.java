package com.sop.cacapp.Fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.sop.cacapp.Classes.OccurrenceDateTimeHandler;
import com.sop.cacapp.Classes.PoopOccurrence;
import com.sop.cacapp.Classes.Profile;
import com.sop.cacapp.LoaderDialog;
import com.sop.cacapp.Persistence.PoopOccurrencePersistent;
import com.sop.cacapp.Persistence.ProfilePersistent;
import com.sop.cacapp.R;


public class DepositionMainFragment extends Fragment {
    private View view;

    private Button btnAddPoop;
    private TextView tvRegisterDate;
    private TextView tvDepositionCounter;
    private TextView tvDayCounter;
    private TextView tvAvgFrequency;
    private TextView tvLastDepositionDate;
    private TextView tvLastDepositionTimeElapsed;
    private Timestamp registerDateTimestamp;

    private Dialog mDialog;
    private LoaderDialog loadingBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_deposition_main, container, false);
        initViews(view);
        LoadData();
        InitListeners();

        return view;
    }

    private void initViews(View view) {
        btnAddPoop = view.findViewById(R.id.btnAddPoopRecord);
        tvRegisterDate = view.findViewById(R.id.tvRegisterDate);
        tvDepositionCounter = view.findViewById(R.id.tvDepositionCounter);
        tvDayCounter = view.findViewById(R.id.tvDayCounter);
        tvAvgFrequency = view.findViewById(R.id.tvAvgFrequency);
        tvLastDepositionDate = view.findViewById(R.id.tvLastDepositionDate);
        tvLastDepositionTimeElapsed = view.findViewById(R.id.tvLastDepositionTimeElapsed);
        mDialog = new Dialog(view.getContext());
        loadingBar = new LoaderDialog(view.getContext());
    }

    private void InitListeners() {
        btnAddPoop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
    }

    private void reloadCalculatedDataWithListener() {
        DocumentReference doc = new PoopOccurrencePersistent().getCalculatedDataDocRef();
        doc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    loadingBar.stopLoading();
                    Log.d("snaplistener", "Listen Failed.");
                    return;
                }
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    if (view.getContext() != null) {
                        OccurrenceDateTimeHandler registerDate = new OccurrenceDateTimeHandler(registerDateTimestamp, view.getContext());

                        tvDepositionCounter.setText(String.format(view.getResources().getConfiguration().locale, "%d", documentSnapshot.getLong("deposition_counter")));
                        tvDayCounter.setText(registerDate.getTimeElapsed());

                        long timeFrequency = documentSnapshot.getLong("deposition_mean_frequency");
                        tvAvgFrequency.setText(registerDate.getTimeElapsedByDiffTime(timeFrequency));

                        if (documentSnapshot.get("last_deposition_date") instanceof String) {
                            tvLastDepositionDate.setText("Sin registros aún");
                            tvLastDepositionTimeElapsed.setText("Sin registros aún");
                        } else {
                            Timestamp last = documentSnapshot.getTimestamp("last_deposition_date");
                            OccurrenceDateTimeHandler lastOccurrence = new OccurrenceDateTimeHandler(last, view.getContext());

                            tvLastDepositionDate.setText(lastOccurrence.getFullOccurrenceDateTime());
                            tvLastDepositionTimeElapsed.setText(lastOccurrence.getTimeElapsed());
                        }
                        loadingBar.stopLoading();
                    } else {
                        loadingBar.stopLoading();
                        Toast.makeText(view.getContext(), "Contexto null", Toast.LENGTH_SHORT).show();

                    }

                } else {
                    loadingBar.stopLoading();
                    Toast.makeText(view.getContext(), "No data or does not exist", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void LoadData() {
        // Load info in the text views
        loadingBar.startLoading();
        ProfilePersistent dbProfile = new ProfilePersistent();

        dbProfile.GetProfile(new ProfilePersistent.MyCallback() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onCallBack(boolean isSuccess, Profile profile) {
                if (isSuccess && getContext() != null) {

                    registerDateTimestamp = profile.getRegisterDate();
                    tvRegisterDate.setText(new OccurrenceDateTimeHandler(registerDateTimestamp, getContext()).getFullOccurrenceDateTime());
                    reloadCalculatedDataWithListener();
                    loadingBar.stopLoading();

                } else {
                    loadingBar.stopLoading();
                    Toast.makeText(getContext(), "No se pudo cargar datos", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void showDialog() {
        final RatingBar ratingBarDepositionSatisfaction;
        Button btnAccept;
        Button btnCancel;

        mDialog.setContentView(R.layout.dialog_pre_deposition);
        ratingBarDepositionSatisfaction = mDialog.findViewById(R.id.ratingBarDepositionSatisfaction);
        btnAccept = mDialog.findViewById(R.id.btnAccept);
        btnCancel = mDialog.findViewById(R.id.btnCancel);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Dialog", "Dialog was canceled");
                mDialog.dismiss();
            }
        });

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float satisfaction = ratingBarDepositionSatisfaction.getRating();
                PoopOccurrence poopOccurrence = new PoopOccurrence();
                poopOccurrence.setSatisfaction(satisfaction);
                PoopOccurrencePersistent dbAccessor = new PoopOccurrencePersistent();
                dbAccessor.addPoopOccurrence(view, poopOccurrence);

                mDialog.dismiss();
                loadingBar.startLoading();
                Log.d("Dialog", "Dialog accepted with a satisfaction of: " + satisfaction);
            }
        });

        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.show();

    }


}
