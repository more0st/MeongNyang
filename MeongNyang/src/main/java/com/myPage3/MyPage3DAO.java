package com.myPage3;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.util.DBConn;

public class MyPage3DAO {
	private Connection conn = DBConn.getConnection();

	public int dataCount(String userId) {
		int result_1 = 0;
		int result_2 = 0;
		int result_3 = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;	
		
		try {
			sql = "SELECT COUNT(*) FROM map WHERE userid=?";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, userId);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				result_1 = rs.getInt(1);
			}
			pstmt.close();
			rs.close();

			
			
			
			sql = "SELECT COUNT(*) FROM gallery WHERE userid=?";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, userId);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				result_2 = rs.getInt(1);
			}
			pstmt.close();
			rs.close();
			
			
			
			sql = "SELECT COUNT(*) FROM club WHERE userid=?";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, userId);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				result_3 = rs.getInt(1);
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
		
		
		
		return result_1 + result_2 + result_3;
	}

	
	
	public int dataCount(String userId, String condition, String keyword) {
		int result_1 = 0;
		int result_2 = 0;
		int result_3 = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;	
		
		try {
			sql = "SELECT COUNT(*) FROM map WHERE userid=?";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, userId);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				result_1 = rs.getInt(1);
			}
			pstmt.close();
			rs.close();

			
			
			
			sql = "SELECT COUNT(*) FROM gallery WHERE userid=?";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, userId);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				result_2 = rs.getInt(1);
			}
			pstmt.close();
			rs.close();
			
			
			
			sql = "SELECT COUNT(*) FROM club WHERE userid=?";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, userId);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				result_3 = rs.getInt(1);
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
		
		
		
		return result_1 + result_2 + result_3;
	}
	
	

	public int categoryDataCount(String userId, String category) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	
	
	

	public List<MyPage3DTO> listBoard(String userId, int offset, int size) {
		List<MyPage3DTO> list = new ArrayList<MyPage3DTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			
			sql = "SELECT subject, reg_date, hitCount, 1 category FROM map"
				+	" WHERE userid=?"
				+	" UNION"
				+	" SELECT subject, reg_date, hitCount, 2 category FROM gallery"
				+	" WHERE userid=?"
				+	" UNION"
				+	" SELECT subject, reg_date, hitCount, 3 category FROM club"
				+	" WHERE userid=?"
				+	" ORDER BY reg_date DESC"
				+   " OFFSET ? ROWS FETCH FIRST ? ROWS ONLY";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, userId);
			pstmt.setString(2, userId);
			pstmt.setString(3, userId);
			pstmt.setInt(4, offset);
			pstmt.setInt(5, size);
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				MyPage3DTO dto = new MyPage3DTO();
				
				dto.setSubject(rs.getString("subject"));
				dto.setReg_date(rs.getString("reg_date"));
				dto.setHitCount(rs.getInt("hitCount"));
				dto.setCategory(rs.getInt("category"));
				
				
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

	
	// 검색 게시글리스트
	public List<MyPage3DTO> listBoard(String userId, int offset, int size, String condition, String keyword) {
		// TODO Auto-generated method stub
		return null;
	}


	// 카테고리 검색 게시글리스트
	public List<MyPage3DTO> categoryListBoard(String userId, int offset, int size, String category) {

		
		return null;
	}




}
