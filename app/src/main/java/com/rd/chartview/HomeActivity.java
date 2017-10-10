package com.rd.chartview;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.rd.chartview.view.ChartView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_home);
		initViews();
	}

	private void initViews() {
		ChartView chartView = (ChartView) findViewById(R.id.charView);
		List<Integer> dataList = createChartData();
		chartView.setData(dataList);
	}

	@NonNull
	private List<Integer> createChartData() {
		List<Integer> chartDataList = new ArrayList<>();
		chartDataList.add(0);
		chartDataList.add(50);
		chartDataList.add(25);
		chartDataList.add(70);
		chartDataList.add(30);
		chartDataList.add(90);
		chartDataList.add(0);

		return chartDataList;
	}

}
