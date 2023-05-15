package com.map;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
		String cp = req.getContextPath();

		
		// 세션 정보
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		if (info == null) { // 로그인되지 않은 경우
			resp.sendRedirect(cp + "/member/login.do");
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
		} else if(uri.indexOf("insertLikeBoard.do") != -1) {
			insertLikeBoard(req, resp);
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
			int size = 2;
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
			
			dto.setMapNum(Long.parseLong(req.getParameter("mapNum")));
			dto.setSubject(req.getParameter("subject"));
			dto.setContent(req.getParameter("content"));
			dto.setAddr(req.getParameter("addr"));
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

			dao.deleteMap(num);

		} catch (Exception e) {
			e.printStackTrace();
		}

		resp.sendRedirect(cp + "/map/list.do?" + query);

	}
	protected void insertReply(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 댓글 추가
	}
	protected void deleteReply(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 댓글 삭제
	}
	protected void insertLikeBoard(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 게시글 좋아요
	}
	

	
}
