package com.sop.cacapp.Adapters;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Color;
import android.util.SparseBooleanArray;
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
import java.util.List;
import java.util.Locale;

public class SymptomOccurrenceAdapter extends RecyclerView.Adapter<SymptomOccurrenceAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private ArrayList<Symptom> symptomsList;
    private View.OnClickListener listener;
    private Context context;

    private SparseBooleanArray selectedSymptoms;
    private int currentSelectedIdx = -1;

    private OnClickListener onClickListener = null;

    public SymptomOccurrenceAdapter(Context context, ArrayList<Symptom> symptomsList) {
        this.inflater = LayoutInflater.from(context);
        this.symptomsList = symptomsList;
        this.selectedSymptoms = new SparseBooleanArray();
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the element view of the recycler view
        View view = inflater.inflate(R.layout.symptom_recycler_element, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        String patternOne = "dd MMMM yyyy";
        String patternTwo = "hh:mm aa";
        Locale current = context.getResources().getConfiguration().locale;
        final Symptom currentSymptom = symptomsList.get(position);
        holder.tvSymptomDate.setText(new SimpleDateFormat(patternOne, current).format(currentSymptom.toDate()));
        holder.tvSymptomTime.setText(new SimpleDateFormat(patternTwo, current).format(currentSymptom.toDate()));
        holder.rbSymptomIntensity.setRating(currentSymptom.getIntensity());
        holder.tvSymptomDescription.setText(currentSymptom.getDescription().length() > 50 ?
                String.format(currentSymptom.getDescription().substring(0, 49) + "%s", "...") :
                currentSymptom.getDescription());

        // When pics gathered call the pic chooser method here

        holder.element.setActivated(selectedSymptoms.get(position, false));

        holder.element.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener != null) {
                    onClickListener.onItemClick(v, position, currentSymptom);
                }
            }
        });

        holder.element.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onClickListener != null) {
                    onClickListener.onItemLongClick(v, position, currentSymptom);
                }
                return true;
            }
        });

        toggleChecked(holder, position);

    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return symptomsList.size();
    }

    public int getSelectedSymptomsCount() {
        return selectedSymptoms.size();
    }

    private void toggleChecked(ViewHolder holder, int position) {
        if (selectedSymptoms.get(position, false)) {
            holder.element.setCardBackgroundColor(Color.LTGRAY);
        } else {
            holder.element.setCardBackgroundColor(Color.WHITE);
        }
    }

    public void toggleSelection(int pos) {
        currentSelectedIdx = pos;
        if (selectedSymptoms.get(pos, false)) {
            selectedSymptoms.delete(pos);
        } else {
            selectedSymptoms.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    public void clearSelectedSymptoms() {
        selectedSymptoms.clear();
        notifyDataSetChanged();
    }

    public List<Symptom> getSelectedSymptoms() {
        List<Symptom> symptoms = new ArrayList<>(selectedSymptoms.size());
        for (int i = 0; i < selectedSymptoms.size(); i++) {
            if (selectedSymptoms.valueAt(i)) {
                symptoms.add(symptomsList.get(i));
            }
        }
        return symptoms;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        // Set view references
        ImageView ivSymptomPicture;
        TextView tvSymptomDate;
        TextView tvSymptomTime;
        RatingBar rbSymptomIntensity;
        TextView tvSymptomDescription;
        CardView element;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            element = itemView.findViewById(R.id.symptomCardView);
            ivSymptomPicture = itemView.findViewById(R.id.ivSymptomPicture);
            tvSymptomDate = itemView.findViewById(R.id.tvSymptomDate);
            tvSymptomTime = itemView.findViewById(R.id.tvSymptomTime);
            rbSymptomIntensity = itemView.findViewById(R.id.rbSymptomIntensity);
            tvSymptomDescription = itemView.findViewById(R.id.tvSymptomDescription);
        }

        //Then could be change the symptom picture according to intensity, very similar as in defecation part
    }

    public interface OnClickListener {
        void onItemClick(View view, int position, Symptom symptom);
        void onItemLongClick(View view, int position, Symptom symptom);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

}
