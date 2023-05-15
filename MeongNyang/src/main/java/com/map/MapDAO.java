package com.map;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.util.DBConn;

public class MapDAO {
	private Connection conn = DBConn.getConnection();
	
	public void insertMap(MapDTO dto) throws SQLException{
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		long seq;

		try {
			
			sql = " SELECT map_seq.NEXTVAL FROM dual";
			pstmt = conn.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			
			seq = 0;
			if(rs.next()) {
				seq = rs.getLong(1);
			}
			dto.setMapNum(seq);
			
			rs.close();
			pstmt.close();
			rs = null;
			pstmt = null;
			
			sql = "INSERT INTO map (mapNum, subject, content, hitCount, reg_date, userId, addr)"
					+ " VALUES (?, ?, ?, 0, SYSDATE, ?, ?)";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, dto.getMapNum());
			pstmt.setString(2, dto.getSubject());
			pstmt.setString(3, dto.getContent());
			pstmt.setString(4, dto.getUserId());
			pstmt.setString(5, dto.getAddr());
			
			pstmt.executeUpdate();
			
			pstmt.close();
			pstmt = null;
			
			if(dto.getImageFiles() != null) {
				sql = "INSERT INTO mapImg(fileNum, imageFilename, mapNum) "
						+ " VALUES (mapImg_seq.NEXTVAL, ?, ?) ";
				
				pstmt = conn.prepareStatement(sql);
				
				for(int i = 0; i < dto.getImageFiles().length; i++) {
					pstmt.setString(1, dto.getImageFiles()[i]);
					pstmt.setLong(2, dto.getMapNum());
					
					pstmt.executeUpdate();
					
				}
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
	}
	
	public int dataCount() {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			sql = "SELECT NVL(COUNT(*), 0) FROM map";
			pstmt = conn.prepareStatement(sql);
			
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
	public int dataCount(String condition, String keyword) {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			sql = "SELECT NVL(COUNT(*), 0) FROM map b "
					+ " JOIN member m ON b.userId = m.userId ";
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

	

	
	public List<MapDTO> listMap(int offset, int size) {
		List<MapDTO> list = new ArrayList<MapDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;	
		StringBuilder sb = new StringBuilder();
		try {
			sb.append(" SELECT p.mapNum, userName, subject, hitCount, imageFilename, ");
			sb.append("       TO_CHAR(reg_date, 'YYYY-MM-DD') reg_date ") ;
			sb.append(" FROM map p");
			sb.append(" JOIN member m ON p.userId = m.userId ");
			sb.append(" LEFT OUTER JOIN ( ");
			sb.append("     SELECT fileNum, mapNum, imageFilename FROM ( ");
			sb.append("        SELECT fileNum, mapNum, imageFilename, ");
			sb.append("            ROW_NUMBER() OVER(PARTITION BY mapNum ORDER BY fileNum ASC) rank ");
			sb.append("        FROM mapImg");
			sb.append("     ) WHERE rank = 1 ");
			sb.append(" ) i ON p.mapNum = i.mapNum ");
			sb.append(" ORDER BY mapNum DESC ");
			sb.append(" OFFSET ? ROWS FETCH FIRST ? ROWS ONLY ");

			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setInt(1, offset);
			pstmt.setInt(2, size);

			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				MapDTO dto = new MapDTO();

				dto.setMapNum(rs.getLong("mapNum"));
				dto.setUserName(rs.getString("userName"));
				dto.setSubject(rs.getString("subject"));
				dto.setHitCount(rs.getInt("hitCount"));
				dto.setReg_date(rs.getString("reg_date"));
				dto.setImageFilename(rs.getString("imageFilename"));

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
		
	public List<MapDTO> listMap(int offset, int size, String condition, String keyword) {
		List<MapDTO> list = new ArrayList<MapDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
			sb.append(" SELECT p.mapNum, userName, subject, hitCount, imageFilename, ");
			sb.append("       TO_CHAR(reg_date, 'YYYY-MM-DD') reg_date ");
			sb.append(" FROM map p ");
			sb.append(" JOIN member m ON p.userId = m.userId ");
			sb.append(" LEFT OUTER JOIN ( ");
			sb.append("     SELECT fileNum, mapNum, imageFilename FROM ( ");
			sb.append("        SELECT fileNum, mapNum, imageFilename, ");
			sb.append("            ROW_NUMBER() OVER(PARTITION BY mapNum ORDER BY fileNum ASC) rank ");
			sb.append("        FROM mapImg");
			sb.append("     ) WHERE rank = 1 ");
			sb.append(" ) i ON p.mapNum = i.mapNum ");
			if (condition.equals("all")) {
				sb.append(" WHERE INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1 ");
			} else if (condition.equals("reg_date")) {
				keyword = keyword.replaceAll("(\\-|\\/|\\.)", "");
				sb.append(" WHERE TO_CHAR(reg_date, 'YYYYMMDD') = ?");
			} else {
				sb.append(" WHERE INSTR(" + condition + ", ?) >= 1 ");
			}
			sb.append(" ORDER BY mapNum DESC ");
			sb.append(" OFFSET ? ROWS FETCH FIRST ? ROWS ONLY ");

			pstmt = conn.prepareStatement(sb.toString());
			
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
			
			while (rs.next()) {
				MapDTO dto = new MapDTO();

				dto.setMapNum(rs.getLong("mapNum"));
				dto.setUserName(rs.getString("userName"));
				dto.setSubject(rs.getString("subject"));
				dto.setHitCount(rs.getInt("hitCount"));
				dto.setReg_date(rs.getString("reg_date"));
				dto.setImageFilename(rs.getString("imageFilename"));

				
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
		
		
	public MapDTO readMap(long num) {
		MapDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			sql = "SELECT mapNum, p.userId, userName, subject, content, reg_date, hitCount "
					+ " FROM map p "
					+ " JOIN member m ON p.userId=m.userId  "
					+ " WHERE mapNum = ? ";

			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, num);
			
			rs = pstmt.executeQuery();

			if (rs.next()) {
				dto = new MapDTO();
				
				dto.setMapNum(rs.getLong("mapNum"));
				dto.setUserName(rs.getString("userName"));
				dto.setUserId(rs.getString("userId"));
				dto.setSubject(rs.getString("subject"));
				dto.setContent(rs.getString("content"));
				dto.setReg_date(rs.getString("reg_date"));
				dto.setHitCount(rs.getInt("hitCount"));
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
	
	public void updateHitCount(long num) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;

		try {
			sql = "UPDATE map SET hitCount=hitCount+1 WHERE mapNum=?";
			
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
	
	// 이전글 보기
	public MapDTO preReadBoard(long num, String condition, String keyword) {
		MapDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
			if (keyword != null && keyword.length() != 0) {
				sb.append(" SELECT mapNum, subject ");
				sb.append(" FROM map p");
				sb.append(" JOIN member m ON p.userId = m.userId ");
				sb.append(" WHERE ( mapNum > ? ) ");
				if (condition.equals("all")) {
					sb.append("   AND ( INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1 ) ");
				} else if (condition.equals("reg_date")) {
					keyword = keyword.replaceAll("(\\-|\\/|\\.)", "");
					sb.append("   AND ( TO_CHAR(reg_date, 'YYYYMMDD') = ? ) ");
				} else {
					sb.append("   AND ( INSTR(" + condition + ", ?) >= 1 ) ");
				}
				sb.append(" ORDER BY mapNum ASC ");
				sb.append(" FETCH FIRST 1 ROWS ONLY ");

				pstmt = conn.prepareStatement(sb.toString());
				
				pstmt.setLong(1, num);
				pstmt.setString(2, keyword);
				if (condition.equals("all")) {
					pstmt.setString(3, keyword);
				}
			} else {
				sb.append(" SELECT mapNum, subject FROM map ");
				sb.append(" WHERE mapNum > ? ");
				sb.append(" ORDER BY mapNum ASC ");
				sb.append(" FETCH FIRST 1 ROWS ONLY ");

				pstmt = conn.prepareStatement(sb.toString());
				
				pstmt.setLong(1, num);
			}

			rs = pstmt.executeQuery();

			if (rs.next()) {
				dto = new MapDTO();
				
				dto.setMapNum(rs.getLong("mapNum"));
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
	// 다음글 보기
		public MapDTO nextReadBoard(long num, String condition, String keyword) {
			MapDTO dto = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			StringBuilder sb = new StringBuilder();

			try {
				if (keyword != null && keyword.length() != 0) {
					sb.append(" SELECT mapNum, subject ");
					sb.append(" FROM map p");
					sb.append(" JOIN member m ON p.userId = m.userId ");
					sb.append(" WHERE ( mapNum < ? ) ");
					if (condition.equals("all")) {
						sb.append("   AND ( INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1 ) ");
					} else if (condition.equals("reg_date")) {
						keyword = keyword.replaceAll("(\\-|\\/|\\.)", "");
						sb.append("   AND ( TO_CHAR(reg_date, 'YYYYMMDD') = ? ) ");
					} else {
						sb.append("   AND ( INSTR(" + condition + ", ?) >= 1 ) ");
					}
					sb.append(" ORDER BY mapNum DESC ");
					sb.append(" FETCH FIRST 1 ROWS ONLY ");

					pstmt = conn.prepareStatement(sb.toString());
					
					pstmt.setLong(1, num);
					pstmt.setString(2, keyword);
					if (condition.equals("all")) {
						pstmt.setString(3, keyword);
					}
				} else {
					sb.append(" SELECT mapNum, subject FROM map ");
					sb.append(" WHERE mapNum < ? ");
					sb.append(" ORDER BY mapNum DESC ");
					sb.append(" FETCH FIRST 1 ROWS ONLY ");

					pstmt = conn.prepareStatement(sb.toString());
					
					pstmt.setLong(1, num);
				}

				rs = pstmt.executeQuery();

				if (rs.next()) {
					dto = new MapDTO();
					
					dto.setMapNum(rs.getLong("mapNum"));
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
		
		public void updateMap(MapDTO dto) throws SQLException {
			PreparedStatement pstmt = null;
			String sql;

			try {
				sql = "UPDATE map SET subject=?, content=?, addr=? WHERE mapNum=? AND userId=?";
				pstmt = conn.prepareStatement(sql);
				
				pstmt.setString(1, dto.getSubject());
				pstmt.setString(2, dto.getContent());
				pstmt.setString(3, dto.getAddr());
				pstmt.setLong(4, dto.getMapNum());
				pstmt.setString(5, dto.getUserId());
				
				pstmt.executeUpdate();
				
				pstmt.close();
				pstmt = null;

				if (dto.getImageFiles() != null) {
					sql = "INSERT INTO mapImg(fileNum, mapNum, imageFilename) VALUES "
							+ " (mapImg_seq.NEXTVAL, ?, ?)";
					pstmt = conn.prepareStatement(sql);

					for (int i = 0; i < dto.getImageFiles().length; i++) {
						pstmt.setLong(1, dto.getMapNum());
						pstmt.setString(2, dto.getImageFiles()[i]);

					}
					pstmt.executeUpdate();
				}


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
		
		public void deleteMap(long num) throws SQLException {
			PreparedStatement pstmt = null;
			String sql;

			try {
				sql = "DELETE FROM map WHERE mapNum=?";
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
		public List<MapDTO> listImgFile(long num) {
			List<MapDTO> list = new ArrayList<>();
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			String sql;
		
			try {
				sql = "SELECT fileNum, mapNum, imageFilename FROM mapImg WHERE mapNum = ?";
				pstmt = conn.prepareStatement(sql);
				
				pstmt.setLong(1, num);
				
				rs = pstmt.executeQuery();
		
				while (rs.next()) {
					MapDTO dto = new MapDTO();
		
					dto.setFileNum(rs.getLong("fileNum"));
					dto.setMapNum(rs.getLong("mapNum"));
					dto.setImageFilename(rs.getString("imageFilename"));
					
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
		
		public MapDTO readImgFile(long fileNum) {
			MapDTO dto = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			String sql;

			try {
				sql = "SELECT fileNum, mapNum, imageFilename FROM mapImg WHERE fileNum = ?";
				pstmt = conn.prepareStatement(sql);
				
				pstmt.setLong(1, fileNum);
				
				rs = pstmt.executeQuery();

				if (rs.next()) {
					dto = new MapDTO();

					dto.setFileNum(rs.getLong("fileNum"));
					dto.setMapNum(rs.getLong("mapNum"));
					dto.setImageFilename(rs.getString("imageFilename"));
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
		
		
		public void deleteImgFile(String mode, long num) throws SQLException {
			PreparedStatement pstmt = null;
			String sql;

			try {
				if (mode.equals("all")) {
					sql = "DELETE FROM mapImg WHERE mapNum = ?";
				} else {
					sql = "DELETE FROM mapImg WHERE fileNum = ?";
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
