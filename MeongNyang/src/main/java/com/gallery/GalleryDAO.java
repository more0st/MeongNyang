package com.gallery;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.util.DBConn;

public class GalleryDAO {
	private Connection conn = DBConn.getConnection();

	public void insertPhoto(GalleryDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		long seq;

		try {
			sql = "SELECT gallery_seq.NEXTVAL FROM dual";
			pstmt = conn.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			
			seq = 0;
			if (rs.next()) {
				seq = rs.getLong(1);
			}
			dto.setPhotoNum(seq);

			rs.close();
			pstmt.close();
			rs = null;
			pstmt = null;

			sql = "INSERT INTO gallery ( photoNum, userId, subject, content, reg_date , hitCount) "
					+ " VALUES (?, ?, ?, ?, SYSDATE, 0)";
			pstmt = conn.prepareStatement(sql);

			pstmt.setLong(1, dto.getPhotoNum());
			pstmt.setString(2, dto.getUserId());
			pstmt.setString(3, dto.getSubject());
			pstmt.setString(4, dto.getContent());

			pstmt.executeUpdate();

			pstmt.close();
			pstmt = null;

			if (dto.getImageFiles() != null) {
				sql = "INSERT INTO galleryImgFile(fileNum, photoNum, imageFilename) VALUES "
						+ " (galleryImgFile_seq.NEXTVAL, ?, ?)";
				pstmt = conn.prepareStatement(sql);
				
				for (int i = 0; i < dto.getImageFiles().length; i++) {
					pstmt.setLong(1, dto.getPhotoNum());
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

	public int dataCount() {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			//sql = "SELECT NVL(COUNT(*), 0) FROM gallery WHERE userId = ?";
			sql = "SELECT NVL(COUNT(*), 0) FROM gallery";
			pstmt = conn.prepareStatement(sql);
			
			//pstmt.setString(1, userId);
			
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
			//sql = "SELECT NVL(COUNT(*), 0) FROM gallery WHERE userId = ?";
			sql = "SELECT NVL(COUNT(*), 0) FROM gallery g"
					+ " JOIN member m ON g.userId = m.userId ";
			if (condition.equals("all")) {
				sql += " WHERE INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1 ";
			} else if (condition.equals("reg_date")) {
				keyword = keyword.replaceAll("(\\-|\\/|\\.)", "");
				sql += " WHERE TO_CHAR(reg_date, 'YYYYMMDD') = ? ";
			} else {
				sql += " WHERE INSTR(" + condition + ", ?) >= 1 ";
			}
			
			pstmt = conn.prepareStatement(sql);
			
			//pstmt.setString(1, userId);
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
	

	public List<GalleryDTO> listPhoto(int offset, int size) {
		List<GalleryDTO> list = new ArrayList<GalleryDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
			sb.append(" SELECT g.photoNum, g.userId, userName, subject, imageFilename, hitCount ");
			sb.append(" FROM gallery g ");
			sb.append(" JOIN member m ON g.userId = m.userId ");
			sb.append(" LEFT OUTER JOIN ( ");
			sb.append("     SELECT fileNum, photoNum, imageFilename FROM ( ");
			sb.append("        SELECT fileNum, photoNum, imageFilename, ");
			sb.append("            ROW_NUMBER() OVER(PARTITION BY photoNum ORDER BY fileNum ASC) rank ");
			sb.append("        FROM galleryImgFile");
			sb.append("     ) WHERE rank = 1 ");
			sb.append(" ) i ON g.photoNum = i.photoNum ");
			//sb.append(" WHERE g.userId = ? ");
			sb.append(" ORDER BY photoNum DESC ");
			sb.append(" OFFSET ? ROWS FETCH FIRST ? ROWS ONLY ");
			
			pstmt = conn.prepareStatement(sb.toString());
			
			//pstmt.setString(1, userId);
			pstmt.setInt(1, offset);
			pstmt.setInt(2, size);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				GalleryDTO dto = new GalleryDTO();
				
				dto.setPhotoNum(rs.getLong("photoNum"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserName(rs.getString("userName"));
				dto.setSubject(rs.getString("subject"));
				dto.setImageFilename(rs.getString("imageFilename"));
				dto.setHitCount(rs.getInt("hitCount"));
				
				dto.setReplyCount(dataCountReply(rs.getLong("photoNum")));
				dto.setBoardLikeCount(countBoardLike(rs.getLong("photoNum")));
				
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
	

	public List<GalleryDTO> listPhoto(int offset, int size, String condition, String keyword) {
		List<GalleryDTO> list = new ArrayList<GalleryDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
			sb.append(" SELECT g.photoNum, g.userId, userName, subject, imageFilename, hitCount ");
			sb.append(" FROM gallery g ");
			sb.append(" JOIN member m ON g.userId = m.userId ");
			sb.append(" LEFT OUTER JOIN ( ");
			sb.append("     SELECT fileNum, photoNum, imageFilename FROM ( ");
			sb.append("        SELECT fileNum, photoNum, imageFilename, ");
			sb.append("            ROW_NUMBER() OVER(PARTITION BY photoNum ORDER BY fileNum ASC) rank ");
			sb.append("        FROM galleryImgFile");
			sb.append("     ) WHERE rank = 1 ");
			sb.append(" ) i ON g.photoNum = i.photoNum ");
			//sb.append(" WHERE g.userId = ? ");
			if (condition.equals("all")) {
				sb.append(" WHERE INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1 ");
			} else if (condition.equals("reg_date")) {
				keyword = keyword.replaceAll("(\\-|\\/|\\.)", "");
				sb.append(" WHERE TO_CHAR(reg_date, 'YYYYMMDD') = ?");
			} else {
				sb.append(" WHERE INSTR(" + condition + ", ?) >= 1 ");
			}
			sb.append(" ORDER BY photoNum DESC ");
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
			
			//pstmt.setString(1, userId);
			//pstmt.setInt(1, offset);
			//pstmt.setInt(2, size);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				GalleryDTO dto = new GalleryDTO();
				
				dto.setPhotoNum(rs.getLong("photoNum"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserName(rs.getString("userName"));
				dto.setSubject(rs.getString("subject"));
				dto.setImageFilename(rs.getString("imageFilename"));
				dto.setHitCount(rs.getInt("hitCount"));
				
				dto.setReplyCount(dataCountReply(rs.getLong("photoNum")));
				dto.setBoardLikeCount(countBoardLike(rs.getLong("photoNum")));
				
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


	public GalleryDTO readPhoto(long num) {
		GalleryDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			sql = "SELECT g.photoNum, g.userId, userName, subject, content, reg_date, hitCount, "
					+ " NVL(boardLikeCount, 0) boardLikeCount "
					+ " FROM gallery g "
					+ " JOIN member m ON g.userId=m.userId  "
					+ " LEFT OUTER JOIN ("
					+ "      SELECT photoNum, COUNT(*) boardLikeCount FROM galleryLike"
					+ "      GROUP BY photoNum"
					+ " ) bc ON g.photoNum = bc.photoNum"
					+ " WHERE g.photoNum = ? ";

			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, num);
			
			rs = pstmt.executeQuery();

			if (rs.next()) {
				dto = new GalleryDTO();
				
				dto.setPhotoNum(rs.getLong("photoNum"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserName(rs.getString("userName"));
				dto.setSubject(rs.getString("subject"));
				dto.setContent(rs.getString("content"));
				dto.setReg_date(rs.getString("reg_date"));
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

	public void updatePhoto(GalleryDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;

		try {
			sql = "UPDATE gallery SET subject=?, content=? WHERE photoNum=?";
			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, dto.getSubject());
			pstmt.setString(2, dto.getContent());
			pstmt.setLong(3, dto.getPhotoNum());

			pstmt.executeUpdate();
			
			pstmt.close();
			pstmt = null;

			
			
			if (dto.getImageFiles() != null) {
				sql = "INSERT INTO galleryImgFile(fileNum, photoNum, imageFilename) VALUES "
						+ " (galleryImgFile_seq.NEXTVAL, ?, ?)";
				pstmt = conn.prepareStatement(sql);
				
				for (int i = 0; i < dto.getImageFiles().length; i++) {
					pstmt.setLong(1, dto.getPhotoNum());
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

	public void deletePhoto(long num) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;

		try {
			sql = "DELETE FROM gallery WHERE photoNum=?";
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

	public List<GalleryDTO> listPhotoFile(long num) {
		List<GalleryDTO> list = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			sql = "SELECT fileNum, photoNum, imageFilename FROM galleryImgFile WHERE photoNum = ?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, num);
			
			rs = pstmt.executeQuery();

			while (rs.next()) {
				GalleryDTO dto = new GalleryDTO();

				dto.setFileNum(rs.getLong("fileNum"));
				dto.setPhotoNum(rs.getLong("photoNum"));
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

	public GalleryDTO readPhotoFile(long fileNum) {
		GalleryDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			sql = "SELECT fileNum, photoNum, imageFilename FROM galleryImgFile WHERE fileNum = ?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, fileNum);
			
			rs = pstmt.executeQuery();

			if (rs.next()) {
				dto = new GalleryDTO();

				dto.setFileNum(rs.getLong("fileNum"));
				dto.setPhotoNum(rs.getLong("photoNum"));
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

	public GalleryDTO preReadPhoto(long num) {
		GalleryDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
			sb.append(" SELECT photoNum, subject FROM gallery ");
			sb.append(" WHERE photoNum > ? ");
			sb.append(" ORDER BY photoNum ASC ");
			sb.append(" FETCH FIRST 1 ROWS ONLY ");
			
			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setLong(1, num);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				dto = new GalleryDTO();
				
				dto.setPhotoNum(rs.getLong("photoNum"));
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

	public GalleryDTO nextReadPhoto(long num) {
		GalleryDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
			sb.append(" SELECT photoNum, subject FROM gallery ");
			sb.append(" WHERE photoNum < ? ");
			sb.append(" ORDER BY photoNum DESC ");
			sb.append(" FETCH FIRST 1 ROWS ONLY ");
			
			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setLong(1, num);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				dto = new GalleryDTO();
				
				dto.setPhotoNum(rs.getLong("photoNum"));
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

	public void deletePhotoFile(String mode, long num) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;

		try {
			if (mode.equals("all")) {
				sql = "DELETE FROM galleryImgFile WHERE photoNum = ?";
			} else {
				sql = "DELETE FROM galleryImgFile WHERE fileNum = ?";
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
			sql = "UPDATE gallery SET hitCount=hitCount+1 WHERE photoNum=?";
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
	
	// 게시물의 공감 추가
	public void insertBoardLike(long num, String userId) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;

		try {
			sql = "INSERT INTO galleryLike(photoNum, userId) VALUES (?, ?)";
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
	

	// 게시글 공감 삭제
	public void deleteBoardLike(long num, String userId) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "DELETE FROM galleryLike WHERE photoNum = ? AND userId = ?";
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
			sql = "SELECT NVL(COUNT(*), 0) FROM galleryLike WHERE photoNum=?";
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
	

	// 로그인 유저의 게시글 공감 유무
	public boolean isUserBoardLike(long num, String userId) {
		boolean result = false;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT photoNum, userId FROM galleryLike WHERE photoNum = ? AND userId = ?";
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
	
	// 게시물의 댓글 및 답글 추가
	public void insertReply(ReplyDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "INSERT INTO galleryReply(replyNum, photoNum, userId, content, originalReplyNum, reg_date) "
					+ " VALUES (galleryReply_seq.NEXTVAL, ?, ?, ?, ?, SYSDATE)";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, dto.getNum());
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
			sql = "SELECT NVL(COUNT(*), 0) FROM galleryReply WHERE photoNum=? AND originalReplyNum=0";
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
			sb.append(" SELECT r.replyNum, r.userId, userName, photoNum, content, r.reg_date, ");
			sb.append("     NVL(answerCount, 0) answerCount ");
			//sb.append("     NVL(likeCount, 0) likeCount, ");
			//sb.append("     NVL(disLikeCount, 0) disLikeCount ");
			sb.append(" FROM galleryReply r ");
			sb.append(" JOIN member m ON r.userId = m.userId ");
			sb.append(" LEFT OUTER  JOIN (");
			sb.append("	    SELECT originalReplyNum, COUNT(*) answerCount ");
			sb.append("     FROM galleryReply ");
			sb.append("     WHERE originalReplyNum != 0 ");
			sb.append("     GROUP BY originalReplyNum ");
			sb.append(" ) a ON r.replyNum = a.originalReplyNum ");
			/*
			sb.append(" LEFT OUTER  JOIN ( ");
			sb.append("	    SELECT replyNum, ");
			sb.append("         COUNT(DECODE(replyLike, 1, 1)) likeCount, ");
			sb.append("         COUNT(DECODE(replyLike, 0, 1)) disLikeCount ");
			sb.append("     FROM bbsReplyLike ");
			sb.append("     GROUP BY replyNum ");
			sb.append(" ) b ON r.replyNum = b.replyNum  ");
			*/
			sb.append(" WHERE photoNum = ? AND r.originalReplyNum=0 ");
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
				dto.setNum(rs.getLong("photoNum"));
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
	
	public ReplyDTO readReply(long replyNum) {
		ReplyDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT replyNum, photoNum, r.userId, userName, content , r.reg_date "
					+ " FROM galleryReply r JOIN member m ON r.userId=m.userId  "
					+ " WHERE replyNum = ? ";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, replyNum);

			rs=pstmt.executeQuery();
			
			if(rs.next()) {
				dto=new ReplyDTO();
				
				dto.setReplyNum(rs.getLong("replyNum"));
				dto.setNum(rs.getLong("photoNum"));
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
			sql = "DELETE FROM galleryReply "
					+ " WHERE replyNum IN  "
					+ " (SELECT replyNum FROM galleryReply START WITH replyNum = ?"
					+ "     CONNECT BY PRIOR replyNum = originalReplyNum)";
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
	
	// 댓글의 답글 리스트
	public List<ReplyDTO> listReplyAnswer(long answer) {
		List<ReplyDTO> list = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
			sb.append(" SELECT replyNum, photoNum, r.userId, userName, content, reg_date, originalReplyNum ");
			sb.append(" FROM galleryReply r ");
			sb.append(" JOIN member m ON r.userId=m.userId ");
			sb.append(" WHERE originalReplyNum=? ");
			sb.append(" ORDER BY replyNum DESC ");
			pstmt = conn.prepareStatement(sb.toString());

			pstmt.setLong(1, answer);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				ReplyDTO dto = new ReplyDTO();

				dto.setReplyNum(rs.getLong("replyNum"));
				dto.setNum(rs.getLong("photoNum"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserName(rs.getString("userName"));
				dto.setContent(rs.getString("content"));
				dto.setReg_date(rs.getString("reg_date"));
				dto.setAnswer(rs.getLong("originalReplyNum"));

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

	// 댓글의 답글 개수
	public int dataCountReplyAnswer(long answer) {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			sql = "SELECT NVL(COUNT(*), 0) FROM galleryReply WHERE originalReplyNum=?";
			pstmt = conn.prepareStatement(sql);

			pstmt.setLong(1, answer);

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

}
