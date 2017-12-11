package com.rd.chartview.view.utils;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class DateUtils {

	public static String format(long millis){
		SimpleDateFormat format = new SimpleDateFormat("dd", Locale.getDefault());
		return format.format(millis);
	}
}
