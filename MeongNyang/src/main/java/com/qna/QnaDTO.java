package com.qna;

public class QnaDTO {
	private long qesNum;
	private String subject;
	private String content;
	private String reg_date;
	private String userId;

	private String replyContent;
	private String replyReg_date;

	private String userName;
	
	public long getQesNum() {
		return qesNum;
	}

	public void setQesNum(long qesNum) {
		this.qesNum = qesNum;
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

	public String getReplyContent() {
		return replyContent;
	}

	public void setReplyContent(String replyContent) {
		this.replyContent = replyContent;
	}

	public String getReplyReg_date() {
		return replyReg_date;
	}

	public void setReplyReg_date(String replyReg_date) {
		this.replyReg_date = replyReg_date;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	
	
}
