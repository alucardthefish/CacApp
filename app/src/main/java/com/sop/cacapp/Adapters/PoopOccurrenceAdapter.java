package com.sop.cacapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.sop.cacapp.Classes.PoopOccurrence;
import com.sop.cacapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class PoopOccurrenceAdapter extends RecyclerView.Adapter<PoopOccurrenceAdapter.ViewHolder> implements View.OnClickListener {

    private LayoutInflater inflater;
    private ArrayList<PoopOccurrence> model;
    private Context mContext;

    // Listener
    private View.OnClickListener listener;

    public PoopOccurrenceAdapter(Context context, ArrayList<PoopOccurrence> model) {
        this.inflater = LayoutInflater.from(context);
        this.model = model;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.deposition_list, parent, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Timestamp poopOccurrenceDateTime = model.get(position).getOccurrenceTimestamp();
        float poopOccurrenceSatisfaction = model.get(position).getSatisfaction();
        Date date = poopOccurrenceDateTime.toDate();
        String patternOne = "dd MMMM yyyy";
        String patternTwo = "hh:mm aa";
        Locale current = mContext.getResources().getConfiguration().locale;
        holder.setSatisfactionIcon(poopOccurrenceSatisfaction);
        holder.tvDate.setText(new SimpleDateFormat(patternOne, current).format(date));
        holder.tvTime.setText(new SimpleDateFormat(patternTwo, current).format(date));
        holder.rbStars.setRating(poopOccurrenceSatisfaction);
    }


    @Override
    public int getItemCount() {
        return model.size();
    }

    @Override
    public void onClick(View v) {
        if (listener != null) {
            listener.onClick(v);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        // Set view references
        ImageView ivDepositionIcon;
        TextView tvDate;
        TextView tvTime;
        RatingBar rbStars;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivDepositionIcon = itemView.findViewById(R.id.ivDepositionIcon);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
            rbStars = itemView.findViewById(R.id.rbStars);
        }

        public void setSatisfactionIcon(float rating) {
            if (rating >= 0 && rating <= 1) {
                ivDepositionIcon.setImageResource(R.drawable.shit1);
            } else if (rating > 1 && rating <= 2) {
                ivDepositionIcon.setImageResource(R.drawable.shit2);
            } else if (rating > 3 && rating <= 4) {
                ivDepositionIcon.setImageResource(R.drawable.shit4);
            } else if (rating > 4 && rating <= 5) {
                ivDepositionIcon.setImageResource(R.drawable.shit5);
            }
        }
    }
}
