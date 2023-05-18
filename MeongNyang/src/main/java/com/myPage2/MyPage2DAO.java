package com.myPage2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.util.DBConn;

public class MyPage2DAO {
	private Connection conn = DBConn.getConnection();

	
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

	
	public List<myPage2DTO> listBoard(String userId, int offset, int size) {
		List<myPage2DTO> list = new ArrayList<myPage2DTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT a.marketnum,sellerid,buyerid,subject,content,addr, price, hitCount, TO_CHAR(reg_date, 'YYYY-MM-DD') reg_date, TO_CHAR(pay_date, 'YYYY-MM-DD') pay_date, state, imgname"
					+ " FROM market a"
					+ " JOIN marketimgfile b on a.marketnum = b.marketnum"
					+ " JOIN (SELECT marketnum, MIN(imgnum) imgnum FROM marketimgfile"
					+ " GROUP BY marketnum) c ON c.imgnum = b.imgnum"
					+ " WHERE sellerid = ? ORDER BY marketnum DESC "
					+ " OFFSET ? ROWS FETCH FIRST ? ROWS ONLY";	
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, userId);
			pstmt.setInt(2, offset);
			pstmt.setInt(3, size);
			
			rs = pstmt.executeQuery();			

			while(rs.next() ) {
				myPage2DTO dto = new myPage2DTO();
				
				dto.setMarketnum(rs.getLong("marketnum"));
				dto.setSellerid(rs.getString("sellerid"));
				dto.setBuyerid(rs.getString("buyerid"));
				dto.setSubject(rs.getString("subject"));
				dto.setContent(rs.getString("content"));
				dto.setAddr(rs.getString("addr"));
				dto.setPrice(rs.getString("price"));
				dto.setHitCount(rs.getInt("hitCount"));
				dto.setReg_date(rs.getString("reg_date"));
				dto.setPay_date(rs.getString("pay_date"));
				dto.setState(rs.getInt("state"));
				dto.setImageFilename(rs.getString("imgname"));
				
			
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


	
	public List<myPage2DTO> listBoard(String userId, int offset, int size, String condition, String keyword) {
		List<myPage2DTO> list = new ArrayList<myPage2DTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
			sb.append("SELECT a.marketnum,sellerid,buyerid,subject,content,addr, price, hitCount, TO_CHAR(reg_date, 'YYYY-MM-DD') reg_date, TO_CHAR(pay_date, 'YYYY-MM-DD') pay_date, state, imgname");
			sb.append(" FROM market a");
			sb.append(" JOIN marketimgfile b on a.marketnum = b.marketnum");
			sb.append(" JOIN (SELECT marketnum, MIN(imgnum) imgnum FROM marketimgfile");
			sb.append(" GROUP BY marketnum) c ON c.imgnum = b.imgnum");
			sb.append(" WHERE sellerid = ?");

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
			
			while(rs.next() ) {
				myPage2DTO dto = new myPage2DTO();
				
				dto.setMarketnum(rs.getLong("marketnum"));
				dto.setSellerid(rs.getString("sellerid"));
				dto.setBuyerid(rs.getString("buyerid"));
				dto.setSubject(rs.getString("subject"));
				dto.setContent(rs.getString("content"));
				dto.setAddr(rs.getString("addr"));
				dto.setPrice(rs.getString("price"));
				dto.setHitCount(rs.getInt("hitCount"));
				dto.setReg_date(rs.getString("reg_date"));
				dto.setPay_date(rs.getString("pay_date"));
				dto.setState(rs.getInt("state"));
				dto.setImageFilename(rs.getString("imgname"));
				
			
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

	
	public void updateHitCount(long marketnum) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "UPDATE market SET hitCount=hitCount+1 WHERE marketnum=?";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, marketnum);
			
			pstmt.executeUpdate();			
		} catch (Exception e) {
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
	
	public myPage2DTO readBoard(long marketnum) {
		myPage2DTO dto = new myPage2DTO();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT a.marketnum,sellerid,buyerid,subject,content,addr, price, hitCount, reg_date, pay_date, state, imgname"
					+ " FROM market a"
					+ " JOIN marketimgfile b on a.marketnum = b.marketnum"
					+ " WHERE a.marketnum = ?";
			pstmt = conn.prepareStatement(sql);
					
			pstmt.setLong(1, marketnum);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				
				dto.setMarketnum(rs.getLong("marketnum"));
				dto.setSellerid(rs.getString("sellerid"));
				dto.setBuyerid(rs.getString("buyerid"));
				dto.setSubject(rs.getString("subject"));
				dto.setContent(rs.getString("content"));
				dto.setAddr(rs.getString("addr"));
				dto.setPrice(rs.getString("price"));
				dto.setHitCount(rs.getInt("hitcount"));
				dto.setReg_date(rs.getString("reg_date"));
				dto.setPay_date(rs.getString("pay_date"));
				dto.setState(rs.getInt("state"));
				dto.setImageFilename(rs.getString("imgname"));
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
	
	
	public List<myPage2DTO> listPhotoFile(long marketnum) {
		List<myPage2DTO> list = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT imgnum, marketnum, imgname FROM marketimgfile WHERE marketnum=?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, marketnum);
			
			rs = pstmt.executeQuery();

			while(rs.next()) {
				myPage2DTO dto = new myPage2DTO();
				
				dto.setFileNum(rs.getLong("imgnum"));
				dto.setMarketnum(rs.getLong("marketnum"));
				dto.setImageFilename(rs.getString("imgname"));
				
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
	
	
	public myPage2DTO preReadBoard(long marketnum, String userId) {
		myPage2DTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			sb.append("SELECT marketnum,subject FROM market ");
			sb.append(" WHERE marketnum>? AND sellerid =? ");
			sb.append(" ORDER BY marketnum ASC");
			sb.append(" FETCH FIRST 1 ROWS ONLY");
			
			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setLong(1, marketnum);
			pstmt.setString(2, userId);
			
			rs = pstmt.executeQuery();

			if(rs.next()) {
				dto = new myPage2DTO();
				
				dto.setMarketnum(rs.getLong("marketnum"));
				dto.setSubject(rs.getString("subject"));
			}
			
		} catch (SQLException e) {
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


	public myPage2DTO nextReadBoard(long marketnum, String userId) {
		myPage2DTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
			sb.append("SELECT marketnum,subject FROM market ");
			sb.append(" WHERE marketnum<? AND sellerid =? ");
			sb.append(" ORDER BY marketnum DESC");
			sb.append(" FETCH FIRST 1 ROWS ONLY");
			
			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setLong(1, marketnum);
			pstmt.setString(2, userId);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				dto = new myPage2DTO();
				
				dto.setMarketnum(rs.getLong("marketnum"));
				dto.setSubject(rs.getString("subject"));
			}
			
		} catch (SQLException e) {
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