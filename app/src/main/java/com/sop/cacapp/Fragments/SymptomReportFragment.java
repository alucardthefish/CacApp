package com.sop.cacapp.Fragments;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.sop.cacapp.Classes.OccurrenceDateTimeHandler;
import com.sop.cacapp.Classes.Symptom;
import com.sop.cacapp.Persistence.SymptomPersistent;
import com.sop.cacapp.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class SymptomReportFragment extends Fragment implements View.OnClickListener {

    private View rootView;

    private EditText tvInitDate;
    private EditText tvEndDate;
    private ImageButton btnInitDate;
    private ImageButton btnEndDate;
    private Button btnGenerateReport;
    private Button btnShareReport;
    private TextView tvReportDisplay;
    private Timestamp initDate;
    private Timestamp endDate;

    public SymptomReportFragment() {
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
        rootView = inflater.inflate(R.layout.fragment_symptom_report, container, false);
        initDate = null;
        endDate = null;

        initViews();
        setListeners();

        return rootView;
    }

    private void initViews() {
        tvInitDate = rootView.findViewById(R.id.tvInitDate);
        tvEndDate = rootView.findViewById(R.id.tvEndDate);
        btnInitDate = rootView.findViewById(R.id.btnInitDate);
        btnEndDate = rootView.findViewById(R.id.btnEndDate);
        btnGenerateReport = rootView.findViewById(R.id.btnGenerateReport);
        btnShareReport = rootView.findViewById(R.id.btnShareReport);
        tvReportDisplay = rootView.findViewById(R.id.tvReportDisplay);
        tvReportDisplay.setMovementMethod(new ScrollingMovementMethod());
    }

    private void setListeners() {
        btnInitDate.setOnClickListener(this);
        btnEndDate.setOnClickListener(this);
        btnGenerateReport.setOnClickListener(this);
        btnShareReport.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        selectDate(viewId);
        if (viewId == R.id.btnGenerateReport) {
            generateReport();
        } else if (viewId == R.id.btnShareReport) {
            //Toast.makeText(rootView.getContext(), "Compartiendo", Toast.LENGTH_LONG).show();
            shareReport();
        }

    }

    private void selectDate(int id) {

        Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);

        final String datePattern = "MM/dd/yyyy";
        final Locale currentLocale = rootView.getResources().getConfiguration().locale;

        switch (id) {
            case R.id.btnInitDate:
                DatePickerDialog datePickerDialog = new DatePickerDialog(rootView.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, month, dayOfMonth);
                        initDate = new Timestamp(newDate.getTime());
                        tvInitDate.setText(new SimpleDateFormat(datePattern, currentLocale).format(newDate.getTime()));
                    }
                }, currentYear, currentMonth, currentDay);
                datePickerDialog.show();
                break;
            case R.id.btnEndDate:
                DatePickerDialog datePickerDialog2 = new DatePickerDialog(rootView.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, month, dayOfMonth);
                        endDate = new Timestamp(newDate.getTime());
                        tvEndDate.setText(new SimpleDateFormat(datePattern, currentLocale).format(newDate.getTime()));
                    }
                }, currentYear, currentMonth, currentDay);
                datePickerDialog2.show();
                break;
        }
    }

    private boolean areDatesFilled() {
        return (initDate != null && endDate != null);
    }

    private boolean areDatesInCorrectInterval() {
        Date iniDate = initDate.toDate();
        Date finalDate = endDate.toDate();
        return (finalDate.after(iniDate) || iniDate.equals(finalDate));
    }

    private void generateReport() {
        if (areDatesFilled()) {
            if (areDatesInCorrectInterval()) {
                Snackbar.make(rootView, "Se manda action al modelo firebase", Snackbar.LENGTH_LONG).show();
                executeReport();
            } else {
                Toast.makeText(rootView.getContext(), "El intervalo de tiempo es incorrecto", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(rootView.getContext(), "Las fechas de intervalo deben ser completadas", Toast.LENGTH_LONG).show();
        }
    }

    private void executeReport() {
        SymptomPersistent symptomPersistent = new SymptomPersistent();
        Query query = symptomPersistent.getSymptomsByInterval(initDate, endDate);
        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isComplete() && task.isSuccessful()) {
                            boolean areThereResults = !task.getResult().isEmpty();
                            enableShareButton(areThereResults);
                            if (areThereResults) {
                                String reportString = "";
                                int index = 1;
                                for (DocumentSnapshot ds : task.getResult()) {
                                    reportString += templateReport(ds.toObject(Symptom.class), index);
                                    printReport(reportString);
                                    index++;
                                }
                            } else {
                                // Empty result
                                printReport("No hay registros de sintomas en el intervalo de tiempo establecido");
                            }
                        }
                    }
                });
    }

    private String templateReport(Symptom symptom, int index) {
        String symptomDateTime = new OccurrenceDateTimeHandler(symptom.getOccurrenceTimestamp(), rootView.getContext()).getFullOccurrenceDateTime();
        String msg = "<h3>Symptom "+ index +":</h3> ";
        msg += "<p><b>"+ symptomDateTime +"</b></p>";
        msg += "<p><b>Intensidad:</b> "+ symptom.getIntensity() +"</p>";
        msg += "<p>"+ symptom.getDescription() +"</p><hr>";
        return msg;
    }

    private void printReport(String reportText) {
        tvReportDisplay.setText(Html.fromHtml(reportText));
    }

    private void enableShareButton(boolean isBtnRequired) {
        btnShareReport.setEnabled(isBtnRequired);
        if (isBtnRequired) {
            btnShareReport.setBackgroundColor(rootView.getResources().getColor(R.color.colorPrimary));
        } else {
            btnShareReport.setBackgroundColor(rootView.getResources().getColor(R.color.colorPrimaryDisable));
        }
    }

    private void shareReport() {
        String reportText = tvReportDisplay.getText().toString();
        Intent mSharingIntent = new Intent(Intent.ACTION_SEND);
        mSharingIntent.setType("text/plain");
        mSharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Reporte de sintomas");
        mSharingIntent.putExtra(Intent.EXTRA_TEXT, reportText);
        startActivity(Intent.createChooser(mSharingIntent, "Selecciona el medio para compartir tu reporte de sintomas"));
    }
}
