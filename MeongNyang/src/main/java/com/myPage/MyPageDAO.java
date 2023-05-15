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
	public int dataCount(String userId) {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT COUNT(*) FROM market WHERE sellerid= ?";
			
			
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

	// 검색에서의 데이터 개수
	public int dataCount(String userId ,String condition, String keyword) {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT COUNT(*) FROM market WHERE sellerid=?";
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

	

	// 게시물 리스트
	public List<MyPageDTO> listBoard(String userId, int offset, int size) {
		List<MyPageDTO> list = new ArrayList<MyPageDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT marketnum,sellerid,buyerid,subject,content,hitCount, TO_CHAR(reg_date, 'YYYY-MM-DD') reg_date, TO_CHAR(pay_date, 'YYYY-MM-DD') pay_date"
					+ " FROM market"
					+ " WHERE sellerid = ? ORDER BY marketnum DESC "
					+ " OFFSET ? ROWS FETCH FIRST ? ROWS ONLY";
				
			
			//  select * from market where = '세션에서 받아온 아이디';
			
			pstmt = conn.prepareStatement(sql);
			
			
			pstmt.setString(1, userId);
			pstmt.setInt(2, offset);
			pstmt.setInt(3, size);
			
			rs = pstmt.executeQuery();
			while(rs.next() ) {
				MyPageDTO dto = new MyPageDTO();
				
				dto.setMarketnum(rs.getLong("marketnum"));
				dto.setSellerid(rs.getString("sellerid"));
				dto.setBuyerid(rs.getString("buyerid"));
				dto.setSubject(rs.getString("subject"));
				dto.setContent(rs.getString("content"));
				dto.setHitCount(rs.getInt("hitCount"));
				dto.setReg_date(rs.getString("reg_date"));
				dto.setPay_date(rs.getString("pay_date"));
				
			
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


	// 검색 게시물 리스트
	public List<MyPageDTO> listBoard(String userId, int offset, int size, String condition, String keyword) {
		List<MyPageDTO> list = new ArrayList<MyPageDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
			sb.append(" SELECT marketnum,sellerid,buyerid,subject,hitCount, ");
			sb.append("      TO_CHAR(reg_date, 'YYYY-MM-DD') reg_date, TO_CHAR(pay_date, 'YYYY-MM-DD') pay_date ");
			sb.append(" FROM market a ");
			sb.append(" JOIN member b ON a.sellerId = b.userId ");
			sb.append(" WHERE sellerid=?");
			if (condition.equals("all")) {
				sb.append(" AND INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1 ");
			} else if (condition.equals("reg_date")) {
				keyword = keyword.replaceAll("(\\-|\\/|\\.)", "");
				sb.append(" AND TO_CHAR(reg_date, 'YYYYMMDD') = ?");
			} else {
				sb.append(" AND INSTR(" + condition + ", ?) >= 1 ");
			}
			sb.append(" ORDER BY marketnum DESC ");
			sb.append(" OFFSET ? ROWS FETCH FIRST ? ROWS ONLY ");

			pstmt = conn.prepareStatement(sb.toString());
			
			if (condition.equals("all")) {
				pstmt.setString(1, userId);
				pstmt.setString(2, keyword);
				pstmt.setString(3, keyword);
				pstmt.setInt(4, offset);
				pstmt.setInt(5, size);
			} else {
				pstmt.setString(1, userId);
				pstmt.setString(2, keyword);
				pstmt.setInt(3, offset);
				pstmt.setInt(4, size);
			}

			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				MyPageDTO dto = new MyPageDTO();

				dto.setMarketnum(rs.getLong("marketnum"));
				dto.setSellerid(rs.getString("sellerid"));
				dto.setBuyerid(rs.getString("buyerid"));
				dto.setSubject(rs.getString("subject"));
				dto.setHitCount(rs.getInt("hitCount"));
				dto.setReg_date(rs.getString("reg_date"));
				dto.setPay_date(rs.getString("pay_date"));
				
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
	
	// 조회수 증가하기
	public void updateHitCount(long marketnum) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;

		try {
			sql = "UPDATE market SET hitCount=hitCount+1 WHERE marketnum=?";
			
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
			sql = "SELECT marketnum,sellerid,buyerid,subject,hitCount, reg_date, pay_date"
					+ " FROM market"
					+ " WHERE marketnum = ?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, marketnum);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				dto = new MyPageDTO();
				
				dto.setMarketnum(rs.getLong("marketnum"));
				dto.setSellerid(rs.getString("sellerid"));
				dto.setBuyerid(rs.getString("buyerid"));
				dto.setSubject(rs.getString("subject"));
				dto.setHitCount(rs.getInt("hitcount"));
				dto.setReg_date(rs.getString("reg_date"));
				dto.setPay_date(rs.getString("pay_date"));
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


	/*
	// 이전글
	public MyPageDTO prereadBoard(long marketnum) {
		MyPageDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT marketnum,sellerid,buyerid,subject,hitCount, reg_date, pay_date"
					+ " FROM market"
					+ " WHERE marketnum = ?";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, marketnum);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				dto = new MyPageDTO();
				
				dto.setMarketnum(rs.getLong("marketnum"));
				dto.setSellerid(rs.getString("sellerid"));
				dto.setBuyerid(rs.getString("buyerid"));
				dto.setSubject(rs.getString("subject"));
				dto.setHitCount(rs.getInt("hitcount"));
				dto.setReg_date(rs.getString("reg_date"));
				dto.setPay_date(rs.getString("pay_date"));
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
	public MyPageDTO nextReadBoard(long marketnum) {
		MyPageDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
			if(keyword != null && keyword.length() != 0) {
				// 검색
				sb.append(" SELECT marketnum, subject");
				sb.append(" FROM market ");
				sb.append(" WHERE marketnum > ? ");
				if(condition.equals("all")) {
					sb.append(" AND (INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1) ");
				} else if(condition.equals("reg_date")) {
					keyword = keyword.replaceAll("(\\-|\\/|\\.)","");
					sb.append(" AND (TO_CHAR(reg_date, 'YYYYMMDD') = ?) ");
				} else {
					sb.append(" AND (INSTR(" + condition + ", ?) >= 1");
				}
				sb.append(" ORDER BY marketnum ASC");
				sb.append(" FETCH FIRST 1 ROWS ONLY");
				
				pstmt = conn.prepareStatement(sb.toString());
				pstmt.setLong(1, marketnum);
				pstmt.setString(2, keyword);
				if(condition.equals("all")) {
					pstmt.setString(3, keyword);
				}
			} else {
				// 검색이 아닐때
				sb.append("SELECT marketnum, subject");
				sb.append(" FROM market ");
				sb.append(" WHERE marketnum > ? ");
				sb.append(" ORDER BY marketnum ASC");
				sb.append(" FETCH FIRST 1 ROWS ONLY");
				
				pstmt = conn.prepareStatement(sb.toString());
				pstmt.setLong(1, marketnum);
			} 
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				dto = new MyPageDTO();
				dto.setMarketnum(rs.getLong("marketnum"));
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
	*/
	
	
	// 이전글
	public MyPageDTO preReadBoard(long marketnum, String condition, String keyword) {
		MyPageDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
			if(keyword != null && keyword.length() != 0) {
				// 검색
				sb.append(" SELECT marketnum, subject");
				sb.append(" FROM market ");
				sb.append(" WHERE marketnum > ? ");
				if(condition.equals("all")) {
					sb.append(" AND (INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1) ");
				} else if(condition.equals("reg_date")) {
					keyword = keyword.replaceAll("(\\-|\\/|\\.)","");
					sb.append(" AND (TO_CHAR(reg_date, 'YYYYMMDD') = ?) ");
				} else {
					sb.append(" AND (INSTR(" + condition + ", ?) >= 1");
				}
				sb.append(" ORDER BY marketnum ASC");
				sb.append(" FETCH FIRST 1 ROWS ONLY");
				
				pstmt = conn.prepareStatement(sb.toString());
				pstmt.setLong(1, marketnum);
				pstmt.setString(2, keyword);
				if(condition.equals("all")) {
					pstmt.setString(3, keyword);
				}
			} else {
				// 검색이 아닐때
				sb.append("SELECT marketnum, subject");
				sb.append(" FROM market ");
				sb.append(" WHERE marketnum > ? ");
				sb.append(" ORDER BY marketnum ASC");
				sb.append(" FETCH FIRST 1 ROWS ONLY");
				
				pstmt = conn.prepareStatement(sb.toString());
				pstmt.setLong(1, marketnum);
			} 
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				dto = new MyPageDTO();
				dto.setMarketnum(rs.getLong("marketnum"));
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
	

	// 다음글
	public MyPageDTO nextReadBoard(long marketnum, String condition, String keyword) {
		MyPageDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
			if(keyword != null && keyword.length() != 0) {
				// 검색
				sb.append(" SELECT marketnum, subject");
				sb.append(" FROM market ");
				sb.append(" WHERE marketnum > ? ");
				if(condition.equals("all")) {
					sb.append(" AND (INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1) ");
				} else if(condition.equals("reg_date")) {
					keyword = keyword.replaceAll("(\\-|\\/|\\.)","");
					sb.append(" AND (TO_CHAR(reg_date, 'YYYYMMDD') = ?) ");
				} else {
					sb.append(" AND (INSTR(" + condition + ", ?) >= 1");
				}
				sb.append(" ORDER BY marketnum ASC");
				sb.append(" FETCH FIRST 1 ROWS ONLY");
				
				pstmt = conn.prepareStatement(sb.toString());
				pstmt.setLong(1, marketnum);
				pstmt.setString(2, keyword);
				if(condition.equals("all")) {
					pstmt.setString(3, keyword);
				}
			} else {
				// 검색이 아닐때
				sb.append("SELECT marketnum, subject");
				sb.append(" FROM market ");
				sb.append(" WHERE marketnum > ? ");
				sb.append(" ORDER BY marketnum ASC");
				sb.append(" FETCH FIRST 1 ROWS ONLY");
				
				pstmt = conn.prepareStatement(sb.toString());
				pstmt.setLong(1, marketnum);
			} 
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				dto = new MyPageDTO();
				dto.setMarketnum(rs.getLong("marketnum"));
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
