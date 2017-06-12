package jp.co.it_college.mymemoapp;

/**
 * Created by takagi-yuzuho on 2017/06/12.
 */

		import android.content.ContentUris;
		import android.content.ContentValues;
		import android.content.DialogInterface;
		import android.content.Intent;
		import android.database.Cursor;
		import android.icu.text.Normalizer;
		import android.icu.text.SimpleDateFormat;
		import android.net.Uri;
		import android.os.Build;
		import android.support.annotation.RequiresApi;
		import android.support.v7.app.ActionBarActivity;
		import android.os.Bundle;
		import android.support.v7.app.AlertDialog;
		import android.support.v7.app.AppCompatActivity;
		import android.view.Menu;
		import android.view.MenuItem;
		import android.widget.EditText;
		import android.widget.TextView;
		import android.widget.Toast;

		import java.util.Date;
		import java.util.Locale;


public class FormActivity extends AppCompatActivity {

	private long memoId;

	private EditText titleText;
	private EditText bodyText;
	private TextView updatedText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_form);

		titleText = (EditText) findViewById(R.id.titleText);
		bodyText = (EditText) findViewById(R.id.bodyText);
		updatedText = (TextView) findViewById(R.id.updatedText);

		Intent intent = getIntent();
		memoId = intent.getLongExtra(MainActivity.EXTRA_MYID, 0L);

		if (memoId == 0) {
			// new memo
			if(getSupportActionBar() == null) {
				getSupportActionBar().setTitle("New Memo");
			}
			updatedText.setText("Updated: ----------");

		} else {
			// show memo
			if(getSupportActionBar() == null) {
				getSupportActionBar().setTitle("Edit Memo");
			}
			Uri uri = ContentUris.withAppendedId(
					MemoContentProvider.CONTENT_URI,
					memoId
			);
			String[] projection = {
					MemoContract.Memos.COL_TITLE,
					MemoContract.Memos.COL_BODY,
					MemoContract.Memos.COL_UPDATED
			};
			Cursor c = getContentResolver().query(
					uri,
					projection,
					MemoContract.Memos._ID + " = ?",
					new String[] { Long.toString(memoId) },
					null
			);
			c.moveToFirst();
			titleText.setText(
					c.getString(c.getColumnIndex(MemoContract.Memos.COL_TITLE))
			);
			bodyText.setText(
					c.getString(c.getColumnIndex(MemoContract.Memos.COL_BODY))
			);
			updatedText.setText(
					"Updated: " +
							c.getString(c.getColumnIndex(MemoContract.Memos.COL_UPDATED))
			);
			c.close();
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem deleteItem = menu.findItem(R.id.action_delete);
		if (memoId == 0L) deleteItem.setVisible(false);

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_form, menu);
		return true;
	}

	private void deleteMemo() {
		new AlertDialog.Builder(this)
			.setTitle("Delete Memo")
			.setMessage("Are you sure?")
			.setNegativeButton("Cancel", null)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Uri uri = ContentUris.withAppendedId(
							MemoContentProvider.CONTENT_URI,
							memoId
					);
					getContentResolver().delete(
							uri,
							MemoContract.Memos._ID + " = ?",
							new String[]{ Long.toString(memoId)}
					);
					finish();
				}
			}).show();
	}

	@RequiresApi(api = Build.VERSION_CODES.N)
	private void saveMemo() {
		String title = titleText.getText().toString().trim();
		String body = bodyText.getText().toString().trim();
		String updated =
				new SimpleDateFormat("yyyy-MM-dd kk:mm:ss", Locale.US)
				.format(new Date());

		if(title.isEmpty()) {
			Toast.makeText(
					FormActivity.this,
					"Please Enter Title",
					Toast.LENGTH_LONG
			).show();
		} else {
			ContentValues values = new ContentValues();
			values.put(MemoContract.Memos.COL_TITLE, title);
			values.put(MemoContract.Memos.COL_BODY, body);
			values.put(MemoContract.Memos.COL_UPDATED, updated);

			if(memoId == 0L) {
				// new memo
				getContentResolver().insert(
						MemoContentProvider.CONTENT_URI,
						values
				);
			} else {
				// update memo
				Uri uri = ContentUris.withAppendedId(
						MemoContentProvider.CONTENT_URI,
						memoId
				);
				getContentResolver().update(
						uri,
						values,
						MemoContract.Memos._ID + " = ?",
						new String[] { Long.toString(memoId)}
				);
			}
			finish();
		}
	}

	@RequiresApi(api = Build.VERSION_CODES.N)
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_delete:
				deleteMemo();
				break;
			case R.id.action_save:
				saveMemo();
				break;
		}
		return super.onOptionsItemSelected(item);
	}
}
