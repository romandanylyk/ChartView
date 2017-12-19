package com.rd.chartview.view.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import com.rd.chartview.view.draw.data.Chart;
import com.rd.chartview.view.draw.data.DrawData;
import com.rd.chartview.view.draw.data.InputData;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;

public class ValueUtils {

	public static int getCorrectedMaxValue(int maxValue) {
		for (int value = maxValue; value >= Chart.CHART_PART_VALUE; value--) {
			if (isRightValue(value)) {
				return value;
			}
		}

		return maxValue;
	}

	public static int max(@Nullable List<InputData> dataList) {
		int maxValue = 0;

		if (dataList == null || dataList.isEmpty()) {
			return maxValue;
		}

		for (InputData data : dataList) {
			if (data.getValue() > maxValue) {
				maxValue = data.getValue();
			}
		}

		return maxValue;
	}

	private static boolean isRightValue(int value) {
		int valueResidual = value % Chart.CHART_PART_VALUE;
		return valueResidual == 0;
	}

	@NotificationCompat.NotificationVisibility
	public static List<DrawData> getDrawData(@Nullable Chart chart) {
		if (chart == null || chart.getInputData().isEmpty()) {
			return new ArrayList<>();
		}

		List<InputData> dataList = chart.getInputData();
		correctDataListSize(dataList);
		return createDrawDataList(chart, createValueList(dataList));
	}

	private static void correctDataListSize(@NonNull List<InputData> dataList) {
		if (dataList.size() < Chart.MAX_ITEMS_COUNT) {
			addLackingItems(dataList);

		} else if (dataList.size() > Chart.MAX_ITEMS_COUNT) {
			removeExcessItems(dataList);
		}
	}

	private static void addLackingItems(@NonNull List<InputData> dataList) {
		for (int i = dataList.size(); i < Chart.MAX_ITEMS_COUNT; i++) {
			long millis = dataList.get(0).getMillis() - TimeUnit.DAYS.toMillis(1);
			if (millis < 0) {
				millis = 0;
			}

			dataList.add(0, new InputData(0, millis));
		}
	}

	private static void removeExcessItems(@NonNull List<InputData> dataList) {
		for (ListIterator<InputData> iterator = dataList.listIterator(); iterator.hasNext(); ) {
			if (iterator.nextIndex() > Chart.MAX_ITEMS_COUNT) {
				iterator.remove();
				return;
			}
			iterator.next();
		}
	}

	private static List<Float> createValueList(@NonNull List<InputData> dataList) {
		List<Float> valueList = new ArrayList<>();
		int topValue = ValueUtils.max(dataList);

		for (InputData data : dataList) {
			float value = (float) data.getValue() / topValue;
			valueList.add(value);
		}

		return valueList;
	}

	@NonNull
	private static List<DrawData> createDrawDataList(@NonNull Chart chart, @NonNull List<Float> valueList) {
		List<DrawData> drawDataList = new ArrayList<>();

		for (int i = 0; i < valueList.size() - 1; i++) {
			DrawData drawData = createDrawData(chart, valueList, i);
			drawDataList.add(drawData);
		}

		return drawDataList;
	}

	@NonNull
	private static DrawData createDrawData(@NonNull Chart chart, @NonNull List<Float> valueList, int position) {
		DrawData drawData = new DrawData();
		if (position > valueList.size() - 1) {
			return drawData;
		}

		float value = valueList.get(position);
		int startX = getCoordinateX(chart, position);
		int startY = getCoordinateY(chart, value);

		drawData.setStartX(startX);
		drawData.setStartY(startY);

		int nextPosition = position + 1;
		if (nextPosition < valueList.size()) {
			float nextValue = valueList.get(nextPosition);
			int stopX = getCoordinateX(chart, nextPosition);
			int stopY = getCoordinateY(chart, nextValue);

			drawData.setStopX(stopX);
			drawData.setStopY(stopY);
		}

		return drawData;
	}

	@SuppressWarnings("UnnecessaryLocalVariable")
	private static int getCoordinateX(@NonNull Chart chart, int index) {
		int width = chart.getWidth();
		int titleWidth = chart.getTitleWidth();

		int widthCorrected = width - titleWidth;
		int partWidth = widthCorrected / (Chart.MAX_ITEMS_COUNT - 1);
		int coordinate = titleWidth + (partWidth * index);

		if (coordinate < 0) {
			coordinate = 0;

		} else if (coordinate > width) {
			coordinate = width;
		}

		return coordinate;
	}

	@SuppressWarnings("UnnecessaryLocalVariable")
	private static int getCoordinateY(@NonNull Chart chart, float value) {
		int height = chart.getHeight() - chart.getPadding() - chart.getTextSize();
		int heightOffset = chart.getHeightOffset();

		int heightCorrected = height - heightOffset;
		int coordinate = (int) (heightCorrected - (heightCorrected * value));

		if (coordinate < 0) {
			coordinate = 0;

		} else if (coordinate > heightCorrected) {
			coordinate = heightCorrected;
		}

		coordinate += heightOffset;
		return coordinate;
	}
}
