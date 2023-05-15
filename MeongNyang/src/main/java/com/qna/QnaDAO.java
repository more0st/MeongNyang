package com.qna;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.util.DBConn;

public class QnaDAO {
	private Connection conn = DBConn.getConnection();
	
	public void insertQna(QnaDTO dto) throws SQLException{
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		long seq;
		try {
			sql = " SELECT questions_seq.NEXTVAL FROM dual";
			pstmt = conn.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			
			seq = 0;
			if(rs.next()) {
				seq = rs.getLong(1);
			}
			dto.setQesNum(seq);
			
			rs.close();
			pstmt.close();
			rs = null;
			pstmt = null;
			
			
			if(dto.getUserId() != "admin") {
				sql = " INSERT INTO questions(qesNum, subject, content, reg_date, userId, replyContent, replyReg_date )"
						+ " VALUES( ?, ?, ?, SYSDATE, ?, ?, SYSDATE) ";
				
				pstmt = conn.prepareStatement(sql);
				
				pstmt.setLong(1, dto.getQesNum());
				pstmt.setString(2, dto.getSubject());
				pstmt.setString(3, dto.getContent());
				pstmt.setString(4, dto.getUserId());
				pstmt.setString(5, dto.getReplyContent());
			}
			pstmt.executeUpdate();

			
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}  finally {
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
		
		return result;
	}
	
	public int dataCount(String condition, String keyword) {
		int result = 0;
		
		return result;
	}
	
}