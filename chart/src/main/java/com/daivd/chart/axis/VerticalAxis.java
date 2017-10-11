package com.daivd.chart.axis;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;

import com.daivd.chart.data.ChartData;
import com.daivd.chart.data.LineData;
import com.daivd.chart.data.ScaleData;
import com.daivd.chart.exception.ChartException;

import java.util.List;

/**
 * Created by huang on 2017/9/26.
 */

public class VerticalAxis extends BaseAxis {


    public VerticalAxis() {
        direction = AxisDirection.LEFT;
    }
    public VerticalAxis(AxisDirection direction) {
        this.direction = direction;
    }

    java.text.DecimalFormat df = new java.text.DecimalFormat("0.00");

    @Override
    public void computeScale(ChartData<LineData> chartData, Rect rect, Paint paint) {
        ScaleData scaleData = chartData.getScaleData();
        scaleStyle.fillPaint(paint);
        int length = Math.max(formatVerticalAxisData(scaleData.getMaxScaleValue(direction)).length(),
                formatVerticalAxisData(scaleData.getMinScaleValue(direction)).length());
        int textHeight = (int) (paint.measureText("1", 0, 1) * length);
        int dis = (int) (textHeight + scaleStyle.getPadding() * 2 + lineStyle.getWidth());
        if (direction == AxisDirection.LEFT) {
            scaleData.scaleRect.left = dis;
        } else {
            scaleData.scaleRect.right = dis;
        }
    }

    @Override
    protected void drawScale(Canvas canvas, Rect zoomRect, Rect clipRect, Paint paint,  ChartData<LineData> chartData) {
        ScaleData scaleData = chartData.getScaleData();
        List<Double> scaleList = scaleData.getScaleList(direction);
        float startX;
        if (direction == AxisDirection.LEFT) {
            startX = zoomRect.left + scaleStyle.getPadding();
        } else {
            startX = zoomRect.right - scaleData.scaleRect.right + scaleStyle.getPadding();
        }
        int bottom = zoomRect.bottom ;
        int height = bottom - zoomRect.top;
        float textHeight = paint.measureText("1", 0, 1);
        int perHeight = height / scaleList.size();
        for (int i = 0; i < scaleList.size(); i++) {
            double value = scaleList.get(i);
            float startY = bottom - i * perHeight;

            if (startY+ textHeight / 2 > clipRect.top && startY< clipRect.bottom) {
                drawText(canvas, startX, startY + textHeight / 2, value, paint);
                int startGirdPos = direction == AxisDirection.LEFT ? perHeight : 0;
                drawGrid(canvas, startY - startGirdPos, zoomRect, scaleData.scaleRect, paint);
            }
        }

    }

    /**
     * 绘制文字
     */
    private void drawText(Canvas canvas, float startX, float startY, double value, Paint paint) {
        scaleStyle.fillPaint(paint);
        String content = formatVerticalAxisData(value);
        canvas.drawText(content, startX, startY, paint);
    }

    /**
     * 绘制网格
     */
    public void drawGrid(Canvas canvas, float startY, Rect rect, Rect scaleRect, Paint paint) {
        if (gridStyle != null && isDrawGrid) {
            gridStyle.fillPaint(paint);
            Path path = new Path();
            path.moveTo(rect.left + scaleRect.left, startY);
            path.lineTo(rect.right - scaleRect.right, startY);
            canvas.drawPath(path, paint);
        }
    }

    /**
     * 绘制轴
     */
    @Override
    protected void drawAxis(Canvas canvas, Rect rect, Paint paint,  ChartData<LineData> chartData) {

        Rect scaleRect = chartData.getScaleData().scaleRect;
        lineStyle.fillPaint(paint);
        int[] r = calculation(rect, scaleRect);
        canvas.drawLine(r[0], r[1], r[2], r[3], paint);
    }

    private int[] calculation(Rect rect, Rect scaleRect) {

        int startY = rect.top + scaleRect.top;
        int endY = rect.bottom - scaleRect.bottom;
        int startX, endX;
        if (direction == AxisDirection.LEFT) {
            startX = rect.left + scaleRect.left;
        } else {
            startX = rect.right - scaleRect.right;
        }
        endX = startX;
        return new int[]{startX, startY, endX, endY};
    }

    @Override
    public void setAxisDirection(AxisDirection axisDirection) {
        if (axisDirection == AxisDirection.LEFT || axisDirection == AxisDirection.RIGHT) {
            this.direction = axisDirection;
        } else throw new ChartException("只能设置LEFT,RIGHT方向");
    }

    @Override
    public String formatVerticalAxisData(double value) {
        return df.format(value);
    }

}
