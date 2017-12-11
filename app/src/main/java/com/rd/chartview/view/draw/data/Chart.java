package com.rd.chartview.view.draw.data;

import java.util.ArrayList;
import java.util.List;

public class Chart {

	public static final int CHART_PARTS = 5;
	public static final int MAX_ITEMS_COUNT = 7;
	public static final int CHART_PART_VALUE = 10;
	public static final int TEXT_SIZE_OFFSET = 10;

	private int width;
	private int height;

	private int padding;
	private int titleWidth;
	private int textSize;
	private int heightOffset;

	private int radius;
	private int inerRadius;

	private List<Integer> valueList = new ArrayList<>();
	private List<DrawData> drawData = new ArrayList<>();

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getPadding() {
		return padding;
	}

	public void setPadding(int padding) {
		this.padding = padding;
	}

	public int getTitleWidth() {
		return titleWidth;
	}

	public void setTitleWidth(int titleWidth) {
		this.titleWidth = titleWidth;
	}

	public int getTextSize() {
		return textSize;
	}

	public void setTextSize(int textSize) {
		this.textSize = textSize;
	}

	public int getHeightOffset() {
		return heightOffset;
	}

	public void setHeightOffset(int heightOffset) {
		this.heightOffset = heightOffset;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public int getInerRadius() {
		return inerRadius;
	}

	public void setInerRadius(int inerRadius) {
		this.inerRadius = inerRadius;
	}

	public List<Integer> getValueList() {
		return valueList;
	}

	public void setValueList(List<Integer> valueList) {
		this.valueList = valueList;
	}

	public List<DrawData> getDrawData() {
		return drawData;
	}

	public void setDrawData(List<DrawData> drawData) {
		this.drawData = drawData;
	}
}
