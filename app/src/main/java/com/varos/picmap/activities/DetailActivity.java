package com.varos.picmap.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.varos.picmap.R;
import com.varos.picmap.adapters.SectionsPagerAdapter;
import com.varos.picmap.models.ImageModel;
import com.varos.picmap.utils.DepthPageTransformer;

import java.util.ArrayList;

/**
 * Created by David on 09.12.2016.
 */

public class DetailActivity extends AppCompatActivity {
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private View.OnClickListener mOnClickListener=new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                //case R.id.editBtn:
                   // openEditor();
                    //break;
                //case R.id.showOnMapBtn:
                    //showOnMap();
                    //break;

            }
        }
    };

    public ArrayList<ImageModel> data = new ArrayList<>();
    public ArrayList<String> path = new ArrayList<>();
    int pos;
    Uri mImageUri;
    public static final int EDITOR_REQUEST=1;

    Toolbar toolbar;
    Button mEditBtn,mShowBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        init();

//        toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
//        setSupportActionBar(toolbar);

        data = getIntent().getParcelableArrayListExtra("data");
        pos = getIntent().getIntExtra("pos", 0);
        mImageUri=Uri.parse("file://"+data.get(pos).getUrl());
        setTitle(data.get(pos).getName());


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), data);
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setPageTransformer(true, new DepthPageTransformer());

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                //noinspection ConstantConditions
                setTitle(data.get(position).getName());
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });  mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(pos);



    }

    /*private void openEditor() {
        ToolLoaderFactory.Tools toolses[]={ToolLoaderFactory.Tools.COLOR,ToolLoaderFactory.Tools.DRAW,ToolLoaderFactory.Tools.EFFECTS,
                ToolLoaderFactory.Tools.BLEMISH,ToolLoaderFactory.Tools.ADJUST,ToolLoaderFactory.Tools.ENHANCE,ToolLoaderFactory.Tools.FOCUS,
                ToolLoaderFactory.Tools.LIGHTING,ToolLoaderFactory.Tools.BLUR,ToolLoaderFactory.Tools.MEME,ToolLoaderFactory.Tools.OVERLAYS,
                ToolLoaderFactory.Tools.ORIENTATION,ToolLoaderFactory.Tools.STICKERS,ToolLoaderFactory.Tools.TEXT,ToolLoaderFactory.Tools.SHARPNESS

        };
        Intent imageEditorintent=new AdobeImageIntent.Builder(getApplicationContext()).setData(mImageUri).withToolList(toolses).build();
        startActivityForResult(imageEditorintent,2);
    }
*/
    private void showOnMap() {
        path.add(data.get(pos).getUrl());
        Intent intent = new Intent(DetailActivity.this, MapsActivity.class);
        intent.putStringArrayListExtra("data", path);
        startActivity(intent);
    }

    private void init() {
        findViewById(R.id.editBtn).setOnClickListener(mOnClickListener);
        findViewById(R.id.showOnMapBtn).setOnClickListener(mOnClickListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==EDITOR_REQUEST){
            Log.d("XIAOMI","NKAR EDIT");
        }
    }

    public static class PlaceholderFragment extends Fragment {

        String name, url;
        int pos;
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String ARG_IMG_TITLE = "image_title";
        private static final String ARG_IMG_URL = "image_url";

        @Override
        public void setArguments(Bundle args) {
            super.setArguments(args);
            this.pos = args.getInt(ARG_SECTION_NUMBER);
            this.name = args.getString(ARG_IMG_TITLE);
            this.url = args.getString(ARG_IMG_URL);
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber, String name, String url) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putString(ARG_IMG_TITLE, name);
            args.putString(ARG_IMG_URL, url);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public void onStart() {
            super.onStart();

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

            final ImageView imageView = (ImageView) rootView.findViewById(R.id.detail_image);

            Glide.with(getActivity()).load(url).thumbnail(0.1f).into(imageView);

            return rootView;
        }

    }
}
