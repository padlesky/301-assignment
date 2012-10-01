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
// DatabaseConnector.java
// Provides easy connection and creation of UserContacts database.
package cmput301.battery.tracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class DatabaseConnector {
	// database name
	private static final String DATABASE_NAME = "Logs";
	private SQLiteDatabase database; // database object
	private DatabaseOpenHelper databaseOpenHelper; // database helper
	
	// public log for DatabaseConnector
	public DatabaseConnector(Context context) {
		// create a new DatabaseOpenHelper
		databaseOpenHelper = 
			new DatabaseOpenHelper(context, DATABASE_NAME, null, 1);
	} // end DatabaseConnector constructor
	
	// open the database connection
	public void open() throws SQLException {
		// create or open a database for reading/writing
		database = databaseOpenHelper.getWritableDatabase();
	} // end method open
	
	// close the database connection
	public void close() {
		if (database != null)
			database.close(); // close the database connection
	} // end method close
	
	// inserts a new contact in the database
	public void insertLog(String tag, String start, String end, 
		String duration, String date, String description) {
		ContentValues newLog = new ContentValues();
		newLog.put("tag", tag);
		newLog.put("start", start);
		newLog.put("end", end);
		newLog.put("duration", duration);
		newLog.put("date", date);
		newLog.put("description", description);
		
		open(); // open the database
		database.insert("logs", null, newLog);
		close(); // close the database
	} // end method insertContact
	
	// inserts a new log in the database
	public void updateContact(long id, String tag, String start,
		String end, String duration, String date, String description) {
		ContentValues editLog = new ContentValues();
		editLog.put("tag", tag);
		editLog.put("start", start);
		editLog.put("end", end);
		editLog.put("duration", duration);
		editLog.put("date", date);
		editLog.put("description", description);
		
		open(); // open the database
		database.update("logs", editLog, "_id=" + id, null);
		close(); // close the database
	} // end method updateLog
	
	// return a Cursor with all log tag and id information in the database
	public Cursor getAllLogsTag() {
		return database.query("logs", new String[] {"_id", "tag"},
				null, null, null, null, "tag");
	} // end method getAllLogs
	
	// return a Cursor with all the start battery %, end battery% and duration
	public Cursor getAllLogsData() {
		return database.query("logs", new String[] {"start", "end", "duration"},
				null, null, null, null, "tag");
	} // end method getAllLogs
	
	// get a Cursor containing all information about the log specified
	// by the given id
	public Cursor getOneLog(long id) {
		return database.query(
			"logs", null, "_id=" + id, null, null, null, null);
	} // end method getOnLog
	
	// delete the log specified by the given String name
	public void deleteLog(long id) {
		open(); // open the database
		database.delete("logs", "_id=" + id, null);
		close(); // close the database
	} // end method deleteLog
	
	private class DatabaseOpenHelper extends SQLiteOpenHelper {
		// public constructor
		public DatabaseOpenHelper(Context context, String tag,
			CursorFactory factory, int version) {
			super(context, tag, factory, version);
		} // end DatabaseOpenHelper constructor
		
		// creates the logs table when the database is created
		@Override
		public void onCreate(SQLiteDatabase db) {
			// query to create a new table named logs
			String createQuery = "CREATE TABLE logs" +
				"(_id integer primary key autoincrement," +
				"tag TEXT, start TEXT, end TEXT," +
				"duration TEXT, date TEXT, description TEXT);";
			
			db.execSQL(createQuery); // execute the query
		} // end method onCreate
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {
			
		} // end method onUpgrade
	} // end class DatabaseOpenHelper
} // end class DatabaseConnector
