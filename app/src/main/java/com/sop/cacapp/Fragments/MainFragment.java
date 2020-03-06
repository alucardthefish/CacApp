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
    private TextView tvLastDepositionDate;
    private TextView tvLastDepositionTimeElapsed;
    private int daysElapsed;
    private Timestamp registerDateTimestamp;

    private Dialog mDialog;

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
        tvLastDepositionDate = view.findViewById(R.id.tvLastDepositionDate);
        tvLastDepositionTimeElapsed = view.findViewById(R.id.tvLastDepositionTimeElapsed);

        mDialog = new Dialog(view.getContext());

        LoadData();

        InitListeners();

        return view;
    }

    private void InitListeners() {
        btnAddPoop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(view.getContext(), "Creando deposición", Toast.LENGTH_SHORT).show();
                //PoopOccurrencePersistent dbAccessor = new PoopOccurrencePersistent();
                //dbAccessor.CreatePoopOccurrence(view);
                showDialog();

                /*dbAccessor.CreatePoopOccurrenceTwo(view, new PoopOccurrencePersistent.OnCreatePoopOccurrenceListener() {
                    @Override
                    public void onCallBack(Map<String, Object> data) {
                        Toast.makeText(view.getContext(), "Deposición guardada", Toast.LENGTH_SHORT).show();
                    }
                });*/
            }
        });
    }

    private void loadReloader() {
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
                    if (documentSnapshot.get("last_deposition_date") instanceof String) {
                        tvLastDepositionDate.setText("Sin registros aun");
                        tvLastDepositionTimeElapsed.setText(documentSnapshot.getString("last_deposition_date"));
                    } else {
                        Timestamp last = documentSnapshot.getTimestamp("last_deposition_date");
                        if (getContext() != null) {
                            tvLastDepositionDate.setText(getDateTime(last));
                        } else {
                            tvLastDepositionDate.setText("No cargó");
                        }
                        //tvLastDepositionDate.setText(getDateTime(last));
                        tvLastDepositionTimeElapsed.setText(GetTimeDifference(last.toDate(), new Date()));
                    }
                    long timeFrequency = documentSnapshot.getLong("deposition_mean_frequency");
                    tvAvgFrequency.setText(getTimeFormatFromDifference(timeFrequency));
                    //tvAvgFrequency.setText(Double.toString(getDepositionFrequency(daysElapsed, dposCounter)));
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

                loadReloader();
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public String getDateTime(Timestamp ts) {
        Date date = ts.toDate();
        String pattern = "dd MMMM yyyy hh:mm aa";
        Locale current = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) ? this.getContext().getResources().getConfiguration().getLocales().get(0) : this.getContext().getResources().getConfiguration().locale;
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
            res = (double) numOfDays / numOfDepositions;
        }
        return res;
    }

    private String getTimeFormatFromDifference(long timeDifference) {
        int diffDays = (int) (timeDifference / (24 * 60 * 60 * 1000));
        int diffHours = (int) (timeDifference / (60 * 60 * 1000));
        int diffMinutes = (int) (timeDifference / (60 * 1000));
        int concreteDays = diffDays;
        int concreteHours = diffHours - (concreteDays * 24);
        int concreteMinutes = diffMinutes - ((concreteHours + (concreteDays * 24)) * 60);
        String output = "";
        output += (concreteDays > 0) ? String.format("%d dias ", concreteDays) : "";
        output += (concreteHours > 0) ? String.format("%d horas ", concreteHours) : "";
        output += String.format("%d minutos", concreteMinutes);
        return output;
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
                PoopOccurrencePersistent dbAccessor = new PoopOccurrencePersistent();
                dbAccessor.CreatePoopOccurrence(view, satisfaction);
                mDialog.dismiss();
                Log.d("Dialog", "Dialog accepted with a satisfaction of: " + satisfaction);
            }
        });

        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.show();

    }


}
