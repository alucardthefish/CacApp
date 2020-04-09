package com.sop.cacapp.Adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.sop.cacapp.Classes.Symptom;
import com.sop.cacapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class SymptomOccurrenceAdapter extends RecyclerView.Adapter<SymptomOccurrenceAdapter.ViewHolder> implements View.OnClickListener {

    private LayoutInflater inflater;
    private ArrayList<Symptom> symptomsList;
    private View.OnClickListener listener;
    private Context context;

    public SymptomOccurrenceAdapter(Context context, ArrayList<Symptom> symptomsList) {
        this.inflater = LayoutInflater.from(context);
        this.symptomsList = symptomsList;
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        if (listener != null) {
            listener.onClick(v);
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the element view of the recycler view
        View view = inflater.inflate(R.layout.symptom_recycler_element, parent, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String patternOne = "dd MMMM yyyy";
        String patternTwo = "hh:mm aa";
        Locale current = context.getResources().getConfiguration().locale;
        Symptom currentSymptom = symptomsList.get(position);
        holder.tvSymptomDate.setText(new SimpleDateFormat(patternOne, current).format(currentSymptom.toDate()));
        holder.tvSymptomTime.setText(new SimpleDateFormat(patternTwo, current).format(currentSymptom.toDate()));
        holder.rbSymptomIntensity.setRating(currentSymptom.getIntensity());
        holder.tvSymptomDescription.setText(currentSymptom.getDescription().length() > 50 ?
                String.format(currentSymptom.getDescription().substring(0, 49) + "%s", "...") :
                currentSymptom.getDescription());
        // When pics gathered call the pic chooser method here
    }

    @Override
    public int getItemCount() {
        return symptomsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        // Set view references
        ImageView ivSymptomPicture;
        TextView tvSymptomDate;
        TextView tvSymptomTime;
        RatingBar rbSymptomIntensity;
        TextView tvSymptomDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivSymptomPicture = itemView.findViewById(R.id.ivSymptomPicture);
            tvSymptomDate = itemView.findViewById(R.id.tvSymptomDate);
            tvSymptomTime = itemView.findViewById(R.id.tvSymptomTime);
            rbSymptomIntensity = itemView.findViewById(R.id.rbSymptomIntensity);
            tvSymptomDescription = itemView.findViewById(R.id.tvSymptomDescription);
        }

        //Then could be change the symptom picture according to intensity, very similar as in defecation part
    }

}
