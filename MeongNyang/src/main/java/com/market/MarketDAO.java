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
					+ "VALUES(?, ?, 'X', ?, ?, ?, ?, SYSDATE, 0, 0, SYSDATE)";
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
			
			pstmt.close();
			pstmt = null;
			
			if (dto.getImageFiles() != null) {
				sql = "INSERT INTO marketimgfile(imgnum, marketnum, imgname) VALUES "
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
	
	public void buy(Long marketNum, String buyerId) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "UPDATE market SET STATE = 2, BUYERID = ?, PAY_DATE = SYSDATE "
					+ "WHERE MarketNum = ?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, buyerId);
			pstmt.setLong(2, marketNum);
			
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
	
	public void cardbuy(Long marketNum, String buyerId) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "UPDATE market SET STATE = 1, BUYERID = ?, PAY_DATE = SYSDATE "
					+ "WHERE MarketNum = ?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, buyerId);
			pstmt.setLong(2, marketNum);
			
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
	
	public void deleteZZIM(long marketNum) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "DELETE FROM zzim WHERE marketNum = ?";
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
			sql = "SELECT ma.MARKETNUM, SELLERID, BUYERID, SUBJECT, CONTENT, ADDR, PRICE, REG_DATE, HITCOUNT, STATE, PAY_DATE, IMGNAME, ZZIMCOUNT"
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
				dto.setZzimCount(rs.getInt("ZZIMCOUNT"));
				
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
	
	public List<MarketDTO> mainlistMarket(int offset, int size){
		List<MarketDTO> list = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT ma.MARKETNUM, SELLERID, BUYERID, SUBJECT, CONTENT, ADDR, PRICE, REG_DATE, HITCOUNT, STATE, PAY_DATE, IMGNAME, ZZIMCOUNT"
					+ " FROM market ma"
					+ " JOIN marketimgfile mf on ma.marketnum = mf.marketnum"
					+ " JOIN (SELECT MARKETNUM, MIN(IMGNUM) IMGNUM FROM marketimgfile"
					+ " GROUP BY MARKETNUM)mf2 on mf2.imgnum = mf.imgnum"
					+ " WHERE STATE = 0 ORDER BY hitcount desc OFFSET ? ROWS FETCH FIRST ? ROWS ONLY";
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
				dto.setZzimCount(rs.getInt("ZZIMCOUNT"));
				
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
	
	public List<MarketDTO> listMarket(int offset, int size, String condition, String keyword){
		List<MarketDTO> list = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT ma.MARKETNUM, SELLERID, BUYERID, SUBJECT, CONTENT, ADDR, PRICE, REG_DATE, HITCOUNT, STATE, PAY_DATE, IMGNAME, ZZIMCOUNT"
					+ " FROM market ma"
					+ " JOIN marketimgfile mf on ma.marketnum = mf.marketnum"
					+ " JOIN (SELECT MARKETNUM, MIN(IMGNUM) IMGNUM FROM marketimgfile"
					+ " GROUP BY MARKETNUM)mf2 on mf2.imgnum = mf.imgnum";
			if (condition.equals("all")) {
				sql += " WHERE INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1 ";
			} else if (condition.equals("reg_date")) {
				keyword = keyword.replaceAll("(\\-|\\/|\\.)", "");
				sql += " WHERE TO_CHAR(reg_date, 'YYYYMMDD') = ?";
			} else {
				sql += " WHERE INSTR(" + condition + ", ?) >= 1 ";
			}
				sql	+= " ORDER BY marketnum DESC OFFSET ? ROWS FETCH FIRST ? ROWS ONLY";
			pstmt = conn.prepareStatement(sql);
			
			if (condition.equals("all")) {
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
				dto.setZzimCount(rs.getInt("ZZIMCOUNT"));
				
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
			sql = "SELECT NVL(COUNT(*), 0) from market";
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
	
	public int dataCount(String condition, String keyword) {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			sql = "SELECT NVL(COUNT(*), 0) FROM market ";
			if (condition.equals("all")) {
				sql += "  WHERE INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1 ";
			} else if (condition.equals("reg_date")) {
				keyword = keyword.replaceAll("(\\-|\\/|\\.)", "");
				sql += "  WHERE TO_CHAR(reg_date, 'YYYYMMDD') = ? ";
			} else {
				sql += "  WHERE INSTR(" + condition + ", ?) >= 1 ";
			}

			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, keyword);
			if (condition.equals("all")) {
				pstmt.setString(2, keyword);
			}

			rs = pstmt.executeQuery();
			
			if (rs.next()) {
				result = rs.getInt(1);
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

		return result;
	}
	
	public MarketDTO readMarket(long marketnum){
		MarketDTO dto = new MarketDTO();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			sql = "SELECT ma.MARKETNUM, SELLERID, BUYERID, SUBJECT, CONTENT, ADDR, PRICE, REG_DATE, HITCOUNT, STATE, PAY_DATE, IMGNAME, NVL(boardLikeCount, 0) ZZIMCOUNT"
					+ " FROM market ma"
					+ " JOIN marketimgfile mf on ma.marketnum = mf.marketnum"
					+ " LEFT OUTER JOIN ( SELECT marketnum, COUNT(*) boardLikeCount FROM zzim GROUP BY marketnum ) bc ON ma.marketnum = bc.marketnum"
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
				dto.setZzimCount(rs.getInt("ZZIMCOUNT"));
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
	
	public MarketDTO preReadPhoto(long MARKETNUM) {
		MarketDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
			sb.append(" SELECT MARKETNUM, subject FROM market ");
			sb.append(" WHERE MARKETNUM > ? ");
			sb.append(" ORDER BY MARKETNUM ASC ");
			sb.append(" FETCH FIRST 1 ROWS ONLY ");
			
			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setLong(1, MARKETNUM);

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

	public MarketDTO nextReadPhoto(long MARKETNUM) {
		MarketDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
			sb.append(" SELECT MARKETNUM, subject FROM market ");
			sb.append(" WHERE MARKETNUM < ? ");
			sb.append(" ORDER BY MARKETNUM DESC ");
			sb.append(" FETCH FIRST 1 ROWS ONLY ");
			
			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setLong(1, MARKETNUM);

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
	
	public void updateHitCount(long num) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;

		try {
			sql = "UPDATE market SET hitCount=hitCount+1 WHERE marketnum=?";
			
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
	
	public MarketDTO readPhotoFile(long fileNum) {
		MarketDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			sql = "SELECT imgnum, marketnum, imgname FROM marketimgfile WHERE imgnum = ?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, fileNum);
			
			rs = pstmt.executeQuery();

			if (rs.next()) {
				dto = new MarketDTO();

				dto.setFileNum(rs.getLong("imgnum"));
				dto.setMarketNum(rs.getLong("marketnum"));
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
	
	// 게시물의 공감 추가
	public void insertBoardLike(long num, String userId) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "INSERT INTO zzim(marketnum, userId, reg_date) VALUES (?, ?, SYSDATE)";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, num);
			pstmt.setString(2, userId);
			
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
				}
			}
		}
		
	}
	
	public void deleteBoardLike(long num, String userId) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "DELETE FROM zzim WHERE marketnum = ? AND userId = ?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, num);
			pstmt.setString(2, userId);
			
			pstmt.executeUpdate();
		} catch (SQLException e) {
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
	
	public boolean isUserBoardLike(long num, String userId) {
		boolean result = false;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT marketnum, userId FROM zzim WHERE marketnum = ? AND userId = ?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, num);
			pstmt.setString(2, userId);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				result = true;
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
	
	
	public int countBoardLike(long num) {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT NVL(COUNT(*), 0) FROM zzim WHERE marketnum=?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, num);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				result = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
				
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
				}
			}
		}
		
		return result;
	}
	
	
	public void insertReply(ReplyDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "INSERT INTO marketReply(REPLYNUM, MARKETNUM, USERID, CONTENT, REG_DATE, REREPLYNUM) "
					+ " VALUES (MARKETREPLY_SEQ.NEXTVAL, ?, ?, ?, SYSDATE, ?)";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, dto.getMarketNum());
			pstmt.setString(2, dto.getUserId());
			pstmt.setString(3, dto.getContent());
			pstmt.setLong(4, dto.getAnswer());
			
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if(pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
				}
		}
		
	}
	
	// 게시물의 댓글 개수
	public int dataCountReply(long num) {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT NVL(COUNT(*), 0) FROM marketReply WHERE MARKETNUM=? AND REREPLYNUM=0";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, num);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				result = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
				
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
				}
			}
		}
		
		return result;
	}

	// 게시물 댓글 리스트
	public List<ReplyDTO> listReply(long num, int offset, int size) {
		List<ReplyDTO> list = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		try {
			sql = "SELECT r.REPLYNUM, r.userId, userName, MARKETNUM, content, r.reg_date, NVL(answerCount, 0) answerCount FROM marketReply "
					+ " r JOIN member m ON r.userId = m.userId LEFT OUTER  JOIN ( SELECT REREPLYNUM, COUNT(*) answerCount FROM marketReply "
					+ " WHERE REREPLYNUM != 0 GROUP BY REREPLYNUM ) a ON r.REPLYNUM = a.REREPLYNUM "
					+ " WHERE MARKETNUM = ? AND r.REREPLYNUM=0 ORDER BY r.replyNum DESC"
					+ " OFFSET ? ROWS FETCH FIRST ? ROWS ONLY";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, num);
			pstmt.setInt(2, offset);
			pstmt.setInt(3, size);

			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				ReplyDTO dto = new ReplyDTO();
				
				dto.setReplyNum(rs.getLong("REPLYNUM"));
				dto.setMarketNum(rs.getLong("MARKETNUM"));
				dto.setUserId(rs.getString("userId"));
				dto.setContent(rs.getString("content"));
				dto.setReg_date(rs.getString("reg_date"));
				dto.setAnswerCount(rs.getInt("answerCount"));
				
				list.add(dto);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
				}
			}
		}
		
		return list;
	}
	
	public List<ReplyDTO> listReplyAnswer(long answer) {
		List<ReplyDTO> list = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb=new StringBuilder();
		
		try {
			sb.append(" SELECT replyNum, marketnum, r.userId, content, reg_date, REREPLYNUM ");
			sb.append(" FROM marketReply r ");
			sb.append(" JOIN member m ON r.userId=m.userId ");
			sb.append(" WHERE REREPLYNUM = ? ");
			sb.append(" ORDER BY replyNum DESC ");
			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setLong(1, answer);

			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				ReplyDTO dto=new ReplyDTO();
				
				dto.setReplyNum(rs.getLong("replyNum"));
				dto.setMarketNum(rs.getLong("marketnum"));
				dto.setUserId(rs.getString("userId"));
				dto.setContent(rs.getString("content"));
				dto.setReg_date(rs.getString("reg_date"));
				dto.setAnswer(rs.getLong("REREPLYNUM"));
				
				list.add(dto);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
				
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
				}
			}
		}
		return list;
	}
	
	public int dataCountReplyAnswer(long answer) {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT NVL(COUNT(*), 0) FROM marketReply WHERE REREPLYNUM=?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, answer);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				result=rs.getInt(1);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
				}
			}
		}
		
		return result;
	}
	
	public ReplyDTO readReply(long replyNum) {
		ReplyDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT replyNum, marketnum, r.userId, content ,r.reg_date "
					+ " FROM marketReply r JOIN member m ON r.userId=m.userId  "
					+ " WHERE replyNum = ? ";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, replyNum);

			rs=pstmt.executeQuery();
			
			if(rs.next()) {
				dto=new ReplyDTO();
				
				dto.setReplyNum(rs.getLong("replyNum"));
				dto.setMarketNum(rs.getLong("marketnum"));
				dto.setUserId(rs.getString("userId"));
				dto.setContent(rs.getString("content"));
				dto.setReg_date(rs.getString("reg_date"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
				
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
				}
			}
		}
		
		return dto;
	}
	
	public void deleteReply(long replyNum, String userId) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		if(! userId.equals("admin")) {
			ReplyDTO dto = readReply(replyNum);
			if(dto == null || (! userId.equals(dto.getUserId()))) {
				return;
			}
		}
		
		try {
			sql = "DELETE FROM marketReply "
					+ " WHERE replyNum IN  "
					+ " (SELECT replyNum FROM marketReply START WITH replyNum = ?"
					+ "     CONNECT BY PRIOR replyNum = REREPLYNUM)";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, replyNum);
			
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
				}
			}
		}		
		
	}

}
