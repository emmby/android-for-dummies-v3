package com.dummies.tasks.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

/**
 * A Content Provider that knows how to read and write tasks from our
 * tasks database.
 */
public class TaskProvider extends ContentProvider implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient
        .OnConnectionFailedListener {
    // Content Provider Uri and Authority
    public static final String AUTHORITY = "com.dummies" +
            ".tasks.provider.TaskProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" +
            AUTHORITY + "/task");

    // Database Columns
    public static final String COLUMN_TASKID = "_id";
    public static final String COLUMN_DATE_TIME = "task_date_time";
    public static final String COLUMN_NOTES = "notes";
    public static final String COLUMN_TITLE = "title";

    // MIME types used for searching words or looking up a single
    // definition
    private static final String TASKS_MIME_TYPE = ContentResolver
            .CURSOR_DIR_BASE_TYPE
            + "/vnd.com.dummies.tasks.task";
    private static final String TASK_MIME_TYPE = ContentResolver
            .CURSOR_ITEM_BASE_TYPE
            + "/vnd.com.dummies.tasks.task";

    // Database Related Constants
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "tasks";

    // Google Play Constants
    private static final String PLAY_BASE_URL = "/" + DATABASE_TABLE;

    // UriMatcher stuff
    private static final int LIST_TASK = 0;
    private static final int ITEM_TASK = 1;
    private static final UriMatcher URI_MATCHER = buildUriMatcher();


    // The database
    SQLiteDatabase db;

    // The Google Play api client, used for Android Wearable syncing
    GoogleApiClient googleApiClient;

    /**
     * Builds up a UriMatcher for search suggestion and shortcut refresh
     * queries.
     */
    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        // to get definitions...
        matcher.addURI(AUTHORITY, "task", LIST_TASK);
        matcher.addURI(AUTHORITY, "task/#", ITEM_TASK);

        return matcher;
    }

    public static String getImageUrlForTask(long taskId) {

        return "http://lorempixel.com/600/400/cats/?fakeId=" + taskId;
    }

    /**
     * This method is called when our ContentProvider is created
     */
    @Override
    public boolean onCreate() {
        // Grab a connection to our database
        db = new DatabaseHelper(getContext()).getWritableDatabase();

        googleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();

        return true;
    }



    /**
     * This method is called when someone wants to read something from
     * our content provider.  We'll turn around and ask our database
     * for the information, and then return it in a Cursor.
     */
    @Override
    public Cursor query(Uri uri, String[] ignored1, String selection,
                        String[] selectionArgs, String sortOrder) {

        String[] projection = new String[]{
                COLUMN_TASKID,
                COLUMN_TITLE,
                COLUMN_NOTES,
                COLUMN_DATE_TIME};

        // Use the UriMatcher to see what kind of query we have and
        // format the db query accordingly
        Cursor c;
        switch (URI_MATCHER.match(uri)) {

            // We were asked to return a list of tasks
            case LIST_TASK:
                c = db.query(DATABASE_TABLE,
                        projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;

            // We were asked to return a specific task
            case ITEM_TASK:
                c = db.query(DATABASE_TABLE, projection,
                        COLUMN_TASKID + "=?",
                        new String[]{Long.toString(ContentUris.parseId
                                (uri))},
                        null, null, null, null);
                if (c.getCount() > 0) {
                    c.moveToFirst();
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }

        // Set the notification URI for this cursor.  Our Loader will
        // use this URI to watch for any changes to our data,
        // and if the data changes the Loader will automatically refresh.
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    /**
     * This method is called when someone wants to insert something
     * into our content provider.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // you can't choose your own task id
        if( values.containsKey(COLUMN_TASKID))
            throw new UnsupportedOperationException();

        long id = db.insertOrThrow(DATABASE_TABLE, null,
                values);
        getContext().getContentResolver().notifyChange(uri, null);

        // Save to google Play for wearable support
        PutDataMapRequest dataMap = PutDataMapRequest.create(
                PLAY_BASE_URL + "/" + id);
        DataMap map = dataMap.getDataMap();
        map.putLong(COLUMN_TASKID, id);
        map.putString(COLUMN_TITLE, values.getAsString(COLUMN_TITLE));
        map.putLong(COLUMN_DATE_TIME, values.getAsLong(COLUMN_DATE_TIME));
        map.putString(COLUMN_NOTES, values.getAsString(COLUMN_NOTES));
        PutDataRequest request = dataMap.asPutDataRequest();
        Wearable.DataApi.putDataItem(googleApiClient, request);


        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * This method is called when someone wants to delete something
     * from our content provider.
     */
    @Override
    public int delete(Uri uri, String ignored1, String[] ignored2) {
        // TODO delete from google play

        int count = db.delete(DATABASE_TABLE,
                COLUMN_TASKID + "=?",
                new String[]{Long.toString(ContentUris.parseId(uri))});
        if (count > 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    /**
     * This method is called when someone wants to update something
     * in our content provider.
     */
    @Override
    public int update(Uri uri, ContentValues values, String ignored1,
                      String[] ignored2) {
        // you can't change a task id
        if( values.containsKey(COLUMN_TASKID))
            throw new UnsupportedOperationException();

        long id = ContentUris.parseId(uri);
        int count = db.update(DATABASE_TABLE, values,
                COLUMN_TASKID + "=?",
                new String[]{Long.toString(id)});
        if (count > 0)
            getContext().getContentResolver().notifyChange(uri, null);

        // Update to google Play for wearable support
        PutDataMapRequest dataMap = PutDataMapRequest.create(
                PLAY_BASE_URL + "/" + id);
        DataMap map = dataMap.getDataMap();
        map.putLong(COLUMN_TASKID, values.getAsLong(COLUMN_TASKID));
        map.putString(COLUMN_TITLE, values.getAsString(COLUMN_TITLE));
        map.putLong(COLUMN_DATE_TIME, values.getAsLong(COLUMN_DATE_TIME));
        map.putString(COLUMN_NOTES, values.getAsString(COLUMN_NOTES));
        PutDataRequest request = dataMap.asPutDataRequest();
        Wearable.DataApi.putDataItem(googleApiClient, request);


        return count;
    }

    /**
     * This method is required in order to query the supported types.
     */
    @Override
    public String getType(Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case LIST_TASK:
                return TASKS_MIME_TYPE;
            case ITEM_TASK:
                return TASK_MIME_TYPE;
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        // TODO
    }

    @Override
    public void onConnectionSuspended(int i) {
        // TODO
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // TODO
    }

    /**
     * A helper class which knows how to create and update our database.
     */
    protected static class DatabaseHelper extends SQLiteOpenHelper {

        static final String DATABASE_CREATE =
            "create table " + DATABASE_TABLE + " (" +
                COLUMN_TASKID + " integer primary key autoincrement, " +
                COLUMN_TITLE + " text not null, " +
                COLUMN_NOTES + " text not null, " +
                COLUMN_DATE_TIME + " integer not null);";

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        /**
         * This method is called when the app is first installed and no
         * database has yet been created.
         */
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }


        /**
         * This method will be called in the future when we release
         * version 2.0 of our Tasks app, at which point we'll need to
         * upgrade our database from version 1.0 to version 2.0.
         * For now, there's nothing that needs to be done here.
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                              int newVersion) {
            throw new UnsupportedOperationException();
        }
    }
}
