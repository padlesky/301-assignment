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
// Log_Data.java
// Activity for viewing data

package cmput301.battery.tracker;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

public class Log_Data extends Activity {

	private EditText batteryUsedEditText; // displays Battery % used
	private EditText elapsedTimeEditText; // displays Elapsed Time in seconds
	private EditText consumptionRateEditText; // displays consumptionRate
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_data);
        
        batteryUsedEditText = (EditText) findViewById(R.id.batteryUsedEditText); // displays log's batteryUsed
        elapsedTimeEditText = (EditText) findViewById(R.id.elapsedTimeEditText); // displays log's elapsedTime
        consumptionRateEditText = (EditText) findViewById(R.id.consumptionRateEditText); // displays log's consupmtionRate
    }
    
    public void onResume () {
    	super.onResume();
    	
    	calculateConsumption ();
    }
    private void calculateConsumption () {
    	DatabaseConnector databaseConnector = 
				new DatabaseConnector(Log_Data.this);
    	
    	databaseConnector.open();
    	
    	// get cursor containing all entries
    	Cursor allLogs = databaseConnector.getAllLogsData();
    	android.util.Log.d("WTF", allLogs.getColumnName(0));
    	android.util.Log.d("WTF", allLogs.getColumnName(1));
    	android.util.Log.d("WTF", allLogs.getColumnName(2));
    	// check to see how many
    	int numberRows = allLogs.getCount();
    		
    	// check to see if there is any rows and if so calculate consumption rate
    	if (numberRows >= 0) {
    		// initializes the allLogs to its first row
    		allLogs.moveToFirst();
    		double batteryUsed = 0;
    		double elapsedTime = 0;
    		double consumptionRate = 0;
    		for (int i = 0; i < numberRows; i++) {
    			// get column index for the desired data items
    			int startIndex = allLogs.getColumnIndex("start");
    			int endIndex = allLogs.getColumnIndex("end");
    			int durationIndex = allLogs.getColumnIndex("duration");
    			
    			// creates doubles from the string given by the data
    			double batteryStart = Double.parseDouble(allLogs.getString(startIndex));
    			double batteryEnd = Double.parseDouble(allLogs.getString(endIndex));
    			double duration = Double.parseDouble(allLogs.getString(durationIndex));
    		
    			// calculates a running total of battery % used
    			// and elapsed time in seconds
    			batteryUsed = (batteryStart - batteryEnd) + batteryUsed;
    			elapsedTime = elapsedTime + duration;
    			
    			// moves allLogs to the next row of data
    			allLogs.moveToNext();
	    	}
	   		
    		// calculates the consumption rate from the battery used and elapsed time totals
    		consumptionRate = (batteryUsed) / (elapsedTime/3600);
			
    		// changes the edit text views to the new values
    		batteryUsedEditText.setText(String.format("%.02f", batteryUsed));
    		elapsedTimeEditText.setText(String.format("%.02f", elapsedTime));
    		consumptionRateEditText.setText(String.format("%.02f", consumptionRate));
    	}
		allLogs.close();
		databaseConnector.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.log_data_menu, menu);
        return true;
    } // end method onCreateOptionsMenu
    
    // handle choice from options menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	// create a new Intent to launch the Logs Activity
    	Intent viewLogs =
    			new Intent(Log_Data.this, Logs.class);
    	startActivity(viewLogs); // start the Logs Activity
    	return super.onOptionsItemSelected(item); // call supers's method
    } // end method onOptionsItemSelected
}
