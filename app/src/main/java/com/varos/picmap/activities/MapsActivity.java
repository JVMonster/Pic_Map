package com.varos.picmap.activities;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapEncoder;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.gif.GifResourceEncoder;
import com.bumptech.glide.load.resource.gifbitmap.GifBitmapWrapperResourceEncoder;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;

import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.varos.picmap.R;
import com.varos.picmap.models.BaseImageMarker;
import com.varos.picmap.models.ImageMarker;
import com.varos.picmap.models.MultiDrawable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.realm.Realm;
import io.realm.RealmResults;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{

    private Random mRandom = new Random(1984);

    public ClusterManager<ImageMarker> mClusterManager;

    GoogleMap mMap;
    ArrayList paths=new ArrayList();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        paths=getIntent().getStringArrayListExtra("data");
        setUpMap();

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (mMap != null) {
            return;
        }
        mMap = googleMap;
        startDemo();



    }

    @Override
    protected void onResume() {

        super.onResume();
        setUpMap();
    }
    protected GoogleMap getMap() {
        return mMap;
    }
    private void setUpMap() {
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
    }

    public class ImageRenderer extends DefaultClusterRenderer<ImageMarker>
    {



        private final IconGenerator mIconGenerator = new IconGenerator(getApplicationContext());
        private final IconGenerator mClusterIconGenerator = new IconGenerator(getApplicationContext());
        private final ImageView mImageView;
        private final ImageView mClusterImageView;
        private final int mDimension;

        public ImageRenderer() {
            super(getApplicationContext(), getMap(), mClusterManager);

            View multiProfile = getLayoutInflater().inflate(R.layout.cluster, null);
            mClusterIconGenerator.setContentView(multiProfile);
            mClusterImageView = (ImageView) multiProfile.findViewById(R.id.image);

            mImageView = new ImageView(getApplicationContext());
            mDimension = (int) getResources().getDimension(R.dimen.custom_profile_image);
            mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));
            int padding = (int) getResources().getDimension(R.dimen.custom_profile_padding);
            mImageView.setPadding(padding, padding, padding, padding);
            mIconGenerator.setContentView(mImageView);
        }

        @Override
        protected void onBeforeClusterItemRendered(final ImageMarker imageMarker, MarkerOptions markerOptions) {
            // Draw a single person.
            // Set the info window to show their name.
            /*GifBitmapWrapperResourceEncoder encoder =new GifBitmapWrapperResourceEncoder(new BitmapEncoder(Bitmap.CompressFormat.JPEG, 50), new GifResourceEncoder(Glide.get(getApplicationContext()).getBitmapPool()));
            // Draw a single person.
            // Set the info window to show their name.
            Glide.with(getApplicationContext()).load(imageMarker.getmImageUrl()).asBitmap().override(50,50).centerCrop().diskCacheStrategy(DiskCacheStrategy.RESULT).into(mImageView);
            Bitmap icon = mIconGenerator.makeIcon();
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(imageMarker.mImageName);*/
            Glide
                    .with(getApplicationContext())
                    .load(imageMarker.getmImageUrl())
                    .thumbnail(0.5f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(new SimpleTarget<GlideDrawable>() {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                            mImageView.setImageDrawable(resource);
                            Bitmap icon = mIconGenerator.makeIcon();
                            Marker markerToChange = null;
                            for (Marker marker : mClusterManager.getMarkerCollection().getMarkers()) {
                                if (marker.getPosition().equals(imageMarker.getPosition())) {
                                    markerToChange = marker;
                                }
                            }
                            // if found - change icon
                            if (markerToChange != null) {
                                markerToChange.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
                            }

                        }
                    });
           // Bitmap icon = mIconGenerator.makeIcon();
            //markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        }

        @Override
        protected void onBeforeClusterRendered(final Cluster<ImageMarker> cluster, final MarkerOptions markerOptions) {
            // Draw multiple people.
            // Note: this method runs on the UI thread. Don't spend too much time in here (like in this example).
           /* List<Drawable> profilePhotos = new ArrayList<Drawable>(Math.min(4, cluster.getSize()));
            int width = mDimension;
            int height = mDimension;

            for (ImageMarker p : cluster.getItems()) {
                // Draw 4 at most.
                if (profilePhotos.size() == 4) break;
                Drawable drawable = GlideBitmapDrawable.createFromPath(p.getmImageUrl());
                drawable.setBounds(0, 0, width, height);
                profilePhotos.add(drawable);
            }
            MultiDrawable multiDrawable = new MultiDrawable(profilePhotos);
            multiDrawable.setBounds(0, 0, width, height);

            mClusterImageView.setImageDrawable(multiDrawable);
            Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));*/
            final List<Drawable> profilePhotos = new ArrayList<Drawable>(Math.min(4, cluster.getSize()));
            final int width = mDimension;
            final int height = mDimension;

            int i = 0;

            for (final ImageMarker p : cluster.getItems()) {
                // Draw 4 at most.
                i++;
                Glide
                        .with(getApplicationContext())
                        .load(p.getmImageUrl())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(new SimpleTarget<GlideDrawable>() {
                            @Override
                            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                                resource.setBounds(0, 0, width, height);
                                profilePhotos.add(resource);
                                MultiDrawable multiDrawable = new MultiDrawable(profilePhotos);
                                multiDrawable.setBounds(0, 0, width, height);

                                mClusterImageView.setImageDrawable(multiDrawable);
                                Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
                                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
                            }
                        });

                if (i == 4) break;
            }
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            // Always render clusters.
            return cluster.getSize() > 1;
        }


    /*@Override
    public boolean onClusterClick(Cluster<ImageMarker> cluster) {
        // Show a toast with some info when the cluster is clicked.
        String firstName = cluster.getItems().iterator().next().getmMarkerName();
        Toast.makeText(this, cluster.getSize() + " (including " + firstName + ")", Toast.LENGTH_SHORT).show();

        // Zoom in the cluster. Need to create LatLngBounds and including all the cluster items
        // inside of bounds, then animate to center of the bounds.

        // Create the builder to collect all essential cluster items for the bounds.
        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (ClusterItem item : cluster.getItems()) {
            builder.include(item.getPosition());
        }
        // Get the LatLngBounds
        final LatLngBounds bounds = builder.build();

        // Animate camera to the bounds
        try {
            getMap().animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<ImageMarker> cluster) {
        // Does nothing, but you could go to a list of the users.
    }

    @Override
    public boolean onClusterItemClick(ImageMarker item) {
        // Does nothing, but you could go into the user's profile page, for example.
        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(ImageMarker item) {
        // Does nothing, but you could go into the user's profile page, for example.
    }*/




    }
    public void startDemo() {
        getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(51.503186, -0.126446), 9.5f));

        mClusterManager = new ClusterManager<ImageMarker>(getApplicationContext(), getMap());
        mClusterManager.setRenderer(new ImageRenderer());
        getMap().setOnCameraIdleListener(mClusterManager);
        getMap().setOnMarkerClickListener(mClusterManager);
        getMap().setOnInfoWindowClickListener(mClusterManager);
      /*  mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterInfoWindowClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);
        mClusterManager.setOnClusterItemInfoWindowClickListener(this);*/

        addItems();
        mClusterManager.cluster();
    }

    private void addItems() {
        for(int i=0;i<10;i++)
        {

            mClusterManager.addItem(new ImageMarker(position(),paths.get(i).toString(),paths.get(i).toString()));
        }

    }
    private LatLng position() {
        return new LatLng(random(51.6723432, 51.38494009999999), random(0.148271, -0.3514683));
    }

    private double random(double min, double max) {
        return mRandom.nextDouble() * (max - min) + min;
    }
}




