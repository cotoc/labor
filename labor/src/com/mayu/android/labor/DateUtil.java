package com.mayu.android.labor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

	private static final String datePattern = "yyyy/MM/dd HH:mm:ss";
	
	public DateUtil() {
		// TODO Auto-generated constructor stub
	}
	
    //Date日付型をString文字列型へ変換  
    public String _date2string(Date date) {
    	if( date == null) return null;
    	
        SimpleDateFormat sdf = new SimpleDateFormat(datePattern);  
        return sdf.format(date);  
    }  
      
    //String文字列型をDate日付型へ変換  
    public Date _string2date(String value) {
    	if(value == null) return null;
 
        SimpleDateFormat sdf = new SimpleDateFormat(datePattern);  
        try {  
            return sdf.parse(value);  
        } catch (ParseException e) {  
            return null;  
        }  
    }

	public String getDatePattern() {
		return datePattern;
	}
	
	public String getNowTimeString(){
		Date date = Calendar.getInstance().getTime();
		String strDate = _date2string(date);
		return strDate;
	}
}
