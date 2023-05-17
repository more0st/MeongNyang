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
	
	public void insertMap(MapDTO dto) throws SQLException {
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;
	    String sql;
	    long seq;

	    try {
	        sql = "SELECT map_seq.NEXTVAL FROM dual";
	        pstmt = conn.prepareStatement(sql);

	        rs = pstmt.executeQuery();

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

	        if (dto.getImageFiles() != null) {
	            sql = "INSERT INTO mapImg(fileNum, imageFilename, mapNum) "
	                    + " VALUES (mapImg_seq.NEXTVAL, ?, ?)";

	            pstmt = conn.prepareStatement(sql);

	            for (int i = 0; i < dto.getImageFiles().length; i++) {
	                pstmt.setString(1, dto.getImageFiles()[i]);
	                pstmt.setLong(2, dto.getMapNum());

	                pstmt.executeUpdate();
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        if (pstmt != null) {
	            try {
	                pstmt.close();
	            } catch (Exception e2) {
	            }
	        }
	        if (rs != null) {
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

	

	// 게시물 리스트
	public List<MapDTO> listMap(int offset, int size) {
		List<MapDTO> list = new ArrayList<MapDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;	
		StringBuilder sb = new StringBuilder();
		try {
			sb.append(" SELECT p.mapNum, userName, subject, hitCount, imageFilename, ");
			sb.append("       TO_CHAR(reg_date, 'YYYY-MM-DD') reg_date ") ;
			// sb.append("        NVL(replyCount, 0) replyCount ");
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
		
	// 공감추가	
	public MapDTO readMap(long num) {
		MapDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		
		try {
			sql = "SELECT p.mapNum, p.userId, userName, subject, content, reg_date, hitCount, p.addr,"
					+ "    NVL(boardLikeCount, 0) boardLikeCount "
					+ " FROM map p "
					+ " JOIN member m ON p.userId = m.userId  "
					+ " LEFT OUTER JOIN ("
					+ "      SELECT mapNum, COUNT(*) boardLikeCount FROM mapLike"
					+ "      GROUP BY mapNum"
					+ " ) mc ON p.mapNum = mc.mapNum"
					+ " WHERE p.mapNum = ? ";

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
				dto.setAddr(rs.getString("addr"));
				dto.setHitCount(rs.getInt("hitCount"));
				
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
				sql = "SELECT mapNum, userId FROM mapLike WHERE mapNum = ? AND userId = ?";
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
		
		public void deleteMap(long num, String userId) throws SQLException {
			PreparedStatement pstmt = null;
			String sql;

			try {
				if (userId.equals("admin")) {
					sql = "DELETE FROM map WHERE mapNum=?";
					pstmt = conn.prepareStatement(sql);
					
					pstmt.setLong(1, num);
					
					pstmt.executeUpdate();
				} else {
					sql = "DELETE FROM map WHERE mapNum=? AND userId=?";
					
					pstmt = conn.prepareStatement(sql);
					
					pstmt.setLong(1, num);
					pstmt.setString(2, userId);
					
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
		
		// 게시물의 공감 추가
		public void insertBoardLike(long num, String userId) throws SQLException {
			PreparedStatement pstmt = null;
			String sql;
			
			try {
				sql = "INSERT INTO mapLike(mapNum, userId) VALUES (?, ?)";
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
				sql = "DELETE FROM mapLike WHERE mapNum = ? AND userId = ?";
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
				sql = "SELECT NVL(COUNT(*), 0) FROM mapLike WHERE mapNum=?";
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
		
		// 게시물의 댓글 및 답글 추가
		public void insertReply(MapReplyDTO dto) throws SQLException {
			PreparedStatement pstmt = null;
			String sql;
			
			try {
				sql = "INSERT INTO mapReply(replyNum, num, userId, content, originalReplyNum, reg_date) "
						+ " VALUES (mapReply_seq.NEXTVAL, ?, ?, ?, ?, SYSDATE)";
				pstmt = conn.prepareStatement(sql);
				
				pstmt.setLong(1, dto.getMapNum());
				pstmt.setString(2, dto.getUserId());
				pstmt.setString(3, dto.getContent());
				pstmt.setLong(4, dto.getOriginalReplyNum());
				
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
				sql = "SELECT NVL(COUNT(*), 0) FROM mapReply WHERE num=? AND originalReplyNum = 0";
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
		public List<MapReplyDTO> listReply(long num, int offset, int size) {
			List<MapReplyDTO> list = new ArrayList<>();
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			StringBuilder sb = new StringBuilder();
			
			try {
				sb.append(" SELECT r.replyNum, r.userId, userName, num, content, r.reg_date, ");
				sb.append("     NVL(originalReplyNumCount, 0) originalReplyNumCount ");
				
				//sb.append("     NVL(likeCount, 0) likeCount, ");
				//sb.append("     NVL(disLikeCount, 0) disLikeCount ");
				
				sb.append(" FROM mapReply r ");
				sb.append(" JOIN member m ON r.userId = m.userId ");
				sb.append(" LEFT OUTER  JOIN (");
				sb.append("	    SELECT originalReplyNum, COUNT(*) originalReplyNumCount ");
				sb.append("     FROM mapReply ");
				sb.append("     WHERE originalReplyNum != 0 ");
				sb.append("     GROUP BY originalReplyNum ");
				sb.append(" ) a ON r.replyNum = a.originalReplyNum ");
				
				sb.append(" WHERE num = ? AND r.originalReplyNum=0 ");
				sb.append(" ORDER BY r.replyNum DESC ");
				sb.append(" OFFSET ? ROWS FETCH FIRST ? ROWS ONLY ");
				
				pstmt = conn.prepareStatement(sb.toString());
				
				pstmt.setLong(1, num);
				pstmt.setInt(2, offset);
				pstmt.setInt(3, size);

				rs = pstmt.executeQuery();
				
				while(rs.next()) {
					MapReplyDTO dto = new MapReplyDTO();
					
					dto.setReplyNum(rs.getLong("replyNum"));
					dto.setMapNum(rs.getLong("num"));
					dto.setUserId(rs.getString("userId"));
					dto.setUserName(rs.getString("userName"));
					dto.setContent(rs.getString("content"));
					dto.setReg_date(rs.getString("reg_date"));
					dto.setOriginalReplyNum(rs.getInt("originalReplyNumCount"));
					//dto.setLikeCount(rs.getInt("likeCount"));
					//dto.setDisLikeCount(rs.getInt("disLikeCount"));
					
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
	
		
}
