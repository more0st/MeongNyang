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
	public int dataCount(int category) {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT COUNT(*) FROM sbbs WHERE category = ?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, category);
			
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
			
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e2) {
				}
			}
		}
		
		return result;
	}

	// 검색에서의 데이터 개수
	public int dataCount(int category, String condition, String keyword) {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT COUNT(*) FROM sbbs s "
					+ " JOIN member1 m ON s.userId = m.userId "
					+ " WHERE category = ? ";
			if(condition.equals("all")) {
				sql += "  AND (INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1) ";
			} else if(condition.equals("reg_date")) {
				keyword = keyword.replaceAll("(\\-|\\.|\\/)", "");
				sql += "  AND TO_CHAR(reg_date, 'YYYYMMDD') = ?";
			} else {
				sql += "  AND INSTR(" + condition + ", ?) >= 1";
			}
			
			pstmt = conn.prepareStatement(sql);

			pstmt.setInt(1, category);
			pstmt.setString(2, keyword);
			if(condition.equals("all")) {
				pstmt.setString(3, keyword);
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
			
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e2) {
				}
			}
		}
		
		return result;
	}

	// 게시물 리스트
	public List<MyPageDTO> listBoard(int category, int offset, int size) {
		List<MyPageDTO> list = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT num, userName, subject, hitCount, saveFilename, "
					+ "    TO_CHAR(reg_date, 'YYYY-MM-DD') reg_date "
					+ "  FROM sbbs s "
					+ "  JOIN member1 m ON s.userId = m.userId"
					+ "  WHERE category = ? "
					+ "  ORDER BY num DESC "
					+ "  OFFSET ? ROWS FETCH FIRST ? ROWS ONLY ";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, category);
			pstmt.setInt(2, offset);
			pstmt.setInt(3, size);
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				MyPageDTO dto = new MyPageDTO();
				
				dto.setSubject(rs.getString("subject"));
				
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
			
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e2) {
				}
			}
		}
		
		return list;
	}
	
	//
	public List<MyPageDTO> listBoard(int category, int offset, int size, String condition, String keyword) {
		List<MyPageDTO> list = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			sb.append("SELECT num, userName, subject, hitCount, saveFilename, ");
			sb.append("    TO_CHAR(reg_date, 'YYYY-MM-DD') reg_date ");
			sb.append("  FROM sbbs s ");
			sb.append("  JOIN member1 m ON s.userId = m.userId ");
			sb.append("  WHERE category = ? ");
			if(condition.equals("all")) {
				sb.append("  AND (INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1) ");
			} else if(condition.equals("reg_date")) {
				keyword = keyword.replaceAll("(\\-|\\.|\\/)", "");
				sb.append("  AND TO_CHAR(reg_date, 'YYYYMMDD') = ?");
			} else {
				sb.append("  AND INSTR(" + condition + ", ?) >= 1");
			}
			sb.append("  ORDER BY num DESC ");
			sb.append("  OFFSET ? ROWS FETCH FIRST ? ROWS ONLY ");
			
			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setInt(1, category);
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
			rs = pstmt.executeQuery();
			while(rs.next()) {
				MyPageDTO dto = new MyPageDTO();
				
				dto.setSubject(rs.getString("subject"));
				
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
			
			if(pstmt != null) {
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
	public MyPageDTO preReadBoard(int category, long marketnum, String condition, String keyword) {
		MyPageDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
			if (keyword != null && keyword.length() != 0) {
				sb.append(" SELECT num, subject ");
				sb.append(" FROM sbbs b ");
				sb.append(" JOIN member1 m ON b.userId = m.userId ");
				sb.append(" WHERE category = ? AND num > ? ");
				if (condition.equals("all")) {
					sb.append("   AND ( INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1 ) ");
				} else if (condition.equals("reg_date")) {
					keyword = keyword.replaceAll("(\\-|\\/|\\.)", "");
					sb.append("   AND TO_CHAR(reg_date, 'YYYYMMDD') = ? ");
				} else {
					sb.append("   AND INSTR(" + condition + ", ?) >= 1 ");
				}
				sb.append(" ORDER BY num ASC ");
				sb.append(" FETCH FIRST 1 ROWS ONLY ");

				pstmt = conn.prepareStatement(sb.toString());
				
				pstmt.setInt(1, category);
				pstmt.setLong(2, marketnum);
				pstmt.setString(3, keyword);
				if (condition.equals("all")) {
					pstmt.setString(4, keyword);
				}
			} else {
				sb.append(" SELECT num, subject FROM sbbs ");
				sb.append(" WHERE category = ? AND num > ? ");
				sb.append(" ORDER BY num ASC ");
				sb.append(" FETCH FIRST 1 ROWS ONLY ");

				pstmt = conn.prepareStatement(sb.toString());
				
				pstmt.setInt(1, category);
				pstmt.setLong(2, marketnum);
			}

			rs = pstmt.executeQuery();

			if (rs.next()) {
				dto = new MyPageDTO();
				
				dto.setSubject(rs.getString("subject"));
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

	// 다음글
	public MyPageDTO nextReadBoard(int category, long marketnum, String condition, String keyword) {
		MyPageDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
			if (keyword != null && keyword.length() != 0) {
				sb.append(" SELECT num, subject ");
				sb.append(" FROM sbbs b ");
				sb.append(" JOIN member1 m ON b.userId = m.userId ");
				sb.append(" WHERE category = ? AND num < ? ");
				if (condition.equals("all")) {
					sb.append("   AND ( INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1 ) ");
				} else if (condition.equals("reg_date")) {
					keyword = keyword.replaceAll("(\\-|\\/|\\.)", "");
					sb.append("   AND TO_CHAR(reg_date, 'YYYYMMDD') = ? ");
				} else {
					sb.append("   AND INSTR(" + condition + ", ?) >= 1 ");
				}
				sb.append(" ORDER BY num DESC ");
				sb.append(" FETCH FIRST 1 ROWS ONLY ");

				pstmt = conn.prepareStatement(sb.toString());
				
				pstmt.setInt(1, category);
				pstmt.setLong(2, marketnum);
				pstmt.setString(3, keyword);
				if (condition.equals("all")) {
					pstmt.setString(4, keyword);
				}
			} else {
				sb.append(" SELECT num, subject FROM sbbs ");
				sb.append(" WHERE category = ? AND num < ? ");
				sb.append(" ORDER BY num DESC ");
				sb.append(" FETCH FIRST 1 ROWS ONLY ");

				pstmt = conn.prepareStatement(sb.toString());
				
				pstmt.setInt(1, category);
				pstmt.setLong(2, marketnum);
			}

			rs = pstmt.executeQuery();

			if (rs.next()) {
				dto = new MyPageDTO();
				
				dto.setSubject(rs.getString("subject"));
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

}
