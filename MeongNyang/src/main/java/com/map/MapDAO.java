package com.map;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.util.DBConn;

public class MapDAO {
	private Connection conn = DBConn.getConnection();
	
	public void insertMap(MapDTO dto) throws SQLException{
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		long seq;

		try {
			sql = " SELECT map_seq.NEXTVAL FROM dula";
			pstmt = conn.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			
			seq = 0;
			
			seq = 0;
			if (rs.next()) {
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
}
