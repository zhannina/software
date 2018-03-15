package com.example.zsarsenbayev.demoiaps;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.util.HashMap;

/**
 * Created by zsarsenbayev on 3/15/18.
 */

public class EmotionsProvider extends ContentProvider {

    public static final int DATABASE_VERSION = 1;
    public static String AUTHORITY = "com.example.zsarsenbayev.demoiaps.emotions";
    private static final int SENSOR_DEV = 1;
    private static final int SENSOR_DEV_ID = 2;

    public static final class EmotionsTable implements BaseColumns {
        private EmotionsTable() {
        }

        public static final Uri CONTENT_URI = Uri.parse("content://"
                + AUTHORITY + "/emotions");
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.contextdatareading.emotions";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.contextdatareading.emotions";

        public static final String TIMESTAMP = "timestamp";
        public static final String DEVICE_ID = "device_id";
        public static final String ANGER = "anger";  //anger
        public static final String CONTEMPT = "contempt";  //contempt
        public static final String DISGUST = "disgust"; // disgust
        public static final String FEAR = "fear"; // fear
        public static final String JOY = "joy"; // joy
        public static final String SADNESS = "sadness"; // sadness
        public static final String SURPRISE = "surprise"; // surprise
        public static final String VALENCE = "valence"; // valence
        public static final String SMILE = "smile"; // smile
    }

    public static String DATABASE_NAME = "emotions.db";
    public static final String[] DATABASE_TABLES = { "emotions" };
    public static final String[] TABLES_FIELDS = {
            EmotionsTable._ID + " integer primary key autoincrement,"
                    + EmotionsTable.TIMESTAMP + " real default 0,"
                    + EmotionsTable.DEVICE_ID + " text default '',"
                    + EmotionsTable.ANGER + " real default 0,"
                    + EmotionsTable.CONTEMPT + " real default 0,"
                    + EmotionsTable.DISGUST + " real default 0,"
                    + EmotionsTable.FEAR + " real default 0,"
                    + EmotionsTable.JOY + " real default 0,"
                    + EmotionsTable.SADNESS + " real default 0,"
                    + EmotionsTable.SURPRISE + " real default 0,"
                    + EmotionsTable.VALENCE + " real default 0,"
                    + EmotionsTable.SMILE + " real default 0 "
    };

    private static UriMatcher sUriMatcher = null;
    private static HashMap<String, String> sensorMap = null;
    private static DatabaseHelper databaseHelper = null;
    private static SQLiteDatabase database = null;

    private boolean initializeDB() {
        if (databaseHelper == null) {
            databaseHelper = new DatabaseHelper( getContext(), DATABASE_NAME, null, DATABASE_VERSION, DATABASE_TABLES, TABLES_FIELDS );
        }
        if( databaseHelper != null && ( database == null || ! database.isOpen() )) {
            database = databaseHelper.getWritableDatabase();
        }
        return( database != null && databaseHelper != null);
    }

    public static void resetDB(Context c ) {
        Log.d("AWARE", "Resetting " + DATABASE_NAME + "...");

        File db = new File(DATABASE_NAME);
        db.delete();
        databaseHelper = new DatabaseHelper( c, DATABASE_NAME, null, DATABASE_VERSION, DATABASE_TABLES, TABLES_FIELDS);
        if( databaseHelper != null ) {
            database = databaseHelper.getWritableDatabase();
        }
    }

