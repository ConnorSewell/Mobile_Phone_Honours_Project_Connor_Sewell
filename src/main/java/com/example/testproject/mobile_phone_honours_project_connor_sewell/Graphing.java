package com.example.testproject.mobile_phone_honours_project_connor_sewell;

import android.graphics.Color;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

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
        graph.setPinchZoom(true);
        graph.setBackgroundColor(Color.GRAY);
        graph.setDescription(null);

        LineData graphData = new LineData();
        graphData.setValueTextColor(Color.WHITE);

        graph.setData(graphData);

        Legend graphLegend = graph.getLegend();
        graphLegend.setForm(Legend.LegendForm.LINE);
        graphLegend.setTextColor(Color.WHITE);

        XAxis graphAxisX = graph.getXAxis();
        graphAxisX.setTextColor(Color.WHITE);
        graphAxisX.setAvoidFirstLastClipping(true);

        YAxis graphAxisY = graph.getAxisLeft();
        graphAxisY.setTextColor(Color.WHITE);

        YAxis graphAxisYRight = graph.getAxisRight();
        graphAxisYRight.setEnabled(false);

        return graph;
    }

    float x;
    float y;
    float z;
    long time;
    int counter = 0;

    public LineChart update3SeriesGraph(String inputLine, LineChart graph, String graphName)
    {
        String[] values = inputLine.split(",");
        x = Float.parseFloat(values[0]);
        y = Float.parseFloat(values[1]);
        z = Float.parseFloat(values[2]);
        time = Long.parseLong(values[3]);
        LineData data = graph.getData();

        if(data != null)
        {
            ILineDataSet set = data.getDataSetByIndex(0);
            ILineDataSet set2 = data.getDataSetByIndex(1);
            ILineDataSet set3 = data.getDataSetByIndex(2);

            if(set == null)
            {
                set = createSet(Color.CYAN, graphName + "X");
                data.addDataSet(set);
            }

            if(set2 == null)
            {
                set2 = createSet(Color.YELLOW, graphName + "Y");
                data.addDataSet(set2);
            }

            if(set3 == null)
            {
                set3 = createSet(Color.GREEN, graphName + "Z");
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

    public LineChart updateSingleSeriesGraph(String inputLine, LineChart graph, String graphName)
    {
        String[] values = inputLine.split(",");
        x = Float.parseFloat(values[0]);
        time = Long.parseLong(values[1]);
        LineData data = graph.getData();

        if(data != null)
        {
            ILineDataSet set = data.getDataSetByIndex(0);

            if(set == null)
            {
                set = createSet(Color.CYAN, graphName + "X");
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
