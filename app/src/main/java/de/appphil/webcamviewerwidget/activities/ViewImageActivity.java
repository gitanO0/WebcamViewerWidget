package de.appphil.webcamviewerwidget.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import java.io.File;

import de.appphil.webcamviewerwidget.R;
import de.appphil.webcamviewerwidget.utils.Vars;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ViewImageActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewimage);

        ImageView iv = (ImageView) findViewById(R.id.viewimage_iv);

        // load image from internal storage into iv
        Picasso picasso = Picasso.with(this);
        picasso.setLoggingEnabled(true);
        File file = new File(this.getFilesDir() + "/" + Vars.IMAGE_FILENAME);
        picasso.invalidate(file);
        picasso.load(file).into(iv);

        // allow to zoom
        PhotoViewAttacher attacher = new PhotoViewAttacher(iv);
        attacher.setMaximumScale(1000);
    }
}