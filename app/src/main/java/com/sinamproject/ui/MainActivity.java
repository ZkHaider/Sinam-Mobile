package com.sinamproject.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.sinamproject.R;
import com.sinamproject.bus.BusProvider;
import com.sinamproject.data.Document;
import com.sinamproject.data.databasehelper.DocumentSQLiteHelper;
import com.sinamproject.events.LoadOCREvent;
import com.sinamproject.events.LoadedOCREvent;
import com.sinamproject.utils.PixelToDP;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;
import retrofit.mime.TypedFile;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private FloatingActionButton mPhotoButton;
    private ImageView mDocumentImageView;
    private View mRevealView;
    private SmoothProgressBar mSmoothProgressBar;

    private Uri imageUri;
    private String mCurrentPhotoPath;
    private File imageFile;
    private TypedFile photo;

    private Bus mBus;

    private DocumentSQLiteHelper mDocumentSQLiteHelper;

    // Document
    private Document mDocument;
    private String mTitle;
    private String mRecognizedText;

    /*
    Tracking
     */
    private static final int TAKE_PHOTO_REQUEST = 1;
    private static final int GET_FROM_GALLERY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initBus();
        initDatabaseHelper();
        initActionBar();
        initViews();
        initListeners();
    }

    private void initBus() {
        mBus = getBus();
    }

    private void initDatabaseHelper() {
        mDocumentSQLiteHelper = DocumentSQLiteHelper.getDatabaseHelper(this);
    }

    private void initActionBar() {
        getSupportActionBar().setElevation(0);
    }

    private void initViews() {
        mPhotoButton = (FloatingActionButton) findViewById(R.id.takePicture);
        mDocumentImageView = (ImageView) findViewById(R.id.documentImageView);
        mRevealView = findViewById(R.id.revealView);
        mSmoothProgressBar = (SmoothProgressBar) findViewById(R.id.sinamProgressBar);
        mSmoothProgressBar.progressiveStop();
    }

    private void initListeners() {
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showListNoTitle();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        mBus.unregister(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        mBus.register(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.documents_button:
                goToListOfDocuments();
                break;
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
        Material Dialog list with no title
    */
    private void showListNoTitle() {
        new AlertDialog.Builder(this)
                .setItems(R.array.pictureOptions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                dispatchTakePicture();
                                break;
                            case 1:
                                dispatchUploadFromGallery();
                                break;
                        }
                    }
                })
                .show();
    }

    /*
    Take picture from your camera
     */
    private void dispatchTakePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Make sure that there is a camera activity to handle the intent
        if (intent.resolveActivity(getPackageManager()) != null) {

            // Create the File where the photo would go
            File picFile = null;

            try {
                picFile = createImageFile();
                imageFile = picFile;
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }

            // Continue only if the file was successfully created
            if (picFile != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(picFile));
                startActivityForResult(intent, TAKE_PHOTO_REQUEST);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create the Image File name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName, // Prefix
                ".jpg", // Suffix
                storageDir // Directory
        );

        // Save the file, path for ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        imageUri = Uri.fromFile(image);

        return image;
    }

    /*
    Take a photo from your gallery
     */
    private void dispatchUploadFromGallery() {
        // Launch gallery intent
        startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore
                .Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TAKE_PHOTO_REQUEST && resultCode == RESULT_OK) {
            aspectBitmap();
            revealView();
        } else if (requestCode == GET_FROM_GALLERY && resultCode == RESULT_OK) {
            imageUri = data.getData();
            aspectBitmap();
            String selectedImagePath = null;
            Uri selectedImageUri = data.getData();
            Cursor cursor = this.getContentResolver().query(selectedImageUri, null, null, null, null);
            if (cursor == null) {
                selectedImagePath = selectedImageUri.getPath();
                Log.d(TAG, "With the cursor: " + selectedImagePath);
            } else {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                selectedImagePath = cursor.getString(idx);
                Log.d(TAG, "With the cursor: " + selectedImagePath);
            }
            imageFile = new File(imageUri.getPath());
            Log.d(TAG, imageUri.toString());
            revealView();
        } else if (resultCode != Activity.RESULT_CANCELED) {
            Toast.makeText(this, R.string.generalError, Toast.LENGTH_LONG).show();
        }


    }

    private void aspectBitmap() {

        int viewWidth = mDocumentImageView.getMeasuredWidth();
        int viewHeight = mDocumentImageView.getMeasuredHeight();

        if (imageUri != null) {
            Picasso.with(MainActivity.this)
                    .load(imageUri)
                    .resize(viewWidth, viewHeight)
                    .centerCrop()
                    .into(mDocumentImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            // Not the most optimized code, but eh
                            Bitmap bitmap = ((BitmapDrawable) mDocumentImageView.getDrawable()).getBitmap();
                            Log.d(TAG, "Image Load Success");
                            getBus().post(new LoadOCREvent(MainActivity.this, bitmap));
                            mSmoothProgressBar.progressiveStart();
                        }

                        @Override
                        public void onError() {
                            Toast.makeText(MainActivity.this, "The image failed to load", Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Image Uri is null", Toast.LENGTH_LONG).show();
        }
    }

    private void revealView() {

        // get the center for the clipping circle
        int cx = (mRevealView.getLeft() + mRevealView.getRight()) / 2;
        int cy = (mRevealView.getTop() + mRevealView.getBottom()) / 2;

        // get the final radius for the clipping circle
        int finalRadius = Math.max(mRevealView.getWidth(), mRevealView.getHeight());

        // create the animator for this view (the start radius is zero)
        SupportAnimator anim = ViewAnimationUtils
                .createCircularReveal(mRevealView, cx, cy, 0, finalRadius);
        // make the view visible and start the animation
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.setDuration(1500);
        anim.start();
    }

    protected Bus getBus() {
        if (mBus == null)
            mBus = BusProvider.getInstance();
        return mBus;
    }

    protected void setBus(Bus bus) {
        mBus = bus;
    }

    @Subscribe
    public void onLoadedOCRTextEvent(LoadedOCREvent loadedOCREvent) {
        mSmoothProgressBar.progressiveStop();
        String result = loadedOCREvent.getOCRText();
        mRecognizedText = result;
        showDocumentDialog();
    }

    private void showDocumentDialog() {

        LayoutInflater layoutInflater = MainActivity.this.getLayoutInflater();
        View layout = layoutInflater.inflate(R.layout.document_edittext, null);
        final EditText editText = (EditText) layout.findViewById(R.id.documentEditText);


        new AlertDialogWrapper.Builder(MainActivity.this)
                .setTitle(getResources().getString(R.string.documentDialogTitle))
                .setView(layout)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (editText.getText().toString().isEmpty() || editText.getText().toString() == null)
                            Toast.makeText(MainActivity.this, "Please enter a title", Toast.LENGTH_LONG).show();
                        else {
                            mTitle = editText.getText().toString();
                            mTitle.trim();
                        }

                        mDocument = new Document();
                        mDocument.setTitle(mTitle);
                        mDocument.setRecognizedText(mRecognizedText);
                        addToDatabase(mDocument);

                        goToListOfDocuments();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    private void addToDatabase(Document document) {
        mDocumentSQLiteHelper.addDocument(document);
    }

    private void goToListOfDocuments() {
        Intent i = new Intent(MainActivity.this, ListDocumentsActivity.class);
        startActivity(i);
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

}
