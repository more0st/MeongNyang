package com.myPage4;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
				sql += " AND ( INSTR(subject, ?) >= 1 OR INSTR(content, ? ) >= 1 )";
			} else if(condition.equals("zzim_date")) {
				keyword = keyword.replaceAll("(\\-|\\.|\\/)", "");
				sql += " AND TO_CHAR(a.reg_date, 'YYYYMMDD') = ?";
			} else if(condition.equals("seller")) {
				sql += " AND sellerid=?";
			} else {	// subject, content, name
				sql += " AND INSTR(" + condition + ", ?) >= 1";
			}
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, userId);
			if(condition.equals("all")) {
				pstmt.setString(2, keyword);
				pstmt.setString(3, keyword);
			} else if(condition.equals("zzim_date")) {
				pstmt.setString(2, keyword);
			} else if(condition.equals("seller")) {
				pstmt.setString(2, keyword);
			} else {
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

	
	
	public List<MyPage4DTO> listBoard(String userId, int offset, int size) {
		List<MyPage4DTO> list = new ArrayList<MyPage4DTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
			
		try {
			sql = "SELECT a.marketnum marketnum,sellerid,subject,addr, price, TO_CHAR(a.reg_date, 'YYYY-MM-DD') reg_date, imgname"
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
				dto.setAddr(rs.getString("addr"));
				dto.setPrice(rs.getString("price"));
				dto.setReg_Date(rs.getString("reg_date"));
				dto.setImageFileName(rs.getString("imgname"));
				
			
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
		List<MyPage4DTO> list = new ArrayList<MyPage4DTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			sb.append("SELECT a.marketnum marketnum,sellerid,subject,addr, price, TO_CHAR(a.reg_date, 'YYYY-MM-DD') reg_date, imgname");
			sb.append(" FROM market a");
			sb.append(" JOIN marketimgfile b on a.marketnum = b.marketnum");
			sb.append(" JOIN (SELECT marketnum, MIN(imgnum) imgnum FROM marketimgfile");
			sb.append(" GROUP BY marketnum) c ON c.imgnum = b.imgnum");
			sb.append(" JOIN zzim z on a.marketnum = z.marketnum");
			sb.append(" WHERE z.userid = ? AND state = 0 ");

			if(condition.equals("all")) {
				sb.append(" AND INSTR(subject ,?) >= 1 OR INSTR(content, ?) >= 1 ");
			} else if(condition.equals("zzim_date")) {
				keyword = keyword.replaceAll("(\\-|\\.|\\/)", "");
				sb.append(" AND TO_CHAR(a.reg_date, 'YYYYMMDD') = ?");
			} else if(condition.equals("seller")) {
				sb.append(" AND sellerid=?");
			} else {
				sb.append(" AND INSTR(" + condition + ", ?) >= 1");
			}
			
			sb.append(" ORDER BY marketnum DESC");
			sb.append(" OFFSET ? ROWS FETCH FIRST ? ROWS ONLY");
			
			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setString(1, userId);
			if(condition.equals("all")) {
				pstmt.setString(2, keyword);
				pstmt.setString(3, keyword);
				pstmt.setInt(4, offset);
				pstmt.setInt(5, size);
			} else if(condition.equals("zzim_date")) {
				pstmt.setString(2, keyword);
				pstmt.setInt(3, offset);
				pstmt.setInt(4, size);
			} else if(condition.equals("seller")) {
				pstmt.setString(2, keyword);
				pstmt.setInt(3, offset);
				pstmt.setInt(4, size);
			} else {
				pstmt.setString(2, keyword);
				pstmt.setInt(3, offset);
				pstmt.setInt(4, size);
			}
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				MyPage4DTO dto = new MyPage4DTO();
				
				dto.setMarketNum(rs.getLong("marketnum"));
				dto.setSellerId(rs.getString("sellerid"));
				dto.setSubject(rs.getString("subject"));
				dto.setAddr(rs.getString("addr"));
				dto.setPrice(rs.getString("price"));
				dto.setReg_Date(rs.getString("reg_date"));
				dto.setImageFileName(rs.getString("imgname"));
				
				
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


	public List<MyPage4DTO> listPhotoFile(long marketnum) {
		List<MyPage4DTO> list = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT imgnum, marketnum, imgname FROM marketimgfile WHERE marketnum=?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, marketnum);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				MyPage4DTO dto = new MyPage4DTO();
				
				dto.setFileNum(rs.getLong("imgnum"));
				dto.setMarketNum(rs.getLong("marketnum"));
				dto.setImageFileName(rs.getString("imgname"));
				
				list.add(dto);
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
		
		
		return list;
	}



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

	

	public MyPage4DTO readBoard(String userId, long marketnum) {
		MyPage4DTO dto = new MyPage4DTO();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT a.MARKETNUM marketnum, SELLERID, SUBJECT, ADDR, PRICE, TO_CHAR(a.reg_date, 'YYYY-MM-DD') reg_date, IMGNAME"
					+ " FROM market a" 
					+ " JOIN marketimgfile b on a.marketnum = b.marketnum"
					+ " JOIN zzim c ON a.marketnum = c.marketnum"
					+ " WHERE a.MARKETNUM = ? AND userid=? AND state = 0";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, marketnum);
			pstmt.setString(2, userId);
			
			rs = pstmt.executeQuery();

			if (rs.next()) {
				dto.setMarketNum(rs.getLong("marketnum"));
				dto.setSubject(rs.getString("SUBJECT"));
				dto.setSellerId(rs.getString("SELLERID"));
				dto.setAddr(rs.getString("ADDR"));
				dto.setPrice(rs.getString("PRICE"));
				dto.setReg_Date(rs.getString("REG_DATE"));
				dto.setImageFileName(rs.getString("IMGNAME"));
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





	public MyPage4DTO preReadBoard(long marketnum, String userId) {
		MyPage4DTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			sb.append(" SELECT a.MARKETNUM marketnum, subject FROM market a");
			sb.append(" JOIN zzim c ON a.marketnum = c.marketnum");
			sb.append(" WHERE a.MARKETNUM > ? AND userid=? AND state = 0");
			sb.append(" ORDER BY a.MARKETNUM ASC ");
			sb.append(" FETCH FIRST 1 ROWS ONLY ");	
			
			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setLong(1, marketnum);
			pstmt.setString(2, userId);
			
			rs = pstmt.executeQuery();
			
			if (rs.next()) {
				dto = new MyPage4DTO();
				
				dto.setMarketNum(rs.getLong("marketnum"));
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



	public MyPage4DTO nextReadBoard(long marketnum, String userId) {
		MyPage4DTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			sb.append(" SELECT a.MARKETNUM marketnum, subject FROM market a");
			sb.append(" JOIN zzim c ON a.marketnum = c.marketnum");
			sb.append(" WHERE a.MARKETNUM < ? AND userid=? AND state = 0");
			sb.append(" ORDER BY a.MARKETNUM DESC ");
			sb.append(" FETCH FIRST 1 ROWS ONLY ");
					
			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setLong(1, marketnum);
			pstmt.setString(2, userId);

			rs = pstmt.executeQuery();

			if(rs.next()) {
				dto = new MyPage4DTO();
				
				dto.setMarketNum(rs.getLong("MARKETNUM"));
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
