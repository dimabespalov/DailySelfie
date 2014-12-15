package com.cacao.selfie;

public class SelfieRecord {
	private String mName;
	private String mPhotoPath;
	private String mDate;
	
	public SelfieRecord(String name, String photoPath, String date){
		mName = name;
		mPhotoPath = photoPath;
		mDate = date;
	}
	
	public void setName(String mName) {
		this.mName = mName;
	}

	public String getName() {
		return mName;
	}

	public String getPhotoPath() {
		return mPhotoPath;
	}
	
	public void setDate(String date){
		this.mDate = date;
	}
	
	public String getDate() {
		return mDate;
	}
}
