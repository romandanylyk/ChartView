package com.rd.chartview.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.rd.chartview.view.animation.AnimationManager;
import com.rd.chartview.view.animation.data.AnimationValue;
import com.rd.chartview.view.draw.data.Chart;
import com.rd.chartview.view.draw.DrawManager;

public class ChartManager implements AnimationManager.AnimationListener {

	private DrawManager drawManager;
	private AnimationManager animationManager;
	private AnimationListener listener;

	public interface AnimationListener {

		void onAnimationUpdated();
	}


	public ChartManager(@NonNull Context context, @Nullable AnimationListener listener) {
		this.drawManager = new DrawManager(context);
		this.animationManager = new AnimationManager(drawManager.chart(), this);
		this.listener = listener;
	}

	public Chart chart() {
		return drawManager.chart();
	}

	public DrawManager drawer() {
		return drawManager;
	}

	public void animate() {
		animationManager.animate();
	}

	@Override
	public void onAnimationUpdated(@NonNull AnimationValue value) {
		drawManager.updateValue(value);
		if (listener != null) {
			listener.onAnimationUpdated();
		}
	}
}
