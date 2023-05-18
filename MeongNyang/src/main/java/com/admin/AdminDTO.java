package com.admin;

import java.util.List;

public class AdminDTO {

	private String userId;
	private String userName;
	private String birth;
	private String tel;
	private String addr;
	private String email;
	private int enabled;
	
	private Long marketNum;	// 멍냥마켓
	private String sellerId;
	private String buyerId;
	private int sellCount;	// 판매횟수
	private int buyCount;	// 구매횟수
	private int marketState;
	
	private Long clubNum;	// 멍냥모임
	private List<String> clubName;
	
	
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
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getBirth() {
		return birth;
	}
	public void setBirth(String birth) {
		this.birth = birth;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getAddr() {
		return addr;
	}
	public void setAddr(String addr) {
		this.addr = addr;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Long getMarketNum() {
		return marketNum;
	}
	public void setMarketNum(Long marketNum) {
		this.marketNum = marketNum;
	}
	public String getSellerId() {
		return sellerId;
	}
	public void setSellerId(String sellerId) {
		this.sellerId = sellerId;
	}
	public String getBuyerId() {
		return buyerId;
	}
	public void setBuyerId(String buyerId) {
		this.buyerId = buyerId;
	}
	public int getSellCount() {
		return sellCount;
	}
	public void setSellCount(int sellCount) {
		this.sellCount = sellCount;
	}
	public int getBuyCount() {
		return buyCount;
	}
	public void setBuyCount(int buyCount) {
		this.buyCount = buyCount;
	}
	public int getMarketState() {
		return marketState;
	}
	public void setMarketState(int marketState) {
		this.marketState = marketState;
	}
	public Long getClubNum() {
		return clubNum;
	}
	public void setClubNum(Long clubNum) {
		this.clubNum = clubNum;
	}
	public List<String> getClubName() {
		return clubName;
	}
	public void setClubName(List<String> clubName) {
		this.clubName = clubName;
	}
	
	
	

	
}
