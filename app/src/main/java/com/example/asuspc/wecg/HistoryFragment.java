package com.example.asuspc.wecg;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.File;
import java.util.Objects;

import static android.support.constraint.Constraints.TAG;

public class HistoryFragment extends Fragment implements OnChartValueSelectedListener {

    public ArrayAdapter<String> mHistoryAdapter;
    private ListView mHistoryListView;
    private LineChart chart1;
    private LineChart chart2;
    private LineChart chart3;
    private LineChart chart4;
    private LineChart chart5;
    private LineChart chart6;
    private LineChart chart7;
    private LineChart chart8;
    private LineChart chart9;
    private LineChart chart10;
    private LineChart chart11;
    private LineChart chart12;

    //fragment's view
    View view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_history, container, false);
        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        //set title for toolbar
        getActivity().setTitle(R.string.action_history);

        //getting permission to external storage
        final boolean readPermissionGranted;
        if (isReadStoragePermissionGranted()) {
            readPermissionGranted = true;
        } else {
            readPermissionGranted = false;
        }

        //getting the view
        chart1 = view.findViewById(R.id.chart1);
        chart2 = view.findViewById(R.id.chart2);
        chart3 = view.findViewById(R.id.chart3);
        chart4 = view.findViewById(R.id.chart4);
        chart5 = view.findViewById(R.id.chart5);
        chart6 = view.findViewById(R.id.chart6);
        chart7 = view.findViewById(R.id.chart7);
        chart8 = view.findViewById(R.id.chart8);
        chart9 = view.findViewById(R.id.chart9);
        chart10 = view.findViewById(R.id.chart10);
        chart11 = view.findViewById(R.id.chart11);
        chart12 = view.findViewById(R.id.chart12);

        //setting up the MPAndroid chart
        chart1.setOnChartValueSelectedListener(this);
        chart2.setOnChartValueSelectedListener(this);
        chart3.setOnChartValueSelectedListener(this);
        chart4.setOnChartValueSelectedListener(this);
        chart5.setOnChartValueSelectedListener(this);
        chart6.setOnChartValueSelectedListener(this);
        chart7.setOnChartValueSelectedListener(this);
        chart8.setOnChartValueSelectedListener(this);
        chart9.setOnChartValueSelectedListener(this);
        chart10.setOnChartValueSelectedListener(this);
        chart11.setOnChartValueSelectedListener(this);
        chart12.setOnChartValueSelectedListener(this);

        // enable touch gestures
        chart1.setTouchEnabled(true);
        chart2.setTouchEnabled(true);
        chart3.setTouchEnabled(true);
        chart4.setTouchEnabled(true);
        chart5.setTouchEnabled(true);
        chart6.setTouchEnabled(true);
        chart7.setTouchEnabled(true);
        chart8.setTouchEnabled(true);
        chart9.setTouchEnabled(true);
        chart10.setTouchEnabled(true);
        chart11.setTouchEnabled(true);
        chart12.setTouchEnabled(true);

        // enable scaling and dragging
        chart1.setDragEnabled(true);
        chart1.setScaleEnabled(true);
        chart1.setDrawGridBackground(false);
        // enable scaling and dragging
        chart2.setDragEnabled(true);
        chart2.setScaleEnabled(true);
        chart2.setDrawGridBackground(false);
        // enable scaling and dragging
        chart3.setDragEnabled(true);
        chart3.setScaleEnabled(true);
        chart3.setDrawGridBackground(false);
        // enable scaling and dragging
        chart4.setDragEnabled(true);
        chart4.setScaleEnabled(true);
        chart4.setDrawGridBackground(false);
        // enable scaling and dragging
        chart5.setDragEnabled(true);
        chart5.setScaleEnabled(true);
        chart5.setDrawGridBackground(false);
        // enable scaling and dragging
        chart6.setDragEnabled(true);
        chart6.setScaleEnabled(true);
        chart6.setDrawGridBackground(false);
        // enable scaling and dragging
        chart7.setDragEnabled(true);
        chart7.setScaleEnabled(true);
        chart7.setDrawGridBackground(false);
        // enable scaling and dragging
        chart8.setDragEnabled(true);
        chart8.setScaleEnabled(true);
        chart8.setDrawGridBackground(false);
        // enable scaling and dragging
        chart9.setDragEnabled(true);
        chart9.setScaleEnabled(true);
        chart9.setDrawGridBackground(false);
        // enable scaling and dragging
        chart10.setDragEnabled(true);
        chart10.setScaleEnabled(true);
        chart10.setDrawGridBackground(false);
        // enable scaling and dragging
        chart11.setDragEnabled(true);
        chart11.setScaleEnabled(true);
        chart11.setDrawGridBackground(false);
        // enable scaling and dragging
        chart12.setDragEnabled(true);
        chart12.setScaleEnabled(true);
        chart12.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        chart1.setPinchZoom(true);
        chart2.setPinchZoom(true);
        chart3.setPinchZoom(true);
        chart4.setPinchZoom(true);
        chart5.setPinchZoom(true);
        chart6.setPinchZoom(true);
        chart7.setPinchZoom(true);
        chart8.setPinchZoom(true);
        chart9.setPinchZoom(true);
        chart10.setPinchZoom(true);
        chart11.setPinchZoom(true);
        chart12.setPinchZoom(true);

        // set an alternative background color
        chart1.setBackgroundColor(Color.WHITE);
        chart2.setBackgroundColor(Color.WHITE);
        chart3.setBackgroundColor(Color.WHITE);
        chart4.setBackgroundColor(Color.WHITE);
        chart5.setBackgroundColor(Color.WHITE);
        chart6.setBackgroundColor(Color.WHITE);
        chart7.setBackgroundColor(Color.WHITE);
        chart8.setBackgroundColor(Color.WHITE);
        chart9.setBackgroundColor(Color.WHITE);
        chart10.setBackgroundColor(Color.WHITE);
        chart11.setBackgroundColor(Color.WHITE);
        chart12.setBackgroundColor(Color.WHITE);

        final LineData dataI = new LineData();
        final LineData dataII = new LineData();
        final LineData dataIII = new LineData();
        final LineData dataaVR = new LineData();
        final LineData dataaVL = new LineData();
        final LineData dataaVF = new LineData();
        final LineData dataV1 = new LineData();
        final LineData dataV2 = new LineData();
        final LineData dataV3 = new LineData();
        final LineData dataV4 = new LineData();
        final LineData dataV5 = new LineData();
        final LineData dataV6 = new LineData();

        dataI.setValueTextColor(Color.BLACK);
        dataII.setValueTextColor(Color.BLACK);
        dataIII.setValueTextColor(Color.BLACK);
        dataaVR.setValueTextColor(Color.BLACK);
        dataaVL.setValueTextColor(Color.BLACK);
        dataaVF.setValueTextColor(Color.BLACK);
        dataV1.setValueTextColor(Color.BLACK);
        dataV2.setValueTextColor(Color.BLACK);
        dataV3.setValueTextColor(Color.BLACK);
        dataV4.setValueTextColor(Color.BLACK);
        dataV5.setValueTextColor(Color.BLACK);
        dataV6.setValueTextColor(Color.BLACK);

        // add empty data
        chart1.setData(dataI);
        chart2.setData(dataII);
        chart3.setData(dataIII);
        chart4.setData(dataaVR);
        chart5.setData(dataaVL);
        chart6.setData(dataaVF);
        chart7.setData(dataV1);
        chart8.setData(dataV2);
        chart9.setData(dataV3);
        chart10.setData(dataV4);
        chart11.setData(dataV5);
        chart12.setData(dataV6);

        // get the legend (only possible after setting data)
        Legend l1 = chart1.getLegend();
        Legend l2 = chart2.getLegend();
        Legend l3 = chart3.getLegend();
        Legend l4 = chart4.getLegend();
        Legend l5 = chart5.getLegend();
        Legend l6 = chart6.getLegend();
        Legend l7 = chart7.getLegend();
        Legend l8 = chart8.getLegend();
        Legend l9 = chart9.getLegend();
        Legend l10 = chart10.getLegend();
        Legend l11 = chart11.getLegend();
        Legend l12 = chart12.getLegend();

        // modify the legend ...
        l1.setForm(Legend.LegendForm.LINE);
        l1.setTextColor(Color.BLACK);
        // modify the legend ...
        l2.setForm(Legend.LegendForm.LINE);
        l2.setTextColor(Color.BLACK);
        // modify the legend ...
        l3.setForm(Legend.LegendForm.LINE);
        l3.setTextColor(Color.BLACK);
        // modify the legend ...
        l4.setForm(Legend.LegendForm.LINE);
        l4.setTextColor(Color.BLACK);
        // modify the legend ...
        l5.setForm(Legend.LegendForm.LINE);
        l5.setTextColor(Color.BLACK);
        // modify the legend ...
        l6.setForm(Legend.LegendForm.LINE);
        l6.setTextColor(Color.BLACK);
        // modify the legend ...
        l7.setForm(Legend.LegendForm.LINE);
        l7.setTextColor(Color.BLACK);
        // modify the legend ...
        l8.setForm(Legend.LegendForm.LINE);
        l8.setTextColor(Color.BLACK);
        // modify the legend ...
        l9.setForm(Legend.LegendForm.LINE);
        l9.setTextColor(Color.BLACK);
        // modify the legend ...
        l10.setForm(Legend.LegendForm.LINE);
        l10.setTextColor(Color.BLACK);
        // modify the legend ...
        l11.setForm(Legend.LegendForm.LINE);
        l11.setTextColor(Color.BLACK);
        // modify the legend ...
        l12.setForm(Legend.LegendForm.LINE);
        l12.setTextColor(Color.BLACK);

        XAxis xl1 = chart1.getXAxis();
        xl1.setTextColor(Color.BLACK);
        xl1.setDrawGridLines(true);
        xl1.setAvoidFirstLastClipping(true);
        xl1.setEnabled(true);

        XAxis xl2 = chart2.getXAxis();
        xl2.setTextColor(Color.BLACK);
        xl2.setDrawGridLines(true);
        xl2.setAvoidFirstLastClipping(true);
        xl2.setEnabled(true);

        XAxis xl3 = chart3.getXAxis();
        xl3.setTextColor(Color.BLACK);
        xl3.setDrawGridLines(true);
        xl3.setAvoidFirstLastClipping(true);
        xl3.setEnabled(true);

        XAxis xl4 = chart4.getXAxis();
        xl4.setTextColor(Color.BLACK);
        xl4.setDrawGridLines(true);
        xl4.setAvoidFirstLastClipping(true);
        xl4.setEnabled(true);

        XAxis xl5 = chart5.getXAxis();
        xl5.setTextColor(Color.BLACK);
        xl5.setDrawGridLines(true);
        xl5.setAvoidFirstLastClipping(true);
        xl5.setEnabled(true);

        XAxis xl6 = chart6.getXAxis();
        xl6.setTextColor(Color.BLACK);
        xl6.setDrawGridLines(true);
        xl6.setAvoidFirstLastClipping(true);
        xl6.setEnabled(true);

        XAxis xl7 = chart7.getXAxis();
        xl7.setTextColor(Color.BLACK);
        xl7.setDrawGridLines(true);
        xl7.setAvoidFirstLastClipping(true);
        xl7.setEnabled(true);

        XAxis xl8 = chart8.getXAxis();
        xl8.setTextColor(Color.BLACK);
        xl8.setDrawGridLines(true);
        xl8.setAvoidFirstLastClipping(true);
        xl8.setEnabled(true);

        XAxis xl9 = chart9.getXAxis();
        xl9.setTextColor(Color.BLACK);
        xl9.setDrawGridLines(true);
        xl9.setAvoidFirstLastClipping(true);
        xl9.setEnabled(true);

        XAxis xl10 = chart10.getXAxis();
        xl10.setTextColor(Color.BLACK);
        xl10.setDrawGridLines(true);
        xl10.setAvoidFirstLastClipping(true);
        xl10.setEnabled(true);

        XAxis xl11 = chart11.getXAxis();
        xl11.setTextColor(Color.BLACK);
        xl11.setDrawGridLines(true);
        xl11.setAvoidFirstLastClipping(true);
        xl11.setEnabled(true);

        XAxis xl12 = chart12.getXAxis();
        xl12.setTextColor(Color.BLACK);
        xl12.setDrawGridLines(true);
        xl12.setAvoidFirstLastClipping(true);
        xl12.setEnabled(true);

        //set y axis
        YAxis leftAxis1 = chart1.getAxisLeft();
        leftAxis1.setTextColor(Color.BLACK);
        leftAxis1.setDrawGridLines(true);
        YAxis rightAxis1 = chart1.getAxisRight();
        rightAxis1.setEnabled(false);

        YAxis leftAxis2 = chart2.getAxisLeft();
        leftAxis2.setTextColor(Color.BLACK);
        leftAxis2.setDrawGridLines(true);
        YAxis rightAxis2 = chart2.getAxisRight();
        rightAxis2.setEnabled(false);

        YAxis leftAxis3 = chart3.getAxisLeft();
        leftAxis3.setTextColor(Color.BLACK);
        leftAxis3.setDrawGridLines(true);
        YAxis rightAxis3 = chart3.getAxisRight();
        rightAxis3.setEnabled(false);

        YAxis leftAxis4 = chart4.getAxisLeft();
        leftAxis4.setTextColor(Color.BLACK);
        leftAxis4.setDrawGridLines(true);
        YAxis rightAxis4 = chart4.getAxisRight();
        rightAxis4.setEnabled(false);

        YAxis leftAxis5 = chart5.getAxisLeft();
        leftAxis5.setTextColor(Color.BLACK);
        leftAxis5.setDrawGridLines(true);
        YAxis rightAxis5 = chart5.getAxisRight();
        rightAxis5.setEnabled(false);

        YAxis leftAxis6 = chart6.getAxisLeft();
        leftAxis6.setTextColor(Color.BLACK);
        leftAxis6.setDrawGridLines(true);
        YAxis rightAxis6 = chart6.getAxisRight();
        rightAxis6.setEnabled(false);

        YAxis leftAxis7 = chart7.getAxisLeft();
        leftAxis7.setTextColor(Color.BLACK);
        leftAxis7.setDrawGridLines(true);
        YAxis rightAxis7 = chart7.getAxisRight();
        rightAxis7.setEnabled(false);

        YAxis leftAxis8 = chart8.getAxisLeft();
        leftAxis8.setTextColor(Color.BLACK);
        leftAxis8.setDrawGridLines(true);
        YAxis rightAxis8 = chart8.getAxisRight();
        rightAxis8.setEnabled(false);

        YAxis leftAxis9 = chart9.getAxisLeft();
        leftAxis9.setTextColor(Color.BLACK);
        leftAxis9.setDrawGridLines(true);
        YAxis rightAxis9 = chart9.getAxisRight();
        rightAxis9.setEnabled(false);

        YAxis leftAxis10 = chart10.getAxisLeft();
        leftAxis10.setTextColor(Color.BLACK);
        leftAxis10.setDrawGridLines(true);
        YAxis rightAxis10 = chart10.getAxisRight();
        rightAxis10.setEnabled(false);

        YAxis leftAxis11 = chart11.getAxisLeft();
        leftAxis11.setTextColor(Color.BLACK);
        leftAxis11.setDrawGridLines(true);
        YAxis rightAxis11 = chart11.getAxisRight();
        rightAxis11.setEnabled(false);

        YAxis leftAxis12 = chart12.getAxisLeft();
        leftAxis12.setTextColor(Color.BLACK);
        leftAxis12.setDrawGridLines(true);
        YAxis rightAxis12 = chart12.getAxisRight();
        rightAxis12.setEnabled(false);
        mHistoryListView = (ListView)view.findViewById(R.id.historyListView);
        mHistoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showUploadDialog(mHistoryAdapter.getItem(i),i);
            }
        });

        //getting list of files
        if(readPermissionGranted == true){
            displayListOfFiles();
        } else {
            boolean secondPermission;
            secondPermission = isReadStoragePermissionGranted();
            if(secondPermission == false){
                Toast.makeText(view.getContext(), "Cannot read storage", Toast.LENGTH_SHORT).show();
            }
        }

    }

    //method to add entry for line chart
    private void addEntry(Float val, LineChart chart, String label) {

        LineData data = chart.getData();
        data.setDrawValues(false);

        LineDataSet set = (LineDataSet) data.getDataSetByIndex(0);

        // set.addEntry(...); // can be called as well
        if (set == null) {
            set = createSet(label);
            data.addDataSet(set);
        }

        data.addEntry(new Entry(set.getEntryCount(), val), 0);
        data.notifyDataChanged();

        // let the chart know it's data has changed
        chart.notifyDataSetChanged();
        chart.invalidate();

        // limit the number of visible entries
        chart.setVisibleXRangeMaximum(1000);

        //chart.setVisibleYRange(30, YAxis.AxisDependency.LEFT);
        // move to the latest entry
        chart.moveViewToX(data.getEntryCount());

        // this automatically refreshes the chart (calls invalidate())
        chart.moveViewTo(data.getEntryCount()-10, 55f, YAxis.AxisDependency.LEFT);

    }

    //method to create dataset from bluetooth
    private LineDataSet createSet(String label) {

        LineDataSet set = new LineDataSet(null, label);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ColorTemplate.getHoloBlue());
        set.setDrawCircles(false);
        set.setLineWidth(2f);
        set.setFillAlpha(65);
        set.setFillColor(Color.BLACK);
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(Color.BLACK);
        set.setValueTextSize(12f);
        set.setDrawValues(false);

        return set;

    }

    //method to select a value on chart
    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("Entry selected", e.toString());
    }

    //method of when nothing in chart is selected
    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }

    public void displayListOfFiles() {
        File dir = new File(Environment.getExternalStorageDirectory().getPath()+File.separator+"Kardium");
        File[] fileList = dir.listFiles();
        if(fileList != null) {
            String[] theNamesOfFiles = new String[fileList.length];
            for (int i = 0; i < theNamesOfFiles.length; i++) {
                theNamesOfFiles[i] = fileList[i].getName();
            }
            mHistoryAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1, theNamesOfFiles);
            mHistoryListView.setAdapter(mHistoryAdapter);
        }
    }

    private void showUploadDialog(final String item, final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setMessage("Reload this file?")
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        addEntryFromFile(item);
                    }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle(R.string.app_name);
        alert.show();
    }

    //this method will upload the file
    private void addEntryFromFile(final String item) {
        FileOperations fop = new FileOperations();
        String[] val = fop.read(item);
        for (int i = 0; i < val.length; i++) {
            if (val[i].contains("A")){
                val[i] = val[i].replace("A","");
                float fVal = Float.parseFloat(val[i]);
                addEntry(fVal, chart1, "Lead I");
            } else if (val[i].contains("B")){
                val[i] = val[i].replace("B","");
                float fVal = Float.parseFloat(val[i]);
                addEntry(fVal, chart2, "Lead II");
            } else if(val[i].contains("C")){
                val[i] = val[i].replace("C","");
                float fVal = Float.parseFloat(val[i]);
                addEntry(fVal, chart3, "Lead III");
            } else if (val[i].contains("D")){
                val[i] = val[i].replace("D","");
                float fVal = Float.parseFloat(val[i]);
                addEntry(fVal, chart4, "Lead aVR");
            } else if (val[i].contains("E")){
                val[i] = val[i].replace("E","");
                float fVal = Float.parseFloat(val[i]);
                addEntry(fVal, chart5, "Lead aVL");
            } else if (val[i].contains("F")){
                val[i] = val[i].replace("F","");
                float fVal = Float.parseFloat(val[i]);
                addEntry(fVal, chart6, "Lead aVF");
            } else if (val[i].contains("G")){
                val[i] = val[i].replace("G","");
                float fVal = Float.parseFloat(val[i]);
                addEntry(fVal, chart7, "Lead V1");
            } else if (val[i].contains("H")){
                val[i] = val[i].replace("H","");
                float fVal = Float.parseFloat(val[i]);
                addEntry(fVal, chart8, "Lead V2");
            } else if (val[i].contains("I")){
                val[i] = val[i].replace("I","");
                float fVal = Float.parseFloat(val[i]);
                addEntry(fVal, chart9, "Lead V3");
            } else if (val[i].contains("J")){
                val[i] = val[i].replace("J","");
                float fVal = Float.parseFloat(val[i]);
                addEntry(fVal, chart10, "Lead V4");
            } else if (val[i].contains("K")){
                val[i] = val[i].replace("K","");
                float fVal = Float.parseFloat(val[i]);
                addEntry(fVal, chart11, "Lead V5");
            } else if (val[i].contains("L")){
                val[i] = val[i].replace("L","");
                float fVal = Float.parseFloat(val[i]);
                addEntry(fVal, chart12, "Lead V6");
            }
            SystemClock.sleep(1);
        }
    }

    public  boolean isReadStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (Objects.requireNonNull(getActivity()).checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }

}
