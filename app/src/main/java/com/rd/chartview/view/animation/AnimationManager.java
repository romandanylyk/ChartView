package com.rd.chartview.view.animation;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.animation.AccelerateDecelerateInterpolator;
import com.rd.chartview.view.animation.data.AnimationValue;
import com.rd.chartview.view.draw.data.Chart;
import com.rd.chartview.view.draw.data.DrawData;

import java.util.ArrayList;
import java.util.List;

public class AnimationManager {

	public static final String PROPERTY_X = "PROPERTY_X";
	public static final String PROPERTY_Y = "PROPERTY_Y";
	public static final String PROPERTY_ALPHA = "PROPERTY_ALPHA";

	public static final int VALUE_NONE = -1;
	public static final int ALPHA_START = 0;
	public static final int ALPHA_END = 255;
	private static final int ANIMATION_DURATION = 250;

	private Chart chart;
	private AnimatorSet animatorSet;
	private AnimationListener listener;
	private AnimationValue lastValue;

	public interface AnimationListener {

		void onAnimationUpdated(@NonNull AnimationValue value);
	}

	public AnimationManager(@NonNull Chart chart, @Nullable AnimationListener listener) {
		this.chart = chart;
		this.listener = listener;
		this.animatorSet = new AnimatorSet();
	}

	public void animate() {
		this.animatorSet.playSequentially(createAnimatorList());
		animatorSet.start();
	}

	private List<Animator> createAnimatorList() {
		List<DrawData> dataList = chart.getDrawData();
		List<Animator> animatorList = new ArrayList<>();

		for (DrawData drawData : dataList) {
			animatorList.add(createAnimator(drawData));
		}
		return animatorList;
	}

	private ValueAnimator createAnimator(@NonNull DrawData drawData) {
		PropertyValuesHolder propertyX = PropertyValuesHolder.ofInt(PROPERTY_X, drawData.getStartX(), drawData.getStopX());
		PropertyValuesHolder propertyY = PropertyValuesHolder.ofInt(PROPERTY_Y, drawData.getStartY(), drawData.getStopY());
		PropertyValuesHolder propertyAlpha = PropertyValuesHolder.ofInt(PROPERTY_ALPHA, ALPHA_START, ALPHA_END);

		ValueAnimator animator = new ValueAnimator();
		animator.setValues(propertyX, propertyY, propertyAlpha);
		animator.setDuration(ANIMATION_DURATION);
		animator.setInterpolator(new AccelerateDecelerateInterpolator());

		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				AnimationManager.this.onAnimationUpdate(valueAnimator);
			}
		});

		return animator;
	}

	private void onAnimationUpdate(@Nullable ValueAnimator valueAnimator) {
		if (valueAnimator == null || listener == null) {
			return;
		}

		int x = (int) valueAnimator.getAnimatedValue(PROPERTY_X);
		int y = (int) valueAnimator.getAnimatedValue(PROPERTY_Y);
		int alpha = (int) valueAnimator.getAnimatedValue(PROPERTY_ALPHA);
		int runningAnimationPosition = getRunningAnimationPosition();

		AnimationValue value = new AnimationValue();
		value.setX(x);
		value.setY(y);
		value.setAlpha(adjustAlpha(runningAnimationPosition, alpha));
		value.setRunningAnimationPosition(runningAnimationPosition);

		listener.onAnimationUpdated(value);
		lastValue = value;
	}

	private int getRunningAnimationPosition() {
		ArrayList<Animator> childAnimations = animatorSet.getChildAnimations();
		for (int i = 0; i < childAnimations.size(); i++) {
			Animator animator = childAnimations.get(i);
			if (animator.isRunning()) {
				return i;
			}
		}

		return VALUE_NONE;
	}

	private int adjustAlpha(int runningPos, int alpha) {
		if (lastValue == null) {
			return alpha;
		}

		boolean isPositionIncreased = runningPos > lastValue.getRunningAnimationPosition();
		boolean isAlphaIncreased = alpha > lastValue.getAlpha();

		if (!isPositionIncreased && !isAlphaIncreased) {
			return lastValue.getAlpha();
		} else {
			return alpha;
		}
	}
}
