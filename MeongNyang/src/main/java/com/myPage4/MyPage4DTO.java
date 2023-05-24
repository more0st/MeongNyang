package com.myPage4;

public class MyPage4DTO {
	
	private long marketNum;
	private String sellerId;
	private String subject;
	private String content;
	private String addr;
	private String price;
	private int hitCount;
	private String reg_Date;
	private int state; // 상품상태(0:판매중, 1:온라인거래완료, 2:직거래완료)
	private int zzimCount;
	
	private long fileNum;
	private String imageFileName;
	private String[] imageFiles;
	
	
	
	
	public int getZzimCount() {
		return zzimCount;
	}
	public void setZzimCount(int zzimCount) {
		this.zzimCount = zzimCount;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public long getMarketNum() {
		return marketNum;
	}
	public void setMarketNum(long marketNum) {
		this.marketNum = marketNum;
	}
	public String getSellerId() {
		return sellerId;
	}
	public void setSellerId(String sellerId) {
		this.sellerId = sellerId;
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
	public String getAddr() {
		return addr;
	}
	public void setAddr(String addr) {
		this.addr = addr;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public int getHitCount() {
		return hitCount;
	}
	public void setHitCount(int hitCount) {
		this.hitCount = hitCount;
	}
	public String getReg_Date() {
		return reg_Date;
	}
	public void setReg_Date(String reg_Date) {
		this.reg_Date = reg_Date;
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
	public String[] getImageFiles() {
		return imageFiles;
	}
	public void setImageFiles(String[] imageFiles) {
		this.imageFiles = imageFiles;
	}
	
	

}
