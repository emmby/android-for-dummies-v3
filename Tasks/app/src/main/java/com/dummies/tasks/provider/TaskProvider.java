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

/**
 * A Content Provider that knows how to read and write tasks from our
 * tasks database.
 */
public class TaskProvider extends ContentProvider {
    // Content Provider Uri and Authority
    public static final String AUTHORITY = "com.dummies" +
            ".tasks.provider.TaskProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" +
            AUTHORITY + "/task");

    // Database Columns
    public static final String COLUMN_TASKID = "_id";
    public static final String COLUMN_DATE_TIME = "task_date_time";
    public static final String COLUMN_BODY = "body";
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

    // UriMatcher stuff
    private static final int LIST_TASK = 0;
    private static final int ITEM_TASK = 1;
    private static final UriMatcher URI_MATCHER = buildUriMatcher();


    private SQLiteDatabase db;

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

    @Override
    public boolean onCreate() {
        db = new DatabaseHelper(getContext()).getWritableDatabase();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] ignored1, String ignored2,
                        String[] ignored3, String ignored4) {

        String[] projection = new String[]{
                TaskProvider.COLUMN_TASKID,
                TaskProvider.COLUMN_TITLE,
                TaskProvider.COLUMN_BODY,
                TaskProvider.COLUMN_DATE_TIME};

        // Use the UriMatcher to see what kind of query we have and
        // format the
        // db query accordingly
        Cursor c;
        switch (URI_MATCHER.match(uri)) {
            case LIST_TASK:
                c = db.query(TaskProvider.DATABASE_TABLE,
                        projection, null,
                        null, null, null, null);
                break;
            case ITEM_TASK:
                c = db.query(TaskProvider.DATABASE_TABLE, projection,
                        TaskProvider.COLUMN_TASKID + "=?",
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

        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // you can't insert and specify a row id, so remove it if present
        values.remove(TaskProvider.COLUMN_TASKID);
        long id = db.insertOrThrow(TaskProvider.DATABASE_TABLE, null,
                values);
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String ignored1, String[] ignored2) {
        int count = db.delete(TaskProvider.DATABASE_TABLE,
                TaskProvider.COLUMN_TASKID + "=?",
                new String[]{Long.toString(ContentUris.parseId(uri))});
        if (count > 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String ignored1,
                      String[] ignored2) {
        int count = db.update(TaskProvider.DATABASE_TABLE, values,
                COLUMN_TASKID + "=?",
                new String[]{Long.toString(ContentUris.parseId(uri))});
        if (count > 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    /**
     * This method is required in order to query the supported types.
     * It's also
     * useful in our own query() method to determine the type of Uri
     * received.
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

    protected static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }        // since it's easier

        // to read in the db that way
        private static final String DATABASE_CREATE = "create table "
                + DATABASE_TABLE + " (" + COLUMN_TASKID
                + " integer primary key autoincrement, " + COLUMN_TITLE
                + " text not null, " + COLUMN_BODY + " text not null, "
                + COLUMN_DATE_TIME + " integer not null);";

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                              int newVersion) {
            throw new UnsupportedOperationException();
        }


    }
}
