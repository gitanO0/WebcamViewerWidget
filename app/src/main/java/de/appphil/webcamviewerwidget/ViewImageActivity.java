package de.appphil.webcamviewerwidget;


import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.IOException;

import de.appphil.webcamviewerwidget.utils.CurrentLink;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ViewImageActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewimage);

        // get current link name
        String currentLinkName = null;
        try {
            currentLinkName = CurrentLink.getCurrentLinkName(getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if(currentLinkName == null) return;

        String link = LinkListIO.getLinkByName(getApplicationContext(), currentLinkName);

        ImageView iv = (ImageView) findViewById(R.id.viewimage_iv);

        // load image into iv
        Picasso picasso = Picasso.with(this);
        picasso.setLoggingEnabled(true);
        picasso.load(link).into(iv);

        PhotoViewAttacher attacher = new PhotoViewAttacher(iv);
        attacher.setMaximumScale(1000);
    }
}
