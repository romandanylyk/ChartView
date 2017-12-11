package com.rd.chartview.view.draw;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import com.rd.chartview.view.animation.data.AnimationValue;
import com.rd.chartview.view.draw.data.Chart;

public class DrawManager {

	private DrawController controller;
	private Chart chart;

	public DrawManager(@NonNull Context context) {
		chart = new Chart();
		controller = new DrawController(context, chart);
	}

	public Chart chart() {
		return chart;
	}

	public void updateTitleWidth() {
		controller.updateTitleWidth();
	}

	public void draw(@NonNull Canvas canvas) {
		controller.draw(canvas);
	}

	public void updateValue(@NonNull AnimationValue value) {
		controller.updateValue(value);
	}
}