    @Override
    public boolean onCreate() {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(EmotionsProvider.AUTHORITY, DATABASE_TABLES[0],
                SENSOR_DEV);
        sUriMatcher.addURI(EmotionsProvider.AUTHORITY, DATABASE_TABLES[0] + "/#",
                SENSOR_DEV_ID);


        sensorMap = new HashMap<String, String>();
        sensorMap.put(EmotionsTable._ID, EmotionsTable._ID);
        sensorMap.put(EmotionsTable.TIMESTAMP, EmotionsTable.TIMESTAMP);
        sensorMap.put(EmotionsTable.DEVICE_ID, EmotionsTable.DEVICE_ID);
        sensorMap.put(EmotionsTable.ANGER, EmotionsTable.ANGER);
        sensorMap.put(EmotionsTable.CONTEMPT, EmotionsTable.CONTEMPT);
        sensorMap.put(EmotionsTable.DISGUST, EmotionsTable.DISGUST);
        sensorMap.put(EmotionsTable.FEAR, EmotionsTable.FEAR);
        sensorMap.put(EmotionsTable.JOY, EmotionsTable.JOY);
        sensorMap.put(EmotionsTable.SADNESS, EmotionsTable.SADNESS);
        sensorMap.put(EmotionsTable.SURPRISE, EmotionsTable.SURPRISE);
        sensorMap.put(EmotionsTable.VALENCE, EmotionsTable.VALENCE);
        sensorMap.put(EmotionsTable.SMILE, EmotionsTable.SMILE);

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        if( ! initializeDB() ) {
            //Log.w(AUTHORITY,"Database unavailable...");
            return null;
        }

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        switch (sUriMatcher.match(uri)) {
            case SENSOR_DEV:
                qb.setTables(DATABASE_TABLES[0]);
                qb.setProjectionMap(sensorMap);
                break;
            default:

                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        try {
            Cursor c = qb.query(database, projection, selection, selectionArgs,
                    null, null, sortOrder);
            c.setNotificationUri(getContext().getContentResolver(), uri);
            return c;
        } catch (IllegalStateException e) {

            Log.e("Aware.TAG", e.getMessage());

            return null;
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case SENSOR_DEV:
                return EmotionsTable.CONTENT_TYPE;
            case SENSOR_DEV_ID:
                return EmotionsTable.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues initialValues) {
        if( ! initializeDB() ) {
            //Log.w(AUTHORITY,"Database unavailable...");
            return null;
        }

        ContentValues values = (initialValues != null) ? new ContentValues(
                initialValues) : new ContentValues();

        switch (sUriMatcher.match(uri)) {
            case SENSOR_DEV:
                database.beginTransaction();
                long accel_id = database.insertWithOnConflict(DATABASE_TABLES[0],
                        EmotionsTable.DEVICE_ID, values, SQLiteDatabase.CONFLICT_IGNORE);
                database.setTransactionSuccessful();
                database.endTransaction();
                if (accel_id > 0) {
                    Uri accelUri = ContentUris.withAppendedId(
                            EmotionsTable.CONTENT_URI, accel_id);
                    getContext().getContentResolver().notifyChange(accelUri, null);
                    return accelUri;
                }
                throw new SQLException("Failed to insert row into " + uri);
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        if( ! initializeDB() ) {
            //Log.w(AUTHORITY,"Database unavailable...");
            return 0;
        }
        int count = 0;
        switch (sUriMatcher.match(uri)) {
            case SENSOR_DEV:
                database.beginTransaction();
                count = database.delete(DATABASE_TABLES[0], selection,
                        selectionArgs);
                database.setTransactionSuccessful();
                database.endTransaction();
                break;
            default:

                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        if( ! initializeDB() ) {
            //Log.w(AUTHORITY,"Database unavailable...");
            return 0;
        }

        int count = 0;
        switch (sUriMatcher.match(uri)) {
            case SENSOR_DEV:
                database.beginTransaction();
                count = database.update(DATABASE_TABLES[0], values, selection,
                        selectionArgs);
                database.setTransactionSuccessful();
                database.endTransaction();
                break;

            default:

                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        if( ! initializeDB() ) {
//            Log.w(AUTHORITY,"Database unavailable...");
            return 0;
        }

        int count = 0;
        switch ( sUriMatcher.match(uri) ) {
            case SENSOR_DEV:
                database.beginTransaction();
                for (ContentValues v : values) {
                    long id;
                    try {
                        id = database.insertOrThrow( DATABASE_TABLES[0], EmotionsTable.DEVICE_ID, v );
                    } catch ( SQLException e ) {
                        id = database.replace( DATABASE_TABLES[0], EmotionsTable.DEVICE_ID, v );
                    }
                    if( id <= 0 ) {
                        Log.w("Light.TAG", "Failed to insert/replace row into " + uri);
                    } else {
                        count++;
                    }
                }
                database.setTransactionSuccessful();
                database.endTransaction();
                getContext().getContentResolver().notifyChange(uri, null);
                return count;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }
}
