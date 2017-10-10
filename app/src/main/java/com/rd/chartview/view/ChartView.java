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

	private static final int CHART_PARTS = 5;
	private static final int CHART_PART_VALUE = 10;

	private static final int CIRCLE_RADIUS = 20;

	private static final int VALUE_NONE = -1;
	private static final int MAX_ITEMS_COUNT = 7;

	private List<DrawData> drawDataList;
	private List<Integer> dataList;

	private Paint frameLinePaint;
	private Paint frameInternalPaint;
	private Paint frameTextPaint;

	private Paint linePaint;
	private Paint strokePaint;
	private Paint fillPaint;

	private int padding;
	private int textSize;
	private int titleWidth;

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
		drawFrame(canvas);
		drawLines(canvas);
	}

	private void drawFrame(@NonNull Canvas canvas) {
		drawFrameLines(canvas);
		drawFrameText(canvas);
	}

	@SuppressWarnings("SuspiciousNameCombination")
	private void drawFrameLines(@NonNull Canvas canvas) {
		int height = getHeight();
		int width = getWidth();

		canvas.drawLine(titleWidth, padding, titleWidth, height - padding, frameLinePaint);
		canvas.drawLine(titleWidth, height - padding, width - padding, height - padding, frameLinePaint);
	}

	private void drawFrameText(@NonNull Canvas canvas) {
		int maxValue = Collections.max(dataList);
		int correctedMaxValue = getCorrectedMaxValue(maxValue);
		float value = (float) correctedMaxValue / maxValue;

		int height = getHeight() - (padding * 2);
		int chartPartHeight = (int) ((height * value) / CHART_PARTS);
		int titleValue = 0;

		for (int i = 0; i <= CHART_PARTS; i++) {
			int currHeight = getHeight() - (chartPartHeight * i) - padding;

			if (i > 0) {
				canvas.drawLine(titleWidth, currHeight, getWidth() - padding, currHeight, frameInternalPaint);
			}

			if (i >= CHART_PARTS && maxValue == correctedMaxValue) {
				currHeight = textSize + padding;
			}

			String strTitle = String.valueOf(titleValue);
			canvas.drawText(strTitle, padding, currHeight, frameTextPaint);

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

				canvas.drawLine(startX, startY, stopX, stopY, linePaint);
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
		updateTitleWidth();

		post(new Runnable() {
			@Override
			public void run() {
				createNextDrawData(dataList);
				invalidate();
			}
		});
	}

	private void createNextDrawData(@NonNull List<Integer> dataList) {
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

		for (int i = 0; i < valueList.size(); i++) {
			float value = valueList.get(i);
			DrawData nextDrawData = createNextDrawData(i, value);

			if (i > 0 && !drawDataList.isEmpty()) {
				DrawData previousDrawData = drawDataList.get(drawDataList.size() - 1);
				updatePreviousDrawData(previousDrawData, nextDrawData);
			}

			drawDataList.add(nextDrawData);
		}

		drawDataList.remove(drawDataList.size() - 1);
		return drawDataList;
	}

	@NonNull
	private DrawData createNextDrawData(int index, float value) {
		int startX = getCoordinateX(index);
		int startY = getCoordinateY(value);

		DrawData drawData = new DrawData();
		drawData.setStartX(startX);
		drawData.setStartY(startY);

		return drawData;
	}

	private void updatePreviousDrawData(@NonNull DrawData previousDrawData, @NonNull DrawData nextDrawData) {
		previousDrawData.setStopX(nextDrawData.getStartX());
		previousDrawData.setStopY(nextDrawData.getStartY());
	}

	@SuppressWarnings("UnnecessaryLocalVariable")
	private int getCoordinateX(int index) {
		int width = getWidth() - titleWidth - padding;
		int partWidth = width / (MAX_ITEMS_COUNT - 1);

		int coordinate = titleWidth + (partWidth * index);
		return coordinate;
	}

	@SuppressWarnings("UnnecessaryLocalVariable")
	private int getCoordinateY(float value) {
		int height = getHeight() - padding;
		int coordinate = (int) (height - (height * value));
		if (value >= 1) {
			coordinate += padding;
		}

		return coordinate;
	}

	private void updateTitleWidth() {
		if (titleWidth <= 0) {
			titleWidth = getTitleWidth();
		}
	}

	private int getTitleWidth() {
		if (dataList == null || dataList.isEmpty()) {
			return 0;
		}

		String maxValue = String.valueOf(Collections.max(dataList));
		int titleWidth = (int) frameTextPaint.measureText(maxValue);
		return padding + titleWidth + padding;
	}

	private void init() {
		drawDataList = new ArrayList<>();
		dataList = new ArrayList<>();

		padding = (int) getResources().getDimension(R.dimen.frame_padding);
		textSize = (int) getResources().getDimension(R.dimen.frame_text_size);

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
		frameTextPaint.setTextSize(textSize);
		frameTextPaint.setColor(getContext().getResources().getColor(R.color.gray_400));

		linePaint = new Paint();
		linePaint.setAntiAlias(true);
		linePaint.setStrokeWidth(getResources().getDimension(R.dimen.line_width));
		linePaint.setColor(getContext().getResources().getColor(R.color.blue_500));
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
	}
}
