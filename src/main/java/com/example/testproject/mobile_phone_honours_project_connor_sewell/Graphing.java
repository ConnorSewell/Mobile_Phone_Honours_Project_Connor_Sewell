package com.example.testproject.mobile_phone_honours_project_connor_sewell;

import android.graphics.Color;
import android.util.Log;

import com.github.mikephil.charting.*;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Connor on 09/03/2017.
 *
 * https://www.youtube.com/watch?v=a20EchSQgpw Referenced 02/03/2017 @ 02:59
 * ^ AND https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/com/xxmassdeveloper/mpchartexample/RealtimeLineChartActivity.java
 * ^ Referenced 02/03/2017 @ 03:00 used for all graphing code
 */

public class Graphing
{
    public LineChart setUpGraph(LineChart graph)
    {
        graph.setScaleEnabled(true);
        graph.setDragEnabled(false);
        //graph.setPinchZoom(true);
        graph.setBackgroundColor(Color.GRAY);
        graph.setDescription(null);
        graph.setTag("Accelerometer");


        LineData graphData = new LineData();
        graphData.setValueTextColor(Color.WHITE);

        graph.setData(graphData);

        Legend graphLegend = graph.getLegend();
        graphLegend.setForm(Legend.LegendForm.LINE);
        graphLegend.setTextColor(Color.WHITE);
        graphLegend.setTextSize(7);
        graphLegend.setXOffset(0);

        XAxis graphAxisX = graph.getXAxis();
        graphAxisX.setTextColor(Color.WHITE);
        graphAxisX.setAvoidFirstLastClipping(true);

        YAxis graphAxisY = graph.getAxisLeft();
        graphAxisY.setTextColor(Color.WHITE);
        graphAxisY.setLabelCount(3);
        graphAxisY.setValueFormatter(new MyValueFormatter());
        //graphAxisY.setGranularity(2f);

        graphAxisY.setGranularityEnabled(false);
        YAxis graphAxisYRight = graph.getAxisRight();
        graphAxisYRight.setEnabled(false);



        //graph.setVisibleYRangeMaximum(5, YAxis.AxisDependency.LEFT);
        //graph.setVisibleYRangeMinimum(0, YAxis.AxisDependency.LEFT);
        return graph;
    }

    float x;
    float y;
    float z;
    long time;
    int counter = 0;

    public LineChart update3SeriesGraph(String inputLine, LineChart graph, int graphIndex)
    {
        String[] values = inputLine.split(",");
        x = Float.parseFloat(values[0]);
        y = Float.parseFloat(values[1]);
        z = Float.parseFloat(values[2]);
        time = Long.parseLong(values[3]);
        LineData data = graph.getData();

        ;
        String graphInput = null;

        if(data != null)
        {
            ILineDataSet set = data.getDataSetByIndex(0);
            ILineDataSet set2 = data.getDataSetByIndex(1);
            ILineDataSet set3 = data.getDataSetByIndex(2);

            if(set == null)
            {
                if(graphIndex == 0)
                {
                    graphInput = "Accel Up/Down";
                }
                else if(graphIndex == 1)
                {
                    graphInput = "Gyro Tilt Up/Down";
                }
                set = createSet(Color.CYAN, graphInput);
                data.addDataSet(set);
            }

            if(set2 == null)
            {
                if(graphIndex == 0)
                {
                    graphInput = "Accel Left/Right";
                }
                else if(graphIndex == 1)
                {
                    graphInput = "Gyro Tilt Left/Right";
                }
                set2 = createSet(Color.YELLOW, graphInput);
                data.addDataSet(set2);
            }

            if(set3 == null)
            {
                if(graphIndex == 0)
                {
                    graphInput = "Accel Forwards/Backwards";
                }
                else if(graphIndex == 1)
                {
                    graphInput = "Gyro Rotate Downwards/Upwards";
                }
                set3 = createSet(Color.GREEN, graphInput);
                data.addDataSet(set3);
            }

            data.addEntry(new Entry(set.getEntryCount(), x), 0);
            data.addEntry(new Entry(set2.getEntryCount(), y), 1);
            data.addEntry(new Entry(set3.getEntryCount(), z), 2);
            //data.removeEntry(0, 0);
            data.notifyDataChanged();
            //graph.clear();
            graph.notifyDataSetChanged();
            graph.setVisibleXRangeMaximum(50);
            graph.moveViewToX(data.getEntryCount());
        }
        return graph;
    }

    public LineChart updateSingleSeriesGraph(String inputLine, LineChart graph, int graphIndex)
    {
        String[] values = inputLine.split(",");
        x = Float.parseFloat(values[0]);
        time = Long.parseLong(values[1]);
        LineData data = graph.getData();
        String graphInput = null;

        if(data != null)
        {
            ILineDataSet set = data.getDataSetByIndex(0);

            if(set == null)
            {
                if(graphIndex == 0)
                {
                    graphInput = "Audio Levels";
                }
                set = createSet(Color.CYAN, graphInput);
                data.addDataSet(set);
            }

            data.addEntry(new Entry(set.getEntryCount(), x), 0);
            data.notifyDataChanged();

            graph.notifyDataSetChanged();
            graph.setVisibleXRangeMaximum(50);
            graph.moveViewToX(data.getEntryCount());
        }
        return graph;
    }

    private LineDataSet createSet(int colour, String setName)
    {
        LineDataSet set = new LineDataSet(null, setName);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(colour);
        set.setDrawCircles(false);
        set.setLineWidth(0.5f);
        set.setDrawValues(false);
        return set;
    }
}

//https://github.com/PhilJay/MPAndroidChart/wiki/The-AxisValueFormatter-interface
//^Accessed: 04/04/2017 @ 06:30
class MyValueFormatter implements IAxisValueFormatter
{
    @Override
    public String getFormattedValue(float value, AxisBase axis)
    {
        return "m/s " + value;
    }
}
