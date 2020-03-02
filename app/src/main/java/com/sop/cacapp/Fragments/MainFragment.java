package com.sop.cacapp.Fragments;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.sop.cacapp.Object.Profile;
import com.sop.cacapp.Persistence.PoopOccurrencePersistent;
import com.sop.cacapp.Persistence.ProfilePersistent;
import com.sop.cacapp.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;


public class MainFragment extends Fragment {
    private View view;

    private Button btnAddPoop;
    private TextView tvRegisterDate;
    private TextView tvDepositionCounter;
    private TextView tvDayCounter;
    private TextView tvAvgFrequency;
    private TextView tvDepositionStatus;
    private TextView tvLastDeposition;
    private int daysElapsed;
    private Timestamp registerDateTimestamp;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.main_fragment, container, false);

        btnAddPoop = view.findViewById(R.id.btnAddPoopRecord);
        tvRegisterDate = view.findViewById(R.id.tvRegisterDate);
        tvDepositionCounter = view.findViewById(R.id.tvDepositionCounter);
        tvDayCounter = view.findViewById(R.id.tvDayCounter);
        tvAvgFrequency = view.findViewById(R.id.tvAvgFrequency);
        tvDepositionStatus = view.findViewById(R.id.tvDepositionStatus);
        tvLastDeposition = view.findViewById(R.id.tvLastDeposition);

        LoadData();

        InitListeners();

        return view;
    }

    private void InitListeners() {
        btnAddPoop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(view.getContext(), "Creando deposición", Toast.LENGTH_SHORT).show();
                PoopOccurrencePersistent dbAccessor = new PoopOccurrencePersistent();
                //dbAccessor.CreatePoopOccurrence(view);
                dbAccessor.CreatePoopOccurrenceTwo(view, new PoopOccurrencePersistent.OnCreatePoopOccurrenceListener() {
                    @Override
                    public void onCallBack(Map<String, Object> data) {
                        Toast.makeText(view.getContext(), "Deposición guardada", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void LoadReloader() {
        DocumentReference doc = new PoopOccurrencePersistent().getmCollectedDataReference();
        doc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d("snaplistener", "Listen Failed.");
                    return;
                }
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    daysElapsed = GetDaysDifference(registerDateTimestamp.toDate(), new Date());
                    int dposCounter = documentSnapshot.getLong("deposition_counter").intValue();
                    tvDepositionCounter.setText(documentSnapshot.getData().get("deposition_counter").toString());
                    tvDayCounter.setText(""+daysElapsed);
                    Timestamp last = documentSnapshot.getTimestamp("last_deposition_date");
                    tvDepositionStatus.setText(getDateTime(last).toString());
                    tvLastDeposition.setText(GetTimeDifference(last.toDate(), new Date()));
                    tvAvgFrequency.setText(""+getDepositionFrequency(daysElapsed, dposCounter));
                }
            }
        });
    }

    private void LoadData() {
        // Load info in the text views
        ProfilePersistent dbProfile = new ProfilePersistent();

        dbProfile.GetProfile(new ProfilePersistent.MyCallback() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onCallBack(Profile profile) {
                registerDateTimestamp = profile.getRegisterDate();
                tvRegisterDate.setText(getDateTime(registerDateTimestamp));
                daysElapsed = GetDaysDifference(registerDateTimestamp.toDate(), new Date());
                tvDayCounter.setText(GetTimeDifference(registerDateTimestamp.toDate(), new Date()));

                LoadReloader();
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public String getDateTime(Timestamp ts) {
        Date date = ts.toDate();
        String pattern = "dd MMMM yyyy hh:mm";
        Locale current = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) ? getResources().getConfiguration().getLocales().get(0) : getResources().getConfiguration().locale;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, current);
        return simpleDateFormat.format(date);
    }

    public String GetTimeDifference(Date d1, Date d2) {
        int timeDiff = 0;
        String unidad = "dia(s)";
        long diff = d2.getTime() - d1.getTime();
        long diffDays = diff / (24 * 60 * 60 * 1000);
        Log.d("DiffTime-date1", Long.toString(d1.getTime()));
        Log.d("DiffTime-date2", Long.toString(d2.getTime()));
        Log.d("DiffTime", Long.toString(diffDays));
        if (diffDays < 1) {
            // Set to hours
            unidad = "hora(s)";
            diffDays = diff / (60 * 60 * 1000) ;
            if (diffDays < 1) {
                // Set to minutes
                unidad = "minutos";
                diffDays = diff / (60 * 1000) ;
            }
        }
        timeDiff = (int) diffDays;
        String msg = "" + timeDiff + " " + unidad;
        return msg;
    }

    public int GetDaysDifference(Date d1, Date d2) {
        int timeDiff = 0;
        long diff = d2.getTime() - d1.getTime();
        long diffDays = diff / (24 * 60 * 60 * 1000);
        timeDiff = (int) diffDays;
        return timeDiff;
    }

    public double getDepositionFrequency(int numOfDays, int numOfDepositions) {
        double res = 0;
        if (numOfDepositions > 0) {
            res = numOfDays / numOfDepositions;
        }
        return res;
    }


}
