package com.event;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.util.DBConn;

public class EventDAO {
	private Connection conn=DBConn.getConnection();

// 이벤트 
	public void insertEvent(EventDTO dto) throws SQLException{
		//이벤트 등록
		PreparedStatement pstmt=null;
		String sql;
		try {
			sql="insert into event(eNum, subject, content, start_date, end_date, enabled, passCount) values(event_seq.nextval,?,?,TO_DATE(?,'YYYY-MM-DD'),TO_DATE(?,'YYYY-MM-DD'),1,?)";
			
			pstmt=conn.prepareStatement(sql);
			
			pstmt.setString(1, dto.getSubject());
			pstmt.setString(2, dto.getContent());
			pstmt.setString(3, dto.getStart_date());
			pstmt.setString(4, dto.getEnd_date());
			pstmt.setLong(5, dto.getPassCount());
			
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
		//이벤트 전체 개수
		int result=0;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		String sql;
		
		try {
			sql="select count(*) from event";
			
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
	public int dataCount(int eventStatus) {
		//진행중인 또는 종료된 이벤트 전체 개수
		int result=0;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		String sql;
		
		try {
			sql="select count(*) from event where enabled=?";
			
			pstmt=conn.prepareStatement(sql);
			pstmt.setInt(1, eventStatus);
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
	
	public List<EventDTO> listEvent(int offset, int size){
		//이벤트 전체 리스트
		List<EventDTO> list=new ArrayList<EventDTO>();
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		StringBuilder sb=new StringBuilder();
		
		try {
			sb.append("select eNum, subject, content, to_char(start_date, 'YYYY-MM-DD') start_date, to_char(end_date, 'YYYY-MM-DD') end_date, enabled ,passCount ");
			sb.append(" from event ");
			//sb.append(" left join participant p on e.enum=p.enum ");
			sb.append(" order by eNum DESC ");
			sb.append(" offset ? rows fetch first ? rows only ");
			
			pstmt=conn.prepareStatement(sb.toString());
			pstmt.setInt(1, offset);
			pstmt.setInt(2, size);
			rs=pstmt.executeQuery();
			
			while(rs.next()) {
				EventDTO dto=new EventDTO();
				
				dto.seteNum(rs.getLong("eNum"));
				dto.setSubject(rs.getString("subject"));
				dto.setContent(rs.getString("content"));
				dto.setStart_date(rs.getString("start_date"));
				dto.setEnd_date(rs.getString("end_date"));
				dto.setEnabled(rs.getInt("enabled"));
				dto.setPassCount(rs.getLong("passCount"));
				//dto.setUserId(rs.getString("userId"));
				
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
	public List<EventDTO> listEvent(int offset, int size, int eventStatus){
		//진행여부에 따른 이벤트 리스트
		List<EventDTO> list=new ArrayList<EventDTO>();
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		StringBuilder sb=new StringBuilder();
		
		try {
			sb.append("select eNum, subject, content, to_char(start_date, 'YYYY-MM-DD') start_date, to_char(end_date, 'YYYY-MM-DD') end_date, enabled ,passCount ");
			sb.append(" from event ");
			//sb.append(" left join participant p on e.enum=p.enum ");
			sb.append(" where enabled=? ");
			sb.append(" order by eNum DESC ");
			sb.append(" offset ? rows fetch first ? rows only ");
			
			pstmt=conn.prepareStatement(sb.toString());
			
			pstmt.setInt(1, eventStatus);
			pstmt.setInt(2, offset);
			pstmt.setInt(3, size);
			rs=pstmt.executeQuery();
			
			while(rs.next()) {
				EventDTO dto=new EventDTO();
				
				dto.seteNum(rs.getLong("eNum"));
				dto.setSubject(rs.getString("subject"));
				dto.setContent(rs.getString("content"));
				dto.setStart_date(rs.getString("start_date"));
				dto.setEnd_date(rs.getString("end_date"));
				dto.setEnabled(rs.getInt("enabled"));
				dto.setPassCount(rs.getLong("passCount"));
				//dto.setUserId(rs.getString("userId"));
				
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
	
	public EventDTO readEvent(long eNum) {
		//이벤트 가져오기
		EventDTO dto=null;
		
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		String sql;
		
		try {
			sql="select eNum, subject, content, to_char(start_date, 'YYYY-MM-DD') start_date, to_char(end_date, 'YYYY-MM-DD') end_date, enabled ,passCount "
					+ "from event "
					+ "where eNum=?";
			
			pstmt=conn.prepareStatement(sql);
			pstmt.setLong(1, eNum);
			rs=pstmt.executeQuery();
			
			if(rs.next()) {
				dto=new EventDTO();
				
				dto.seteNum(rs.getLong("eNum"));
				dto.setSubject(rs.getString("subject"));
				dto.setContent(rs.getString("content"));
				dto.setStart_date(rs.getString("start_date"));
				dto.setEnd_date(rs.getString("end_date"));
				dto.setEnabled(rs.getInt("enabled"));
				dto.setPassCount(rs.getLong("passCount"));
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
	
	public EventDTO preReadEvent(long eNum, int eventStatus) {
		//이전글
		EventDTO dto=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		StringBuilder sb=new StringBuilder();
		
		try {
			if(eventStatus!=2) {
				sb.append("select eNum, subject ");
				sb.append(" from event ");
				sb.append(" where (eNum>?) and (enabled=?) ");
				sb.append(" order by eNum asc ");
				sb.append(" fetch first 1 rows only ");
				
				pstmt=conn.prepareStatement(sb.toString());
				
				pstmt.setLong(1, eNum);
				pstmt.setInt(2, eventStatus);
			} else {
				sb.append("select eNum, subject ");
				sb.append(" from event ");
				sb.append(" where eNum>? ");
				sb.append(" order by eNum asc ");
				sb.append(" fetch first 1 rows only ");

				pstmt=conn.prepareStatement(sb.toString());
				
				pstmt.setLong(1, eNum);
				
			}
			
			rs=pstmt.executeQuery();
			
			if(rs.next()) {
				dto=new EventDTO();
				
				dto.seteNum(rs.getLong("eNum"));
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

	public EventDTO nextReadEvent(long eNum, int eventStatus) {
		//다음글
		EventDTO dto=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		StringBuilder sb=new StringBuilder();
		
		try {
			if(eventStatus!=2) {
				sb.append("select eNum, subject ");
				sb.append(" from event ");
				sb.append(" where (eNum<?) and (enabled=?) ");
				sb.append(" order by eNum desc ");
				sb.append(" fetch first 1 rows only ");
				
				pstmt=conn.prepareStatement(sb.toString());
				
				pstmt.setLong(1, eNum);
				pstmt.setInt(2, eventStatus);
			} else {
				sb.append("select eNum, subject ");
				sb.append(" from event ");
				sb.append(" where eNum<? ");
				sb.append(" order by eNum desc ");
				sb.append(" fetch first 1 rows only ");

				pstmt=conn.prepareStatement(sb.toString());
				
				pstmt.setLong(1, eNum);
				
			}
			
			rs=pstmt.executeQuery();
			
			if(rs.next()) {
				dto=new EventDTO();
				
				dto.seteNum(rs.getLong("eNum"));
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
	
	public void updateEvent(EventDTO dto) throws SQLException{
		//이벤트 수정
		PreparedStatement pstmt=null;
		String sql;
		
		try {
			sql="update event set subject=?, content=?, "
					+ "start_date=to_date(?,'YYYY-MM-DD'), end_date=to_date(?,'YYYY-MM-DD'), passCount=? "
					+ "where eNum=? ";
			
			pstmt=conn.prepareStatement(sql);
			
			pstmt.setString(1, dto.getSubject());
			pstmt.setString(2, dto.getContent());
			pstmt.setString(3, dto.getStart_date());
			pstmt.setString(4, dto.getEnd_date());
			pstmt.setLong(5, dto.getPassCount());
			pstmt.setLong(6, dto.geteNum());
			
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
	
	public void updateEnabled(long eNum, int eventStatus) throws SQLException{
		//진행여부 수정
		PreparedStatement pstmt=null;
		String sql;
		
		try {
			sql="update event set enabled=? "
					+ "where eNum=? ";
			
			pstmt=conn.prepareStatement(sql);
			
			if(eventStatus==1) {
				pstmt.setInt(1, 0);
			} else {
				pstmt.setInt(1, 1);
			}
			
			pstmt.setLong(2, eNum);
			
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
	
	public void deleteEvent(long eNum) throws SQLException{
		//이벤트 삭제
		PreparedStatement pstmt=null;
		String sql;
		
		try {
			sql="delete from event where eNum=?";
			pstmt=conn.prepareStatement(sql);
			pstmt.setLong(1, eNum);
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

//참여자
	public void insertParticipant(long eNum, String userId) throws SQLException{
		//이벤트 등록
		PreparedStatement pstmt=null;
		String sql;
		
		try {
			sql="insert into participant (eNum, userId) values(?,?)";
			
			pstmt=conn.prepareStatement(sql);
			
			pstmt.setLong(1, eNum);
			pstmt.setString(2, userId);
			
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
	public int memberCount(long eNum) {
		//이벤트 참여인원 수
		int result=0;
		return result;
	}
	public List<EventDTO> participantList(long eNum){
		//이벤트 참여인원 리스트
		List<EventDTO> list=new ArrayList<EventDTO>();
		return list;
	}
	//참여인원 증가 감소가 필요한 이유?
	
	public void deleteParticipant(long eNum, String userId) throws SQLException{
		//이벤트 참여 취소
	}
//당첨자
	public void insertPass(EventDTO dto) throws SQLException{
		//이벤트 당첨자 등록
		//몇명을 당첨시킬건지에 대한 컬럼을 추가해야되나
	}
	public List<EventDTO> passList(long eNum){
		//이벤트 당첨자 리스트
		List<EventDTO> list=new ArrayList<EventDTO>();
		return list;
	}
//이미지
	
	public EventDTO readFile(long num) {
		//fileNum으로 이미지가져오기
		//eNum으로 이미지가져오기
		EventDTO dto=null;
		return dto;
	}
	public void deleteFile(long fileNum) throws SQLException{
		//이미지삭제하기
	}
	
	//이미지수정하기
	
	
}
