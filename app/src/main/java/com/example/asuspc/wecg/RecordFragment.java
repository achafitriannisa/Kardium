package com.example.asuspc.wecg;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

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

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import androidx.annotation.RequiresApi;

import static android.app.Activity.RESULT_OK;

public class RecordFragment extends Fragment implements OnChartValueSelectedListener {

    // GUI Components
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
    private TextView mReadBuffer;
    private ToggleButton mRecordBtn;
    private ImageButton mClearBtn, mWriteBtn;
    public BluetoothAdapter mBTAdapter;
    private String readMessage;
    private String str;
    private StringBuilder fileData = new StringBuilder();
    private FragmentMessage mCallback; //for communication between fragments
    public boolean viewMode = false;
    public String address, name;
    private float dI, dII, dIII, daVR, daVL, daVF, dV1, dV2, dV3, dV4, dV5, dV6;

    //fragment view
    View view;

    public Handler mHandler; // handler for bluetooth
    public ConnectedThread mConnectedThread; // bluetooth background worker thread to send and receive data
    public BluetoothSocket mBTSocket = null; // bi-directional client-to-client data path

    private static final UUID BT_MODULE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier

    // defines for identifying shared types between calling functions
    private final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names
    private final static int CONNECTING_STATUS = 2; // used in bluetooth handler to identify connection status
    //private final static int BLUETOOTH_DEVICE = 3; // used to identify bluetooth device mac address and name
    private final static int READ_MESSAGE = 3; // used to identify read message from bluetooth
    private final static String BLUETOOTH_DEVICE = "3";
    private final static String FILE_CREATED = "6";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_record, container, false);

        return view;
    }

    @SuppressLint("HandlerLeak")
    @Override

    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //getting permission to external storage
        final boolean writePermissionGranted;
        if (isWriteStoragePermissionGranted()) {
            writePermissionGranted = true;
        } else {
            writePermissionGranted = false;
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
        mReadBuffer = (TextView) view.findViewById(R.id.readBuffer);
        mRecordBtn = (ToggleButton) view.findViewById(R.id.record);
        mClearBtn = (ImageButton)view.findViewById(R.id.clear);
        mWriteBtn = (ImageButton)view.findViewById(R.id.write);

        //set title for toolbar
        getActivity().setTitle(R.string.action_record);

        //define bluetooth adapter
        mBTAdapter = BluetoothAdapter.getDefaultAdapter(); // get a handle on the bluetooth radio

        //setting up visibility of the chart
        if (viewMode) {
            chart1.setVisibility(View.VISIBLE);
            chart3.setVisibility(View.VISIBLE);
            chart4.setVisibility(View.VISIBLE);
            chart5.setVisibility(View.VISIBLE);
            chart6.setVisibility(View.VISIBLE);
            chart7.setVisibility(View.VISIBLE);
            chart8.setVisibility(View.VISIBLE);
            chart9.setVisibility(View.VISIBLE);
            chart10.setVisibility(View.VISIBLE);
            chart11.setVisibility(View.VISIBLE);
            chart12.setVisibility(View.VISIBLE);
        } else {
            chart1.setVisibility(View.GONE);
            chart3.setVisibility(View.GONE);
            chart4.setVisibility(View.GONE);
            chart5.setVisibility(View.GONE);
            chart6.setVisibility(View.GONE);
            chart7.setVisibility(View.GONE);
            chart8.setVisibility(View.GONE);
            chart9.setVisibility(View.GONE);
            chart10.setVisibility(View.GONE);
            chart11.setVisibility(View.GONE);
            chart12.setVisibility(View.GONE);
        }

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

        mHandler = new Handler() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public void handleMessage(Message msg){
                switch (msg.what) {
                    case READ_MESSAGE:
                        try {
                            readMessage = new String((byte[]) msg.obj, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        float[] easi = {0, 0, 0, 0};
                        if(viewMode == true) {
                            if (readMessage.contains("A")) {
                                str = readMessage.replace("A", "");
                                float fVal = Float.parseFloat(str);
                                easi[0] = fVal;
                            } else if (readMessage.contains("B")) {
                                str = readMessage.replace("B", "");
                                float fVal = Float.parseFloat(str);
                                easi[1] = fVal;
                            } else if (readMessage.contains("C")) {
                                str = readMessage.replace("C", "");
                                float fVal = Float.parseFloat(str);
                                easi[2] = fVal;
                            } else if (readMessage.contains("D")) {
                                str = readMessage.replace("D", "");
                                float fVal = Float.parseFloat(str);
                                easi[3] = fVal;
                                deriveSignal(easi);
                            } else if (readMessage.contains("E")) {
                                str = readMessage.replace("D", "");
                                mReadBuffer.setText(str + " BPM");
                            }
                        } else {
                            if (readMessage.contains("A")) {
                                str = readMessage.replace("A", "");
                                float fVal = Float.parseFloat(str);
                                easi[0] = fVal;
                            } else if (readMessage.contains("B")) {
                                str = readMessage.replace("B", "");
                                float fVal = Float.parseFloat(str);
                                easi[1] = fVal;
                            } else if (readMessage.contains("C")) {
                                str = readMessage.replace("C", "");
                                float fVal = Float.parseFloat(str);
                                easi[2] = fVal;
                            } else if (readMessage.contains("D")) {
                                str = readMessage.replace("D", "");
                                float fVal = Float.parseFloat(str);
                                easi[3] = fVal;
                                deriveSignal(easi);
                            } else if (readMessage.contains("E")) {
                                str = readMessage.replace("D", "");
                                mReadBuffer.setText(str + " BPM");
                            }
                        }
                        break;
                    case CONNECTING_STATUS:
                        if (msg.arg1 == 1) {
                            mRecordBtn.setChecked(true);
                        }
                        break;
                }
            }
        };

        //implementing OnClickListener on buttons
        mRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mRecordBtn.isChecked()) {
                    startRecording(v);
                } else {
                    if(mConnectedThread.isAlive()){
                        mConnectedThread.cancel();
                    }
                }
            }
        });

        mClearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chart1.getData().clearValues();
                chart2.getData().clearValues();
                chart3.getData().clearValues();
                chart4.getData().clearValues();
                chart5.getData().clearValues();
                chart6.getData().clearValues();
                chart7.getData().clearValues();
                chart8.getData().clearValues();
                chart9.getData().clearValues();
                chart10.getData().clearValues();
                chart11.getData().clearValues();
                chart12.getData().clearValues();
            }
        });
        mWriteBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mConnectedThread.cancel();
                mRecordBtn.setChecked(false);
                if(writePermissionGranted && fileData != null) {
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    String fileName = timeStamp + ".txt";
                    FileOperations fop = new FileOperations();
                    fop.write(fileName, fileData.toString());
                    if (fop.write(fileName, fileData.toString())) {
                        Toast.makeText(v.getContext(), fileName + " created", Toast.LENGTH_SHORT).show();
                        Bundle fileCreated = new Bundle();
                        fileCreated.putString(FILE_CREATED, fileName);
                        mCallback.sentMessage(fileCreated);
                    } else {
                        Toast.makeText(v.getContext(), "I/O error", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    boolean secondPermissionGranted = isWriteStoragePermissionGranted();
                    if (!secondPermissionGranted){
                        Toast.makeText(v.getContext(), "Cannot write a file", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    //method to add entry for line chart
    private void addEntry(float val, LineChart chart, String label) {

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
        chart.setVisibleXRangeMaximum(500);

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

    public void deriveSignal(float[] easi){
        dI = -0.0302f*easi[0]+0.083f*easi[1]+0.0718f*easi[2]-1.7402f*easi[3]+1.0043f;
        if(viewMode){
            addEntry(dI, chart1, "Lead I");
        }
        fileData.append("A").append(dI).append("\n");
        dII = 0.1992f*easi[0]+0.1561f*easi[1]-1.0576f*easi[2]-0.1414f*easi[3]-2.5664f;
        addEntry(dII, chart2, "Lead II");
        fileData.append(" B").append(dII).append("\n");
        dIII = 0.2295f*easi[0]+0.0731f*easi[1]-1.1295f*easi[2]+1.5988f*easi[3]-3.5707f;
        if(viewMode) {
            addEntry(dIII, chart3, "Lead III");
        }
        fileData.append(" C").append(dIII).append("\n");
        daVR = -0.0845f*easi[0]-0.1195f*easi[1]+0.4929f*easi[2]+0.9408f*easi[3]+0.7811f;
        if(viewMode) {
            addEntry(daVR, chart4, "Lead aVR");
        }
        fileData.append(" D").append(daVR).append("\n");
        daVL = -0.1298f*easi[0]+0.5988f*easi[2]-1.6804f*easi[3]+2.3043f;
        if(viewMode) {
            addEntry(daVL, chart5, "Lead aVL");
        }
        fileData.append(" E").append(daVL).append("\n");
        daVF = 0.2143f*easi[0]+0.1146f*easi[1]-1.0935f*easi[2]+0.7287f*easi[3]-3.0685f;
        if(viewMode) {
            addEntry(daVF, chart6, "Lead aVF");
        }
        fileData.append(" F").append(daVF).append("\n");
        dV1 = 0.6344f*easi[0]+0.0799f*easi[1]+0.501f*easi[2]+0.4933f*easi[3]+4.0389f;
        if(viewMode) {
            addEntry(dV1, chart7, "Lead V1");
        }
        fileData.append(" G").append(dV1).append("\n");
        dV2 = 1.0836f*easi[0]-0.095f*easi[1]+0.5252f*easi[2]-1.249f*easi[3]+13.6635f;
        if(viewMode) {
            addEntry(dV2, chart8, "Lead V2");
        }
        fileData.append(" H").append(dV2).append("\n");
        dV3 = 0.7993f*easi[0]+0.2801f*easi[1]+0.0881f*easi[2]-2.3115f*easi[3]+5.0573f;
        if(viewMode) {
            addEntry(dV3, chart9, "Lead V3");
        }
        fileData.append(" I").append(dV3).append("\n");
        dV4 = 0.368f*easi[0]+1.2349f*easi[1]+0.0869f*easi[2]-1.1872f*easi[3];
        if(viewMode) {
            addEntry(dV4, chart10, "Lead V4");
        }
        fileData.append(" J").append(dV4).append("\n");
        dV5 = 0.1384f*easi[0]+1.5578f*easi[1]+0.0865f*easi[2]+0.3616f*easi[3]+0.024f;
        if(viewMode) {
            addEntry(dV5, chart11, "Lead V5");
        }
        fileData.append(" K").append(dV5).append("\n");
        dV6 = 0.0362f*easi[0]+1.2552f*easi[1]-0.1469f*easi[2]+0.706f*easi[3]-1.2352f;
        if(viewMode) {
            addEntry(dV6, chart12, "Lead V6");
        }
        fileData.append(" L").append(dV6).append("\n");
    }

    //method to turn bluetooth on
    public void startRecording(View view){
        if (!mBTAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            connectingDevices();
        }

    }

    //method to pair devices
    public void connectingDevices() {
        if (address != null && name != null) {
            // Spawn a new thread to avoid blocking the GUI one
            new Thread()
            {
                public void run() {
                    boolean fail = false;

                    BluetoothDevice device = mBTAdapter.getRemoteDevice(address);

                    try {
                        mBTSocket = createBluetoothSocket(device);
                    } catch (IOException e) {
                        fail = true;
                        Toast.makeText(view.getContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                    }
                    // Establish the Bluetooth socket connection.
                    try {
                        mBTSocket.connect();
                    } catch (IOException e) {
                        try {
                            fail = true;
                            mBTSocket.close();
                            mHandler.obtainMessage(CONNECTING_STATUS, -1, -1)
                                    .sendToTarget();
                        } catch (IOException e2) {
                            //insert code to deal with this
                            Toast.makeText(view.getContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                    if(fail == false) {
                        mConnectedThread = new ConnectedThread(mBTSocket);
                        mConnectedThread.start();

                        mHandler.obtainMessage(CONNECTING_STATUS, 1, -1)
                                .sendToTarget();
                    }
                }
            }.start();
        } else {
            Bundle startFragment = new Bundle();
            startFragment.putString(BLUETOOTH_DEVICE,"settingsFragment");
            mCallback.sentMessage(startFragment);
        }
    }

    //method to create bluetooth socket for data transfer
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        return  device.createRfcommSocketToServiceRecord(BT_MODULE_UUID);
        //creates secure outgoing connection with BT device using UUID
    }


    public boolean isWriteStoragePermissionGranted() {
        String TAG = "Storage Permission";
        if (Build.VERSION.SDK_INT >= 23) {
            if (Objects.requireNonNull(getActivity()).checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {
                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                if (getActivity().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    Log.v(TAG, "Permission is granted");
                    return true;
                } else {
                    return false;
                }
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentMessage) {
            mCallback = (FragmentMessage) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FragmentMessage");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    //method to see the result of pairing
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent Data){
        // Check which request we're responding to
        if (requestCode == REQUEST_ENABLE_BT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                connectingDevices();
            }
        }
    }

    public class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;

        ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;

        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()
            final byte delimiter = 10; //ASCII for new line character
            final byte leadII = 66; //ASCII for B character
            int readBufferPosition = 0;


            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.available();
                    if(bytes != 0) {
                        byte[] packetBytes = new byte[bytes];

                        mmInStream.read(packetBytes);
                        for (int i = 0; i < bytes; i++) {
                            byte b = packetBytes[i]; //running index to identify delimiter
                            if (b == delimiter) {
                                byte[] encodedBytes = new byte[readBufferPosition];
                                System.arraycopy(buffer, 0, encodedBytes, 0, encodedBytes.length);
                                readBufferPosition = 0;
                                if (encodedBytes.length < 3) {
                                    continue;
                                }
                                mHandler.obtainMessage(READ_MESSAGE, bytes, -1, encodedBytes)
                                        .sendToTarget();
                            } else {
                                buffer[readBufferPosition++] = b;
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

        /* Call this from the main activity to shutdown the connection */
        void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    private void refreshList(int listId, int refreshedData) {

    }
}