package com.club;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.util.DBConn;

public class ClubDAO {
	private Connection conn = DBConn.getConnection();
	
	//모임글 추가
	public void insertClub(ClubDTO dto) throws SQLException{
		PreparedStatement pstmt = null;
		String sql;
		ResultSet rs = null;
		long seq;
		
		try {
			conn.setAutoCommit(false);
			//club테이블 시퀀스 가져오기
			sql = "SELECT club_seq.NEXTVAL FROM dual";
			pstmt = conn.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			
			seq = 0;
			if (rs.next()) {
				seq = rs.getLong(1);
			}
			dto.setClubNum(seq);

			rs.close();
			pstmt.close();
			rs = null;
			pstmt = null;
			
			//모임글과 모임장 인서트해주기
			sql = " INSERT ALL "
					+ " INTO club (clubNum,clubName,content,reg_date, maxMember, nowMember, hitCount, subject, userId) "
					+ " VALUES (?,?,?,SYSDATE,?,1,0,?,?) "
					+ " INTO clubMember (clubNum, userId, join_date, status) "
					+ " VALUES (?,?,SYSDATE,1 ) "
					+ " SELECT * FROM dual ";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, dto.getClubNum());
			pstmt.setString(2, dto.getClubName());
			pstmt.setString(3, dto.getContent());
			pstmt.setInt(4, dto.getMaxMember());
			pstmt.setString(5, dto.getSubject());
			pstmt.setString(6, dto.getUserId());
			pstmt.setLong(7, dto.getClubNum());
			pstmt.setString(8, dto.getUserId());
			
			pstmt.executeUpdate();
			
			pstmt.close();
			pstmt = null;
			
			//이미지가 있으면 clubImgFile에 인서트해주기
			if (dto.getImageFiles() != null) {
				sql = "INSERT INTO clubImgFile(fileNum, clubNum, imageFilename) VALUES "
						+ " (clubImgFile_seq.NEXTVAL, ?, ?)";
				pstmt = conn.prepareStatement(sql);
				
				for (int i = 0; i < dto.getImageFiles().length; i++) {
					pstmt.setLong(1, dto.getClubNum());
					pstmt.setString(2, dto.getImageFiles()[i]);
					
					pstmt.executeUpdate();
				}
			}
			
			conn.commit();
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
			
			try {
				conn.setAutoCommit(true);
			} catch (SQLException e2) {
			}
			
		}
		
		
	}
	
	
	//페이징처리를 위한 데이터 총개수
		public int dataCount(String userId) {
			int result = 0;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			String sql;
			
			try {
				sql = "SELECT NVL(COUNT(*), 0) FROM club c "
						+ " JOIN clubMember m  ON c.clubNum = m.clubNum "
						+ " WHERE m.userId = ?";
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
		
		
	//  페이징 처리를 위한 검색에서의 데이터 개수
			public int dataCount(String condition, String keyword, String userId) {
			int result = 0;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			String sql;

			try {
				sql = "SELECT NVL(COUNT(*), 0) FROM club c "
						+ " JOIN member m ON c.userId = m.userId ";
				if (condition.equals("all")) {
					sql +=" WHERE (c.userId =  ? AND INSTR(subject, ?) >= 1 ) OR ( c.userId =  ? AND INSTR(content, ?) >= 1 ) ";
				} else if (condition.equals("reg_date")) {
					keyword = keyword.replaceAll("(\\-|\\/|\\.)", "");
					sql +=" WHERE c.userId =  ? AND TO_CHAR(reg_date, 'YYYYMMDD') = ?";
				} else {
					sql +=" WHERE c.userId =  ? AND INSTR(" + condition + ", ?) >= 1 ";
				}
				pstmt = conn.prepareStatement(sql);
				
				
				if (condition.equals("all")) {
					pstmt.setString(1, userId);
					pstmt.setString(2, keyword);
					pstmt.setString(3, userId);
					pstmt.setString(4, keyword);
				}else {
					pstmt.setString(1, userId);
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
		
	
	//페이징처리를 위한 데이터 총개수
	public int dataCount() {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT NVL(COUNT(*), 0) FROM club";
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
	
	
	//  페이징 처리를 위한 검색에서의 데이터 개수
		public int dataCount(String condition, String keyword) {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			sql = "SELECT NVL(COUNT(*), 0) FROM club c "
					+ " JOIN member m ON c.userId = m.userId ";
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
	
	// 게시물 리스트
	public List<ClubDTO> listClub(int offset, int size) {
		List<ClubDTO> list = new ArrayList<ClubDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		//번호,제목,모임장,작성일,인원표시,조회수,좋아요(따로가져와야함)
		try {
			sb.append(" SELECT c.clubNum, userName, subject, hitCount, imageFilename, ");
			sb.append("       TO_CHAR(reg_date, 'YYYY-MM-DD') reg_date, "
					+ " maxMember, nowMember,  ");
			sb.append(" NVL(boardLikeCount, 0) boardLikeCount " );
			sb.append(" FROM club c ");
			sb.append(" JOIN member m ON c.userId = m.userId ");
			sb.append(" LEFT OUTER JOIN ( ");
			sb.append("     SELECT fileNum, clubNum, imageFilename FROM ( ");
			sb.append("        SELECT fileNum, clubNum, imageFilename, ");
			sb.append("            ROW_NUMBER() OVER(PARTITION BY clubNum ORDER BY fileNum ASC) rank ");
			sb.append("        FROM clubImgFile");
			sb.append("     ) WHERE rank = 1 ");
			sb.append(" ) i ON c.clubNum = i.clubNum ");
			sb.append(" LEFT OUTER JOIN (" );
			sb.append("      SELECT clubNum, COUNT(*) boardLikeCount FROM clubLike" );
			sb.append("     GROUP BY clubNum" );
			sb.append(" ) bc ON c.clubNum = bc.clubNum " );
			sb.append(" ORDER BY clubnum DESC ");
			sb.append(" OFFSET ? ROWS FETCH FIRST ? ROWS ONLY ");

			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setInt(1, offset);
			pstmt.setInt(2, size);

			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				ClubDTO dto = new ClubDTO();

				dto.setClubNum(rs.getLong("clubNum"));
				dto.setUserName(rs.getString("userName"));
				dto.setSubject(rs.getString("subject"));
				dto.setHitCount(rs.getInt("hitCount"));
				dto.setReg_date(rs.getString("reg_date"));
				dto.setMaxMember(rs.getInt("maxMember"));
				dto.setNowMember(rs.getInt("nowMember"));
				dto.setImageFilename(rs.getString("imageFilename"));

				dto.setBoardLikeCount(rs.getInt("boardLikeCount"));
				
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
		
	//조건있는 리스트
	public List<ClubDTO> listClub(int offset, int size, String condition, String keyword) {
		List<ClubDTO> list = new ArrayList<ClubDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
			sb.append(" SELECT c.clubNum, userName, subject, hitCount, imageFilename, ");
			sb.append("       TO_CHAR(reg_date, 'YYYY-MM-DD') reg_date, "
					+ " maxMember, nowMember,  ");
			sb.append(" NVL(boardLikeCount, 0) boardLikeCount " );
			sb.append(" FROM club c ");
			sb.append(" JOIN member m ON c.userId = m.userId ");
			sb.append(" LEFT OUTER JOIN ( ");
			sb.append("     SELECT fileNum, clubNum, imageFilename FROM ( ");
			sb.append("        SELECT fileNum, clubNum, imageFilename, ");
			sb.append("            ROW_NUMBER() OVER(PARTITION BY clubNum ORDER BY fileNum ASC) rank ");
			sb.append("        FROM clubImgFile");
			sb.append("     ) WHERE rank = 1 ");
			sb.append(" ) i ON c.clubNum = i.clubNum ");
			sb.append(" LEFT OUTER JOIN (" );
			sb.append("      SELECT clubNum, COUNT(*) boardLikeCount FROM clubLike" );
			sb.append("     GROUP BY clubNum" );
			sb.append(" ) bc ON c.clubNum = bc.clubNum " );
			
			if (condition.equals("all")) {
				sb.append(" WHERE INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1 ");
			} else if (condition.equals("reg_date")) {
				keyword = keyword.replaceAll("(\\-|\\/|\\.)", "");
				sb.append(" WHERE TO_CHAR(reg_date, 'YYYYMMDD') = ?");
			} else {
				sb.append(" WHERE INSTR(" + condition + ", ?) >= 1 ");
			}
			sb.append(" ORDER BY clubnum DESC ");
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
				ClubDTO dto = new ClubDTO();

				dto.setClubNum(rs.getLong("clubNum"));
				dto.setUserName(rs.getString("userName"));
				dto.setSubject(rs.getString("subject"));
				dto.setHitCount(rs.getInt("hitCount"));
				dto.setReg_date(rs.getString("reg_date"));
				dto.setMaxMember(rs.getInt("maxMember"));
				dto.setNowMember(rs.getInt("nowMember"));
				dto.setImageFilename(rs.getString("imageFilename"));
				
				dto.setBoardLikeCount(rs.getInt("boardLikeCount"));

				
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
	
	
	
	//내모임 리스트(조건)
	public List<ClubDTO> myClubList(int offset, int size, String condition, String keyword, String userId) {
		List<ClubDTO> list = new ArrayList<ClubDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
			sb.append(" SELECT distinct c.clubNum, userName, subject, hitCount, imageFilename, ");
			sb.append("       TO_CHAR(reg_date, 'YYYY-MM-DD') reg_date, "
					+ " maxMember, nowMember,  ");
			sb.append(" NVL(boardLikeCount, 0) boardLikeCount " );
			sb.append(" FROM club c ");
			sb.append(" JOIN member m ON c.userId = m.userId ");
			sb.append(" JOIN clubMember cm ON c.clubNum = cm.clubNum ");
			sb.append(" LEFT OUTER JOIN ( ");
			sb.append("     SELECT fileNum, clubNum, imageFilename FROM ( ");
			sb.append("        SELECT fileNum, clubNum, imageFilename, ");
			sb.append("            ROW_NUMBER() OVER(PARTITION BY clubNum ORDER BY fileNum ASC) rank ");
			sb.append("        FROM clubImgFile");
			sb.append("     ) WHERE rank = 1 ");
			sb.append(" ) i ON c.clubNum = i.clubNum ");
			sb.append(" LEFT OUTER JOIN (" );
			sb.append("      SELECT clubNum, COUNT(*) boardLikeCount FROM clubLike" );
			sb.append("     GROUP BY clubNum" );
			sb.append(" ) bc ON c.clubNum = bc.clubNum " );
	
	
			if (condition.equals("all")) {
				sb.append(" WHERE (cm.userId =  ? AND INSTR(subject, ?) >= 1 ) OR ( cm.userId =  ? AND INSTR(content, ?) >= 1 ) ");
			} else if (condition.equals("reg_date")) {
				keyword = keyword.replaceAll("(\\-|\\/|\\.)", "");
				sb.append(" WHERE cm.userId =  ? AND TO_CHAR(reg_date, 'YYYYMMDD') = ?");
			} else {
				sb.append(" WHERE cm.userId =  ? AND INSTR(" + condition + ", ?) >= 1 ");
			}
			sb.append(" ORDER BY clubnum DESC ");
			sb.append(" OFFSET ? ROWS FETCH FIRST ? ROWS ONLY ");

			pstmt = conn.prepareStatement(sb.toString());
			
			if (condition.equals("all")) {
				pstmt.setString(1, userId);
				pstmt.setString(2, keyword);
				pstmt.setString(3, userId);
				pstmt.setString(4, keyword);
				pstmt.setInt(5, offset);
				pstmt.setInt(6, size);
			} else {
				pstmt.setString(1, userId);
				pstmt.setString(2, keyword);
				pstmt.setInt(3, offset);
				pstmt.setInt(4, size);
			}

			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				ClubDTO dto = new ClubDTO();

				dto.setClubNum(rs.getLong("clubNum"));
				dto.setUserName(rs.getString("userName"));
				dto.setSubject(rs.getString("subject"));
				dto.setHitCount(rs.getInt("hitCount"));
				dto.setReg_date(rs.getString("reg_date"));
				dto.setMaxMember(rs.getInt("maxMember"));
				dto.setNowMember(rs.getInt("nowMember"));
				dto.setImageFilename(rs.getString("imageFilename"));

				dto.setBoardLikeCount(rs.getInt("boardLikeCount"));
				
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
	
	
	
	//내모임 리스트
	public List<ClubDTO> myClubList(int offset, int size, String userId) {
		List<ClubDTO> list = new ArrayList<ClubDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
			sb.append(" SELECT c.clubNum, userName, subject, hitCount, imageFilename, ");
			sb.append("       TO_CHAR(reg_date, 'YYYY-MM-DD') reg_date, "
					+ " maxMember, nowMember,  ");
			sb.append(" NVL(boardLikeCount, 0) boardLikeCount " );
			sb.append(" FROM club c ");
			sb.append(" JOIN member m ON c.userId = m.userId ");
			sb.append(" JOIN clubMember cm ON c.clubNum = cm.clubNum ");
			sb.append(" LEFT OUTER JOIN ( ");
			sb.append("     SELECT fileNum, clubNum, imageFilename FROM ( ");
			sb.append("        SELECT fileNum, clubNum, imageFilename, ");
			sb.append("            ROW_NUMBER() OVER(PARTITION BY clubNum ORDER BY fileNum ASC) rank ");
			sb.append("        FROM clubImgFile");
			sb.append("     ) WHERE rank = 1 ");
			sb.append(" ) i ON c.clubNum = i.clubNum ");
			sb.append(" LEFT OUTER JOIN (" );
			sb.append("      SELECT clubNum, COUNT(*) boardLikeCount FROM clubLike" );
			sb.append("     GROUP BY clubNum" );
			sb.append(" ) bc ON c.clubNum = bc.clubNum " );
			sb.append(" WHERE cm.userId =  ? ");
	
			sb.append(" ORDER BY c.clubnum DESC ");
			sb.append(" OFFSET ? ROWS FETCH FIRST ? ROWS ONLY ");

			pstmt = conn.prepareStatement(sb.toString());
			
				pstmt.setString(1, userId);
				pstmt.setInt(2, offset);
				pstmt.setInt(3, size);

			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				ClubDTO dto = new ClubDTO();

				dto.setClubNum(rs.getLong("clubNum"));
				dto.setUserName(rs.getString("userName"));
				dto.setSubject(rs.getString("subject"));
				dto.setHitCount(rs.getInt("hitCount"));
				dto.setReg_date(rs.getString("reg_date"));
				dto.setMaxMember(rs.getInt("maxMember"));
				dto.setNowMember(rs.getInt("nowMember"));
				dto.setImageFilename(rs.getString("imageFilename"));
				
				dto.setBoardLikeCount(rs.getInt("boardLikeCount"));

				
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
	
	
	
	
	

		
	// 조회수 증가하기
	public void updateHitCount(long num) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;

		try {
			sql = "UPDATE club SET hitCount=hitCount+1 WHERE clubnum=?";
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
		
		
	// 해당 게시물 보기
	public ClubDTO readClub(long num) {
		ClubDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			sql = "SELECT c.clubNum, c.userId, userName, subject, content, TO_CHAR(reg_date,'YYYY-MM-DD')reg_date, hitCount, "
					+ " clubName, nowMember, maxMember, "
					+ " NVL(boardLikeCount, 0) boardLikeCount "
					+ " FROM club c "
					+ " JOIN member m ON c.userId=m.userId "
					
					+ " LEFT OUTER JOIN ("
					+ "      SELECT clubNum, COUNT(*) boardLikeCount FROM clubLike"
					+ "      GROUP BY clubNum"
					+ " ) bc ON c.clubNum = bc.clubNum"
					
					+ " WHERE c.clubNum = ? ";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, num);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				dto = new ClubDTO();
				
				dto.setClubNum(rs.getLong("clubNum"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserName(rs.getString("userName"));
				dto.setSubject(rs.getString("subject"));
				dto.setContent(rs.getString("content"));
				dto.setHitCount(rs.getInt("hitCount"));
				dto.setReg_date(rs.getString("reg_date"));
				dto.setClubName(rs.getString("clubName"));
				dto.setNowMember(rs.getInt("nowMember"));
				dto.setMaxMember(rs.getInt("maxMember"));
				
				dto.setBoardLikeCount(rs.getInt("boardLikeCount"));
				
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
		

	
	
	// 로그인 유저의 게시글 공감 유무
		public boolean isUserBoardLike(long num, String userId) {
			boolean result = false;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			String sql;
			
			try {
				sql = "SELECT clubNum, userId FROM clubLike WHERE clubNum = ? AND userId = ?";
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
	
	
	
		
	// 이전글
	public ClubDTO preReadBoard(long num, String condition, String keyword) {
		ClubDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
			if (keyword != null && keyword.length() != 0) {
				sb.append(" SELECT clubNum, subject ");
				sb.append(" FROM club c");
				sb.append(" JOIN member m ON c.userId = m.userId ");
				sb.append(" WHERE ( clubNum > ? ) ");
				if (condition.equals("all")) {
					sb.append("   AND ( INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1 ) ");
				} else if (condition.equals("reg_date")) {
					keyword = keyword.replaceAll("(\\-|\\/|\\.)", "");
					sb.append("   AND ( TO_CHAR(reg_date, 'YYYYMMDD') = ? ) ");
				} else {
					sb.append("   AND ( INSTR(" + condition + ", ?) >= 1 ) ");
				}
				sb.append(" ORDER BY clubNum ASC ");
				sb.append(" FETCH FIRST 1 ROWS ONLY ");

				pstmt = conn.prepareStatement(sb.toString());
				
				pstmt.setLong(1, num);
				pstmt.setString(2, keyword);
				if (condition.equals("all")) {
					pstmt.setString(3, keyword);
				}
			} else {
				sb.append(" SELECT clubNum, subject FROM club ");
				sb.append(" WHERE clubNum > ? ");
				sb.append(" ORDER BY clubNum ASC ");
				sb.append(" FETCH FIRST 1 ROWS ONLY ");

				pstmt = conn.prepareStatement(sb.toString());
				
				pstmt.setLong(1, num);
			}

			rs = pstmt.executeQuery();

			if (rs.next()) {
				dto = new ClubDTO();
				
				dto.setClubNum(rs.getLong("clubNum"));
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
	public ClubDTO nextReadBoard(long num, String condition, String keyword) {
		ClubDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
			if (keyword != null && keyword.length() != 0) {
				sb.append(" SELECT clubNum, subject ");
				sb.append(" FROM club c ");
				sb.append(" JOIN member m ON c.userId = m.userId ");
				sb.append(" WHERE ( clubNum < ? ) ");
				if (condition.equals("all")) {
					sb.append("   AND ( INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1 ) ");
				} else if (condition.equals("reg_date")) {
					keyword = keyword.replaceAll("(\\-|\\/|\\.)", "");
					sb.append("   AND ( TO_CHAR(reg_date, 'YYYYMMDD') = ? ) ");
				} else {
					sb.append("   AND ( INSTR(" + condition + ", ?) >= 1 ) ");
				}
				sb.append(" ORDER BY clubNum DESC ");
				sb.append(" FETCH FIRST 1 ROWS ONLY ");

				pstmt = conn.prepareStatement(sb.toString());
				
				pstmt.setLong(1, num);
				pstmt.setString(2, keyword);
				if (condition.equals("all")) {
					pstmt.setString(3, keyword);
				}
			} else {
				sb.append(" SELECT clubNum, subject FROM club ");
				sb.append(" WHERE clubNum < ? ");
				sb.append(" ORDER BY clubNum DESC ");
				sb.append(" FETCH FIRST 1 ROWS ONLY ");

				pstmt = conn.prepareStatement(sb.toString());
				
				pstmt.setLong(1, num);
			}

			rs = pstmt.executeQuery();

			if (rs.next()) {
				dto = new ClubDTO();
				
				dto.setClubNum(rs.getLong("clubNum"));
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

	
		
	// 게시물 수정
	public void updateClub(ClubDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;

		try {
			sql = "UPDATE club SET subject=?, content=?, maxMember=?, clubName=? WHERE clubNum=? AND userId=?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, dto.getSubject());
			pstmt.setString(2, dto.getContent());
			pstmt.setInt(3, dto.getMaxMember());
			pstmt.setString(4, dto.getClubName());
			pstmt.setLong(5, dto.getClubNum());
			pstmt.setString(6, dto.getUserId());
			
			pstmt.executeUpdate();
			
			pstmt.close();
			pstmt = null;

			if (dto.getImageFiles() != null) {
				sql = "INSERT INTO clubImgFile(fileNum, clubNum, imageFilename) VALUES "
						+ " (club_seq.NEXTVAL, ?, ?)";
				pstmt = conn.prepareStatement(sql);
				
				for (int i = 0; i < dto.getImageFiles().length; i++) {
					pstmt.setLong(1, dto.getClubNum());
					pstmt.setString(2, dto.getImageFiles()[i]);
					
					pstmt.executeUpdate();
				}
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
	
	//게시물 삭제
	public void deleteClub(long num) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;

		try {
			sql = "DELETE FROM club WHERE clubNum=?";
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
		

	
	
	//게시글의 이미지파일 가져오기
	public List<ClubDTO> listPhotoFile(long num) {
		List<ClubDTO> list = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
	
		try {
			sql = "SELECT fileNum, clubNum, imageFilename FROM clubImgFile WHERE clubNum = ?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, num);
			
			rs = pstmt.executeQuery();
	
			while (rs.next()) {
				ClubDTO dto = new ClubDTO();
	
				dto.setFileNum(rs.getLong("fileNum"));
				dto.setClubNum(rs.getLong("clubNum"));
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
		
			
	
	
	//이미지파일 하나만 가져오기
	public ClubDTO readPhotoFile(long fileNum) {
		ClubDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			sql = "SELECT fileNum, clubNum, imageFilename FROM clubImgFile WHERE fileNum = ?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, fileNum);
			
			rs = pstmt.executeQuery();

			if (rs.next()) {
				dto = new ClubDTO();

				dto.setFileNum(rs.getLong("fileNum"));
				dto.setClubNum(rs.getLong("clubNum"));
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
	
		
	//이미지 삭제하기
	public void deletePhotoFile(String mode, long num) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;

		try {
			if (mode.equals("all")) {
				sql = "DELETE FROM clubImgFile WHERE clubNum = ?";
			} else {
				sql = "DELETE FROM clubImgFile WHERE fileNum = ?";
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
			
	//모임멤버가입
	public void insertClubMember(ClubDTO dto) throws SQLException{
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "INSERT INTO clubMember (clubNum, userId, join_date, status)  "
					+ " VALUES (?,?,SYSDATE,0) ";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, dto.getClubNum());
			pstmt.setString(2, dto.getUserId());
			
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
		
	//멤버가입수
	public int memberCount(long num) {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT NVL(COUNT(*), 0) FROM clubMember WHERE clubNum = ?";
			pstmt = conn.prepareStatement(sql);

			pstmt.setLong(1, num);
			
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
		
	
	//모임 멤버 리스트
	public List<ClubDTO> memberList(long num) {
		List<ClubDTO> list = new ArrayList<ClubDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql =  " SELECT clubNum, c.userId, join_date, status, userName FROM clubMember c "
                   + " JOIN member m ON m.userId = c.userId "
                   + " WHERE clubNum = ?" ;
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, num);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				ClubDTO dto = new ClubDTO();
				
				dto.setClubNum(rs.getLong("clubNum"));
				dto.setUserId(rs.getString("userId"));
				dto.setJoin_date(rs.getString("join_date"));
				dto.setStatus(rs.getInt("status"));
				dto.setUserName(rs.getString("userName"));
				
				list.add(dto);
			}
			
		} catch (Exception e) {
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
		

	
	// 멤버수 증가하기
	public void updateMemberCount(long num) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;

		try {
			sql = "UPDATE club SET nowMember = nowMember+1 WHERE clubNum = ? ";
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
		
	// 멤버수 감소하기
	public void deleteMemberCount(long num) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;

		try {
			sql = "UPDATE club SET nowMember = nowMember-1 WHERE clubNum = ? ";
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

	//멤버 탈퇴하기
	public void deleteMember(long num, String userId) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;

		try {
			
			sql = "DELETE FROM clubMember WHERE clubNum=? AND userId=? AND status=0 ";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, num);
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
			
			
	//멤버가 존재하는지 확인
	public boolean isMemberCheck(long num, String userId) {
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String sql;
	boolean result = false;
	
	try {
		sql =  " SELECT clubNum, userId FROM clubMember "
             + " WHERE clubNum = ? AND userId = ? " ;
		
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
	
	
	return result;
}
		
		//멤버가 존재하는지 확인
	public int statusCheck(long num, String userId) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		int result = 2;
		
		try {
			sql =  " SELECT status FROM clubMember "
	             + " WHERE clubNum = ? AND userId = ? " ;
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, num);
			pstmt.setString(2, userId);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				result = rs.getInt("status");
	
			}
			
		} catch (Exception e) {
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
		
		
		return result;
	}
				
				
				
	// 게시물의 공감 추가
	public void insertBoardLike(long num, String userId) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "INSERT INTO clubLike(clubNum, userId) VALUES (?, ?)";
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
	
	
	
	// 게시글 공감 삭제
	public void deleteBoardLike(long num, String userId) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "DELETE FROM clubLike WHERE clubNum = ? AND userId = ?";
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
				

	// 게시물의 공감 개수
	public int countBoardLike(long num) {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT NVL(COUNT(*), 0) FROM clubLike WHERE clubNum=?";
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
				
				
				
				
	// 게시물의 댓글 및 답글 추가
	public void insertReply(ReplyDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "INSERT INTO clubReply(replyNum, clubNum, userId, content, answer, reg_date) "
					+ " VALUES (clubReply_seq.NEXTVAL, ?, ?, ?, ?, SYSDATE)";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, dto.getClubNum());
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
			sql = "SELECT NVL(COUNT(*), 0) FROM clubReply WHERE clubNum=? AND answer=0";
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
		StringBuilder sb = new StringBuilder();
		
		try {
			sb.append(" SELECT r.replyNum, r.userId, userName, clubNum, content, r.reg_date, ");
			sb.append("     NVL(answerCount, 0) answerCount ");
			sb.append(" FROM clubReply r ");
			sb.append(" JOIN member m ON r.userId = m.userId ");
			sb.append(" LEFT OUTER  JOIN (");
			sb.append("	    SELECT answer, COUNT(*) answerCount ");
			sb.append("     FROM clubReply ");
			sb.append("     WHERE answer != 0 ");
			sb.append("     GROUP BY answer ");
			sb.append(" ) a ON r.replyNum = a.answer ");
			sb.append(" WHERE clubNum = ? AND r.answer = 0 ");
			sb.append(" ORDER BY r.replyNum DESC ");
			sb.append(" OFFSET ? ROWS FETCH FIRST ? ROWS ONLY ");
			
			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setLong(1, num);
			pstmt.setInt(2, offset);
			pstmt.setInt(3, size);

			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				ReplyDTO dto = new ReplyDTO();
				
				dto.setReplyNum(rs.getLong("replyNum"));
				dto.setClubNum(rs.getLong("clubNum"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserName(rs.getString("userName"));
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
	
	
	// 댓글의 답글 리스트
	public List<ReplyDTO> listReplyAnswer(long answer) {
		List<ReplyDTO> list = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb=new StringBuilder();
		
		try {
			sb.append(" SELECT replyNum, clubNum, r.userId, userName, content, reg_date, answer ");
			sb.append(" FROM clubReply r ");
			sb.append(" JOIN member m ON r.userId=m.userId ");
			sb.append(" WHERE answer=? ");
			sb.append(" ORDER BY replyNum DESC ");
			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setLong(1, answer);

			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				ReplyDTO dto=new ReplyDTO();
				
				dto.setReplyNum(rs.getLong("replyNum"));
				dto.setClubNum(rs.getLong("clubNum"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserName(rs.getString("userName"));
				dto.setContent(rs.getString("content"));
				dto.setReg_date(rs.getString("reg_date"));
				dto.setAnswer(rs.getLong("answer"));
				
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

	
	// 댓글의 답글 개수
	public int dataCountReplyAnswer(long answer) {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT NVL(COUNT(*), 0) FROM clubReply WHERE answer=?";
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
			sql = "SELECT replyNum, clubNum, r.userId, userName, content ,r.reg_date "
					+ " FROM clubReply r JOIN member m ON r.userId=m.userId  "
					+ " WHERE replyNum = ? ";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, replyNum);

			rs=pstmt.executeQuery();
			
			if(rs.next()) {
				dto=new ReplyDTO();
				
				dto.setReplyNum(rs.getLong("replyNum"));
				dto.setClubNum(rs.getLong("clubNum"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserName(rs.getString("userName"));
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
	
	
	
	
	// 게시물의 댓글 삭제
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
			sql = "DELETE FROM clubReply "
					+ " WHERE replyNum IN  "
					+ " (SELECT replyNum FROM clubReply START WITH replyNum = ?"
					+ "     CONNECT BY PRIOR replyNum = answer)";
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
		
		

