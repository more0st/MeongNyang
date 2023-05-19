package com.map;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

import com.member.SessionInfo;
import com.util.FileManager;
import com.util.MyUploadServlet;
import com.util.MyUtil;

@MultipartConfig
@WebServlet("/map/*")
public class MapServlet extends MyUploadServlet{
	private static final long serialVersionUID = 1L;
	
	private String pathname;

	@Override
	protected void execute(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");
		
		String uri = req.getRequestURI();

		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");

		String ajax = req.getHeader("AJAX");
		
		// 세션 정보
		if (ajax!=null && info == null) {
			// AJAX로 요청해서 로그인이 안된 경우 403 이라는 에러코드를 던짐 
			resp.sendError(403);
			return;
		} else if(info == null) {
			// AJAX로 요청하지 않고 로그인되지 않은 상태 
			forward(req, resp, "/WEB-INF/views/member/login.jsp");
			return;
		}

		
		String root = session.getServletContext().getRealPath("/");
		pathname = root + "uploads" + File.separator + "map";
		
		// uri에 따른 작업 구분
		if(uri.indexOf("list.do") != -1) {
			list(req, resp);
		} else if(uri.indexOf("write.do") != -1) {
			writeForm(req,resp);
		} else if(uri.indexOf("write_ok.do") != -1) {
			writeSubmit(req,resp);
		} else if(uri.indexOf("article.do") != -1) {
			article(req,resp);
		} else if(uri.indexOf("update.do") != -1) {
			updateForm(req,resp);
		} else if(uri.indexOf("update_ok.do") != -1) {
			updateSubmit(req,resp);
		} else if(uri.indexOf("deleteFile.do") != -1) {
			deleteFile(req, resp);
		} else if(uri.indexOf("delete.do") != -1) {
			delete(req, resp);
		} else if(uri.indexOf("insertReply.do") != -1) {
			insertReply(req, resp);
		} else if(uri.indexOf("deleteReply.do") != -1) {
			deleteReply(req, resp);
		} else if(uri.indexOf("insertBoardLike.do") != -1) {
			insertBoardLike(req, resp);
		} else if (uri.indexOf("insertReply.do") != -1) {
			// 댓글 등록
			insertReply(req, resp);
		} else if (uri.indexOf("listReply.do") != -1) {
			// 댓글 리스트
			listReply(req, resp);
		} else if (uri.indexOf("deleteReply.do") != -1) {
			// 댓글 삭제
			deleteReply(req, resp);
		} else if (uri.indexOf("insertReplyLike.do") != -1) {
			// 댓글 좋아요/싫어요 등록
			insertReplyLike(req, resp);			
		} else if (uri.indexOf("countReplyLike.do") != -1) {
			// 댓글 좋아요/실헝요 개수
			countReplyLike(req, resp);
		} else if (uri.indexOf("insertReplyAnswer.do") != -1) {
			// 댓글의 답글 등록 
			insertReplyAnswer(req, resp);
		} else if (uri.indexOf("listReplyAnswer.do") != -1) {
			// 댓글의 답글 리스트
			listReplyAnswer(req, resp);
		} else if (uri.indexOf("deleteReplyAnswer.do") != -1) {
			// 댓글의 답글 삭제
			deleteReplyAnswer(req, resp);
		} else if (uri.indexOf("countReplyAnswer.do") != -1) {
			// 댓글의 답글 개수 
			countReplyAnswer(req, resp);
		}
		
	}
	
	protected void list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 게시글 리스트
		
		MapDAO dao = new MapDAO();
		MyUtil util = new MyUtil();
		
		String cp = req.getContextPath();
		
