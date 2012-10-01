/*
 * Copyright 2012 Aaron Padlesky

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
// ViewLog.java
// Activity for viewing a single log
package cmput301.battery.tracker;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;


public class ViewLog extends Activity {
	private long rowID; // selected log's name
	private TextView tagTextView; // displays log's tag
	private TextView batteryStartTextView; // displays log's battery start
	private TextView batteryEndTextView; // displays log's battery end
	private TextView durationTextView; // displays log's duration
	private TextView dateTextView; // displays log's date
	private TextView descriptionTextView; // displays log's description
	
	// called when the activity is first created
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_log);
		
		// get the EditTexts
		tagTextView = (TextView) findViewById(R.id.tagTextView);
		batteryStartTextView = (TextView) findViewById(R.id.batteryStartTextView);
		batteryEndTextView = (TextView) findViewById(R.id.batteryEndTextView);
		durationTextView = (TextView) findViewById(R.id.durationTextView);
		dateTextView = (TextView) findViewById(R.id.dateTextView);
		descriptionTextView = (TextView) findViewById(R.id.descriptionTextView);
		
		// get the selected log's row ID
		Bundle extras = getIntent().getExtras();
		rowID = extras.getLong("row_id");
	} // end method onCreate
	
	// called when the activity is first created
	@Override
	protected void onResume() {
		super.onResume();
		
		// create new LoadLogTask and execute it
		new LoadLogTask().execute(rowID);
	} // end method onResume
	
	// performs database query outside GUI thread
	private class LoadLogTask extends AsyncTask<Long, Object, Cursor> {
		DatabaseConnector databaseConnector = 
				new DatabaseConnector(ViewLog.this);
		
		// perform the database access
		@Override
		protected Cursor doInBackground(Long... params) {
			databaseConnector.open();
			
			// get a cursor containing all data on given entry
			return databaseConnector.getOneLog(params[0]);
		} // end method doInBackground
		
		// use the Cursor returned from the doInBackground Method
		@Override
		protected void onPostExecute(Cursor result) {
			super.onPostExecute(result);
			
			result.moveToFirst(); // move to the first item
			
			// get the column index for each data item
			int tagIndex = result.getColumnIndex("tag");
			int startIndex = result.getColumnIndex("start");
			int endIndex = result.getColumnIndex("end");
			int durationIndex = result.getColumnIndex("duration");
			int dateIndex = result.getColumnIndex("date");
			int descriptionIndex = result.getColumnIndex("description");
			
			// fill TextView with the retrieved data
			tagTextView.setText(result.getString(tagIndex));
			batteryStartTextView.setText(result.getString(startIndex));
			batteryEndTextView.setText(result.getString(endIndex));
			durationTextView.setText(result.getString(durationIndex));
			dateTextView.setText(result.getString(dateIndex));
			descriptionTextView.setText(result.getString(descriptionIndex));
			
			result.close(); // close the result cursor
			databaseConnector.close(); // close database connection
		} // end method onPostExecute
	} // end class LoadLog Task
	
	// create the Activity's menu from a menu resource XML file
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.view_log_menu, menu);
		return true;
	} // end method onCreatOptionsMenu
	
	// handle choice from options menu
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) { // switch based on selected MenuItem's ID
			case R.id.editItem:
				// create an Intent to launch the AddEditLog Activity
				Intent addEditLog = 
				new Intent(this, AddEditLog.class);
				
				// pass the selected log's data as extras with the Intent
				addEditLog.putExtra("row_id", rowID);
				addEditLog.putExtra("tag", tagTextView.getText());
				addEditLog.putExtra("start", batteryStartTextView.getText());
				addEditLog.putExtra("end", batteryEndTextView.getText());
				addEditLog.putExtra("duration", durationTextView.getText());
				addEditLog.putExtra("date", dateTextView.getText());
				addEditLog.putExtra("description", descriptionTextView.getText());
				startActivity(addEditLog); // starts the Activity
				return true;
			case R.id.deleteItem:
				deleteLog(); // delete the displayed Log
				return true;
			default:
				return super.onOptionsItemSelected(item);
		} // end switch
	} // end method onOptionsItemSelected
	
	// delete a contact
	private void deleteLog() {
		// create a new AlertDialog Builder
		AlertDialog.Builder builder = 
				new AlertDialog.Builder(ViewLog.this);
		
		builder.setTitle(R.string.confirmTitle); // title bar string
		builder.setMessage(R.string.confirmMessage); // message to display
		
		// provide an OK button that simply dismisses the dialog
		builder.setPositiveButton(R.string.button_delete, 
			new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int button) {
					final DatabaseConnector databaseConnector = 
							new DatabaseConnector(ViewLog.this);
					
					// create an AsyncTask that deletes the contact in another
					// thread, then calls finish after the deletion
					AsyncTask<Long, Object, Object> deleteTask =
						new AsyncTask<Long, Object, Object> () {
							@Override
							protected Object doInBackground(Long... params) {
								databaseConnector.deleteLog(params[0]);
								return null;
							} // end method doInBackground
							
							@Override
							protected void onPostExecute(Object result) {
								finish(); // return to AddressBook Activity
							} // end method onPostExecute
					}; // end new AsyncTask
					
					// execute the AsyncTask to delete contact at rowID
					deleteTask.execute(new Long[] { rowID });
				} // end method onClick
			} // end anonymous inner class
		); // end call method setPositiveButton
		
		builder.setNegativeButton(R.string.button_cancel, null);
		builder.show(); // display the Dialog
	} // end method deleteLog
}  // end class ViewLog
