package com.map;

public class MapDTO {
	private long mapNum;
	private String subject;
	private String content;
	private int hitCount;
	private String reg_date;
	private String userId;
	private String addr;

	private Long fileNum;
	private String imageFilename;
	private String[] imageFiles;
	
	
	private String userName;
	
	public long getMapNum() {
		return mapNum;
	}

	public void setMapNum(long mapNum) {
		this.mapNum = mapNum;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getHitCount() {
		return hitCount;
	}

	public void setHitCount(int hitCount) {
		this.hitCount = hitCount;
	}

	public String getReg_date() {
		return reg_date;
	}

	public void setReg_date(String reg_date) {
		this.reg_date = reg_date;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public Long getFileNum() {
		return fileNum;
	}

	public void setFileNum(Long fileNum) {
		this.fileNum = fileNum;
	}

	public String getImageFilename() {
		return imageFilename;
	}

	public void setImageFilename(String imageFilename) {
		this.imageFilename = imageFilename;
	}

	public String[] getImageFiles() {
		return imageFiles;
	}

	public void setImageFiles(String[] imageFiles) {
		this.imageFiles = imageFiles;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	
	
}