		try {
			String page = req.getParameter("page");
			int current_page = 1;
			if (page != null) {
				current_page = Integer.parseInt(page);
			}
			
			// 검색
			String condition = req.getParameter("condition");
			String keyword = req.getParameter("keyword");
			if (condition == null) {
				condition = "all";
				keyword = "";
			}

			// GET 방식인 경우 디코딩
			if (req.getMethod().equalsIgnoreCase("GET")) {
				keyword = URLDecoder.decode(keyword, "utf-8");
			}
			
			// 전체 데이터 개수
			int dataCount;
			if (keyword.length() == 0) {
				dataCount = dao.dataCount();
			} else {
				dataCount = dao.dataCount(condition, keyword);
			}
			
			// 전체 페이지 수
			int size = 10;
			int total_page = util.pageCount(dataCount, size);
			if (current_page > total_page) {
				current_page = total_page;
			}
			
			// 게시물 가져오기
			int offset = (current_page - 1) * size;
			if(offset < 0) offset = 0;
			
			List<MapDTO> list = null;
			if (keyword.length() == 0) {
				list = dao.listMap(offset, size);
			} else {
				list = dao.listMap(offset, size, condition, keyword);
			}

			String query = "";
			if (keyword.length() != 0) {
				query = "condition=" + condition + "&keyword=" + URLEncoder.encode(keyword, "utf-8");
			}
			
			// 페이징 처리
			String listUrl = cp + "/map/list.do";
			String articleUrl = cp + "/map/article.do?page=" + current_page;
			if (query.length() != 0) {
				listUrl += "?" + query;
				articleUrl += "&" + query;
			}			
			
			String paging = util.paging(current_page, total_page, listUrl);
			
			// 포워딩할 JSP에 전달할 속성
			req.setAttribute("list", list);
			req.setAttribute("page", current_page);
			req.setAttribute("total_page", total_page);
			req.setAttribute("dataCount", dataCount);
			req.setAttribute("size", size);
			req.setAttribute("articleUrl", articleUrl);
			req.setAttribute("paging", paging);
			req.setAttribute("condition", condition);
			req.setAttribute("keyword", keyword);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		forward(req, resp, "/WEB-INF/views/map/list.jsp");
	}

	protected void writeForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 글쓰기 폼
		
