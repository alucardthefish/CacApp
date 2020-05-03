package com.sop.cacapp.Fragments;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.sop.cacapp.Persistence.PoopOccurrencePersistent;
import com.sop.cacapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PoopStatisticsFragment extends Fragment {

    private LineChart lineChart;
    private PieChart pieChart;
    private TextView sticky;
    private LinearLayout layout;

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
        pieChart = view.findViewById(R.id.pieChart);
        sticky = view.findViewById(R.id.sticky_label);
        layout = view.findViewById(R.id.poopStatisticsLayout);
        loadDataChart();
        //createGraph();
        loadDataPieChart();
        return view;
    }

    private void createGraph() {
        List<Entry> yDataL = new ArrayList<>();
        for (int i = 0; i < 400; ++i) {
            yDataL.add(new Entry(2f*i, (float)Math.sin(0.1f*i)));
        }
        LineDataSet lds = new LineDataSet(yDataL, "Data");
        lds.setCircleColor(Color.BLACK);
        lds.setCircleRadius(6f);
        lds.setCircleHoleRadius(3f);
        lds.setCircleHoleColor(Color.WHITE);
        lds.setColor(Color.BLACK);
        lds.setLineWidth(4f);

        LineData ld = new LineData(lds);
        ld.setDrawValues(false);
        lineChart.setData(ld);

        final float textSize = 20f;
        final XAxis xa = lineChart.getXAxis();
        xa.setGranularity(1f);
        xa.setGranularityEnabled(true);
        //Here
        xa.setValueFormatter(new StickyDateAxisValueFormatter(lineChart, sticky));
        xa.setPosition(XAxis.XAxisPosition.BOTTOM);
        xa.setTextSize(textSize);
        xa.setDrawGridLines(true);

        lineChart.setPinchZoom(false);
        lineChart.zoom(28f,1f,0f,0f);
        sticky.setTextSize(textSize);

        ViewTreeObserver vto = layout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                float xo = xa.getXOffset();
                float yo = xa.getYOffset();
                final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                float rho = displayMetrics.density;

                float ty = lineChart.getY() + lineChart.getMeasuredHeight() - rho*yo - 1.5f*textSize - 10f;
                float tx = lineChart.getX() + rho*xo;

                sticky.setTranslationY(ty);
                sticky.setTranslationX(tx);
            }
        });

        sticky.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.TOP);

        lineChart.getAxisLeft().setTextSize(textSize);
        lineChart.setExtraBottomOffset(10f);

        lineChart.getAxisRight().setEnabled(false);
        Description desc = new Description();
        desc.setText("");
        lineChart.setDescription(desc);
        lineChart.getLegend().setEnabled(false);
        lineChart.getAxisLeft().setDrawGridLines(true);
    }

    private void createGraph2() {
        float[] yDataL = {30, 60, 500,231};
        //float[] yDataL = {30, 60};
        String[] xDataL = {"Jan", "Feb" , "Mac" , "Apr"};

        ArrayList<Entry> yEntrys = new ArrayList<>();
        final ArrayList<String> xEntrys = new ArrayList<>();

        for(int i = 0; i < yDataL.length; i++){
            yEntrys.add(new Entry(i, yDataL[i]));
        }


        for(int i = 1; i < xDataL.length; i++){
            xEntrys.add(xDataL[i]);
        }

        LineDataSet dataSet = new LineDataSet(yEntrys, "");
        dataSet.setColor(Color.parseColor("#7500ca"));
        dataSet.setCircleColor(Color.parseColor("#7500ca"));
        dataSet.setLineWidth(1f);
        dataSet.setCircleRadius(5f);
        dataSet.setDrawCircleHole(false);
        dataSet.setDrawValues(false);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setValueFormatter(new MonthValueFormatter());

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);

        LineData pieData = new LineData(dataSet);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisRight().setDrawGridLines(false);
        lineChart.getAxisLeft().setDrawAxisLine(false);
        lineChart.getAxisRight().setDrawAxisLine(false);
        lineChart.getAxisLeft().setDrawLabels(false);
        lineChart.getAxisRight().setDrawLabels(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.setDescription(null);
        lineChart.setData(pieData);
        lineChart.invalidate();
    }

    private void loadDataChart() {
        PoopOccurrencePersistent poopPersistence = new PoopOccurrencePersistent();
        poopPersistence.getMyPoopData(new PoopOccurrencePersistent.PoopDataCallback() {
            @Override
            public void onCallback(ArrayList allData) {
                Dictionary dic = (Dictionary) allData.get(0);
                ArrayList arrKeys = (ArrayList) allData.get(1);
                if (!dic.isEmpty()) {
                    lineChart.refreshDrawableState();
                    lineChart.getXAxis().setDrawGridLines(false);

                    lineChart.getXAxis().setValueFormatter(new DateAxisValueFormatter(lineChart, sticky));
                    List<Entry> entries = new ArrayList<>();

                    for (int i = 0; i < arrKeys.size(); i++) {
                        int counter = (int) dic.get(arrKeys.get(i));
                        entries.add(new Entry(i, counter));
                    }

                    LineDataSet dataSet = new LineDataSet(entries, "Frequencia");    // Add entries to dataset
                    dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                    dataSet.setDrawFilled(true);
                    dataSet.setDrawValues(false);
                    LineData lineData = new LineData(dataSet);
                    lineChart.getXAxis().setDrawGridLines(false);
                    lineChart.setData(lineData);

                    lineChart.invalidate();
                    lineChart.getDescription().setEnabled(false);
                    Log.d("Chart", "LineChart loaded");
                }
            }
        });
    }

    private void loadDataChart2() {
        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(2023, 1));
        /*entries.add(new Entry(3, 6));
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
        entries.add(new Entry(27, 120));*/
        /*PoopOccurrencePersistent poopPersistence = new PoopOccurrencePersistent();
        poopPersistence.getMyPoopData(new PoopOccurrencePersistent.PoopDataCallback() {
            @Override
            public void onCallback(Dictionary dic) {
                if (dic.isEmpty()) {
                    Log.d("LineChart", "Data for line chart is empty");
                } else {
                    List<Entry> entries = new ArrayList<>();
                    for (Enumeration k = dic.keys(); k.hasMoreElements();) {
                        //long key = (long) k.nextElement();
                        //float value = (float) key;
                        int key = (int) k.nextElement();
                        int counter = (int) dic.get(key);
                        //Log.d("LineChart", "key: " + key + " - data: " + counter + " - value: " + value);
                        Log.d("LineChart", "key: " + key + " - data: " + counter);
                        //entries.add(new Entry(value, counter));
                        entries.add(new Entry(key, counter));
                    }
                }
            }
        });*/
        lineChart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {

                return "hola";
            }
        });
        LineDataSet dataSet = new LineDataSet(entries, "Label");    // Add entries to dataset
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setDrawFilled(true);
        dataSet.setDrawValues(false);
        LineData lineData = new LineData(dataSet);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.setData(lineData);

        //lineChart.invalidate();
        Log.d("Chart", "LineChart loaded");
    }

    private void loadDataPieChart() {
        final String[] nameLabels = {"None", "Malo", "Regular", "Normal", "Bueno", "Excelente"};
        PoopOccurrencePersistent poopPersistence = new PoopOccurrencePersistent();
        poopPersistence.getMyPoopSatisfactionData(new PoopOccurrencePersistent.PoopSatisfactionDataCallback() {
            @Override
            public void onCallback(Map<Double, Integer> satisfactionData) {
                ArrayList<PieEntry> pieEntries = new ArrayList<>();
                for (Map.Entry<Double, Integer> entry : satisfactionData.entrySet()) {
                    int value = entry.getValue();
                    if (value > 0) {
                        pieEntries.add(new PieEntry(value, nameLabels[entry.getKey().intValue()]));
                    }
                }
                //Description description = new Description();
                //description.setText("Gráfico de pastel fácil");
                //pieChart.setDescription(description);
                pieChart.getDescription().setEnabled(false);

                PieDataSet dataSet = new PieDataSet(pieEntries, "");
                dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                PieData pieData = new PieData(dataSet);

                pieChart.setData(pieData);
                pieChart.invalidate();
            }
        });
    }

    private void loadDataPieChart2() {
        Description description = new Description();
        description.setText("Gráfico de pastel fácil");
        pieChart.setDescription(description);
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry(31, "Feliz"));
        pieEntries.add(new PieEntry(14, "Neutral"));
        pieEntries.add(new PieEntry(5, "Mal"));

        PieDataSet dataSet = new PieDataSet(pieEntries, "texto descripcion");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData pieData = new PieData(dataSet);

        pieChart.setData(pieData);

    }

    public class MonthValueFormatter extends ValueFormatter {
        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            String format;
            if (value == 0.0f) {
                format = "Enero";
            } else if (value == 1.0f) {
                format = "Febrero";
            } else if (value == 2.0f) {
                format = "Marzo";
            } else if (value == 3.0f) {
                format = "Abril";
            } else if (value == 4.0f) {
                format = "Mayo";
            } else if (value == 5.0f) {
                format = "Junio";
            } else if (value == 6.0f) {
                format = "Julio";
            } else if (value == 7.0f) {
                format = "Agosto";
            } else if (value == 8.0f) {
                format = "Septiembre";
            } else if (value == 9.0f) {
                format = "Octubre";
            } else if (value == 10.0f) {
                format = "Noviembre";
            } else if (value == 11.0f) {
                format = "Diciembre";
            } else {
                throw new IllegalArgumentException(""+value+" is not a valid month");
            }
            return format;
        }
    }

    public class StickyDateAxisValueFormatter extends ValueFormatter {
        private Calendar c;
        private LineChart chart;
        private TextView sticky;
        private float lastFormattedValue = 1e9f;
        private int lastMonth = 0;
        private int lastYear = 0;
        private int stickyMonth = -1;
        private int stickyYear = -1;
        private SimpleDateFormat monthFormatter = new SimpleDateFormat("MMM", Locale.getDefault());

        StickyDateAxisValueFormatter(LineChart chart, TextView sticky) {
            c = new GregorianCalendar();
            this.chart = chart;
            this.sticky = sticky;
        }

        @Override
        public String getFormattedValue(float value) {
            if (value < chart.getLowestVisibleX()) {
                return "";
            }

            int days = (int) value;
            boolean isFirstValue = value < lastFormattedValue;

            if (isFirstValue) {
                lastMonth = 50;
                lastYear = 5000;

                c.set(2018, 0, 1);
                c.add(Calendar.DATE, (int) chart.getLowestVisibleX());

                stickyMonth = c.get(Calendar.MONTH);
                stickyYear = c.get(Calendar.YEAR);

                String stickyText = monthFormatter.format(c.getTime()) + "\n" + stickyYear;
                sticky.setText(stickyText);
            }

            c.set(2018, 0, 1);
            c.add(Calendar.DATE, days);
            Date d = c.getTime();

            int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
            int month = c.get(Calendar.MONTH);
            int year = c.get(Calendar.YEAR);

            String monthStr = monthFormatter.format(d);

            if( (month > stickyMonth || year > stickyYear) && isFirstValue ) {
                stickyMonth = month;
                stickyYear = year;
                String stickyText = monthStr + "\n" + year;
                sticky.setText(stickyText);
            }

            String ret;

            if( (month > lastMonth || year > lastYear) && !isFirstValue ) {
                ret = monthStr;
            }
            else {
                ret = Integer.toString(dayOfMonth);
            }

            lastMonth = month;
            lastYear = year;
            lastFormattedValue = value;

            return ret;
        }
    }

    public class DateAxisValueFormatter extends ValueFormatter {
        private Calendar c;
        private LineChart chart;
        private TextView sticky;
        private float lastFormattedValue = 1e9f;
        private int lastMonth = 0;
        private int lastYear = 0;
        private int stickyMonth = -1;
        private int stickyYear = -1;
        private SimpleDateFormat monthFormatter = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());

        private Date initialDate;

        DateAxisValueFormatter(LineChart chart, TextView sticky) {
            c = new GregorianCalendar();
            this.chart = chart;
            this.sticky = sticky;
        }

        @Override
        public String getFormattedValue(float value) {
            if (value < chart.getLowestVisibleX()) {
                return "";
            }

            int months = (int) value;
            boolean isFirstValue = value < lastFormattedValue;

            if (isFirstValue) {
                lastMonth = 50;
                lastYear = 5000;

                c.set(2020, 2, 1);
                c.add(Calendar.MONTH, (int) chart.getLowestVisibleX());

                stickyMonth = c.get(Calendar.MONTH);
                stickyYear = c.get(Calendar.YEAR);

                String stickyText = monthFormatter.format(c.getTime());// + "\n" + stickyYear;
                sticky.setText(stickyText);
            }

            c.set(2020, 2, 1);
            c.add(Calendar.MONTH, months);
            Date d = c.getTime();

            int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
            int month = c.get(Calendar.MONTH);
            int year = c.get(Calendar.YEAR);

            String monthStr = monthFormatter.format(d);

            if( (month > stickyMonth || year > stickyYear) && isFirstValue ) {
                stickyMonth = month;
                stickyYear = year;
                String stickyText = monthStr;// + "\n" + year;
                sticky.setText(stickyText);
            }

            String ret = "";

            if( (month > lastMonth || year > lastYear) && !isFirstValue ) {
                ret = monthStr;
            }
            else {
                //ret = Integer.toString(dayOfMonth);
            }

            lastMonth = month;
            lastYear = year;
            lastFormattedValue = value;

            return ret;
        }
    }
}
