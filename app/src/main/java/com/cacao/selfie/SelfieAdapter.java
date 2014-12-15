package com.cacao.selfie;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SelfieAdapter extends BaseAdapter{
	
	private static final String TAG = "Selfie";

	private ArrayList<SelfieRecord> mSelfieList = new ArrayList<SelfieRecord>();
	
	private Context mContext;
	private static LayoutInflater inflater = null;
	

	public SelfieAdapter(Context context) {
		mContext = context;
		inflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		return mSelfieList.size();
	}

	@Override
	public Object getItem(int position) {
		return mSelfieList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View newView = convertView;
		ViewHolder holder;

		SelfieRecord curr = mSelfieList.get(position);

		if (null == convertView) {
			holder = new ViewHolder();
			newView = inflater.inflate(R.layout.selfie_record_view, null);
			holder.thumbnail = (ImageView) newView.findViewById(R.id.thumbnail);
			holder.imageName = (TextView) newView.findViewById(R.id.imageName);
			holder.date = (TextView) newView.findViewById(R.id.date);
			newView.setTag(holder);
			
		} else {
			holder = (ViewHolder) newView.getTag();
		}

		holder.thumbnail.setImageBitmap(getThumbnail(curr.getPhotoPath()));
		holder.imageName.setText("Name: " + curr.getName());
		holder.date.setText("Date: " + curr.getDate());

		return newView;
	}

	
	static class ViewHolder {
		
		ImageView thumbnail;
		TextView imageName;
		TextView date;
	}
	

	public void add(SelfieRecord listItem) {
		mSelfieList.add(listItem);
		notifyDataSetChanged();
	}
	
	private Bitmap getThumbnail(String photoPath) {
		Log.i(TAG, "setThumbnail");
	    // Get the dimensions of the View
	    int targetW = 100;
	    int targetH = 100;

	    // Get the dimensions of the bitmap
	    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
	    bmOptions.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(photoPath, bmOptions);
	    int photoW = bmOptions.outWidth;
	    int photoH = bmOptions.outHeight;

	    // Determine how much to scale down the image
	    int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

	    // Decode the image file into a Bitmap sized to fill the View
	    bmOptions.inJustDecodeBounds = false;
	    bmOptions.inSampleSize = scaleFactor;
	    bmOptions.inPurgeable = true;

	    Bitmap bitmap = BitmapFactory.decodeFile(photoPath, bmOptions);
	    Log.i(TAG, "Thumbnail created");
	    return bitmap;
	}

	
	public ArrayList<SelfieRecord> getList(){
		return mSelfieList;
	}
	
	public void removeAllViews(){
		mSelfieList.clear();
		this.notifyDataSetChanged();
	}
}
