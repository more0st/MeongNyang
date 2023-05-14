package com.myPage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.util.DBConn;

public class MyPageDAO {
	private Connection conn = DBConn.getConnection();

	// 데이터 개수
	public int dataCount() {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT COUNT(*) FROM bbs";
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

	// 검색에서의 데이터 개수
	public int dataCount(String condition, String keyword) {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT COUNT(*) FROM bbs";
			if(condition.equals("all")) {
				sql += " WHERE INSTR(subject, ?) >= 1 OR INSTR(content, ? ) >= 1";
			} else if(condition.equals("reg_data")) {
				keyword = keyword.replaceAll("(\\-|\\.|\\/)", "");
				sql += " WHERE TO_CHAR(reg_date, 'YYYYMMDD') = ?";
			} else {	// subject, content, name
				sql += " WHERE INSTR(" + condition + ", ?) >= 1";
			}
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, keyword);
			if(condition.equals("all")) {
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

	// 게시물 리스트
	public List<MyPageDTO> listBoard(int offset, int size) {
		List<MyPageDTO> list = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT num,name,subject,hitCount, TO_CHAR(reg_date, 'YYYY-MM-DD') reg_date"
					+ " FROM bbs ORDER BY num DESC "
					+ " OFFSET ? ROWS FETCH FIRST ? ROWS ONLY";
				
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, offset);
			pstmt.setInt(2, size);
			
			rs = pstmt.executeQuery();
			while(rs.next() ) {
				MyPageDTO dto = new MyPageDTO();
				
				dto.setSubject(rs.getString("subject"));
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
	
	//
	public List<MyPageDTO> listBoard(int offset, int size, String condition, String keyword) {
		List<MyPageDTO> list = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT num, name, subject, hitCount, TO_CHAR(reg_date, 'YYYY-MM-DD') reg_date FROM bbs";
			
			if(condition.equals("all")) {
				sql += " WHERE INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1 ";
			} else if(condition.equals("reg_date")) {
				keyword= keyword.replaceAll("(\\-|\\.|\\/)", "");
				sql += " WHERE TO_CHAR(reg_date, 'YYYYMMDD') = ?";
			} else {
				sql += " WHERE INSTR(" + condition + ", ?) >= 1";
			}
			sql += " ORDER BY num DESC";
			sql += " OFFSET ? ROWS FETCH FIRST ? ROWS ONLY ";
			
			pstmt = conn.prepareStatement(sql);
			
			if(condition.equals("all")) {
				pstmt.setString(1, keyword);
				pstmt.setString(2, keyword);
				pstmt.setInt(3, offset);
				pstmt.setInt(4, size);
			} else {
				pstmt.setString(1, keyword);
				pstmt.setInt(2, offset);
				pstmt.setInt(3, size);
			}
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				MyPageDTO dto = new MyPageDTO();
				
				dto.setSubject(rs.getString("subject"));
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
	
	
	// 조회수 증가하기
	public void updateHitCount(long marketnum) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;

		try {
			sql = "UPDATE sbbs SET hitCount=hitCount+1 WHERE num=?";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, marketnum);
			
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e2) {
				}
			}
		}
	}

	// 해당 게시물 보기
	public MyPageDTO readBoard(long marketnum) {
		MyPageDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			sql = "SELECT num, category, b.userId, userName, subject, content, "
					+ " saveFilename, originalFilename, filesize, reg_date, hitCount "
					+ " FROM sbbs b "
					+ " JOIN member1 m ON b.userId=m.userId "
					+ " WHERE num = ? ";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, marketnum);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				dto = new MyPageDTO();
				
				dto.setSubject(rs.getString("subject"));
				dto.setContent(rs.getString("content"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}

			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
				}
			}
		}

		return dto;
	}

	// 이전글
	public MyPageDTO preReadBoard(long marketnum, String condition, String keyword) {
		MyPageDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			sql = "SELECT num, name, subject, content, pwd, ipAddr, hitCount, reg_date"
					+ " FROM bbs"
					+ " WHERE num = ?";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, marketnum);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				dto = new MyPageDTO();
				
				dto.setSubject(rs.getString("Subject"));
				dto.setContent(rs.getString("content"));
				dto.setHitCount(rs.getInt("hitCount"));
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
			
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e2) {
				}
			}
		}
		
		return dto;
	}

	// 다음글
	public MyPageDTO nextReadBoard(long marketnum, String condition, String keyword) {
		MyPageDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
			if(keyword != null && keyword.length() != 0) {
				// 검색
				sb.append(" SELECT num, subject");
				sb.append(" FROM bbs ");
				sb.append(" WHERE num > ? ");
				if(condition.equals("all")) {
					sb.append(" AND (INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1) ");
				} else if(condition.equals("reg_date")) {
					keyword = keyword.replaceAll("(\\-|\\/|\\.)","");
					sb.append(" AND (TO_CHAR(reg_date, 'YYYYMMDD') = ?) ");
				} else {
					sb.append(" AND (INSTR(" + condition + ", ?) >= 1");
				}
				sb.append(" ORDER BY num ASC");
				sb.append(" FETCH FIRST 1 ROWS ONLY");
				
				pstmt = conn.prepareStatement(sb.toString());
				pstmt.setLong(1, marketnum);
				pstmt.setString(2, keyword);
				if(condition.equals("all")) {
					pstmt.setString(3, keyword);
				}
			} else {
				// 검색이 아닐때
				sb.append("SELECT num, subject");
				sb.append(" FROM bbs ");
				sb.append(" WHERE num > ? ");
				sb.append(" ORDER BY NUM ASC");
				sb.append(" FETCH FIRST 1 ROWS ONLY");
				
				pstmt = conn.prepareStatement(sb.toString());
				pstmt.setLong(1, marketnum);
			} 
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				dto = new MyPageDTO();
				dto.setMarketnum(rs.getLong("num"));
				dto.setSubject(rs.getString("subject"));
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
			
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e2) {
				}
			}
		}
	
		return dto;
	}

}
