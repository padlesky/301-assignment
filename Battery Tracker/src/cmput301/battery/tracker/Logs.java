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

// Logs.java
// Main activity for the Address Book app.
package cmput301.battery.tracker;


import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class Logs extends ListActivity {

	public static final String ROW_ID = "row_id"; // Intent extra key
	private ListView logListView; // the ListActivity's ListView
	private CursorAdapter logAdapter; // adapter for ListView
	
	// called when the activity is first created
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // call super's onCreate
        logListView = getListView(); // get the built-in ListView
        logListView.setOnItemClickListener(viewLogListener);
        
        // map each log's tag to a TextView in the ListView layout
        String[] from = new String[] { "tag" };
        int[] to = new int[] { R.id.logTextView };
        logAdapter = new SimpleCursorAdapter(
        	Logs.this, R.layout.log_list_view, null, from, to);
        setListAdapter(logAdapter); // set logView's adapter
    } // end method onCreate
    
    @Override
    protected void onResume() {
    	super.onResume(); // call super's onResume method
    	
    	// create new GetLogsTask and execute it
    	new GetLogsTask().execute((Object[]) null);
    } // end method onResume
    
    @Override
    protected void onStop() {
    	Cursor cursor = logAdapter.getCursor(); // get current Cursor
    	
    	if (cursor != null)
    		cursor.deactivate(); // deactivate it
    	
    	logAdapter.changeCursor(null); // adapted now has no Cursor
    	super.onStop();
    } // end method onStop

    // performs database query outside GUI thread
    private class GetLogsTask extends AsyncTask<Object, Object, Cursor> {
    	DatabaseConnector databaseConnector =
    			new DatabaseConnector(Logs.this);
    	
    	// perform the database access
    	@Override
    	protected Cursor doInBackground(Object... params) {
    		databaseConnector.open();
    		
    		// get a cursor containing call logs
    		return databaseConnector.getAllLogsTag();
    	} // end method doInBackground
    	
    	// use the Cursor to return from the doInBackground method
    	@Override
    	protected void onPostExecute(Cursor result) {
    		logAdapter.changeCursor(result); // set the adapter's Cursor
    		databaseConnector.close();
    	} // end method onPostExecute
    } // end class GetLogsTask
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logs_menu, menu);
        return true;
    } // end method onCreateOptionsMenu
    
    // handle choice from options menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	// create a new Intent to launch the AddEditLog Activity
    	Intent addNewLog =
    			new Intent(Logs.this, AddEditLog.class);
    	startActivity(addNewLog); // start the AddEditLog Activity
    	return super.onOptionsItemSelected(item); // call supers's method
    } // end method onOptionsItemSelected
    
    // event listener that responds to the user touching a logs's tag
    // in the ListView
    OnItemClickListener viewLogListener = new OnItemClickListener() {
    	@Override
    	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
    		// create an Intent to launch the viewLog Activity
    		Intent viewLog =
    				new Intent(Logs.this, ViewLog.class);
    		
    		// pass the selected Logs's row ID as an extra with the Intent
    		viewLog.putExtra(ROW_ID, arg3);
    		startActivity(viewLog); // start the ViewLog Activity
    	} // end method onItemClick
    }; // end viewLogListener
} // end class Logs
