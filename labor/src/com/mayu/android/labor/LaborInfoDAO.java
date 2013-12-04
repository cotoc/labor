/*
 * Copyright © 2011 Infotec Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.mayu.android.labor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

/**
 * @author 0a6055 資産情報用 データアクセスクラス
 */
public class LaborInfoDAO {

	private static final String TAG = LaborInfoDAO.class.getSimpleName();
	@SuppressWarnings("unused")
	private final LaborInfoDAO self = this;

	private LaborDbOpenHelper helper = null;
	private Context mContext;

	private DateUtil dateUtil; 
//	private static final String DATE_PATTERN = "yyyy/MM/dd HH:mm:ss";
	 
	/*
	 *
	 */
	public LaborInfoDAO(Context context) {
		helper = new LaborDbOpenHelper(context);
		mContext = context;
		dateUtil = new DateUtil();
	}

	/*
	 * 資産情報の保存＜IDがNULLならInsert,それ以外ならUpdateで全項目更新＞
	 *
	 * @param AssetInfo 保存対象のデータ
	 *
	 * @return 保存したデータ　Error：null
	 */
	public LaborInfo save(LaborInfo laborInfo) {
		SQLiteDatabase db;
		try {
			db = helper.getWritableDatabase();

		} catch (SQLiteException e) {
			// TODO: handle exception
			Log.w(TAG, e.toString());
			return null;
		}

		LaborInfo result = null;
		try {
			ContentValues values = new ContentValues();
			values.put(LaborInfo.COLUMN_ID, laborInfo.getId());
			values.put(LaborInfo.COLUMN_TIME, setDateNow(laborInfo.getInputDate()));
			values.put(LaborInfo.COLUMN_START, setDateNow(laborInfo.getStartTime()));
			values.put(LaborInfo.COLUMN_END, setDate(laborInfo.getEndTime()));
			values.put(LaborInfo.COLUMN_TYPE, laborInfo.getInputType());
			values.put(LaborInfo.COLUMN_MEMO, laborInfo.getMemo());
			values.put(LaborInfo.COLUMN_DURATION, laborInfo.getDuration());

			Long rowId = laborInfo.getId();

			int updateCount = 0;

			// IDがnullの場合はinsert
			if (rowId == null) {
				rowId = db.insert(LaborInfo.TABLE_NAME, null, values);
				if (rowId < 0) {
					// エラー処理
					Log.w(TAG, "save Insert Error");
					throw new SQLException();
				}
				Log.v(TAG, "save Insert Success!");
			} else {
				updateCount = db.update(LaborInfo.TABLE_NAME, values,
						LaborInfo.COLUMN_ID + "=?",
						new String[] { String.valueOf(rowId) });
				if (updateCount != 1) {
					// エラー処理
					Log.w(TAG,
							"save UPDATE Error : Update ID = "
									+ String.valueOf(rowId)
									+ "| Update Count : "
									+ String.valueOf(updateCount));
					throw new SQLException();
				}
				Log.v(TAG, "save update Success!");
			}
			result = load(rowId);
		} catch (SQLException e) {
			Log.e(TAG, e.toString());
			result = null;
		} finally {
			db.close();
		}
		return result;

	}


