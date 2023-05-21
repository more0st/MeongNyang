package com.event;

public class EventDTO {
	
	private long eNum;
	private String subject;
	private String content;
	private String start_date;
	private String end_date;
	private int enabled;
	private String userId;
	private long passCount;
	
	private long fileNum;
	private String imageFileName;
	private String[] imageFiles;
	
	
	public long getPassCount() {
		return passCount;
	}
	public void setPassCount(long passCount) {
		this.passCount = passCount;
	}
	
	public String[] getImageFiles() {
		return imageFiles;
	}
	public void setImageFiles(String[] imageFiles) {
		this.imageFiles = imageFiles;
	}
	public long geteNum() {
		return eNum;
	}
	public void seteNum(long eNum) {
		this.eNum = eNum;
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
	public String getStart_date() {
		return start_date;
	}
	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}
	public String getEnd_date() {
		return end_date;
	}
	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}
	public int getEnabled() {
		return enabled;
	}
	public void setEnabled(int enabled) {
		this.enabled = enabled;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public long getFileNum() {
		return fileNum;
	}
	public void setFileNum(long fileNum) {
		this.fileNum = fileNum;
	}
	public String getImageFileName() {
		return imageFileName;
	}
	public void setImageFileName(String imageFileName) {
		this.imageFileName = imageFileName;
	}
	
	
}