		req.setAttribute("mode", "write");
		forward(req, resp, "/WEB-INF/views/map/write.jsp");
	}
	protected void writeSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	    // 글 저장
	    MapDAO dao = new MapDAO();
	    
	    HttpSession session = req.getSession();
	    SessionInfo info = (SessionInfo) session.getAttribute("member");

	    String cp = req.getContextPath();
	    if (req.getMethod().equalsIgnoreCase("GET")) {
	        resp.sendRedirect(cp + "/map/list.do");
	        return;
	    }
	    
	    try {
	        MapDTO dto = new MapDTO();
	        
	        dto.setUserId(info.getUserId());
	        
	        dto.setSubject(req.getParameter("subject"));
	        dto.setContent(req.getParameter("content"));
	        dto.setAddr(req.getParameter("addr"));
	        
	        // 숨겨진 좌표 값을 받아와서 DTO에 설정합니다.
	        String coordinate = req.getParameter("coordinate");
	        dto.setAddr(coordinate);
	        
	        
	        Map<String, String[]> map = doFileUpload(req.getParts(), pathname);
	        if (map != null) {
	            String[] saveFiles = map.get("saveFilenames");
	            dto.setImageFiles(saveFiles);
	        }

	        dao.insertMap(dto);
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    resp.sendRedirect(cp + "/map/list.do");
	}
	protected void article(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 글보기
				MapDAO dao = new MapDAO();
				MyUtil util = new MyUtil();
				
				String cp = req.getContextPath();
				
				String page = req.getParameter("page");
				String query = "page=" + page;

				try {
					long num = Long.parseLong(req.getParameter("num"));
					String condition = req.getParameter("condition");
					String keyword = req.getParameter("keyword");
					if (condition == null) {
						condition = "all";
						keyword = "";
					}
					keyword = URLDecoder.decode(keyword, "utf-8");

					if (keyword.length() != 0) {
						query += "&condition=" + condition + "&keyword=" + URLEncoder.encode(keyword, "UTF-8");
					}

					// 조회수 증가
					dao.updateHitCount(num);

					// 게시물 가져오기
					MapDTO dto = dao.readMap(num);
					if (dto == null) { // 게시물이 없으면 다시 리스트로
						resp.sendRedirect(cp + "/map/list.do?" + query);
						return;
					}
					
					dto.setContent(util.htmlSymbols(dto.getContent()));

					
					// 로그인 유저의 게시글 공감 여부 
					HttpSession session = req.getSession();
					SessionInfo info = (SessionInfo) session.getAttribute("member"); 
					boolean isUserLike = dao.isUserBoardLike(num, info.getUserId());
					
					
					// 이전글 다음글
					MapDTO preReadDto = dao.preReadBoard(dto.getMapNum(), condition, keyword);
					MapDTO nextReadDto = dao.nextReadBoard(dto.getMapNum(), condition, keyword);

					List<MapDTO> listFile = dao.listImgFile(num);
					

					
					// JSP로 전달할 속성
					req.setAttribute("dto", dto);
					req.setAttribute("page", page);
					req.setAttribute("query", query);
					req.setAttribute("preReadDto", preReadDto);
					req.setAttribute("nextReadDto", nextReadDto);
					req.setAttribute("listFile", listFile);
					// req.setAttribute("list", list);

					req.setAttribute("isUserLike", isUserLike);
					
					// 포워딩
					forward(req, resp, "/WEB-INF/views/map/article.jsp");
					return;
				} catch (Exception e) {
					e.printStackTrace();
				}

		
		resp.sendRedirect(cp + "/map/list.do?page=" + page);
	}
	protected void updateForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 글 수정
		MapDAO dao = new MapDAO();

		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");
		
		String cp = req.getContextPath();

		String page = req.getParameter("page");

		try {
			long num = Long.parseLong(req.getParameter("num"));
			MapDTO dto = dao.readMap(num);

			if (dto == null) {
				resp.sendRedirect(cp + "/map/list.do?page=" + page);
				return;
			}

			if (! dto.getUserId().equals(info.getUserId())) {
				resp.sendRedirect(cp + "/map/list.do?page=" + page);
				return;
			}

			List<MapDTO> listFile = dao.listImgFile(num);
			
			req.setAttribute("dto", dto);
			req.setAttribute("page", page);
			req.setAttribute("listFile", listFile);
			
			req.setAttribute("mode", "update");

			forward(req, resp, "/WEB-INF/views/map/write.jsp");
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}

		resp.sendRedirect(cp + "/map/list.do?page=" + page);
	
		
	}
	protected void updateSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 글 수정 완료
		MapDAO dao = new MapDAO();

		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");
		
		String cp = req.getContextPath();
		if (req.getMethod().equalsIgnoreCase("GET")) {
			resp.sendRedirect(cp + "/map/list.do");
			return;
		}

		String page = req.getParameter("page");
		//long num = 0;
		
		try {
			MapDTO dto = new MapDTO();
			
			
			// num = Long.parseLong(req.getParameter("mapNum"));
			// dto.setMapNum(num);
			
			
			// 숨겨진 좌표 값을 받아와서 DTO에 설정합니다.
	        String coordinate = req.getParameter("coordinate");
	        dto.setAddr(coordinate);
			
			dto.setMapNum(Long.parseLong(req.getParameter("mapNum")));
			dto.setSubject(req.getParameter("subject"));
			dto.setContent(req.getParameter("content"));
			dto.setUserId(info.getUserId());
			
			Map<String, String[]> map = doFileUpload(req.getParts(), pathname);
			if (map != null) {
				String[] saveFiles = map.get("saveFilenames");
				dto.setImageFiles(saveFiles);
			}

			dao.updateMap(dto);
		} catch (Exception e) {
			e.printStackTrace();
		}

		resp.sendRedirect(cp + "/map/list.do?page=" + page);

		// resp.sendRedirect(cp + "/map/article.do?page=" +page+ "&num="+ num);
		
	}
		
	protected void deleteFile(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 수정에서 파일 삭제
		
		MapDAO dao = new MapDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");

		String cp = req.getContextPath();

		String page = req.getParameter("page");

		try {
			long num = Long.parseLong(req.getParameter("num"));
			long fileNum = Long.parseLong(req.getParameter("fileNum"));
			
			
			
			MapDTO dto = dao.readMap(num);

			
			if (dto == null) {
				resp.sendRedirect(cp + "/map/list.do?page=" + page);
				return;
			}

			if (!info.getUserId().equals(dto.getUserId())) {
				resp.sendRedirect(cp + "/map/list.do?page=" + page);
				return;
			}
			
			MapDTO vo = dao.readImgFile(fileNum);
			if(vo != null) {
				FileManager.doFiledelete(pathname, vo.getImageFilename());
				
				dao.deleteImgFile("one", fileNum);
			}
			

			resp.sendRedirect(cp + "/map/update.do?num=" + num + "&page=" + page);
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}

		resp.sendRedirect(cp + "/map/list.do?page=" + page);
	}
		
		
	protected void delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 게시글 삭제
		MapDAO dao = new MapDAO();

		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");
		
		String cp = req.getContextPath();
		
		String page = req.getParameter("page");
		String query = "page=" + page;

		try {
			long num = Long.parseLong(req.getParameter("mapNum"));
			String condition = req.getParameter("condition");
			String keyword = req.getParameter("keyword");
			if (condition == null) {
				condition = "all";
				keyword = "";
			}
			keyword = URLDecoder.decode(keyword, "utf-8");

			if (keyword.length() != 0) {
				query += "&condition=" + condition + "&keyword=" + URLEncoder.encode(keyword, "UTF-8");
			}
			
			MapDTO dto = dao.readMap(num);
			if (dto == null) {
				resp.sendRedirect(cp + "/map/list.do?page=" + page);
				return;
			}
			
			// 게시물을 올린 사용자가 아니면
			if (!dto.getUserId().equals(info.getUserId())) {
				resp.sendRedirect(cp + "/map/list.do?page=" + page);
				return;
			}

			// 이미지 파일 지우기
			List<MapDTO> listFile = dao.listImgFile(num);
			for (MapDTO vo : listFile) {
				FileManager.doFiledelete(pathname, vo.getImageFilename());
			}
			dao.deleteImgFile("all", num);

			dao.deleteMap(num, info.getUserId());

		} catch (Exception e) {
			e.printStackTrace();
		}

		resp.sendRedirect(cp + "/map/list.do?" + query);

	}
	protected void insertBoardLike(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 게시글 공감(좋아요) 저장 : AJAX-JSON 으로.  
		
		  
		MapDAO dao = new MapDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");
		
		String state = "false";
		int boardLikeCount = 0;
		
		try {
			long num = Long.parseLong(req.getParameter("mapNum"));
			String isNoLike = req.getParameter("isNoLike");
			
			if(isNoLike.equals("true")) {
				dao.insertBoardLike(num, info.getUserId());	// 공감
			} else {
				dao.deleteBoardLike(num, info.getUserId());	// 공감 취소
			}
			
			// 공감 개수 
			boardLikeCount = dao.countBoardLike(num);
			
			state = "true";
			
		} catch (SQLException e) {
			state = "liked";
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JSONObject job = new JSONObject();
		job.put("state", state);
		job.put("boardLikeCount", boardLikeCount);
		
		
		resp.setContentType("text/html;charset=utf-8");
		PrintWriter out = resp.getWriter();
		out.print(job.toString());
		
	}

	protected void insertReply(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		 // 게시글 댓글/답글 저장 : AJAX-JSON 으로.  
		  
		MapDAO dao = new MapDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");
		
		String state = "false";
		try {
			MapReplyDTO dto = new MapReplyDTO();
			
			long num = Long.parseLong(req.getParameter("num"));
			dto.setMapNum(num);
			dto.setUserId(info.getUserId());
			dto.setContent(req.getParameter("content"));
			String originalReplyNum = req.getParameter("originalReplyNum");
			if(originalReplyNum != null) {
				dto.setOriginalReplyNum(Long.parseLong(originalReplyNum));
			}
			
			dao.insertReply(dto);
			
			state = "true";
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JSONObject job = new JSONObject();
		job.put("state", state);
		
		resp.setContentType("text/html;charset=utf-8");
		PrintWriter out = resp.getWriter();
		out.print(job.toString());
		
	}

	protected void listReply(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 게시글 댓글 리스트 : AJAX-Text  
		
		MapDAO dao = new MapDAO();
		MyUtil util = new MyUtil();
		
		try {
			long num = Long.parseLong(req.getParameter("mapNum"));
			String pageNo = req.getParameter("pageNo");
			int current_page = 1;
			if(pageNo != null) {
				current_page = Integer.parseInt(pageNo);
			}
			
			int size = 5;
			int total_page = 0;
			int replyCount = 0;
			
			replyCount = dao.dataCountReply(num);
			total_page = util.pageCount(replyCount, size);
			if(current_page > total_page) {
				current_page = total_page;
			}
			
			// 리스트에 출력할 데이터
			int offset = (current_page -1) * size;
			if(offset < 0) offset = 0;
			
			List<MapReplyDTO> listReply = dao.listReply(num, offset, size);
			
			// 엔터를 <br>
			for(MapReplyDTO dto : listReply) {
				dto.setContent(dto.getContent().replaceAll("\n", "<br>"));
			}
			
			// 페이징 처리 : AJAX 용 - listPage : 자바스크립트 함수명
			String paging = util.pagingMethod(current_page, total_page, "listPage");
			
			req.setAttribute("listReply", listReply);
			req.setAttribute("pageNo", pageNo);
			req.setAttribute("replyCount", replyCount);
			req.setAttribute("total_page", total_page);
			req.setAttribute("paging", paging);

			forward(req, resp, "/WEB-INF/views/map/listReply.jsp");
			return;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		resp.sendError(400);
		
	}
	

	protected void deleteReply(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 게시글 댓글 삭제 : AJAX-JSON  
		MapDAO dao = new MapDAO();

		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		String state = "false";
		
		try {
			long replyNum = Long.parseLong(req.getParameter("replyNum"));
			dao.deleteReply(replyNum, info.getUserId());
			state = "true";
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JSONObject job = new JSONObject();
		job.put("state", state);
		
		resp.setContentType("text/html; charset=utf-8");
		PrintWriter out = resp.getWriter();
		out.print(job.toString());
		
		
	}

	protected void insertReplyLike(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 게시글 댓글 좋아요/싫어요 추가 : AJAX-JSON  
	
	}
	
	protected void countReplyLike(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 게시글 댓글 좋아요/싫어요 개수 : AJAX-JSON  
	
	}

	protected void insertReplyAnswer(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 게시글 댓글에 답글 추가 : AJAX-JSON  
	
	}

	protected void listReplyAnswer(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 게시글 댓글에 답글 리스트 : AJAX-Text  
			MapDAO dao = new MapDAO();
		
				try {
					long originalReplyNum = Long.parseLong(req.getParameter("originalReplyNum"));
					
					List<MapReplyDTO> listReplyAnswer = dao.listReplyAnswer(originalReplyNum);
					
					for(MapReplyDTO dto : listReplyAnswer) {
						dto.setContent(dto.getContent().replaceAll("\n", "<br>"));
					}
					
					req.setAttribute("listReplyAnswer", listReplyAnswer);
					
					forward(req, resp,"/WEB-INF/views/map/listReplyAnswer.jsp");
					return;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		
		
	
	
	protected void deleteReplyAnswer(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 게시글 댓글에 답글 삭제 : AJAX-JSON  
	
	}

	protected void countReplyAnswer(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 게시글 댓글에 답글 개수 : AJAX-JSON  
		
		MapDAO dao = new MapDAO();
		int count = 0;
		
		try {
			long originalReplyNum = Long.parseLong(req.getParameter("originalReplyNum"));
			count = dao.dataCountReplyAnswer(originalReplyNum);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	JSONObject job = new JSONObject();
	job.put("count", count);
	
	resp.setContentType("text/html; charset=utf-8");
	PrintWriter out = resp.getWriter();
	out.print(job.toString());
		
		
	}	
	

	
}
