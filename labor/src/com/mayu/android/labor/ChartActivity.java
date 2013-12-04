package com.mayu.android.labor;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.widget.LinearLayout;

public class ChartActivity extends Activity {

	protected GraphicalView mChartView;
	private ListLaborInfos mLaborInfos = null;

	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.graph);

		LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
		mChartView = ChartFactory.getLineChartView(getApplicationContext(),
				getChartDataset(), getRenderer());
		layout.addView(mChartView);
	}

	private void loadList() {
		if( mLaborInfos != null) mLaborInfos.clear();
		
		LaborInfoDAO dao = new LaborInfoDAO(getApplicationContext());

		mLaborInfos = dao.list();

	}
	
	public XYMultipleSeriesDataset getChartDataset() {
		
		loadList();
		

		
		XYMultipleSeriesDataset myData = new XYMultipleSeriesDataset();
		XYSeries dataSeries = new XYSeries("data");
		if(mLaborInfos != null) {
			for( int i = 0; i < mLaborInfos.size(); i ++){
				// データを追加していく
				dataSeries.add(i + 1, mLaborInfos.get(i).getInterval()/(1000 * 60));
			}
		}

		myData.addSeries(dataSeries);
		return myData;
	}

	public XYMultipleSeriesRenderer getRenderer() {
		XYSeriesRenderer renderer = new XYSeriesRenderer();

		// 棒グラフの色
		renderer.setColor(Color.parseColor("#158aea"));

		XYMultipleSeriesRenderer myRenderer = new XYMultipleSeriesRenderer();
		myRenderer.addSeriesRenderer(renderer);

		// XY（初期表示の？）最大最小値
		myRenderer.setXAxisMin(0);
		myRenderer.setXAxisMax(10);
		myRenderer.setYAxisMin(0);
		myRenderer.setYAxisMax(120);
		
		// グリッド表示
		myRenderer.setShowGrid(true);
		// グリッド色
		myRenderer.setGridColor(Color.parseColor("#c9c9c9"));

		// スクロール許可(X,Y)
		myRenderer.setPanEnabled(true, false);
		// スクロール幅（X最少, X最大, Y最少, Y最大）
		myRenderer.setPanLimits(new double[] { 0, 31.5, 0, 0 });

		// 凡例表示
		myRenderer.setShowLegend(false);

		// ラベル表示
		myRenderer.setXLabels(10);
		myRenderer.setYLabels(20);
		myRenderer.setLabelsTextSize(20);
		myRenderer.setYLabelsAlign(Align.RIGHT);

		// XY軸表示
		myRenderer.setShowAxes(false);
		// バー間の間隔
		myRenderer.setBarSpacing(0.5);
		// ズーム許可
		myRenderer.setZoomEnabled(false, false);
		// 余白
		int[] margin = { 20, 50, 50, 30 };
		myRenderer.setMargins(margin);
		// 余白背景色
		myRenderer.setMarginsColor(Color.parseColor("#FFFFFF"));

		return myRenderer;
	}

}
