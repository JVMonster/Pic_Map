package com.varos.picmap.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import com.bignerdranch.android.multiselector.ModalMultiSelectorCallback;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.bignerdranch.android.multiselector.SwappingHolder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.varos.picmap.R;
import com.varos.picmap.models.ImageModel;
import java.util.ArrayList;

/**
 * Created by David on 10.12.2016.
 */

public class FirebaseBaseGalleryFragment extends Fragment {
    private ProgressDialog mProgressDialog;
    RecyclerView mRecyclerView;
    MultiSelector mMultiSelector=new MultiSelector();
    ArrayList<ImageModel> mImageModels;
    private boolean mSubTitleVisible;
    private FirebaseStorage mFirebaseStorage=FirebaseStorage.getInstance();
    private StorageReference mStorageReference;
    private StorageReference mStorageReferenceImages;
    private String mMail;
    private String mUid;
    private FirebaseDatabase mDataBase= FirebaseDatabase.getInstance();
    private DatabaseReference mDataBaseReference=mDataBase.getReferenceFromUrl("https://picmap-150621.firebaseio.com");
    private ArrayList<String> mUris =new ArrayList<String>();
    GenericTypeIndicator <ArrayList<String>> mIndicator=new GenericTypeIndicator<ArrayList<String>>() {};


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.image_title);
        setRetainInstance(true);
        mSubTitleVisible=false;
        mStorageReference = mFirebaseStorage.getReferenceFromUrl("gs://picmap-150621.appspot.com");
        mStorageReferenceImages = mStorageReference.child(mMail);

    }

    public void setImagesPath(ArrayList<ImageModel> imageModels)
    {
        this.mImageModels=imageModels;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.recycler_base,container,false);
        if (mSubTitleVisible) getActionBar().setSubtitle(R.string.subtitle);
        mRecyclerView= (RecyclerView) v.findViewById(R.id.recycler_bases);
        ImageModelAdpter adpter =new ImageModelAdpter();
        mRecyclerView.setAdapter(adpter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));


        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.base_fragment_menu,menu);
        MenuItem menuItem=menu.findItem(R.id.menu_item_show_subtitle);
        if(mSubTitleVisible&&menu!=null) menuItem.setTitle(R.string.hide_subtitle);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(R.menu.base_fragment_menu_mul,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.menu_item_new_crime:
                return true;
            case R.id.menu_item_show_subtitle:
               /* ActionBar actionBar = getActionBar();
                if (actionBar.getSubtitle() == null) {
                    actionBar.setSubtitle(R.string.subtitle);
                    mSubTitleVisible = true;
                    item.setTitle(R.string.hide_subtitle);
                }  else {
                    actionBar.setSubtitle(null);
                    mSubTitleVisible = false;
                    item.setTitle("show");
                }*/
                FireBaseUploadedImagesFragment uploadedImagesFragment=new FireBaseUploadedImagesFragment();
                FragmentTransaction transaction=getFragmentManager().beginTransaction();
                uploadedImagesFragment.setId(mUid);
                uploadedImagesFragment.setMail(mMail);
                transaction.replace(R.id.container_layout,uploadedImagesFragment);
                transaction.commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class ImageModelHolder extends SwappingHolder implements View.OnClickListener,View.OnLongClickListener{
        private final ImageView mImageView;
        private ImageModel mImageModel;
        private int mPos;
        private long mId;


        @Override
        public void setSelectionModeBackgroundDrawable(Drawable selectionModeBackgroundDrawable) {
            super.setSelectionModeBackgroundDrawable(selectionModeBackgroundDrawable);
        }

        public ImageModelHolder(View itemView) {
            super(itemView, mMultiSelector);
            mImageView= (ImageView) itemView.findViewById(R.id.item_img_base);
            itemView.setOnClickListener(ImageModelHolder.this);
            itemView.setOnLongClickListener(ImageModelHolder.this);
            itemView.setLongClickable(true);

        }
        public void bindImageModel(ImageModel imageModel,int position,long id) {
            mPos=position;
            mImageModel=imageModel;
            mId=id;
            Glide.with(getContext()).load(imageModel.getUrl())
                    .override(200,200)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(mImageView);

        }


        @Override
        public void onClick(View view) {
          if(mImageModel==null)
              return;
            if(!mMultiSelector.tapSelection(ImageModelHolder.this)) {
                mImageView.setBackgroundColor(R.color.colorPrimaryDark);
                mMultiSelector.setSelected(mPos,mId,true);
            }
        }

        @Override
        public boolean onLongClick(View view) {

            ((AppCompatActivity)getActivity()).startSupportActionMode(multiSelectorCallback);
            mMultiSelector.setSelectable(true);
            mMultiSelector.setSelected(ImageModelHolder.this,true);
            return true;
        }
    }

    private class ImageModelAdpter extends RecyclerView.Adapter<ImageModelHolder> {

       @Override
       public ImageModelHolder onCreateViewHolder(ViewGroup parent, int viewType) {
           View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_base,parent,false);
           return new ImageModelHolder(view);
       }

       @Override
       public void onBindViewHolder(ImageModelHolder holder, int position) {
               ImageModel imageModel=mImageModels.get(position);
               holder.bindImageModel(imageModel,position,holder.getItemId());
       }

       @Override
       public int getItemCount() {
           return mImageModels.size();
       }
   }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public void showHorizontalProgressDialog(String title, String body) {

        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.setTitle(title);
            mProgressDialog.setMessage(body);
        } else {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setTitle(title);
            mProgressDialog.setMessage(body);
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setProgress(0);
            mProgressDialog.setMax(100);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }
    }

    public void updateProgress(int progress) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.setProgress(progress);
        }
    }

    ModalMultiSelectorCallback multiSelectorCallback=new ModalMultiSelectorCallback(mMultiSelector) {

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            //super.onCreateActionMode(actionMode, menu);
            //getActivity().getMenuInflater().inflate(R.menu.base_fragment_menu_mul, menu);
            MenuInflater menuInflater =actionMode.getMenuInflater();
            menuInflater.inflate(R.menu.base_fragment_menu_mul,menu);
            return true;
        }


        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            if(item.getItemId()==R.id.menu_item_upload_images)
                mode.finish();
            for(int i=0;i<mImageModels.size();i++)
            {
                if(mMultiSelector.isSelected(i,0))
                    uploadImage(mImageModels.get(i));
            }
            mMultiSelector.clearSelections();
            return true;
        }
    };

    public void setMail(String mail)
    {
        this.mMail=mail;
    }

    public  void setId(String id){
        this.mUid=id;}

    public void uploadImage(ImageModel imageModel) {
        final Uri uri= Uri.parse("file://"+imageModel.getUrl());
        StorageReference uploadStorageReference = mStorageReferenceImages.child(imageModel.getName());
        Log.d("MY",uri.getLastPathSegment()+"");
        final UploadTask uploadTask = uploadStorageReference.putFile(uri);
        showHorizontalProgressDialog("Uploading", "Please wait...");

        uploadTask
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        hideProgressDialog();
                        final Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        final DatabaseReference imagesReference=mDataBaseReference.child("users").child(mUid);
                        final DatabaseReference boolReference=imagesReference.child("bool");
                        imagesReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if(dataSnapshot!=null)
                                    mUris = dataSnapshot.getValue(mIndicator);
                                imagesReference.removeEventListener(this);
                            }


                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                                imagesReference.removeEventListener(this);
                            }
                        });
                        boolReference.setValue(true);

                        mUris.add(downloadUrl.toString());
                        imagesReference.setValue(mUris);

                        Log.d("MainActivity", downloadUrl.toString());


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        exception.printStackTrace();
                        // Handle unsuccessful uploads
                        hideProgressDialog();
                    }
                })
                .addOnProgressListener(getActivity(), new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        int progress = (int) (100 * (float) taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        Log.i("Progress", progress + "");
                        updateProgress(progress);
                    }
                });

    }

    protected ActionBar getActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

}
