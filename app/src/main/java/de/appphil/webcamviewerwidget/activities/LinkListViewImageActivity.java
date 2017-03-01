package de.appphil.webcamviewerwidget.activities;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import de.appphil.webcamviewerwidget.R;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class LinkListViewImageActivity extends AppCompatActivity {

    public static final String EXTRA_LINK_NAME = "name";
    public static final String EXTRA_LINK_LINK = "link";

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

        PhotoView iv = (PhotoView) findViewById(R.id.linklistviewimage_iv);
        final ProgressBar pb = (ProgressBar) findViewById(R.id.linklistviewimage_pb);

        Picasso picasso = Picasso.with(this);
        picasso.setLoggingEnabled(true);
        picasso.invalidate(link);
        picasso.load(link).into(iv, new Callback() {
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
        PhotoViewAttacher attacher = new PhotoViewAttacher(iv);
        attacher.setMaximumScale(1000);
    }
}
