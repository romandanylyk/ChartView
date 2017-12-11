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
		if (chart == null) {
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
		for (int i = dataList.size(); i <= Chart.MAX_ITEMS_COUNT; i++) {
			dataList.add(0, new InputData());
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

		for (int i = 0; i < valueList.size(); i++) {
			float value = valueList.get(i);
			DrawData nextDrawData = updateDrawData(chart, i, value);

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
	private static DrawData updateDrawData(@NonNull Chart chart, int index, float value) {
		int startX = getCoordinateX(chart, index);
		int startY = getCoordinateY(chart, value);

		DrawData drawData = new DrawData();
		drawData.setStartX(startX);
		drawData.setStartY(startY);

		return drawData;
	}

	private static void updatePreviousDrawData(@NonNull DrawData previousDrawData, @NonNull DrawData nextDrawData) {
		previousDrawData.setStopX(nextDrawData.getStartX());
		previousDrawData.setStopY(nextDrawData.getStartY());
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
