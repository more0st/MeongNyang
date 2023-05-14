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
			
			sql = "INSERT INTO map (mapNum, subject, content, hitCount, reg_date, userId, addr)"
					+ " VALUES (map_seq.NEXTVAL, ?, ?, 0, SYSDATE, ?, ?)";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, dto.getSubject());
			pstmt.setString(2, dto.getContent());
			pstmt.setString(3, dto.getUserId());
			pstmt.setString(4, dto.getAddr());
			
			pstmt.executeUpdate();
			
			pstmt.close();
			pstmt = null;
			
			if(dto.getImageFilename() != null) {
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

	
	public List<MapDTO> listMap(int offset, int size, String userId) {
		List<MapDTO> list = new ArrayList<MapDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
			sb.append(" SELECT p.mapNum, p.userId, userName, subject, imageFilename ");
			sb.append(" FROM Map p ");
			sb.append(" JOIN member m ON p.userId = m.userId ");
			sb.append(" LEFT OUTER JOIN ( ");
			sb.append("     SELECT fileNum, mapNum, imageFilename FROM ( ");
			sb.append("        SELECT fileNum, mapNum, imageFilename, ");
			sb.append("            ROW_NUMBER() OVER(PARTITION BY mapNum ORDER BY fileNum ASC) rank ");
			sb.append("        FROM mapImg");
			sb.append("     ) WHERE rank = 1 ");
			sb.append(" ) i ON p.mapNum = i.mapNum ");
			sb.append(" WHERE p.userId = ? ");
			sb.append(" ORDER BY mapNum DESC ");
			sb.append(" OFFSET ? ROWS FETCH FIRST ? ROWS ONLY ");
			
			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setString(1, userId);
			pstmt.setInt(2, offset);
			pstmt.setInt(3, size);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				MapDTO dto = new MapDTO();
				
				dto.setMapNum(rs.getLong("mapNum"));
				dto.setUserId(rs.getString("userId"));
				dto.setSubject(rs.getString("subject"));
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
	
	public MapDTO readMap(long num) {
		MapDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			sql = "SELECT mapNum, p.userId, userName, subject, content, reg_date "
					+ " FROM map p "
					+ " JOIN member m ON p.userId=m.userId  "
					+ " WHERE mapNum = ? ";

			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, num);
			
			rs = pstmt.executeQuery();

			if (rs.next()) {
				dto = new MapDTO();
				
				dto.setMapNum(rs.getLong("mapNum"));
				dto.setUserId(rs.getString("userId"));
				dto.setSubject(rs.getString("subject"));
				dto.setContent(rs.getString("content"));
				dto.setReg_date(rs.getString("reg_date"));
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