	/**
	 * 1レコードの削除
	 *
	 * @param LaborInfo
	 *            削除対象のオブジェクト
	 */
	public void delete(LaborInfo laborInfo) {
		SQLiteDatabase db;
		try {
			db = helper.getWritableDatabase();

		} catch (SQLiteException e) {
			// TODO: handle exception
			Log.w(TAG, e.toString());
			return;
		}

		int deleteCount = 0;
		try {
			deleteCount = db.delete(LaborInfo.TABLE_NAME, LaborInfo.COLUMN_ID
					+ "=?", new String[] { String.valueOf(laborInfo.getId()) });
			if (deleteCount != 1) {
				// エラー処理
				Log.w(TAG,
						"delete Delete Error : Update ID = "
								+ String.valueOf(laborInfo.getId())
								+ "| Delete Count : "
								+ String.valueOf(deleteCount));
				throw new SQLException();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			Log.e(TAG, e.getMessage());
		} finally {
			db.close();
		}
	}

	/**
	 * 全レコードの削除
	 *
	 * @param db
	 */
	private int deleteAll(SQLiteDatabase db) {

		int deleteCount = 0;
		try {
			deleteCount = db.delete(LaborInfo.TABLE_NAME, null, null);
			if (deleteCount < 1) {
				// エラー処理
				Log.w(TAG,
						"deleteAll Delete Error Delete Count : "
								+ String.valueOf(deleteCount));
			}
		} finally {
//			db.close();
		}
		return deleteCount;
	}

	/**
	 * IDで資産情報を読み込む
	 *
	 * @param rowId
	 * @return
	 */
	private LaborInfo load(Long rowId) {
		SQLiteDatabase db;
		try {
			db = helper.getReadableDatabase();

		} catch (SQLiteException e) {
			// TODO: handle exception
			Log.w(TAG, e.toString());
			return null;
		}

		LaborInfo assetInfo = null;
		try {
			Cursor cursor = db.query(LaborInfo.TABLE_NAME, null,
					LaborInfo.COLUMN_ID + "=?",
					new String[] { String.valueOf(rowId) }, null, null, null);
			cursor.moveToFirst();
			assetInfo = getLaborInfo(cursor, null);
		} finally {
			db.close();
		}

		return assetInfo;
	}

	/**
	 * 一覧を取得する
	 *
	 * @return 検索結果
	 */
	public ListLaborInfos list() {
		SQLiteDatabase db;
		try {
			db = helper.getReadableDatabase();

		} catch (SQLiteException e) {
			// TODO: handle exception
			Log.w(TAG, e.toString());
			return null;
		}

		ListLaborInfos laborInfoList;
		try {
			Cursor cursor = db.query(LaborInfo.TABLE_NAME, null, null, null,
					null, null, LaborInfo.COLUMN_START);
			laborInfoList = new ListLaborInfos();
			if( cursor.moveToFirst()) {
				Date befor = null;
				while (!cursor.isAfterLast()) {
					if( cursor.getInt(cursor
							.getColumnIndex(LaborInfo.COLUMN_TYPE)) == LaborInfo.LABOR){
						
						laborInfoList.setStartedFlg(true);
						
						laborInfoList.add(getLaborInfo(cursor, befor));
						befor = new Date(cursor.getLong(cursor
								.getColumnIndex(LaborInfo.COLUMN_START)));
//						befor.setTime(cursor.getLong(cursor
//								.getColumnIndex(LaborInfo.COLUMN_START)));

						laborInfoList.setLastStartedTime(befor);
						
						if( cursor.getLong(( cursor.getColumnIndex(LaborInfo.COLUMN_END))) == 0 ){
							laborInfoList.setSubsidedFlg(false);
						} else {
							laborInfoList.setSubsidedFlg(true);
						}
						
					} else {
						laborInfoList.add(getLaborInfo(cursor));
					}
					cursor.moveToNext();
				}
			}
		} finally {
			db.close();
		}
		return laborInfoList;
	}

    /**
     * 指定カラムの一覧を取得する
     * @param column カラム名:String型
     * @return assetNarrowList 検索結果:ArrayList<String>型
     */
    public ArrayList<String> getNarrowColumnList( String column ) {
        SQLiteDatabase db;
        String[] columns = { column };
        try {
            db = helper.getReadableDatabase();

        } catch (SQLiteException e) {
            // TODO: handle exception
            Log.w(TAG, e.toString());
            return null;
        }
        ArrayList<String> assetNarrowList = new ArrayList<String>();
        try {
            Cursor cursor = db.query(LaborInfo.TABLE_NAME, columns, null, null, column, null, null );
            if( cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    if (cursor.getString( cursor.getColumnIndex( column ) ) != null ){
                        assetNarrowList.add( cursor.getString( cursor.getColumnIndex( column ) ) );
                    } else {
                        assetNarrowList.add( "null" );
                    }
                    cursor.moveToNext();
                }

            }
        } finally {
            db.close();
        }
        return assetNarrowList;
    }

