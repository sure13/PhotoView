package com.my.dao;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MyProvider  extends ContentProvider {

    public static final String AUTHORITY = "com.action.myprovider";
    private Context context;
    private SQLiteHelper sqLiteHelper = null;
    private SQLiteDatabase sqLiteDatabase = null;

    private static UriMatcher uriMatcher = null;
    private static final int PHOTO_CODE =1;

    static {
        uriMatcher = new UriMatcher(PHOTO_CODE);
        uriMatcher.addURI(AUTHORITY,Dao.TABLE_NAME,PHOTO_CODE);
    }


    @Override
    public boolean onCreate() {
        context = getContext();
        sqLiteHelper = new SQLiteHelper(context);
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        String tableName = getTableName(uri);
        sqLiteDatabase = sqLiteHelper.getWritableDatabase();
        return sqLiteDatabase.query(tableName,projection,selection,selectionArgs,null,null,sortOrder);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        String tableName = getTableName(uri);
        sqLiteDatabase = sqLiteHelper.getWritableDatabase();
        sqLiteDatabase.insert(tableName,null,values);
        getContext().getContentResolver().notifyChange(uri,null);
        return uri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        String tableName = getTableName(uri);
        sqLiteDatabase = sqLiteHelper.getWritableDatabase();
        int delete = sqLiteDatabase.delete(tableName,selection,selectionArgs);
        getContext().getContentResolver().notifyChange(uri,null);
        return delete;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        String tableName = getTableName(uri);
        sqLiteDatabase = sqLiteHelper.getWritableDatabase();
        int update = sqLiteDatabase.update(tableName,values,selection,selectionArgs);
        getContext().getContentResolver().notifyChange(uri,null);
        return update;
    }

    private String getTableName(Uri uri){
        String tableName = null;
        switch (uriMatcher.match(uri)){
            case PHOTO_CODE:
                tableName = Dao.TABLE_NAME;
                break;
        }
        return tableName;
    }
}
