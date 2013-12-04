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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author 0a6055 資産管理DBオープン用 ヘルパークラス
 */
public class LaborDbOpenHelper extends SQLiteOpenHelper {

	private static final String TAG = LaborDbOpenHelper.class.getSimpleName();
	@SuppressWarnings("unused")
	private final LaborDbOpenHelper self = this;

	// データベース名の定数
	private static final String DB_NAME = "LABOR_INFO";

	// バージョン
	private static final int VERSION = 1;

	/**
	 * @param context
	 */
	public LaborDbOpenHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite
	 * .SQLiteDatabase) データベースを新規に作成した後に呼ばれる。
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		// 内部にテーブルを作成する。
		db.beginTransaction();

		try {
			// テーブルの生成
			StringBuilder createSql = new StringBuilder();
			createSql.append("create table " + LaborInfo.TABLE_NAME + " (");
			createSql.append(LaborInfo.COLUMN_ID + " integer primary key autoincrement not null,");
			createSql.append(LaborInfo.COLUMN_TIME + " integer,");
			createSql.append(LaborInfo.COLUMN_START + " integer,");
			createSql.append(LaborInfo.COLUMN_END + " integer,");
			createSql.append(LaborInfo.COLUMN_DURATION + " integer,");
			createSql.append(LaborInfo.COLUMN_TYPE + " text,");
			createSql.append(LaborInfo.COLUMN_MEMO + " text");
			createSql.append(")");

			db.execSQL(createSql.toString());

			db.setTransactionSuccessful();
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		} finally {
			db.endTransaction();
		}
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite
	 * .SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
