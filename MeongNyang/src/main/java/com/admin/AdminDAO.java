package com.admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.util.DBConn;

public class AdminDAO {
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
	
	public List<AdminDTO> listBoard(int offset, int size) {
		List<AdminDTO> list = new ArrayList<AdminDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			// 회원id 이름 생년월일 전화번호 주소 이메일 마켓판매횟수 마켓구매횟수 참여중인모임 회원상태
			sql = "SELECT userid, username,TO_CHAR(birth, 'YYYY-MM-DD') birth, tel, addr, email, b.buycount buycount, c.sellcount sellcount, enabled"
					+ " FROM member a"
					+ " LEFT OUTER JOIN (SELECT buyerid, count(state) buycount FROM market WHERE state in(1,2) GROUP BY buyerid) b ON a.userid = b.buyerid"
					+ " LEFT OUTER JOIN (SELECT sellerid, count(state) sellcount FROM market WHERE state in(1,2) GROUP BY sellerid) c ON a.userid = c.sellerid"
					+ " ORDER BY enabled DESC"
					+ " OFFSET ? ROWS FETCH FIRST ? ROWS ONLY";	
			
			pstmt = conn.prepareStatement(sql);

			pstmt.setInt(1, offset);
			pstmt.setInt(2, size);			
			
			rs = pstmt.executeQuery();			

			while(rs.next()) {
				AdminDTO dto = new AdminDTO();
				
				dto.setUserId(rs.getString("userId"));
				dto.setUserName(rs.getString("userName"));
				dto.setBirth(rs.getString("birth"));
				dto.setTel(rs.getString("tel"));
				dto.setAddr(rs.getString("addr"));
				dto.setEmail(rs.getString("email"));
				dto.setBuyCount(rs.getInt("buycount"));
				dto.setSellCount(rs.getInt("sellcount"));
				dto.setEnabled(rs.getInt("enabled"));
				
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

	public List<AdminDTO> listBoard(int offset, int size, String condition, String keyword) {
		List<AdminDTO> list = new ArrayList<AdminDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
					
			
			sb.append("SELECT userid, username,TO_CHAR(birth, 'YYYY-MM-DD') birth, tel, addr, email, b.buycount buycount, c.sellcount sellcount, enabled");
			sb.append(" FROM member a");
			sb.append(" LEFT OUTER JOIN (SELECT buyerid, count(state) buycount FROM market WHERE state in(1,2) GROUP BY buyerid) b ON a.userid = b.buyerid");
			sb.append(" LEFT OUTER JOIN (SELECT sellerid, count(state) sellcount FROM market WHERE state in(1,2) GROUP BY sellerid) c ON a.userid = c.sellerid");
			
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
				AdminDTO dto = new AdminDTO();
				
				dto.setUserId(rs.getString("userId"));
				dto.setUserName(rs.getString("userName"));
				dto.setBirth(rs.getString("birth"));
				dto.setTel(rs.getString("tel"));
				dto.setAddr(rs.getString("addr"));
				dto.setEmail(rs.getString("email"));
				dto.setBuyCount(rs.getInt("buycount"));
				dto.setSellCount(rs.getInt("sellcount"));
				dto.setEnabled(rs.getInt("enabled"));
				
				list.add(dto);
			}
			
		}catch (SQLException e) {
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
