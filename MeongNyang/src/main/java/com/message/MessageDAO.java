package com.message;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.util.DBConn;

public class MessageDAO {
	private Connection conn = DBConn.getConnection();

	
	// 데이터 추가
	public void insertMessage(MessageDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			sql = "SELECT userId FROM member WHERE userName = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, dto.getReceiveName());
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				dto.setReceiveId(rs.getString("userId"));
			}
			rs.close();
			pstmt.close();
			pstmt = null;
			
			sql = "INSERT INTO message(messageNum, subject, content, send_date, sendId, receiveId, sendState, receiveState) "
					+ " VALUES (message_seq.NEXTVAL, ?, ?, SYSDATE, ?, ?, 1, 1)";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, dto.getSubject());
			pstmt.setString(2, dto.getContent());
			pstmt.setString(3, dto.getSendId());
			pstmt.setString(4, dto.getReceiveId());
			
			pstmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
				}
		}
	}
	

	// 데이터 개수
	public int dataCount(String userId, String category) {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			sql = "SELECT NVL(COUNT(*), 0) FROM message ";
			
			if(category.equals("receive")) {
				sql += "  WHERE receiveId = ? AND receiveState = 1";
			} else if(category.equals("send")) {
				sql += "  WHERE sendId = ? AND sendState = 1";
			}
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, userId);

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

	/*
	// 검색에서의 데이터 개수
	public int dataCount(String condition, String keyword, String userName, String category) {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			sql = "SELECT NVL(COUNT(*), 0) FROM message ms "
					+ " JOIN member1 m ON b.userId = m.userId ";
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
*/
	

	// 게시물 리스트
	public List<MessageDTO> listBoard(int offset, int size, String userId, String category) {
		List<MessageDTO> list = new ArrayList<MessageDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
			sb.append(" SELECT messageNum, subject, content, sendId, receiveId, m1.userName receiveName, m2.userName sendName, ");
			sb.append("       TO_CHAR(send_date, 'YYYY-MM-DD') send_date ");
			sb.append(" FROM message ms ");
			sb.append(" JOIN member m1 ON m1.userId = ms.receiveId ");
			sb.append(" JOIN member m2 ON m2.userId = ms.sendId ");
			
			if(category.equals("receive")) {
				sb.append(" WHERE receiveId = ? AND receiveState = 1");
			} else if(category.equals("send")) {
				sb.append(" WHERE sendId = ? AND sendState = 1");
			}
			
			sb.append(" ORDER BY messageNum DESC ");
			sb.append(" OFFSET ? ROWS FETCH FIRST ? ROWS ONLY ");

			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setString(1, userId);
			pstmt.setInt(2, offset);
			pstmt.setInt(3, size);

			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				MessageDTO dto = new MessageDTO();

				dto.setMessageNum(rs.getLong("messageNum"));
				dto.setSubject(rs.getString("subject"));
				dto.setContent(rs.getString("content"));
				dto.setSendId(rs.getString("sendId"));
				dto.setReceiveId(rs.getString("receiveId"));
				dto.setSend_date(rs.getString("send_date"));
				dto.setReceiveName(rs.getString("receiveName"));
				dto.setSendName(rs.getString("sendName"));

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
	
	

	/*
	public List<BoardDTO> listBoard(int offset, int size, String condition, String keyword) {
		List<BoardDTO> list = new ArrayList<BoardDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
			sb.append(" SELECT num, userName, subject, hitCount, ");
			sb.append("      TO_CHAR(reg_date, 'YYYY-MM-DD') reg_date ");
			sb.append(" FROM bbs b ");
			sb.append(" JOIN member1 m ON b.userId = m.userId ");
			if (condition.equals("all")) {
				sb.append(" WHERE INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1 ");
			} else if (condition.equals("reg_date")) {
				keyword = keyword.replaceAll("(\\-|\\/|\\.)", "");
				sb.append(" WHERE TO_CHAR(reg_date, 'YYYYMMDD') = ?");
			} else {
				sb.append(" WHERE INSTR(" + condition + ", ?) >= 1 ");
			}
			sb.append(" ORDER BY num DESC ");
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
				BoardDTO dto = new BoardDTO();

				dto.setNum(rs.getLong("num"));
				dto.setUserName(rs.getString("userName"));
				dto.setSubject(rs.getString("subject"));
				dto.setHitCount(rs.getInt("hitCount"));
				dto.setReg_date(rs.getString("reg_date"));

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

*/
	// 해당 게시물 보기
	public MessageDTO readBoard(long num) {
		MessageDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			// 게시물별 좋아요 개수: boardLikeCount
			sql = "SELECT messageNum, subject, content, m1.userName receiveName, m2.userName sendName, send_date, sendId, receiveId "
					+ " FROM message ms "
					+ " JOIN member m1 ON m1.userId = ms.receiveId "
					+ " JOIN member m2 ON m2.userId = ms.sendId "
					+ " WHERE messageNum = ? ";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, num);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				dto = new MessageDTO();
				
				dto.setMessageNum(rs.getLong("messageNum"));
				dto.setSubject(rs.getString("subject"));
				dto.setContent(rs.getString("content"));
				dto.setReceiveName(rs.getString("receiveName"));
				dto.setSendName(rs.getString("sendName"));
				dto.setSend_date(rs.getString("send_date"));
				
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


	// 게시물 삭제
	public void deleteBoard(long num, String category) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;

		try {
				sql = "UPDATE message SET ";
				
				if(category.equals("receive")) {
					sql += " receiveState = 0";
				} else if(category.equals("send")) {
					sql += "  sendState = 0";
				}
				sql += "  WHERE messageNum=?";
				
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
	
}
