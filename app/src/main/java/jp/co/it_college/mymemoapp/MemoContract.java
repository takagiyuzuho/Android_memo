package jp.co.it_college.mymemoapp;

import android.provider.BaseColumns;

/**
 * Created by takagi-yuzuho on 2017/06/12.
 */



//インスタンス化されない
public final class MemoContract {

	public MemoContract(){}

	public static abstract class Memos implements BaseColumns {
		public static final String TABLE_NAME = "memos";
		public static final String COL_TITLE = "title";
		public static final String COL_BODY = "body";
		public static final String COL_CREATE = "created";
		public static final String COL_UPDATED = "updated";


	}
}
