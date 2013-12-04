package com.mayu.android.labor;

import java.util.Calendar;
import java.util.Date;

public class DiffCalendar { 
    private long diffTimemSec = 0;
    
    public DiffCalendar(Date dateFrom, Date dateTo) {
		// TODO Auto-generated constructor stub
    	toDiffDate(dateFrom, dateTo);
	}
    
	public void toDiffDate(Date dateFrom, Date dateTo) {
		
        //現在の日時  
        Calendar nowcal = Calendar.getInstance();  
        nowcal.setTime(dateTo); 
        //比較対象日時  
        Calendar dateCal = Calendar.getInstance();  
        dateCal.setTime(dateFrom);  
          
        //long型の差分（ミリ秒）  
        long diffTime = nowcal.getTimeInMillis() - dateCal.getTimeInMillis();
        
        diffTimemSec = diffTime;
	}
	
	//差分をミリ秒で返す
	public long getDiffTimemSec() {
		return diffTimemSec;
	}
	
	//差分を日数で返す
	public long getDiffDays() {
		return diffTimemSec / 1000 / 60 / 60 / 24;
	}
	
	//差分を　日・時・分・秒　の書式で返す
    public String getDiffDateStr() {  
      
        long diffTime = diffTimemSec;
        
        String diffString = null;
        
        //秒  
        long second = diffTime/1000;
        
        long difSecond = second % 60;
        diffString = difSecond + "秒";
        second = second - difSecond;
        if (second < 60) {  
            return diffString;
        }  
          
        //分  
        long minute = second/60;
        long difMinute = minute % 60;
        diffString = difMinute + "分" + diffString;
        minute = minute - difMinute;
        if (minute < 60) {  
            return diffString ;  
        }  
          
        //時  
        long hour = minute/60;
        long difHour = hour % 24;
        diffString = difHour + "時間" + diffString;
        hour = hour - difHour;
        if (hour < 24) {  
            return diffString;
        }  
          
        //日  
        long day = hour/24;
        diffString = day + "日" + diffString;

        return diffString;

    } 	
}