    /**
     * 絞込み実行後の一覧を取得する
     * @param columns カラム名:String型
     * @param narrows 条件:String型
     * @return assetInfoList 検索結果:List<String>型
     */
    public ListLaborInfos getNarrowList( String[] columns , String[] narrows ) {
        SQLiteDatabase db;
        boolean whereFlag = false;
        String buf = "";

        try {
            db = helper.getReadableDatabase();

        } catch (SQLiteException e) {
            // TODO: handle exception
            Log.w(TAG, e.toString());
            return null;
        }

        String sqlstr = "select *" +
                        " from " + LaborInfo.TABLE_NAME;

        if( columns != null){
	        // where句作成
	        for(int i=0;i<=columns.length - 1;i++){
	            if ( narrows[i].equals("－－－") ){
	                //何もしない
	            } else {
	                whereFlag = true;

	                if ( buf.length() >= 1 ){
	                    buf += " and ";
	                }

	                buf += columns[i] + " = \"" + narrows[i] + "\"";

	            }
	        }
        }

        if ( whereFlag == true ){
            sqlstr += " where ";
        }

        sqlstr += buf +" order by " + LaborInfo.COLUMN_ID;

        ListLaborInfos assetInfoList;
        try {
            Cursor cursor = db.rawQuery(sqlstr,null);
            assetInfoList = new ListLaborInfos();
            if( cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    assetInfoList.add(getLaborInfo(cursor));
                    cursor.moveToNext();
                }
            }
        } finally {
            db.close();
        }
        return assetInfoList;
    }

    /**
     * 絞込み実行後の一覧を取得する
     * @param columns カラム名:String型
     * @param narrows 条件:String型
     * @return assetInfoList 検索結果:List<String>型
     */
    public int getNarrowListCount( String[] columns , String[] narrows ) {
        SQLiteDatabase db;
        boolean whereFlag = false;
        String buf = "";

        try {
            db = helper.getReadableDatabase();

        } catch (SQLiteException e) {
            // TODO: handle exception
            Log.w(TAG, e.toString());
            return 0;
        }

        String sqlstr = "select count(*)" +
                        " from " + LaborInfo.TABLE_NAME;

        if( columns != null){
	        // where句作成
	        for(int i=0;i<=columns.length - 1;i++){
	            if ( narrows[i].equals("－－－") ){
	                //何もしない
	            } else {
	                whereFlag = true;

	                if ( buf.length() >= 1 ){
	                    buf += " and ";
	                }

	                if (narrows[i].equals("null") ){
                        buf += columns[i] + " IS NULL";
	                } else {
	                    buf += columns[i] + " = \"" + narrows[i] + "\"";
	                }
	            }
	        }
        }

        if ( whereFlag == true ){
            sqlstr += " where ";
        }

        sqlstr += buf;

        int listCount = 0;
        try {
            Cursor cursor = db.rawQuery(sqlstr,null);
//            assetInfoList = new ArrayList<AssetInfo>();
            if( cursor.moveToFirst()) {
            	listCount = cursor.getInt(0);
//                while (!cursor.isAfterLast()) {
//                    assetInfoList.add(getAssetInfo(cursor));
//                    cursor.moveToNext();
//                }
            }
        } finally {
            db.close();
        }
        return listCount;
    }
    /**
     * 指定ページのデータリストを取得する
     * @param columns：カラム名:String型
     * @param narrows：条件:String型
     * @param page：ページ番号(0 ～ )
     * @param row：1ページのデータ数
     * @return 1ページ分のデータリスト
     */
    public ListLaborInfos getPageDataList( String[] columns , String[] narrows , int page, int row ) {
        SQLiteDatabase db;
        boolean whereFlag = false;
        String buf = "";
        int offset = 0;

        if(page >= 0){
        	offset = page * row ;
        }

        try {
            db = helper.getReadableDatabase();

        } catch (SQLiteException e) {
            // TODO: handle exception
            Log.w(TAG, e.toString());
            return null;
        }

        String sqlstr = "select *" +
                        " from " + LaborInfo.TABLE_NAME;

        if( columns != null){
	        // where句作成
	        for(int i=0;i<=columns.length - 1;i++){
	            if ( narrows[i].equals("－－－") ){
	                //何もしない
	            } else {
	                whereFlag = true;

	                if ( buf.length() >= 1 ){
	                    buf += " and ";
	                }

                    if (narrows[i].equals("null") ){
                        buf += columns[i] + " IS NULL";
                    } else {
                        buf += columns[i] + " = \"" + narrows[i] + "\"";
                    }
	            }
	        }

	        if ( whereFlag == true ){
	            sqlstr += " where ";
	        }
        }

        sqlstr += buf +" order by " + LaborInfo.COLUMN_ID;
        sqlstr += " limit " + Integer.toString(row) + " offset " + Integer.toString(offset);

        Log.v(TAG, "SQL:" + sqlstr);

        ListLaborInfos assetInfoList;
        try {
            Cursor cursor = db.rawQuery(sqlstr,null);
            assetInfoList = new ListLaborInfos();
            if( cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    assetInfoList.add(getLaborInfo(cursor));
                    cursor.moveToNext();
                }
            }
        } finally {
            db.close();
        }
        return assetInfoList;
    }
	/**
	 * カーソルからオブジェクトへの変換
	 *
	 * @param cursor
	 * @return AssetInfo
	 * 			カーソルから読み込んだデータをAssetInfoクラスに編成
	 */
	private LaborInfo getLaborInfo(Cursor cursor) {
		LaborInfo laborInfo = new LaborInfo();

		laborInfo.setId(cursor.getLong(cursor
				.getColumnIndex(LaborInfo.COLUMN_ID)));
		laborInfo.setInputTime(cursor.getString(cursor
				.getColumnIndex(LaborInfo.COLUMN_TIME)));
		laborInfo.setInputType(cursor.getInt(cursor
				.getColumnIndex(LaborInfo.COLUMN_TYPE)));
		laborInfo.setMemo(cursor.getString(cursor
				.getColumnIndex(LaborInfo.COLUMN_MEMO)));
		laborInfo.setInputDate(cursor.getLong(cursor
				.getColumnIndex(LaborInfo.COLUMN_TIME)));
		laborInfo.setStartTime(cursor.getLong(cursor
				.getColumnIndex(LaborInfo.COLUMN_START)));
		laborInfo.setEndTime(cursor.getLong(cursor
				.getColumnIndex(LaborInfo.COLUMN_END)));
//		laborInfo.setStrStartTime(cursor.getString(cursor
//				.getColumnIndex(LaborInfo.COLUMN_START)));
//		laborInfo.setStrEndTime(cursor.getString(cursor
//				.getColumnIndex(LaborInfo.COLUMN_END)));
		laborInfo.setDuration(cursor.getLong(cursor
				.getColumnIndex(LaborInfo.COLUMN_DURATION)));
		
		return laborInfo;
	}
	
	private LaborInfo getLaborInfo(Cursor cursor, Date prevTime) {
		LaborInfo laborInfo = getLaborInfo(cursor);

		if(prevTime != null) {
			DiffCalendar diffCal = new DiffCalendar(prevTime, laborInfo.getStartTime());
			laborInfo.setInterval(diffCal.getDiffTimemSec());
			laborInfo.setStrInterval( diffCal.getDiffDateStr());
		} else {
			laborInfo.setInterval(0);
			laborInfo.setStrInterval("First Time");			
		}
		return laborInfo;
	}

	/**
	 * リストのデータをすべてDBに登録する＜全件Insert＞
	 *
	 * @param AssetInfoList
	 *            ： 登録対象のリスト
	 * @return recCount : 登録したデータの件数 エラーの場合-1
	 */
	public int saveList(List<LaborInfo> laborInfoList) {
		SQLiteDatabase db;
		try {
			db = helper.getWritableDatabase();

		} catch (SQLiteException e) {
			Log.w(TAG, e.toString());
			return -1;
		}

		int recCount;
		db.beginTransaction();
		try {
			// テーブル内のデータを全件削除
			deleteAll(db);

			ContentValues values = new ContentValues();
			for (recCount = 0; recCount < laborInfoList.size(); recCount++) {

				LaborInfo itemInfo = laborInfoList.get(recCount);

				values.clear();

				values.put(LaborInfo.COLUMN_ID, itemInfo.getId());
				values.put(LaborInfo.COLUMN_TIME,
						itemInfo.getInputTime());
				values.put(LaborInfo.COLUMN_TYPE,
						itemInfo.getInputType());
				values.put(LaborInfo.COLUMN_MEMO,
						itemInfo.getMemo());

				long rowId = db.insert(LaborInfo.TABLE_NAME, null, values);
				if (rowId < 0) {
					Log.e(TAG,
							"saveList insert Error　ID:" + String.valueOf(rowId));
					// TODO エラー処理 ↓でいいのか？
					throw new SQLException();
				}
			}
			db.setTransactionSuccessful();

			Log.v(TAG, "saveList insert is succeeded.");

		} catch (SQLException e) {
			e.printStackTrace();
			Log.e(TAG, e.toString());
			recCount = -1;
		} finally {
			db.endTransaction();
			db.close();
		}
		return recCount;
	}

	/**
	 * 登録済みデータを全削除
	 *
	 * @return recCount : 登録したデータの件数 エラーの場合-1
	 */
	public int deleteAll() {
		SQLiteDatabase db;
		try {
			db = helper.getWritableDatabase();

		} catch (SQLiteException e) {
			Log.w(TAG, e.toString());
			return -1;
		}

		int recCount;
		db.beginTransaction();
		try {
			// テーブル内のデータを全件削除
			recCount = deleteAll(db);

			db.setTransactionSuccessful();

			Log.v(TAG, "Delete All is succeeded.");

		} catch (SQLException e) {
			e.printStackTrace();
			Log.e(TAG, e.toString());
			recCount = -1;
		} finally {
			db.endTransaction();
			db.close();
		}
		return recCount;
	}
	
	public LaborInfo loadLastLabor(){
		SQLiteDatabase db;
		try {
			db = helper.getReadableDatabase();

		} catch (SQLiteException e) {
			// TODO: handle exception
			Log.w(TAG, e.toString());
			return null;
		}

		LaborInfo lastlaborInfo = null;
		LaborInfo prevLaborInfo = null;
		try {
			Cursor cursor = db.query(LaborInfo.TABLE_NAME, 
										null,
										LaborInfo.COLUMN_TYPE + "=?",
										new String[] { String.valueOf(LaborInfo.LABOR) }, 
										null, 
										null,
										LaborInfo.COLUMN_START + " DESC", 
										null);
			if( cursor.moveToFirst() ){
				lastlaborInfo = getLaborInfo(cursor, null);
				
				cursor.moveToNext();
				if( !cursor.isAfterLast() ){
					prevLaborInfo = getLaborInfo(cursor, null);
					DiffCalendar diffCal = new DiffCalendar(prevLaborInfo.getStartTime(), lastlaborInfo.getStartTime());
					lastlaborInfo.setInterval(diffCal.getDiffTimemSec());
					lastlaborInfo.setStrInterval( diffCal.getDiffDateStr());
					
				} else {
					lastlaborInfo.setInterval(0);
					lastlaborInfo.setStrInterval("First Time");						
				}
				
			}
			
		} finally {
			db.close();
		}

		return lastlaborInfo;
	}
	
	private long setDateNow(Date settingDate){

		if(settingDate == null) {
			//現在日時を設定
	        Date date = Calendar.getInstance().getTime();  
	        return date.getTime();
		} else {
			return settingDate.getTime();
		}	


	}
	
	private long setDate(Date settingDate){

		if(settingDate == null) { 
	        return 0;
		} else {
			return settingDate.getTime();
		}	


	}
}
