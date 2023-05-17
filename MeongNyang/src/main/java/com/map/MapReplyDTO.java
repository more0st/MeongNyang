package com.map;

public class MapReplyDTO {

	private long replyNum;
	private long mapNum;
	private String userId;
	private String userName;
	private String content;
	private String reg_date;
	private long originalReplyNum;

	private int originalReplyNumCount;
	private int likeCount;
	private int disLikeCount;

	public long getReplyNum() {
		return replyNum;
	}

	public void setReplyNum(long replyNum) {
		this.replyNum = replyNum;
	}

	public long getMapNum() {
		return mapNum;
	}

	public void setMapNum(long mapNum) {
		this.mapNum = mapNum;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
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

	public long getOriginalReplyNum() {
		return originalReplyNum;
	}

	public void setOriginalReplyNum(long originalReplyNum) {
		this.originalReplyNum = originalReplyNum;
	}

	public int getOriginalReplyNumCount() {
		return originalReplyNumCount;
	}

	public void setOriginalReplyNumCount(int originalReplyNumCount) {
		this.originalReplyNumCount = originalReplyNumCount;
	}

	public int getLikeCount() {
		return likeCount;
	}

	public void setLikeCount(int likeCount) {
		this.likeCount = likeCount;
	}

	public int getDisLikeCount() {
		return disLikeCount;
	}

	public void setDisLikeCount(int disLikeCount) {
		this.disLikeCount = disLikeCount;
	}

}
