package com.dummies.android.taskreminder;

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
public class ReminderProvider extends ContentProvider {
    // Content Provider Uri and Authority
    public static final String AUTHORITY = "com.dummies.android" +
            ".taskreminder.ReminderProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" +
            AUTHORITY + "/reminder");

    // Database Columns
    public static final String COLUMN_TASKID = "_id";
    public static final String COLUMN_DATE_TIME = "reminder_date_time";
    public static final String COLUMN_BODY = "body";
    public static final String COLUMN_TITLE = "title";

    // MIME types used for searching words or looking up a single
    // definition
    private static final String REMINDERS_MIME_TYPE = ContentResolver
            .CURSOR_DIR_BASE_TYPE
            + "/vnd.com.dummies.android.taskreminder.reminder";
    private static final String REMINDER_MIME_TYPE = ContentResolver
            .CURSOR_ITEM_BASE_TYPE
            + "/vnd.com.dummies.android.taskreminder.reminder";

    // Database Related Constants
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "reminders";

    // UriMatcher stuff
    private static final int LIST_REMINDER = 0;
    private static final int ITEM_REMINDER = 1;
    private static final UriMatcher URI_MATCHER = buildUriMatcher();


    private SQLiteDatabase db;

    /**
     * Builds up a UriMatcher for search suggestion and shortcut refresh
     * queries.
     */
    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        // to get definitions...
        matcher.addURI(AUTHORITY, "reminder", LIST_REMINDER);
        matcher.addURI(AUTHORITY, "reminder/#", ITEM_REMINDER);

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

        String[] projection = new String[]{ReminderProvider.COLUMN_TASKID,
                ReminderProvider.COLUMN_TITLE,
                ReminderProvider.COLUMN_BODY,
                ReminderProvider.COLUMN_DATE_TIME};

        // Use the UriMatcher to see what kind of query we have and
        // format the
        // db query accordingly
        Cursor c;
        switch (URI_MATCHER.match(uri)) {
            case LIST_REMINDER:
                c = db.query(ReminderProvider.DATABASE_TABLE,
                        projection, null,
                        null, null, null, null);
                break;
            case ITEM_REMINDER:
                c = db.query(ReminderProvider.DATABASE_TABLE, projection,
                        ReminderProvider.COLUMN_TASKID + "=?",
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
        values.remove(ReminderProvider.COLUMN_TASKID);
        long id = db.insertOrThrow(ReminderProvider.DATABASE_TABLE, null,
                values);
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String ignored1, String[] ignored2) {
        int count = db.delete(ReminderProvider.DATABASE_TABLE,
                ReminderProvider.COLUMN_TASKID + "=?",
                new String[]{Long.toString(ContentUris.parseId(uri))});
        if (count > 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String ignored1,
                      String[] ignored2) {
        int count = db.update(ReminderProvider.DATABASE_TABLE, values,
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
            case LIST_REMINDER:
                return REMINDERS_MIME_TYPE;
            case ITEM_REMINDER:
                return REMINDER_MIME_TYPE;
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
    }

    protected static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }        // BUG we should change COLUMN_DATE_TIME back to being
        // text

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
