package com.rd.chartview.view.draw.data;

public class InputData {

	private int value;
	private long millis;

	public InputData() {
	}

	public InputData(int value) {
		this.value = value;
	}

	public InputData(int value, int millis) {
		this.value = value;
		this.millis = millis;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public long getMillis() {
		return millis;
	}

	public void setMillis(long millis) {
		this.millis = millis;
	}
}
