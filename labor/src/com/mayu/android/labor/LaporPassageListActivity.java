package com.mayu.android.labor;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ListView;

public class LaporPassageListActivity extends ListActivity {

	private LaborAdapter mLaborAdapter = null;
	private ListLaborInfos mLaborInfos = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.list_passage);

		loadList();
		mLaborAdapter = new LaborAdapter(getApplicationContext(), 0, mLaborInfos);
		setListAdapter(mLaborAdapter);

	}
	private void loadList() {
		if( mLaborInfos != null) mLaborInfos.clear();
		
		LaborInfoDAO dao = new LaborInfoDAO(getApplicationContext());

		mLaborInfos = dao.list();

	}
}
