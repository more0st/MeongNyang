package com.myPage3;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.util.DBConn;

public class MyPage3DAO {
	private Connection conn = DBConn.getConnection();


	public int writeCount(String userId, String category) {
		int result =0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			
				
			if(category.equals("map")) {
				sb.append(" SELECT COUNT(*) FROM map WHERE userid=?");
			} else if(category.equals("gallery")) {
				sb.append(" SELECT COUNT(*) FROM gallery WHERE userid=?");
			} else if(category.equals("club")) {
				sb.append(" SELECT COUNT(*) FROM club WHERE userid=?");
			}
			
			pstmt = conn.prepareStatement(sb.toString());
			
			if(category.equals("map")) {
				pstmt.setString(1, userId);
			} else if(category.equals("gallery")) {
				pstmt.setString(1, userId);
			} else if(category.equals("club")) {
				pstmt.setString(1, userId);
			}

			
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



	public int writeCount(String userId, String condition, String keyword, String category) {
		int result =0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			
			if(category.equals("map")) {
				sb.append(" SELECT COUNT(*) FROM map WHERE userid=?");
			} else if(category.equals("gallery")) {
				sb.append(" SELECT COUNT(*) FROM gallery WHERE userid=?");
			} else if(category.equals("club")) {
				sb.append(" SELECT COUNT(*) FROM club WHERE userid=?");
			}
			
			if(condition.equals("all")) {
				sb.append(" AND (INSTR(subject, ?) >= 1 OR INSTR(content, ? ) >= 1)");
			} else if (condition.equals("subject")) {
				sb.append(" AND (INSTR(subject, ?) >= 1)");
			} else if (condition.equals("content")) {
				sb.append(" AND (INSTR(content, ?) >= 1)");
			} else if (condition.equals("reg_date")) {
				keyword = keyword.replaceAll("(\\-|\\.|\\/)", "");
				sb.append(" AND TO_CHAR(reg_date, 'YYYYMMDD') = ?");
			}
			
			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setString(1, userId);
			if(condition.equals("all")) {
				pstmt.setString(2, keyword);
				pstmt.setString(3, keyword);
			} else if (condition.equals("subject")) {
				pstmt.setString(2, keyword);
			} else if (condition.equals("content")) {
				pstmt.setString(2, keyword);
			} else if (condition.equals("reg_date")) {
				pstmt.setString(2, keyword);
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


	
	// 카테고리 검색 게시글리스트
	public List<MyPage3DTO> categoryListBoard(String userId, int offset, int size, String category) {
		List<MyPage3DTO> list = new ArrayList<MyPage3DTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			
				
			if(category.equals("map")) {
				sb.append("SELECT mapnum, subject, TO_CHAR(reg_date, 'YYYY-MM-DD') reg_date, hitCount, 1 category FROM map");
				sb.append(" WHERE userid=?");				
			} else if(category.equals("gallery")) {
				sb.append(" SELECT photonum, subject, TO_CHAR(reg_date, 'YYYY-MM-DD') reg_date, hitCount, 2 category FROM gallery");
				sb.append(" WHERE userid=?");
			} else if(category.equals("club")) {
				sb.append(" SELECT clubnum, subject, TO_CHAR(reg_date, 'YYYY-MM-DD') reg_date, hitCount, 3 category FROM club");
				sb.append(" WHERE userid=?");
			}
			
			sb.append(" ORDER BY reg_date DESC");
			sb.append(" OFFSET ? ROWS FETCH FIRST ? ROWS ONLY");
			
			pstmt = conn.prepareStatement(sb.toString());
			
			if(category.equals("map")) {
				pstmt.setString(1, userId);
				pstmt.setInt(2, offset);
				pstmt.setInt(3, size);
			}  else if(category.equals("gallery")) {
				pstmt.setString(1, userId);
				pstmt.setInt(2, offset);
				pstmt.setInt(3, size);
			}  else if(category.equals("club")) {
				pstmt.setString(1, userId);
				pstmt.setInt(2, offset);
				pstmt.setInt(3, size);
			}
			
			rs = pstmt.executeQuery();
			

			while(rs.next()) {
				MyPage3DTO dto = new MyPage3DTO();

				if(category.equals("map")) {
					dto.setNum(rs.getLong("mapnum"));
					dto.setSubject(rs.getString("subject"));
					dto.setReg_date(rs.getString("reg_date"));
					dto.setHitCount(rs.getInt("hitCount"));
					dto.setCategory(rs.getInt("category"));
				}
				
				else if(category.equals("gallery")) {
					dto.setNum(rs.getLong("photonum"));
					dto.setSubject(rs.getString("subject"));
					dto.setReg_date(rs.getString("reg_date"));
					dto.setHitCount(rs.getInt("hitCount"));
					dto.setCategory(rs.getInt("category"));
				}
				
				else if(category.equals("club")) {
					dto.setNum(rs.getLong("clubnum"));
					dto.setSubject(rs.getString("subject"));
					dto.setReg_date(rs.getString("reg_date"));
					dto.setHitCount(rs.getInt("hitCount"));
					dto.setCategory(rs.getInt("category"));
				}
				
				list.add(dto);
			}
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e2) {
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e2) {
				}
			}
		}

		return list;
	}

	
	

