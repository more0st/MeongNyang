package com.myPage4;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.myPage.MyPageDTO;
import com.util.DBConn;

public class MyPage4DAO {
	private Connection conn = DBConn.getConnection();

	public int dataCount(String userId) {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;	
		
		try {
			sql = "SELECT COUNT(*) FROM zzim a"
					+ " JOIN market b ON a.marketnum = b.marketnum"
					+ " WHERE userid=? AND state = 0";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, userId);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				result = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (Exception e2) {
				}
			}

			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e2) {
				}
			}
		}

		return result;
	}

	
	
	public int dataCount(String userId, String condition, String keyword) {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT COUNT(*) FROM zzim a"
					+ " JOIN market b ON a.marketnum = b.marketnum"
					+ " WHERE userid=? AND state = 0";
			if(condition.equals("all")) {
				sql += " AND INSTR(subject, ?) >= 1 OR INSTR(content, ? ) >= 1";
			} else if(condition.equals("reg_data")) {
				keyword = keyword.replaceAll("(\\-|\\.|\\/)", "");
				sql += " AND TO_CHAR(reg_date, 'YYYYMMDD') = ?";
			} else {	// subject, content, name
				sql += " AND INSTR(" + condition + ", ?) >= 1";
			}
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, userId);
			pstmt.setString(2, keyword);
			if(condition.equals("all")) {
				pstmt.setString(3, keyword);
			}		
			
			rs = pstmt.executeQuery();
			if(rs.next() ) {
				result = rs.getInt(1);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (Exception e2) {
				}
			}

			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e2) {
				}
			}
		}
		
		return result;
	}

	
	
	public List<MyPage4DTO> listBoard(String userId, int offset, int size) {
		List<MyPage4DTO> list = new ArrayList<MyPage4DTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
			
		try {
			sql = "SELECT a.marketnum,sellerid,subject,content,addr, price, hitCount, TO_CHAR(a.reg_date, 'YYYY-MM-DD') reg_date, TO_CHAR(pay_date, 'YYYY-MM-DD') pay_date, state, imgname"
					+ " FROM market a"
					+ " JOIN marketimgfile b on a.marketnum = b.marketnum"
					+ " JOIN (SELECT marketnum, MIN(imgnum) imgnum FROM marketimgfile"
					+ " GROUP BY marketnum) c ON c.imgnum = b.imgnum"
					+ " JOIN zzim z on a.marketnum = z.marketnum"
					+ " WHERE z.userid = ? AND state = 0 ORDER BY marketnum DESC"
					+ " OFFSET ? ROWS FETCH FIRST ? ROWS ONLY";
					
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, userId);
			pstmt.setInt(2, offset);
			pstmt.setInt(3, size);
			
			rs = pstmt.executeQuery();
			while(rs.next() ) {
				MyPage4DTO dto = new MyPage4DTO();
				
				dto.setMarketNum(rs.getLong("marketnum"));
				dto.setSellerId(rs.getString("sellerid"));
				dto.setSubject(rs.getString("subject"));
				dto.setContent(rs.getString("content"));
				dto.setAddr(rs.getString("addr"));
				dto.setPrice(rs.getString("price"));
				dto.setHitCount(rs.getInt("hitCount"));
				dto.setReg_Date(rs.getString("reg_date"));
				dto.setState(rs.getInt("state"));
				dto.setImageFileName(rs.getString("imgname"));
				dto.setHitCount(rs.getInt("hitCount"));
				
			
				list.add(dto);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (Exception e2) {
				}
			}

			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e2) {
				}
			}
		}
		
		return list;
	}

	public List<MyPage4DTO> listBoard(String userId, int offset, int size, String condition, String keyword) {
		// TODO Auto-generated method stub
		return null;
	}

}
