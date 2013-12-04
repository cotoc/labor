package com.mayu.android.labor;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

public class LaporTabActivity extends TabActivity implements
		TabHost.TabContentFactory {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final TabHost tabhost = getTabHost();
		createTabs(tabhost);
	}

	@Override
	public View createTabContent(String tag) {
		final TextView tv = new TextView(this);
		tv.setText("Content for tab with tag " + tag);
		return tv;
	}
	
    private static final int MENU_ID_PREF = (Menu.FIRST + 1);
    private static final int MENU_ID_PREF_CLEAR = (Menu.FIRST + 2);
    private static final int MENU_ID_CLEAR = (Menu.FIRST + 3);

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
        // メニューアイテムを追加します
        menu.add(Menu.NONE, MENU_ID_PREF, Menu.NONE, "Setting");
        menu.add(Menu.NONE, MENU_ID_PREF_CLEAR, Menu.NONE, "Pref Clear");
        menu.add(Menu.NONE, MENU_ID_CLEAR, Menu.NONE, "Data Clear");

		return super.onCreateOptionsMenu(menu);
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean ret = true;
        switch (item.getItemId()) {
        default:
            ret = super.onOptionsItemSelected(item);
            break;
            
        case MENU_ID_PREF:
			Intent intent = new Intent(this, LaborAppPreferences.class);
			startActivityForResult(intent, 0);
            ret = true;
            break;
            
        case MENU_ID_PREF_CLEAR:
        	clearPrefData();
            ret = true;
            break;
            
        case MENU_ID_CLEAR:
        	clearData();
            ret = true;
            break;
        }
        return ret;

    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	// TODO Auto-generated method stub
    	super.onActivityResult(requestCode, resultCode, data);
    	if(requestCode == 0){
    		resetTabs();
    	}
    }
    
    private void createTabs(TabHost tabhost){
//		final TabHost tabhost = getTabHost();

		// アクティビティをタブにホストする
		tabhost.addTab(tabhost
				.newTabSpec("tab1")
				.setIndicator(
						"記録",
						getResources().getDrawable(
								android.R.drawable.ic_menu_edit))
				.setContent(
						new Intent(this, LaborpainsActivity.class)
								.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));

		// アクティビティをタブにホストする
		tabhost.addTab(tabhost
				.newTabSpec("tab2")
				.setIndicator(
						"連絡",
						getResources().getDrawable(
								android.R.drawable.ic_menu_call))
				.setContent(
						new Intent(this, ContactActivity.class)
								.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
		
		// アクティビティをタブにホストする
		tabhost.addTab(tabhost
				.newTabSpec("tab3")
				.setIndicator(
						"グラフ",
						getResources().getDrawable(
								R.drawable.chart))
				.setContent(
						new Intent(this, ChartActivity.class)
								.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));

		// アクティビティをタブにホストする
		tabhost.addTab(tabhost
				.newTabSpec("tab4")
				.setIndicator(
						"確認",
						getResources().getDrawable(
								android.R.drawable.ic_menu_agenda))
				.setContent(
						new Intent(this, LaporPassageListActivity.class)
								.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));

    }
    
    // データ削除
    private void clearData() {
		LaborInfoDAO dao = new LaborInfoDAO(getApplicationContext());
		dao.deleteAll();

		resetTabs();
	}
    
    // 設定データ削除
    private void clearPrefData() {
    	SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    	SharedPreferences.Editor editor = sharedPreferences.edit();
    	editor.clear();
    	editor.commit();
		resetTabs();
	}
    
    private void resetTabs(){
		final TabHost tabhost = getTabHost();
		int index = tabhost.getCurrentTab();
		tabhost.setCurrentTab(0);
		tabhost.clearAllTabs();
		// アクティビティをタブにホストする
		createTabs(tabhost);
		tabhost.setCurrentTab(index);	
    }
    
    public interface OnClearData{
    	public void onClearData();
    }
}
