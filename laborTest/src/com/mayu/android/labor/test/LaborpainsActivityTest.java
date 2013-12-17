package com.mayu.android.labor.test;

import com.mayu.android.labor.LaborpainsActivity;

import android.test.ActivityInstrumentationTestCase2;

public class LaborpainsActivityTest extends
		ActivityInstrumentationTestCase2<LaborpainsActivity> {

	private LaborpainsActivity mActivity;
	
	public LaborpainsActivityTest() {
		super(LaborpainsActivity.class);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		
		mActivity = getActivity();
	}

	public void testInitialDisplay(){
		assertEquals(1, 1);
	}
	
	
}
