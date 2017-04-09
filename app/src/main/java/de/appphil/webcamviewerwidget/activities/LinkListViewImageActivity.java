package de.appphil.webcamviewerwidget.activities;


import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import de.appphil.webcamviewerwidget.R;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class LinkListViewImageActivity extends AppCompatActivity {

    public static final String EXTRA_LINK_NAME = "name";
    public static final String EXTRA_LINK_LINK = "link";
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 3542;

    private ImageView iv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linklistviewimage);

        String name = getIntent().getExtras().getString(EXTRA_LINK_NAME);
        String link = getIntent().getExtras().getString(EXTRA_LINK_LINK);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(name);
        setSupportActionBar(toolbar);

        // shadow for toolbar
        if(Build.VERSION.SDK_INT >= 21) {
            toolbar.setElevation(25);
        }

        iv = (ImageView) findViewById(R.id.linklistviewimage_iv);

        PhotoView pv = (PhotoView) findViewById(R.id.linklistviewimage_iv);
        final ProgressBar pb = (ProgressBar) findViewById(R.id.linklistviewimage_pb);

        Picasso picasso = Picasso.with(this);
        picasso.setLoggingEnabled(true);
        picasso.invalidate(link);
        picasso.load(link).into(pv, new Callback() {
            @Override
            public void onSuccess() {
                pb.setVisibility(View.GONE);
            }

            @Override
            public void onError() {
                pb.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.download_failed), Toast.LENGTH_LONG).show();
            }
        });

        // allow to zoom
        PhotoViewAttacher attacher = new PhotoViewAttacher(pv);
        attacher.setMaximumScale(1000);
        attacher.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showSaveDialog();
                return true;
            }
        });
    }

    /***
     * Shows a dialog to ask the user if the image should be saved.
     */
    private void showSaveDialog() {
        // instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // cain together various setter methods to set the dialog characteristics
        builder.setMessage(R.string.save_image_dialog_message).setTitle(R.string.save_image_dialog_title);

        // Add the buttons
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // user clicked yes button
                saveImage();
                dialog.cancel();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // user cancelled the dialog
                dialog.cancel();
            }
        });

        // get the AlertDialog from create() and then show it
        builder.create().show();
    }

    /***
     * Saves the currently shown image.
     */
    private void saveImage() {
        // check if app has the permission to save the image
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(permissionCheck == PackageManager.PERMISSION_GRANTED) {
            iv.setDrawingCacheEnabled(true);
            Bitmap b = iv.getDrawingCache();
            // TODO: get name of the webcam and set this name as the title or description of the saved image
            insertImage(getContentResolver(), b, "WebcamViewerWidget", "WebcamViewerWidget");
            // give the user some feedback
            Toast.makeText(this, getResources().getString(R.string.image_saved), Toast.LENGTH_LONG).show();
        } else {
            // app has no permission so ask for it and try to get it
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
    }

    private String insertImage(ContentResolver contentResolver, Bitmap source, String title, String description) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, title);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, title);
        values.put(MediaStore.Images.Media.DESCRIPTION, description);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());

        Uri url = null;
        String stringUrl = null;

        try {
            url = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            if (source != null) {
                OutputStream outputStream = contentResolver.openOutputStream(url);
                try {
                    source.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
                } finally {
                    outputStream.close();
                }

                long id = ContentUris.parseId(url);
                Bitmap miniThumb = MediaStore.Images.Thumbnails.getThumbnail(contentResolver, id, MediaStore.Images.Thumbnails.MINI_KIND, null);
                storeThumbnail(contentResolver, miniThumb, id, 50F, 50F, MediaStore.Images.Thumbnails.MICRO_KIND);
            } else {
                contentResolver.delete(url, null, null);
                url = null;
            }
        } catch (Exception e) {
            if (url != null) {
                contentResolver.delete(url, null, null);
                url = null;
            }
        }

        if (url != null) {
            stringUrl = url.toString();
        }

        return stringUrl;
    }

    private Bitmap storeThumbnail(ContentResolver contentResolver, Bitmap source, long id, float width, float height, int kind) {
        // matrix for scaling
        Matrix matrix = new Matrix();

        float scaleX = width / source.getWidth();
        float scaleY = height / source.getHeight();

        matrix.setScale(scaleX, scaleY);

        Bitmap thumbnail = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);

        ContentValues values = new ContentValues(4);
        values.put(MediaStore.Images.Thumbnails.KIND, kind);
        values.put(MediaStore.Images.Thumbnails.IMAGE_ID, (int)id);
        values.put(MediaStore.Images.Thumbnails.HEIGHT, thumbnail.getHeight());
        values.put(MediaStore.Images.Thumbnails.WIDTH, thumbnail.getWidth());

        Uri url = contentResolver.insert(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, values);

        try {
            OutputStream outputStream = contentResolver.openOutputStream(url);
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.close();
            return thumbnail;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    // try saving again
                    saveImage();
                } else {
                    // permission denied
                }
                return;
            }
        }
    }
}
