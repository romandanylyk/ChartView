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
	private static final int MAX_ITEMS_COUNT = 7;
	private static final int CHART_PART_VALUE = 10;
	private static final int TEXT_SIZE_OFFSET = 10;

	private List<DrawData> drawDataList;
	private List<Integer> dataList;

	private Paint frameLinePaint;
	private Paint frameInternalPaint;
	private Paint frameTextPaint;

	private Paint linePaint;
	private Paint strokePaint;
	private Paint fillPaint;

	private int padding;
	private int titleWidth;
	private float textSize;
	private float heightOffset;

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
		drawCircles(canvas);
	}

	private void drawFrame(@NonNull Canvas canvas) {
		drawFrameText(canvas);
		drawFrameLines(canvas);
	}

	@SuppressWarnings("SuspiciousNameCombination")
	private void drawFrameLines(@NonNull Canvas canvas) {
		float height = getHeight();
		float width = getWidth();

		canvas.drawLine(titleWidth, heightOffset, titleWidth, height, frameLinePaint);
		canvas.drawLine(titleWidth, height, width, height, frameLinePaint);
	}

	private void drawFrameText(@NonNull Canvas canvas) {
		int maxValue = Collections.max(dataList);
		int correctedMaxValue = getCorrectedMaxValue(maxValue);
		float value = (float) correctedMaxValue / maxValue;

		float width = getWidth();
		float height = getHeight();
		float chartPartHeight = ((height - heightOffset) * value) / CHART_PARTS;

		float currHeight = height;
		int currTitle = 0;

		for (int i = 0; i <= CHART_PARTS; i++) {
			float titleY = currHeight;

			if (i <= 0) {
				titleY = height;

			} else if (textSize + heightOffset > currHeight) {
				titleY = currHeight + textSize - TEXT_SIZE_OFFSET;
			}

			if (i > 0) {
				canvas.drawLine(titleWidth, currHeight, width, currHeight, frameInternalPaint);
			}

			String strTitle = String.valueOf(currTitle);
			canvas.drawText(strTitle, padding, titleY, frameTextPaint);

			currHeight -= chartPartHeight;
			currTitle += correctedMaxValue / CHART_PARTS;
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
		float radius = getResources().getDimension(R.dimen.radius);
		float inerRadius = getResources().getDimension(R.dimen.iner_radius);

		for (int i = 1; i < drawDataList.size(); i++) {
			DrawData drawData = drawDataList.get(i);
			if (drawData != null) {
				canvas.drawCircle(drawData.getStartX(), drawData.getStartY(), radius, strokePaint);
				canvas.drawCircle(drawData.getStartX(), drawData.getStartY(), inerRadius, fillPaint);
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
		int width = getWidth() - titleWidth;
		int partWidth = width / (MAX_ITEMS_COUNT - 1);
		int coordinate = titleWidth + (partWidth * index);

		if (coordinate < 0) {
			coordinate = 0;

		} else if (coordinate > getWidth()) {
			coordinate = getWidth();
		}

		return coordinate;
	}

	@SuppressWarnings("UnnecessaryLocalVariable")
	private int getCoordinateY(float value) {
		int height = (int) (getHeight() - heightOffset);
		int coordinate = (int) (height - (height * value));

		if (coordinate < 0) {
			coordinate = 0;

		} else if (coordinate > height) {
			coordinate = height;
		}

		coordinate += heightOffset;
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
		heightOffset = getResources().getDimension(R.dimen.radius) + getResources().getDimension(R.dimen.line_width);
		drawDataList = new ArrayList<>();
		dataList = new ArrayList<>();

		padding = (int) getResources().getDimension(R.dimen.frame_padding);
		textSize = getResources().getDimension(R.dimen.frame_text_size);

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
		linePaint.setColor(getContext().getResources().getColor(R.color.blue));

		strokePaint = new Paint();
		strokePaint.setStyle(Paint.Style.STROKE);
		strokePaint.setAntiAlias(true);
		strokePaint.setStrokeWidth(getResources().getDimension(R.dimen.line_width));
		strokePaint.setColor(getContext().getResources().getColor(R.color.blue));

		fillPaint = new Paint();
		fillPaint.setStyle(Paint.Style.FILL);
		fillPaint.setAntiAlias(true);
		fillPaint.setColor(getContext().getResources().getColor(R.color.white));
	}
}