	public List<MyPage3DTO> categoryListBoard(String userId, int offset, int size, String condition, String keyword, String category) {
		List<MyPage3DTO> list = new ArrayList<MyPage3DTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			if(category.equals("map")) {
				sb.append("SELECT mapnum, subject, TO_CHAR(reg_date, 'YYYY-MM-DD') reg_date, hitCount, 1 category FROM map");
				sb.append(" WHERE userid=?");				
			} else if(category.equals("gallery")) {
				sb.append(" SELECT photonum, subject, TO_CHAR(reg_date, 'YYYY-MM-DD') reg_date, hitCount, 2 category FROM gallery");
				sb.append(" WHERE userid=?");
			} else if(category.equals("club")) {
				sb.append(" SELECT clubnum, subject, TO_CHAR(reg_date, 'YYYY-MM-DD') reg_date, hitCount, 3 category FROM club");
				sb.append(" WHERE userid=?");
			}
			
			
			if(condition.equals("all")) {
				sb.append(" AND (INSTR(subject, ?) >= 1 OR INSTR(content, ? ) >= 1)");
			} else if (condition.equals("subject")) {
				sb.append(" AND (INSTR(subject, ?) >= 1)");
			} else if (condition.equals("content")) {
				sb.append(" AND (INSTR(content, ?) >= 1)");
			} else if (condition.equals("reg_date")) {
				keyword = keyword.replaceAll("(\\-|\\.|\\/)", "");
				sb.append(" AND TO_CHAR(reg_date, 'YYYYMMDD') = ?");
			}
			
			sb.append(" ORDER BY reg_date DESC");
			sb.append(" OFFSET ? ROWS FETCH FIRST ? ROWS ONLY");
			
			pstmt = conn.prepareStatement(sb.toString());

			
			
			
			if(category.equals("map")) {
				pstmt.setString(1, userId);
				
				if(condition.equals("all")) {
					pstmt.setString(2, keyword);
					pstmt.setString(3, keyword);
					pstmt.setInt(4, offset);
					pstmt.setInt(5, size);
				} else {
					pstmt.setString(2, keyword);
					pstmt.setInt(3, offset);
					pstmt.setInt(4, size);					
				}
				
				
				
			}  else if(category.equals("gallery")) {
				pstmt.setString(1, userId);
				
				if(condition.equals("all")) {
					pstmt.setString(2, keyword);
					pstmt.setString(3, keyword);
					pstmt.setInt(4, offset);
					pstmt.setInt(5, size);
				} else {
					pstmt.setString(2, keyword);
					pstmt.setInt(3, offset);
					pstmt.setInt(4, size);					
				}
								
				
				
			}  else if(category.equals("club")) {
				pstmt.setString(1, userId);
				
				if(condition.equals("all")) {
					pstmt.setString(2, keyword);
					pstmt.setString(3, keyword);
					pstmt.setInt(4, offset);
					pstmt.setInt(5, size);
				} else {
					pstmt.setString(2, keyword);
					pstmt.setInt(3, offset);
					pstmt.setInt(4, size);					
				}
			}
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				MyPage3DTO dto = new MyPage3DTO();

				if(category.equals("map")) {
					dto.setNum(rs.getLong("mapnum"));
					dto.setSubject(rs.getString("subject"));
					dto.setReg_date(rs.getString("reg_date"));
					dto.setHitCount(rs.getInt("hitCount"));
					dto.setCategory(rs.getInt("category"));
				}
				
				else if(category.equals("gallery")) {
					dto.setNum(rs.getLong("photonum"));
					dto.setSubject(rs.getString("subject"));
					dto.setReg_date(rs.getString("reg_date"));
					dto.setHitCount(rs.getInt("hitCount"));
					dto.setCategory(rs.getInt("category"));
				}
				
				else if(category.equals("club")) {
					dto.setNum(rs.getLong("clubnum"));
					dto.setSubject(rs.getString("subject"));
					dto.setReg_date(rs.getString("reg_date"));
					dto.setHitCount(rs.getInt("hitCount"));
					dto.setCategory(rs.getInt("category"));
				}
				
				list.add(dto);
			}
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e2) {
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e2) {
				}
			}
		}

		return list;
	}
	
	





}
