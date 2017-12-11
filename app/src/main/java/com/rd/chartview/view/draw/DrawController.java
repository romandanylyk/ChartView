package com.rd.chartview.view.draw;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import com.rd.chartview.R;
import com.rd.chartview.view.animation.AnimationManager;
import com.rd.chartview.view.animation.data.AnimationValue;
import com.rd.chartview.view.draw.data.Chart;
import com.rd.chartview.view.draw.data.DrawData;
import com.rd.chartview.view.utils.ValueUtils;

import java.util.Collections;
import java.util.List;

public class DrawController {

	private Context context;
	private Chart chart;
	private AnimationValue value;

	private Paint frameLinePaint;
	private Paint frameInternalPaint;
	private Paint frameTextPaint;

	private Paint linePaint;
	private Paint strokePaint;
	private Paint fillPaint;

	public DrawController(@NonNull Context context, @NonNull Chart chart) {
		this.context = context;
		this.chart = chart;
		init();
	}

	public void updateTitleWidth() {
		int titleWidth = getTitleWidth();
		chart.setTitleWidth(titleWidth);
	}

	public void updateValue(@NonNull AnimationValue value) {
		this.value = value;
	}

	public void draw(@NonNull Canvas canvas) {
		drawFrame(canvas);
		drawChart(canvas);
	}

	private void drawFrame(@NonNull Canvas canvas) {
		drawFrameText(canvas);
		drawFrameLines(canvas);
	}

	private void drawFrameText(@NonNull Canvas canvas) {
		List<Integer> valueList = chart.getValueList();
		if (valueList == null || valueList.isEmpty()) {
			return;
		}

		int maxValue = Collections.max(valueList);
		int correctedMaxValue = ValueUtils.getCorrectedMaxValue(maxValue);
		float value = (float) correctedMaxValue / maxValue;

		int heightOffset = chart.getHeightOffset();
		int padding = chart.getPadding();
		int textSize = chart.getTextSize();
		int titleWidth = chart.getTitleWidth();

		float width = chart.getWidth();
		float height = chart.getHeight();
		float chartPartHeight = ((height - heightOffset) * value) / Chart.CHART_PARTS;

		float currHeight = height;
		int currTitle = 0;

		for (int i = 0; i <= Chart.CHART_PARTS; i++) {
			float titleY = currHeight;

			if (i <= 0) {
				titleY = height;

			} else if (textSize + chart.getHeightOffset() > currHeight) {
				titleY = currHeight + textSize - Chart.TEXT_SIZE_OFFSET;
			}

			if (i > 0) {
				canvas.drawLine(titleWidth, currHeight, width, currHeight, frameInternalPaint);
			}

			String strTitle = String.valueOf(currTitle);
			canvas.drawText(strTitle, padding, titleY, frameTextPaint);

			currHeight -= chartPartHeight;
			currTitle += correctedMaxValue / Chart.CHART_PARTS;
		}
	}

	private void drawFrameLines(@NonNull Canvas canvas) {
		int height = chart.getHeight();
		int width = chart.getWidth();
		int titleWidth = chart.getTitleWidth();
		int heightOffset = chart.getHeightOffset();

		canvas.drawLine(titleWidth, heightOffset, titleWidth, height, frameLinePaint);
		canvas.drawLine(titleWidth, height, width, height, frameLinePaint);
	}

	private void drawChart(@NonNull Canvas canvas) {
		int runningAnimationPosition = value != null ? value.getRunningAnimationPosition() : AnimationManager.VALUE_NONE;

		for (int i = 0; i < runningAnimationPosition; i++) {
			drawChart(canvas, i, false);
		}

		if (runningAnimationPosition > AnimationManager.VALUE_NONE) {
			drawChart(canvas, runningAnimationPosition, true);
		}
	}

	private void drawChart(@NonNull Canvas canvas, int position, boolean isAnimation) {
		List<DrawData> dataList = chart.getDrawData();
		if (dataList == null || position > dataList.size() - 1) {
			return;
		}

		DrawData drawData = dataList.get(position);
		int startX = drawData.getStartX();
		int startY = drawData.getStartY();

		int stopX;
		int stopY;
		int alpha;

		if (isAnimation) {
			stopX = value.getX();
			stopY = value.getY();
			alpha = value.getAlpha();

		} else {
			stopX = drawData.getStopX();
			stopY = drawData.getStopY();
			alpha = AnimationManager.ALPHA_END;
		}

		drawChart(canvas, startX, startY, stopX, stopY, alpha, position);
	}

	private void drawChart(@NonNull Canvas canvas, int startX, int startY, int stopX, int stopY, int alpha, int position) {
		int radius = chart.getRadius();
		int inerRadius = chart.getInerRadius();
		canvas.drawLine(startX, startY, stopX, stopY, linePaint);

		if (position > 0) {
			strokePaint.setAlpha(alpha);
			canvas.drawCircle(startX, startY, radius, strokePaint);
			canvas.drawCircle(startX, startY, inerRadius, fillPaint);
		}
	}

	private int getTitleWidth() {
		List<Integer> valueList = chart.getValueList();
		if (valueList == null || valueList.isEmpty()) {
			return 0;
		}

		String maxValue = String.valueOf(Collections.max(valueList));
		int titleWidth = (int) frameTextPaint.measureText(maxValue);
		int padding = chart.getPadding();

		return padding + titleWidth + padding;
	}

	private void init() {
		Resources res = context.getResources();
		chart.setHeightOffset((int) (res.getDimension(R.dimen.radius) + res.getDimension(R.dimen.line_width)));
		chart.setPadding((int) res.getDimension(R.dimen.frame_padding));
		chart.setTextSize((int) res.getDimension(R.dimen.frame_text_size));
		chart.setRadius((int) res.getDimension(R.dimen.radius));
		chart.setInerRadius((int) res.getDimension(R.dimen.iner_radius));

		frameLinePaint = new Paint();
		frameLinePaint.setAntiAlias(true);
		frameLinePaint.setStrokeWidth(res.getDimension(R.dimen.frame_line_width));
		frameLinePaint.setColor(res.getColor(R.color.gray_400));

		frameInternalPaint = new Paint();
		frameInternalPaint.setAntiAlias(true);
		frameInternalPaint.setStrokeWidth(res.getDimension(R.dimen.frame_line_width));
		frameInternalPaint.setColor(res.getColor(R.color.gray_200));

		frameTextPaint = new Paint();
		frameTextPaint.setAntiAlias(true);
		frameTextPaint.setTextSize(chart.getTextSize());
		frameTextPaint.setColor(res.getColor(R.color.gray_400));

		linePaint = new Paint();
		linePaint.setAntiAlias(true);
		linePaint.setStrokeWidth(res.getDimension(R.dimen.line_width));
		linePaint.setColor(res.getColor(R.color.blue));

		strokePaint = new Paint();
		strokePaint.setStyle(Paint.Style.STROKE);
		strokePaint.setAntiAlias(true);
		strokePaint.setStrokeWidth(res.getDimension(R.dimen.line_width));
		strokePaint.setColor(res.getColor(R.color.blue));

		fillPaint = new Paint();
		fillPaint.setStyle(Paint.Style.FILL);
		fillPaint.setAntiAlias(true);
		fillPaint.setColor(res.getColor(R.color.white));
	}
}
