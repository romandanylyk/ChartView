package com.rd.chartview;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import com.rd.chartview.view.ChartView;
import com.rd.chartview.view.draw.data.InputData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class HomeActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_home);
		initViews();
	}

	private void initViews() {
		ChartView chartView = findViewById(R.id.charView);
		List<InputData> dataList = createChartData();
		chartView.setData(dataList);
	}

	@NonNull
	private List<InputData> createChartData() {
		List<InputData> dataList = new ArrayList<>();
		dataList.add(new InputData(10));
		dataList.add(new InputData(25));
		dataList.add(new InputData(20));
		dataList.add(new InputData(30));
		dataList.add(new InputData(20));
		dataList.add(new InputData(50));
		dataList.add(new InputData(40));

		long currMillis = System.currentTimeMillis();
		currMillis -= currMillis % TimeUnit.DAYS.toMillis(1);

		for (int i = 0; i < dataList.size(); i++) {
			long position = dataList.size() - 1 - i;
			long offsetMillis = TimeUnit.DAYS.toMillis(position);

			long millis = currMillis - offsetMillis;
			dataList.get(i).setMillis(millis);
		}

		return dataList;
	}
}
