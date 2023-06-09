package com.myPage;

public class MyPageDTO {
	
	private long marketnum;	//	판매글 no
	private String sellerid;	// 판매자 id
	private String buyerid;		// 구매자 id
	private String subject;	// 판매글 제목
	private String content;	// 판매글 내용
	private String addr;	// 거래할 주소
	private String price;	// 가격
	private int hitCount;	// 조회수
	private int state;		// 상품상태(0:판매중, 1:온라인거래완료, 2:직거래완료)
	private String reg_date;	// 글 등록날짜
	private String pay_date;	// 결제날짜(판매완료 날짜)
	private int zzimCount;
	
	
	private long fileNum;
	private String imageFilename;
	private String[] imageFiles;
	
	
	public int getZzimCount() {
		return zzimCount;
	}
	public void setZzimCount(int zzimCount) {
		this.zzimCount = zzimCount;
	}
	public long getFileNum() {
		return fileNum;
	}
	public void setFileNum(long fileNum) {
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
	public String getAddr() {
		return addr;
	}
	public void setAddr(String addr) {
		this.addr = addr;
	}
	public String getReg_date() {
		return reg_date;
	}
	public void setReg_date(String reg_date) {
		this.reg_date = reg_date;
	}
	public long getMarketnum() {
		return marketnum;
	}
	public void setMarketnum(long marketnum) {
		this.marketnum = marketnum;
	}
	public String getSellerid() {
		return sellerid;
	}
	public void setSellerid(String sellerid) {
		this.sellerid = sellerid;
	}
	public String getBuyerid() {
		return buyerid;
	}
	public void setBuyerid(String buyerid) {
		this.buyerid = buyerid;
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
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public String getPay_date() {
		return pay_date;
	}
	public void setPay_date(String pay_date) {
		this.pay_date = pay_date;
	}
	
	
	
	
}
