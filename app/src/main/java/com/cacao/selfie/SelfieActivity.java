package com.cacao.selfie;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;


public class SelfieActivity extends ListActivity {

    private static final String TAG = "Selfie";
	private static final int REQUEST_IMAGE_CAPTURE = 1;
	private static final long INITIAL_ALARM_DELAY = 2*60*1000L;
	private String mCurrentPhotoPath;
	private String mCurrentPhotoName;
	private String mCurrentPhotoDate;
	
	private SelfieAdapter mSelfieAdapter;
	private AlarmManager mAlarmManager;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mSelfieAdapter = new SelfieAdapter(this);
        
        setListAdapter(mSelfieAdapter);
        getListView().setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.parse("file://" + mSelfieAdapter.getList().get(position).getPhotoPath()), "image/*");
				startActivity(intent);
			}
        	
        });
        
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent mNotificationReceiverIntent = new Intent(SelfieActivity.this, SelfieAlarmNotification.class);
		PendingIntent mNotificationReceierPendingIntent = PendingIntent.getBroadcast(SelfieActivity.this, 0, mNotificationReceiverIntent, 0);
		mAlarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + INITIAL_ALARM_DELAY, mNotificationReceierPendingIntent );
    }
    
    private File createImageFile() throws IOException {
	    // Create an image file name
		Log.i(TAG, "createImageFile");
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    Log.i(TAG, "timeStamp" + timeStamp);
	    String imageFileName = "Selfie_" + timeStamp;
	    File storageDir = getExternalFilesDir("Selfie");
	    Log.i(TAG, "storageDir" + storageDir);
	    File image = File.createTempFile(
	        imageFileName,  /* prefix */
	        ".jpg",         /* suffix */
	        storageDir      /* directory */
	    );
	    
	    // Save a file: path for use with ACTION_VIEW intents
	    mCurrentPhotoPath = image.getAbsolutePath();
	    mCurrentPhotoName = image.getName();
	    mCurrentPhotoDate = image.getName().replace("Selfie_", "");
	    Log.i(TAG, mCurrentPhotoPath);
	    return image;
	}
	
	private void dispatchTakePictureIntent() {
		Log.i(TAG, "dispatchTakePictureIntent");
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
        	File photoFile = null;
        	try {
				photoFile = createImageFile();
			} catch (IOException e) {
				//e.getStackTrace();
				e.printStackTrace();
			}
        	if (photoFile != null){
        		takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(photoFile));
        		startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
			SelfieRecord selfie = new SelfieRecord(mCurrentPhotoName, mCurrentPhotoPath, mCurrentPhotoDate);
			mSelfieAdapter.add(selfie);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	protected void onResume() {
		Log.i(TAG,"onResume");
		super.onResume();
		mSelfieAdapter.removeAllViews();
		ArrayList<SelfieRecord> list = mSelfieAdapter.getList();
		for (File image : getExternalFilesDir("Selfie").listFiles()) {
			SelfieRecord checkSelfie = new SelfieRecord(image.getName(), image.getAbsolutePath(), image.getName().replace("Selfie_", ""));
			if (!list.contains(checkSelfie)){
					mSelfieAdapter.add(checkSelfie);
			}
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.selfie, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_camera){
        	dispatchTakePictureIntent();
        	return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
