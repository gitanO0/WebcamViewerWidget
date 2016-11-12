package de.appphil.webcamviewerwidget;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import java.io.File;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ViewImageActivity extends Activity {

    private static final String FILENAME = "image.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewimage);

        ImageView iv = (ImageView) findViewById(R.id.viewimage_iv);

        // load image into iv
        Picasso picasso = Picasso.with(this);
        picasso.setLoggingEnabled(true);
        // load image from internal storage
        picasso.load(new File(this.getFilesDir() + "/" + FILENAME)).into(iv);

        // allow to zoom
        PhotoViewAttacher attacher = new PhotoViewAttacher(iv);
        attacher.setMaximumScale(1000);
    }
}
