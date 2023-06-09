package com.admin2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.util.DBConn;

public class Admin2DAO {
	private Connection conn = DBConn.getConnection();

	public int dataCount() {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;	
		
		try {
			sql = "SELECT COUNT(*) FROM member";
			
			pstmt = conn.prepareStatement(sql);
			
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

	public int dataCount(String condition, String keyword) {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT COUNT(*) FROM member";
			
			if (condition.equals("userId")) {
				sql += " WHERE userid = ?";
			} else if (condition.equals("userName")) {
				sql += " WHERE username = ?";
			}
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, keyword);
			
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

	public List<Admin2DTO> listBoard(int offset, int size) {
		// TODO Auto-generated method stub
		List<Admin2DTO> list = new ArrayList<Admin2DTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT userid, username, TO_CHAR(birth, 'YYYY-MM-DD') birth, tel, addr, email, enabled"
					+ " FROM member"
					+ " ORDER BY enabled DESC"
					+ " OFFSET ? ROWS FETCH FIRST ? ROWS ONLY";	

			pstmt = conn.prepareStatement(sql);

			pstmt.setInt(1, offset);
			pstmt.setInt(2, size);			
			
			rs = pstmt.executeQuery();			
			
			while(rs.next()) {
				Admin2DTO dto = new Admin2DTO();
				
				dto.setUserId(rs.getString("userId"));
				dto.setUserName(rs.getString("userName"));
				dto.setBirth(rs.getString("birth"));
				dto.setTel(rs.getString("tel"));
				dto.setAddr(rs.getString("addr"));
				dto.setEmail(rs.getString("email"));	
				dto.setEnabled(rs.getInt("enabled"));
				
				list.add(dto);
			}
			
		}  catch (Exception e) {
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

	public int changeState(String userId, String state) {
		// TODO Auto-generated method stub
		int result = 0;
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = " UPDATE member SET enabled = ? WHERE userID = ?";
			
			pstmt = conn.prepareStatement(sql);
			
				pstmt.setString(1, state);
				pstmt.setString(2, userId);

				result = pstmt.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e2) {
				}
			}
		
		}
		
		
		return result;
		
		
	}

	public List<Admin2DTO> listBoard(int offset, int size, String condition, String keyword) {
		List<Admin2DTO> list = new ArrayList<Admin2DTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();		
		
		try {
			sb.append("SELECT userid, username, TO_CHAR(birth, 'YYYY-MM-DD') birth, tel, addr, email, enabled");
			sb.append(" FROM member");

			if (condition.equals("userId")) {
				sb.append(" WHERE userid = ?");
			} else if (condition.equals("userName")) {
				sb.append(" WHERE username = ?");
			}

			sb.append(" ORDER BY enabled DESC");
			sb.append(" OFFSET ? ROWS FETCH FIRST ? ROWS ONLY ");

			pstmt = conn.prepareStatement(sb.toString());

			if (condition.equals("userId")) {
				pstmt.setString(1, keyword);
				pstmt.setInt(2, offset);
				pstmt.setInt(3, size);
			} else if(condition.equals("userName")){
				pstmt.setString(1, keyword);
				pstmt.setInt(2, offset);
				pstmt.setInt(3, size);
			} 			

			rs = pstmt.executeQuery();

			while(rs.next()) {
				Admin2DTO dto = new Admin2DTO();
				
				dto.setUserId(rs.getString("userId"));
				dto.setUserName(rs.getString("userName"));
				dto.setBirth(rs.getString("birth"));
				dto.setTel(rs.getString("tel"));
				dto.setAddr(rs.getString("addr"));
				dto.setEmail(rs.getString("email"));	
				dto.setEnabled(rs.getInt("enabled"));
				
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
