// AddEditContact.java
// Activity for adding a new entry to or
// editing an existing entry in the address book.
package cmput301.battery.tracker;


import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class AddEditLog extends Activity {
	private long rowID; // id of log being edited, if any
	
	// EditTexts for log information
	private EditText tagEditText;
	private EditText batteryStartEditText;
	private EditText batteryEndEditText;
	private EditText durationEditText;
	private EditText dateEditText;
	private EditText descriptionEditText;
	
	// called when the Activity is first started
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); // call super's onCreat
		setContentView(R.layout.add_log); // inflate the UI
		
		tagEditText = (EditText) findViewById(R.id.tagEditText);
		batteryStartEditText = (EditText) findViewById(R.id.batteryStartEditText);
		batteryEndEditText = (EditText) findViewById(R.id.batteryEndEditText);
		durationEditText = (EditText) findViewById(R.id.durationEditText);
		dateEditText = (EditText) findViewById(R.id.dateEditText);
		descriptionEditText = (EditText) findViewById(R.id.descriptionEditText);
		
		Bundle extras = getIntent().getExtras(); // get Bundle of extras
		
		// if there are extras, use them to populate the EditTexts
		if (extras != null) {
			rowID = extras.getLong("row_id");
			tagEditText.setText(extras.getString("tag"));
			batteryStartEditText.setText(extras.getString("start"));
			batteryEndEditText.setText(extras.getString("end"));
			durationEditText.setText(extras.getString("duration"));
			dateEditText.setText(extras.getString("date"));
			descriptionEditText.setText(extras.getString("description"));
		} // end if
		
		// set event listener for the Save Log Button
		Button saveLogButton = 
				(Button) findViewById(R.id.saveLogButton);
		saveLogButton.setOnClickListener(saveLogButtonClicked);
	} // end method onCreate
	
	// responds to event generated when user clicks the Done Button
	OnClickListener saveLogButtonClicked = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (tagEditText.getText().length() != 0) {
				AsyncTask<Object, Object, Object> saveLogTask = 
					new AsyncTask<Object, Object, Object>() {
						@Override
						protected Object doInBackground(Object... params) {
							saveLog(); // save log to the database
							return null;
						} // end method doInBackground
						
						@Override
						protected void onPostExecute(Object result) {
							finish(); // return to the previous Activity
						} // end method onPostExecute
					}; // end AsyncTask
				
				// save the contact to the database using a separate thread
				saveLogTask.execute((Object[]) null);
			} // end if
			else {
				// create a new AlertDialog Builder
				AlertDialog.Builder builder = 
					new AlertDialog.Builder(AddEditLog.this);
				
				// set dialog title & message, and provide the Button to dismiss
				builder.setTitle(R.string.errorTitle);
				builder.setMessage(R.string.errorMessage);
				builder.setPositiveButton(R.string.errorButton, null);
				builder.show();
			} // end else
		} // end method onClick
	}; // end OnClickListener saveLogButtonClicked
	
	// saves log information to the database
	private void saveLog() {
		// get DatabaseConnector to interact with the SQLite database
		DatabaseConnector databaseConnector = new DatabaseConnector(this);
		
		if (getIntent().getExtras() == null) {
			// insert the log information into the database
			databaseConnector.insertLog(
				tagEditText.getText().toString(),
				batteryStartEditText.getText().toString(),
				batteryEndEditText.getText().toString(),
				durationEditText.getText().toString(),
				dateEditText.getText().toString(),
				descriptionEditText.getText().toString());
		} // end if
		else {
			databaseConnector.updateContact(rowID,
				tagEditText.getText().toString(),
				batteryStartEditText.getText().toString(),
				batteryEndEditText.getText().toString(),
				durationEditText.getText().toString(),
				dateEditText.getText().toString(),
				descriptionEditText.getText().toString());
		} // end else
	} // end class saveContact
} // end class AddEditContact
