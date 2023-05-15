package com.market;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.util.DBConn;

public class MarketDAO {
	private Connection conn = DBConn.getConnection();

	public void insertMarket(MarketDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		long seq;
		
		try {
			sql = "SELECT MARKET_SEQ.NEXTVAL FROM dual";
			pstmt = conn.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			
			seq = 0;
			if (rs.next()) {
				seq = rs.getLong(1);
			}
			dto.setMarketNum(seq);

			rs.close();
			pstmt.close();
			rs = null;
			pstmt = null;
			sql = "INSERT INTO market(MARKETNUM, SELLERID, BUYERID, SUBJECT, CONTENT, ADDR, PRICE, REG_DATE, HITCOUNT, STATE, PAY_DATE) "
					+ "VALUES(?, ?, 'X', ?, ?, ?, ?, SYSDATE, 0, 1, SYSDATE)";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, dto.getMarketNum());
			pstmt.setString(2, dto.getSellerId());
			pstmt.setString(3, dto.getSubject());
			pstmt.setString(4, dto.getContent());
			pstmt.setString(5, dto.getAddr());
			pstmt.setInt(6, dto.getPrice());
			
			pstmt.executeUpdate();
			
			pstmt.close();
			pstmt = null;
			
			if (dto.getImageFiles() != null) {
				sql = "INSERT INTO marketImgFile(imgNum, marketnum, imgname) VALUES "
						+ " (MARKETIMGFILE_SEQ.NEXTVAL, ?, ?)";
				pstmt = conn.prepareStatement(sql);
				
				for (int i = 0; i < dto.getImageFiles().length; i++) {
					pstmt.setLong(1, dto.getMarketNum());
					pstmt.setString(2, dto.getImageFiles()[i]);
					
					pstmt.executeUpdate();
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e2) {
				}
			}
		}
	}
	
	public void updateMarket(MarketDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "UPDATE market SET SUBJECT = ?, CONTENT = ?, ADDR = ?, PRICE = ? "
					+ "WHERE MarketNum = ?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, dto.getSubject());
			pstmt.setString(2, dto.getContent());
			pstmt.setString(3, dto.getAddr());
			pstmt.setInt(4, dto.getPrice());
			pstmt.setLong(5, dto.getMarketNum());
			
			pstmt.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e2) {
				}
			}
		}
	}
	
	public void deleteMarket(long marketNum) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "DELETE FROM market WHERE marketNum = ?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, marketNum);
			
			pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e2) {
				}
			}
		}
	}
	
	public List<MarketDTO> listMarket(int offset, int size){
		List<MarketDTO> list = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT ma.MARKETNUM, SELLERID, BUYERID, SUBJECT, CONTENT, ADDR, PRICE, REG_DATE, HITCOUNT, STATE, PAY_DATE, IMGNAME"
					+ " FROM market ma"
					+ " JOIN marketimgfile mf on ma.marketnum = mf.marketnum"
					+ " JOIN (SELECT MARKETNUM, MIN(IMGNUM) IMGNUM FROM marketimgfile"
					+ " GROUP BY MARKETNUM)mf2 on mf2.imgnum = mf.imgnum"
					+ " ORDER BY marketnum DESC OFFSET ? ROWS FETCH FIRST ? ROWS ONLY";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, offset);
			pstmt.setInt(2, size);
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				MarketDTO dto = new MarketDTO();
				dto.setMarketNum(rs.getLong("MARKETNUM"));
				dto.setSellerId(rs.getString("SELLERID"));
				dto.setSubject(rs.getString("SUBJECT"));
				dto.setContent(rs.getString("CONTENT"));
				dto.setAddr(rs.getString("ADDR"));
				dto.setPrice(rs.getInt("PRICE"));
				dto.setReg_date(rs.getString("REG_DATE"));
				dto.setHitCount(rs.getInt("HITCOUNT"));
				dto.setState(rs.getInt("STATE"));
				dto.setPay_date(rs.getString("PAY_DATE"));
				dto.setImageFilename(rs.getString("IMGNAME"));
				
				list.add(dto);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e2) {
				}
			}
			if(rs != null) {
				try {
					rs.close();
				} catch (Exception e2) {
				}
			}
		}
		return list;
	}
	
	public int dataCount() {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		int result = 0;
		
		try {
			sql = "SELECT count(*) from market";
			pstmt = conn.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				result = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e2) {
				}
			}
			if(rs != null) {
				try {
					rs.close();
				} catch (Exception e2) {
				}
			}
		}
		return result;
	}
	
	public MarketDTO readMarket(long marketnum){
		MarketDTO dto = new MarketDTO();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			sql = "SELECT ma.MARKETNUM, SELLERID, BUYERID, SUBJECT, CONTENT, ADDR, PRICE, REG_DATE, HITCOUNT, STATE, PAY_DATE, IMGNAME "
					+ " FROM market ma"
					+ " JOIN marketimgfile mf on ma.marketnum = mf.marketnum"
					+ " WHERE ma.MARKETNUM = ?";

			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, marketnum);
			
			rs = pstmt.executeQuery();

			if (rs.next()) {
				dto.setMarketNum(rs.getLong("MARKETNUM"));
				dto.setSellerId(rs.getString("SELLERID"));
				dto.setSubject(rs.getString("SUBJECT"));
				dto.setContent(rs.getString("CONTENT"));
				dto.setAddr(rs.getString("ADDR"));
				dto.setPrice(rs.getInt("PRICE"));
				dto.setReg_date(rs.getString("REG_DATE"));
				dto.setHitCount(rs.getInt("HITCOUNT"));
				dto.setState(rs.getInt("STATE"));
				dto.setPay_date(rs.getString("PAY_DATE"));
				dto.setImageFilename(rs.getString("IMGNAME"));
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
	
	public List<MarketDTO> listPhotoFile(long num) {
		List<MarketDTO> list = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			sql = "SELECT IMGNUM, MARKETNUM, IMGNAME FROM marketimgfile WHERE MARKETNUM = ?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, num);
			
			rs = pstmt.executeQuery();

			while (rs.next()) {
				MarketDTO dto = new MarketDTO();

				dto.setFileNum(rs.getLong("IMGNUM"));
				dto.setMarketNum(rs.getLong("MARKETNUM"));
				dto.setImageFilename(rs.getString("IMGNAME"));
				
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
	
	public MarketDTO preReadPhoto(long MARKETNUM, String userId) {
		MarketDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
			sb.append(" SELECT MARKETNUM, subject FROM market ");
			sb.append(" WHERE MARKETNUM > ? AND SELLERID = ? ");
			sb.append(" ORDER BY MARKETNUM ASC ");
			sb.append(" FETCH FIRST 1 ROWS ONLY ");
			
			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setLong(1, MARKETNUM);
			pstmt.setString(2, userId);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				dto = new MarketDTO();
				
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

	public MarketDTO nextReadPhoto(long MARKETNUM, String userId) {
		MarketDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
			sb.append(" SELECT MARKETNUM, subject FROM market ");
			sb.append(" WHERE MARKETNUM < ? AND SELLERID = ? ");
			sb.append(" ORDER BY MARKETNUM DESC ");
			sb.append(" FETCH FIRST 1 ROWS ONLY ");
			
			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setLong(1, MARKETNUM);
			pstmt.setString(2, userId);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				dto = new MarketDTO();
				
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
	
	public void deletePhoto(long num) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;

		try {
			sql = "DELETE FROM marketimgfile WHERE marketnum=?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, num);
			
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
				}
			}
		}
	}
	
	public void deletePhotoFile(String mode, long num) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;

		try {
			if (mode.equals("all")) {
				sql = "DELETE FROM marketimgfile WHERE marketnum = ?";
			} else {
				sql = "DELETE FROM marketimgfile WHERE imgNum = ?";
			}
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, num);

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
}
