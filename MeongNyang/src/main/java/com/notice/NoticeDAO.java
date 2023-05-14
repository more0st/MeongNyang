package com.notice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.util.DBConn;

public class NoticeDAO {
	private Connection conn = DBConn.getConnection();
	
	//공지 인서트
	public void insertNotice(NoticeDTO dto) throws SQLException {
		PreparedStatement pstmt=null;
		String sql;
		
		try {
			sql="insert into notice (noticeNum, subject, content, userId, reg_date, hitCount) values(notice_seq.nextval,?,?,?,sysdate,0)";
			pstmt=conn.prepareStatement(sql);
			
			pstmt.setString(1, dto.getSubject());
			pstmt.setString(2, dto.getContent());
			pstmt.setString(3, dto.getUserId());
			
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

	public int dataCount() {
		int result=0;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		String sql;
		
		try {
			sql="select count(*) from notice";
			
			pstmt=conn.prepareStatement(sql);
			rs=pstmt.executeQuery();
			
			if(rs.next()) {
				result=rs.getInt(1);
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

	// 검색에서 전체의 개수
	public int dataCount(String condition, String keyword) {
		int result=0;
		
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		String sql;
		
		try {
			sql="select count(*) "
					+ "from notice n "
					+ "join member m on n.userId=m.userId";
			
			if (condition.equals("all")) {
				sql += "  WHERE INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1 ";
			} else if (condition.equals("reg_date")) {
				keyword = keyword.replaceAll("(\\-|\\/|\\.)", "");
				sql += "  WHERE TO_CHAR(reg_date, 'YYYYMMDD') = ? ";
			} else {
				sql += "  WHERE INSTR(" + condition + ", ?) >= 1 ";
			}
			
			pstmt=conn.prepareStatement(sql);
			
			pstmt.setString(1, keyword);
			if (condition.equals("all")) {
				pstmt.setString(2, keyword);
			}
			
			rs=pstmt.executeQuery();
			
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

	// 게시물 리스트
	public List<NoticeDTO> listNotice(int offset, int size) {
		List<NoticeDTO> list=new ArrayList<NoticeDTO>();
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		StringBuilder sb=new StringBuilder();
		
		try {
			
			sb.append("select noticeNum, n.userId, userName, subject, hitCount, reg_date ");
			sb.append(" from notice n ");
			sb.append(" join member m on n.userId=m.userId ");
			sb.append(" order by noticeNum DESC ");
			sb.append(" offset ? rows fetch first ? rows only ");
			
			pstmt= conn.prepareStatement(sb.toString());
			
			pstmt.setInt(1, offset);
			pstmt.setInt(2, size);
			
			rs=pstmt.executeQuery();
			
			while(rs.next()) {
				
				NoticeDTO dto=new NoticeDTO();
				
				dto.setNoticeNum(rs.getLong("noticeNum"));
				dto.setUserId(rs.getString("userId"));
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

	// 검색에서 리스트
	public List<NoticeDTO> listNotice(int offset, int size, String condition, String keyword) {
		List<NoticeDTO> list = new ArrayList<NoticeDTO>();
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		StringBuilder sb= new StringBuilder();
		
		try {
			
			sb.append("select noticeNum, n.userId, userName, subject, hitCount, reg_date ");
			sb.append(" from notice n ");
			sb.append(" join member m on n.userId=m.userId ");
			
			if(condition.equals("all")) {
				sb.append(" where instr(subject, ?)>=1 or instr(content, ?)>=1 ");
			} else if(condition.equals("reg_date")) {
				keyword = keyword.replaceAll("(\\-|\\/|\\.)", "");
				sb.append(" where to_char(reg_date, 'YYYYMMDD') = ? ");
			} else {
				sb.append(" where instr("+condition+", ?)>=1 ");
			}
			
			sb.append(" order by noticeNum desc ");
			sb.append(" offset ? rows fetch first ? rows only ");
			
			pstmt=conn.prepareStatement(sb.toString());
			
			if(condition.equals("all")) {
				pstmt.setString(1, keyword);
				pstmt.setString(2, keyword);
				pstmt.setInt(3, offset);
				pstmt.setInt(4, size);
			} else {
				pstmt.setString(1, keyword);
				pstmt.setInt(2, offset);
				pstmt.setInt(3, size);
			}
			
			rs=pstmt.executeQuery();
			
			while(rs.next()) {
				NoticeDTO dto=new NoticeDTO();
				
				dto.setNoticeNum(rs.getLong("noticeNum"));
				dto.setUserId(rs.getString("userId"));
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

	
	//공지 가져오기
	public NoticeDTO readNotice(long noticeNum) {
		NoticeDTO dto = null;
		
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		String sql;
		
		try {
			sql="select noticeNum, n.userId, userName, subject, content, hitCount, reg_date "
					+ "from notice n "
					+ "join member m on n.userId=m.userId "
					+ "where noticeNum=?";
			
			pstmt=conn.prepareStatement(sql);
			
			pstmt.setLong(1, noticeNum);
			
			rs=pstmt.executeQuery();
			
			if(rs.next()) {
				dto=new NoticeDTO();
				
				dto.setNoticeNum(rs.getLong("noticeNum"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserName(rs.getString("userName"));
				dto.setSubject(rs.getString("subject"));
				dto.setContent(rs.getString("content"));
				dto.setHitCount(rs.getInt("hitCount"));
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

	// 이전글
	public NoticeDTO preReadNotice(long noticeNum, String condition, String keyword) {
		NoticeDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			if(keyword != null && keyword.length() != 0) {
				sb.append("select noticeNum, subject ");
				sb.append(" from notice n ");
				sb.append(" join member m on n.userId=m.userId ");
				sb.append(" where (noticeNum>?) ");
				if(condition.equals("all")) {
					sb.append(" and (instr(subject, ?)>=1 or instr(content, ?)>=1) ");
				} else if(condition.equals("reg_date")) {
					keyword = keyword.replaceAll("(\\-|\\/|\\.)", "");
					sb.append(" and ( to_char(reg_date, 'YYYYMMDD') = ? ) ");
				} else {
					sb.append(" and ( instr("+condition+", ?)>=1) ");
				}
				
				sb.append(" order by noticeNum asc ");
				sb.append(" fetch first 1 rows only ");
				
				pstmt=conn.prepareStatement(sb.toString());
				
				pstmt.setLong(1, noticeNum);
				pstmt.setString(2, keyword);
				if(condition.equals("all")) {
					pstmt.setString(3, keyword);
				}
			} else {
				sb.append(" select noticeNum, subject from notice ");
				sb.append(" where noticeNum>? ");
				sb.append(" order by noticeNum asc ");
				sb.append(" fetch first 1 rows only ");
				
				pstmt=conn.prepareStatement(sb.toString());
				
				pstmt.setLong(1, noticeNum);
			}
			
			rs=pstmt.executeQuery();
			
			if(rs.next()) {
				dto=new NoticeDTO();
				
				dto.setNoticeNum(rs.getLong("noticeNum"));
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

	// 다음글
	public NoticeDTO nextReadNotice(long noticeNum, String condition, String keyword) {
		NoticeDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			if(keyword != null && keyword.length() != 0) {
				sb.append("select noticeNum, subject ");
				sb.append(" from notice n ");
				sb.append(" join member m on n.userId=m.userId ");
				sb.append(" where (noticeNum<?) ");
				if(condition.equals("all")) {
					sb.append(" and (instr(subject, ?)>=1 or instr(content, ?)>=1) ");
				} else if(condition.equals("reg_date")) {
					keyword = keyword.replaceAll("(\\-|\\/|\\.)", "");
					sb.append(" and ( to_char(reg_date, 'YYYYMMDD') = ? ) ");
				} else {
					sb.append(" and ( instr("+condition+", ?)>=1) ");
				}
				
				sb.append(" order by noticeNum desc ");
				sb.append(" fetch first 1 rows only ");
				
				pstmt=conn.prepareStatement(sb.toString());
				
				pstmt.setLong(1, noticeNum);
				pstmt.setString(2, keyword);
				if(condition.equals("all")) {
					pstmt.setString(3, keyword);
				}
			} else {
				sb.append(" select noticeNum, subject from notice ");
				sb.append(" where noticeNum<? ");
				sb.append(" order by noticeNum desc ");
				sb.append(" fetch first 1 rows only ");
				
				pstmt=conn.prepareStatement(sb.toString());
				
				pstmt.setLong(1, noticeNum);
			}
			
			rs=pstmt.executeQuery();
			
			if(rs.next()) {
				dto=new NoticeDTO();
				
				dto.setNoticeNum(rs.getLong("noticeNum"));
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
	
	//조회수
	public void updateHitCount(long noticeNum) throws SQLException {
		PreparedStatement pstmt=null;
		String sql;
		
		try {
			sql="update notice set hitCount=hitCount+1 where noticeNum=? ";
			pstmt=conn.prepareStatement(sql);
			
			pstmt.setLong(1, noticeNum);
			
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

	//공지 수정
	public void updateNotice(NoticeDTO dto) throws SQLException {
		PreparedStatement pstmt=null;
		String sql;
		
		try {
			sql="update notice set subject=?, content=? where noticeNum=?";
			
			pstmt=conn.prepareStatement(sql);
			
			pstmt.setString(1, dto.getSubject());
			pstmt.setString(2, dto.getContent());
			pstmt.setLong(3, dto.getNoticeNum());
			
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
	
	//공지 삭제
	public void deleteNotice(long noticeNum) throws SQLException {
		PreparedStatement pstmt=null;
		String sql;
		
		try {
			
			sql="delete from notice where noticeNum=?";
			pstmt=conn.prepareStatement(sql);
			pstmt.setLong(1, noticeNum);
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
	
	//이건 뭐냐,,
	public void deleteNoticeList(long[] nums) throws SQLException {
	}


}
