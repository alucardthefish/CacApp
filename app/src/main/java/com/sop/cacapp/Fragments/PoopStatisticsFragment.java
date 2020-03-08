package com.sop.cacapp.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.sop.cacapp.R;

import java.util.ArrayList;
import java.util.List;

public class PoopStatisticsFragment extends Fragment {

    private LineChart lineChart;

    public PoopStatisticsFragment() {
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
        View view = inflater.inflate(R.layout.fragment_poop_statistics, container, false);
        lineChart = view.findViewById(R.id.lineChart);
        loadDataChart();
        return view;
    }

    private void loadDataChart() {
        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(3, 6));
        entries.add(new Entry(5, 12));
        entries.add(new Entry(7, 24));
        entries.add(new Entry(9, 7));
        entries.add(new Entry(11, 14));
        entries.add(new Entry(13, 28));
        entries.add(new Entry(15, 34));
        entries.add(new Entry(17, 26));
        entries.add(new Entry(19, 75));
        entries.add(new Entry(21, 54));
        entries.add(new Entry(23, 32));
        entries.add(new Entry(25, 86));
        entries.add(new Entry(27, 120));
        LineDataSet dataSet = new LineDataSet(entries, "Label");    // Add entries to dataset
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        //lineChart.invalidate();
        Log.d("Chart", "LineChart loaded");
    }
}
