package com.rd.chartview.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import com.rd.chartview.R;
import com.rd.chartview.view.data.DrawData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

public class ChartView extends View {

	private static final int FRAME_LINE_WIDTH = 1;
	private static final int LINE_WIDTH = 10;

	private static final int CHART_PARTS = 5;
	private static final int CHART_PART_VALUE = 10;

	private static final int CIRCLE_RADIUS = 20;

	private static final int VALUE_NONE = -1;
	private static final int MAX_ITEMS_COUNT = 6;

	private List<DrawData> drawDataList;
	private List<Integer> dataList;

	private Paint frameLinePaint;
	private Paint frameInternalPaint;
	private Paint frameTextPaint;

	private Paint linePaint;
	private Paint strokePaint;
	private Paint fillPaint;

	private int padding;
	private int maxTitleWidth;

	public ChartView(Context context) {
		super(context);
		init();
	}

	public ChartView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public ChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = View.MeasureSpec.getSize(heightMeasureSpec) / 2;
		setMeasuredDimension(widthSize, heightSize);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		updateTitleWidth();
		drawFrame(canvas);
	}

	private void updateTitleWidth() {
		if (maxTitleWidth <= 0) {
			maxTitleWidth = getTitleWidth();
		}
	}

	private int getTitleWidth() {
		if (dataList == null || dataList.isEmpty()) {
			return 0;
		}

		String maxValue = String.valueOf(Collections.max(dataList));
		int titleWidth = (int) frameTextPaint.measureText(maxValue);
		return padding + titleWidth;
	}


	private void drawFrame(@NonNull Canvas canvas) {
		drawFrameLines(canvas);
		drawFrameText(canvas);
	}

	@SuppressWarnings("SuspiciousNameCombination")
	private void drawFrameLines(@NonNull Canvas canvas) {
		int height = getHeight();
		int width = getWidth();
		int leftPadding = padding + maxTitleWidth;

		canvas.drawLine(leftPadding, padding, leftPadding, height - padding, frameLinePaint);
		canvas.drawLine(leftPadding, height - padding, width - padding, height - padding, frameLinePaint);
	}

	private void drawFrameText(@NonNull Canvas canvas) {
		int maxValue = Collections.max(dataList);
		int correctedMaxValue = getCorrectedMaxValue(maxValue);
		float value = (float) correctedMaxValue / maxValue;

		int height = getHeight() - (padding * 2);
		int leftPadding = padding + maxTitleWidth;

		int chartPartHeight = (int) ((height * value) / CHART_PARTS);
		int titleValue = 0;

		for (int i = 0; i <= CHART_PARTS; i++) {
			int currHeight = getHeight() - (chartPartHeight * i) - padding;

			String strTitle = String.valueOf(titleValue);
			canvas.drawText(strTitle, padding, currHeight, frameTextPaint);

			if (i > 0) {
				canvas.drawLine(leftPadding, currHeight, getWidth() - padding, currHeight, frameInternalPaint);
			}

			titleValue += correctedMaxValue / CHART_PARTS;
		}
	}

	private int getCorrectedMaxValue(int maxValue) {
		for (int value = maxValue; value >= CHART_PART_VALUE; value--) {
			if (isRightValue(value)) {
				return value;
			}
		}

		return maxValue;
	}

	private boolean isRightValue(int value) {
		int valueResidual = value % CHART_PART_VALUE;
		return valueResidual == 0;
	}

	private void drawLines(@NonNull Canvas canvas) {
		for (DrawData drawData : drawDataList) {
			if (drawData != null) {
				int startX = drawData.getStartX();
				int startY = drawData.getStartY();

				int stopX = drawData.getStopX();
				int stopY = drawData.getStopY();

				canvas.drawLine(startX, startY, stopX, stopY, frameLinePaint);
			}
		}
	}

	private void drawCircles(@NonNull Canvas canvas) {
		for (DrawData drawData : drawDataList) {
			if (drawData != null) {
				canvas.drawCircle(drawData.getStartX(), drawData.getStartY(), CIRCLE_RADIUS, strokePaint);
				canvas.drawCircle(drawData.getStartX(), drawData.getStartY(), CIRCLE_RADIUS - 5, fillPaint);
			}
		}
	}

	public void setData(@Nullable final List<Integer> dataList) {
		if (dataList == null || dataList.isEmpty()) {
			return;
		}

		this.dataList.clear();
		this.dataList.addAll(dataList);

		post(new Runnable() {
			@Override
			public void run() {
				createDrawData(dataList);
				invalidate();
			}
		});
	}

	private void createDrawData(@NonNull List<Integer> dataList) {
		correctDataListSize(dataList);

		List<Float> valueList = createValueList(dataList);
		List<DrawData> drawDataList = createDrawDataList(valueList);

		this.drawDataList.clear();
		this.drawDataList.addAll(drawDataList);
	}

	private void correctDataListSize(@NonNull List<Integer> dataList) {
		if (dataList.size() < MAX_ITEMS_COUNT) {
			addLackingItems(dataList);

		} else if (dataList.size() > MAX_ITEMS_COUNT) {
			removeExcessItems(dataList);
		}
	}

	private void addLackingItems(@NonNull List<Integer> dataList) {
		for (int i = dataList.size(); i <= MAX_ITEMS_COUNT; i++) {
			dataList.add(0, 0);
		}
	}

	private void removeExcessItems(@NonNull List<Integer> dataList) {
		for (ListIterator<Integer> iterator = dataList.listIterator(); iterator.hasNext(); ) {
			if (iterator.nextIndex() > MAX_ITEMS_COUNT) {
				iterator.remove();
				return;
			}
			iterator.next();
		}
	}

	private List<Float> createValueList(@NonNull List<Integer> dataList) {
		List<Float> valueList = new ArrayList<>();
		int topValue = Collections.max(dataList);

		for (Integer data : dataList) {
			float value = (float) data / topValue;
			valueList.add(value);
		}

		return valueList;
	}

	@NonNull
	private List<DrawData> createDrawDataList(@NonNull List<Float> valueList) {
		List<DrawData> drawDataList = new ArrayList<>();
		double previousValue = 0;

		for (int i = 0; i < valueList.size(); i++) {
			double nextValue = valueList.get(i);
			DrawData drawData = createDrawData(previousValue, nextValue, i);

			drawDataList.add(drawData);
			previousValue = nextValue;
		}

		return drawDataList;
	}

	@Nullable
	private DrawData createDrawData(double previousValue, double nextValue, int index) {
		int width = (getWidth() / MAX_ITEMS_COUNT);
		int height = getHeight() - (CIRCLE_RADIUS * 2);

		int startX = index > 0 ? (index - 1) * width : 0;
		int startY = previousValue > 0 ? (int) (height - (height * previousValue)) : height;

		int stopX = index >= 0 ? index * width : 0;
		int stopY = nextValue > 0 ? (int) (height - (height * nextValue)) : height;

		DrawData drawData = new DrawData();
		drawData.setStartX(startX);
		drawData.setStartY(startY);
		drawData.setStopX(stopX);
		drawData.setStopY(stopY);

		return drawData;
	}

	private void init() {
		drawDataList = new ArrayList<>();
		dataList = new ArrayList<>();

		padding = (int) getResources().getDimension(R.dimen.frame_padding);

		frameLinePaint = new Paint();
		frameLinePaint.setAntiAlias(true);
		frameLinePaint.setStrokeWidth(getResources().getDimension(R.dimen.frame_line_width));
		frameLinePaint.setColor(getContext().getResources().getColor(R.color.gray_400));

		frameInternalPaint = new Paint();
		frameInternalPaint.setAntiAlias(true);
		frameInternalPaint.setStrokeWidth(getResources().getDimension(R.dimen.frame_line_width));
		frameInternalPaint.setColor(getContext().getResources().getColor(R.color.gray_200));

		frameTextPaint = new Paint();
		frameTextPaint.setAntiAlias(true);
		frameTextPaint.setTextSize(getResources().getDimension(R.dimen.frame_text_size));
		frameTextPaint.setColor(getContext().getResources().getColor(R.color.gray_400));

//		linePaint = new Paint();
//		linePaint.setAntiAlias(true);
//		linePaint.setStrokeWidth(LINE_WIDTH);
//		linePaint.setColor(getContext().getResources().getColor(R.color.blue));
//
//		strokePaint = new Paint();
//		strokePaint.setStyle(Paint.Style.STROKE);
//		strokePaint.setAntiAlias(true);
//		strokePaint.setColor(getContext().getResources().getColor(R.color.blue));
//		strokePaint.setStrokeWidth(LINE_WIDTH);
//
//		fillPaint = new Paint();
//		fillPaint.setStyle(Paint.Style.FILL);
//		fillPaint.setAntiAlias(true);
//		fillPaint.setColor(getContext().getResources().getColor(R.color.white));
//

	}
}
