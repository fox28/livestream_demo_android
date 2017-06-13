package cn.kkk.live.db;
/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import cn.kkk.live.LiveApplication;

/**
 * Created by apple on 2017/6/14.
 */

public class DbOpenHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static DbOpenHelper instance;
    private static final String GIFT_TABLE_CREATE = "CREATE TABLE "
            + GiftDao.GIFT_TABLE_NAME + " ("
            + GiftDao.GIFT_COLUMN_NAME + " TEXT, "
            + GiftDao.GIFT_COLUMN_URL  + " TEXT, "
            + GiftDao.GIFT_COLUMN_PRICE  + " INTEGER, "
            + GiftDao.GIFT_COLUMN_ID + " INTEGER PRIMARY KEY);";

    private DbOpenHelper(Context context) {
        super(context, getUserDatabaseName(), null, DATABASE_VERSION);
    }

    public static DbOpenHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DbOpenHelper(context.getApplicationContext());
        }
        return instance;
    }

    private static String getUserDatabaseName() {
        return  LiveApplication.getInstance().getPackageName() + "_demo.db";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(GIFT_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void closeDB() {
        if (instance != null) {
            try {
                SQLiteDatabase db = instance.getWritableDatabase();
                db.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            instance = null;
        }
    }

}
