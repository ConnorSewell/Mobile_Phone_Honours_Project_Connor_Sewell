package com.example.testproject.mobile_phone_honours_project_connor_sewell;

import android.graphics.Color;
import android.util.Log;
import android.view.View;

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
import com.github.mikephil.charting.formatter.PercentFormatter;
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
    public LineChart setUpGraph(LineChart graph, int type)
    {
        //graph.setScaleEnabled(true);
        graph.setDragEnabled(true);
        //graph.setPinchZoom(true);
        graph.setBackgroundColor(Color.GRAY);
        graph.setDescription(null);
        graph.setTag("Accelerometer");

        LineData graphData = new LineData();
        graphData.setValueTextColor(Color.WHITE);

        graph.setData(graphData);

        Legend graphLegend = graph.getLegend();

        graphLegend.setDrawInside(true);
        graphLegend.setEnabled(false);
        //graphLegend.setForm(Legend.LegendForm.LINE);
        //graphLegend.setTextColor(Color.WHITE);
        //graphLegend.setTextSize(7);
        //graphLegend.setXOffset(0);

        XAxis graphAxisX = graph.getXAxis();
        graphAxisX.setXOffset(1);
        graphAxisX.setTextColor(Color.WHITE);
        graphAxisX.setAvoidFirstLastClipping(true);

        YAxis graphAxisY = graph.getAxisLeft();
        graphAxisY.setTextColor(Color.WHITE);

        if(type == 0)
        {
            graphAxisY.setValueFormatter(new MetresSecondSquaredFormatter());
            graphAxisY.setLabelCount(3, true);
        }
        else if(type == 1)
        {
            graphAxisY.setValueFormatter(new RadiansPerSecondFormatter());
            graphAxisY.setLabelCount(3, true);
        }
        else if(type == 2)
        {
            graphAxisY.setValueFormatter(new AudioPercentageFormatter());
        }

        graphAxisX.setLabelCount(2, true);
        graphAxisX.setValueFormatter(new SecondsFormatter());

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

    public LineChart update3SeriesGraph(List<Float> xVals, List<Float> yVals, List<Float> zVals, List<Float> timeStamps, LineChart graph, int graphIndex)
    {
        try {
            //String[] values = inputLine.split(",");
            //x = Float.parseFloat(values[0]);
            //y = Float.parseFloat(values[1]);
            //z = Float.parseFloat(values[2]);
            //time = Long.parseLong(values[3]);
            LineData data = graph.getData();

            String graphInput = null;

            if (data != null) {
                ILineDataSet set = data.getDataSetByIndex(0);
                ILineDataSet set2 = data.getDataSetByIndex(1);
                ILineDataSet set3 = data.getDataSetByIndex(2);

                if (set == null) {
                    if (graphIndex == 0) {
                        graphInput = "Accel Up/Down";
                    } else if (graphIndex == 1) {
                        graphInput = "Gyro Tilt Up/Down";
                    }
                    set = createSet(Color.CYAN, graphInput);
                    data.addDataSet(set);
                }

                if (set2 == null) {
                    if (graphIndex == 0) {
                        graphInput = "Accel Left/Right";
                    } else if (graphIndex == 1) {
                        graphInput = "Gyro Tilt Left/Right";
                    }
                    set2 = createSet(Color.YELLOW, graphInput);
                    data.addDataSet(set2);
                }

                if (set3 == null) {
                    if (graphIndex == 0) {
                        graphInput = "Accel Forwards/Backwards";
                    } else if (graphIndex == 1) {
                        graphInput = "Gyro Rotate Downwards/Upwards";
                    }
                    set3 = createSet(Color.GREEN, graphInput);
                    data.addDataSet(set3);

                }

                for (int i = 0; i < xVals.size(); i++) {
                    data.addEntry(new Entry(timeStamps.get(i), xVals.get(i)), 0);
                    data.addEntry(new Entry(timeStamps.get(i), yVals.get(i)), 1);
                    data.addEntry(new Entry(timeStamps.get(i), zVals.get(i)), 2);
                }


                if (counter < 49) {
                    counter++;
                }

                if (counter == 49) {
                    // data.getDataSetByIndex(0).removeFirst();
                    //data.getDataSetByIndex(1).removeFirst();
                    //data.getDataSetByIndex(2).removeFirst();
                }
                graph.notifyDataSetChanged();
                graph.setVisibleXRangeMaximum(5);
                graph.moveViewToX(data.getDataSetByIndex(0).getEntryCount() - 1);
            }
        }
        catch(Exception e)
        {
            Log.e("Graph Update: ", "Likely updating too quickly");
        }
        return graph;
    }

    public LineChart updateYAxisLabels(LineChart graph, int labelCount, int min, int max, boolean strictLabel)
    {
        graph.getAxisLeft().setAxisMaximum(max);
        graph.getAxisLeft().setAxisMinimum(min);
        graph.getAxisLeft().setLabelCount(labelCount, strictLabel);
        return graph;
    }

    int counter2 = 0;
    public LineChart updateSingleSeriesGraph(LineChart graph, int graphIndex, List<Integer> audioVals, List<Float> timeStamps)
    {
        try {
            LineData data = graph.getData();
            String graphInput = null;

            if (data != null) {
                ILineDataSet set = data.getDataSetByIndex(0);

                if (set == null) {
                    if (graphIndex == 0) {
                        graphInput = "Audio Levels";
                    }
                    set = createSet(Color.CYAN, graphInput);
                    data.addDataSet(set);
                }


                for (int i = 0; i < audioVals.size(); i++) {
                    data.addEntry(new Entry(timeStamps.get(i), audioVals.get(i)), 0);
                }

                //data.notifyDataChanged();
                graph.notifyDataSetChanged();
                graph.setVisibleXRangeMaximum(5);
                graph.moveViewToX(data.getEntryCount());
            }
        }
        catch(Exception e)
        {
            Log.e("Graph Update: ", "Likely updating too quickly");
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
class AudioPercentageFormatter implements IAxisValueFormatter {
    @Override
    public String getFormattedValue(float value, AxisBase axis)
    {
        if (value != 0) {
            if (value > 0) {
                return ((double) (value / 32767) * 100 + " %");
            }
            return ((double) (value / 32768) * 100 + " %");
        }
        return String.valueOf(value);
    }
}

//https://github.com/PhilJay/MPAndroidChart/wiki/The-AxisValueFormatter-interface
//^Accessed: 04/04/2017 @ 06:30
class RadiansPerSecondFormatter implements IAxisValueFormatter
{
    private DecimalFormat mFormat;

    public RadiansPerSecondFormatter() {
        mFormat = new DecimalFormat("###,###,##0.0"); // use one decimal
    }


    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        if (value != 0) {
            if (value > 0) {
                return "rad/s " + mFormat.format(value);
            }
            return "rad/s " + mFormat.format(value);
        }
        return mFormat.format(value);
    }
}

//https://github.com/PhilJay/MPAndroidChart/wiki/The-AxisValueFormatter-interface
//^Accessed: 04/04/2017 @ 06:30
class MetresSecondSquaredFormatter implements IAxisValueFormatter
{

    private DecimalFormat mFormat;

    public MetresSecondSquaredFormatter() {
        mFormat = new DecimalFormat("###,###,##0.0"); // use one decimal
    }


    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        if (value != 0) {
            if (value > 0) {
                return "m/" + "s\u00B2 " + mFormat.format(value);
            }
            return "m/" + "s\u00B2 " + mFormat.format(value);
        }
        return mFormat.format(value);
    }
}


//https://github.com/PhilJay/MPAndroidChart/wiki/The-AxisValueFormatter-interface
//^Accessed: 04/04/2017 @ 06:30
class SecondsFormatter implements IAxisValueFormatter
{
    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return String.valueOf((int)value + " Secs");
    }
}
