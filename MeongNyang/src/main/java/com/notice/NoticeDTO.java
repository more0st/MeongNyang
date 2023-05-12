package com.notice;

public class NoticeDTO {

	private long noticeNum;
	private String subject;
	private String content;
	private String reg_date;
	private String userId;
	private long hitCount;
    private String userName;
    private long gap;
	
    
	public long getGap() {
		return gap;
	}
	public void setGap(long gap) {
		this.gap = gap;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public long getNoticeNum() {
		return noticeNum;
	}
	public void setNoticeNum(long noticeNum) {
		this.noticeNum = noticeNum;
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
	public long getHitCount() {
		return hitCount;
	}
	public void setHitCount(long hitCount) {
		this.hitCount = hitCount;
	}
	

}
