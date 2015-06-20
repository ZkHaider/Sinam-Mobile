package com.sinamproject.data.databasehelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.sinamproject.data.Document;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by ZkHaider on 4/18/15.
 */

public class DocumentSQLiteHelper extends SQLiteOpenHelper {

    private static final String TAG = DocumentSQLiteHelper.class.getSimpleName();

    // Singleton pattern
    private static DocumentSQLiteHelper mDocumentSQLiteHelper;

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "DocumentDB";

    // Define some constants
    private static final String TABLE_DOCUMENTS = "documents";
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_OCR_RESULT = "result";

    private static final String[] COLUMNS = { KEY_ID, KEY_TITLE, KEY_OCR_RESULT };

    public DocumentSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DocumentSQLiteHelper getDatabaseHelper(Context context) {
        if (mDocumentSQLiteHelper == null)
            mDocumentSQLiteHelper = new DocumentSQLiteHelper(context);
        return mDocumentSQLiteHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL Statements to create book table
        String CREATE_BOOK_TABLE = "CREATE TABLE documents ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, " + "result TEXT )";
        // Create documents table
        db.execSQL(CREATE_BOOK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older documents table if existed
        db.execSQL("DROP TABLE IF EXISTS documents");
        // Create fresh documents table
        this.onCreate(db);
    }

    /*
    CRUD OPERATIONS FOR THE DATABASE
     */

    public void addDocument(Document document) {
        // For logging purposes
        Log.d("addDocument", document.toString());

        // 1. Get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. Create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, document.getTitle());
        values.put(KEY_OCR_RESULT, document.getRecognizedText());

        // 3. Insert
        db.insert(TABLE_DOCUMENTS, null, values);

        // 5. Close the database
        db.close();
    }

    public Document getDocument(int id) {

        // 1. Reference to the readable database
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. Build the query
        Cursor cursor = db.query(TABLE_DOCUMENTS,                       // Reference the table
                                 COLUMNS,                               // Reference the column
                                 " id = ?",                             // Select that id
                                 new String[] { String.valueOf(id) },   // Selections arguments
                                 null,                                  // group by
                                 null,                                  // having
                                 null,                                  // order by
                                 null);                                 // limit

        // 3. If we get the results get the first one
        if (cursor != null)
            cursor.moveToFirst();

        // 4. Build a document object
        Document document = new Document();
        document.setId((Integer.parseInt(cursor.getString(0))));
        document.setTitle(cursor.getString(1));
        document.setRecognizedText(cursor.getString(2));

        // 5. Log the document
        Log.d("getDocument", document.toString());

        // 6. Return the document
        return document;
    }

    public List<Document> getAllDocuments() {

        // 1. Initialize LinkedList
        List<Document> documents = new LinkedList<>();

        // 2. Build the query
        String query = "SELECT * FROM " + TABLE_DOCUMENTS;

        // 3. Get reference to writable db
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 4. Go over all the documents, build the document and add to the list
        Document document = null;
        if (cursor.moveToFirst()) {
            do {
                document = new Document();
                document.setId(Integer.parseInt(cursor.getString(0)));
                document.setTitle(cursor.getString(1));
                document.setRecognizedText(cursor.getString(2));

                // Add the document to the list
                documents.add(document);
            } while (cursor.moveToNext());
        }

        Log.d("getAllDocuments", documents.toString());

        // 5. Return the documents
        return documents;
    }

    public int updateDocument(Document document) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put("title", document.getTitle());
        values.put("author", document.getRecognizedText());

        // 3. updating row
        int i = db.update(TABLE_DOCUMENTS,                          // table
                values,                                             // column/value
                KEY_ID + " = ?",                                    // selections
                new String[] { String.valueOf(document.getId()) }); // selection args

        // 4. close
        db.close();

        return i;
    }

    public void deleteDocument(Document document) {
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(TABLE_DOCUMENTS,                                  // table name
                KEY_ID + " = ?",                                    // selections
                new String[] { String.valueOf(document.getId()) }); // selections args

        // 3. close
        db.close();

        //log
        Log.d("deleteDocument", document.toString());
    }



}
