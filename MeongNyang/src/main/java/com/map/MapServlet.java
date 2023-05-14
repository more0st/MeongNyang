package com.map;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.member.SessionInfo;
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
		pathname = root + "uploads" + File.separator + "sphoto";
		
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
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");

		String cp = req.getContextPath();
		
		try {
			String page = req.getParameter("page");
			int current_page = 1;
			if (page != null) {
				current_page = Integer.parseInt(page);
			}

			// 전체데이터 개수
			int dataCount = dao.dataCount();

			// 전체페이지수
			int size = 2;
			int total_page = util.pageCount(dataCount, size);
			if (current_page > total_page) {
				current_page = total_page;
			}

			// 게시물 가져오기
			int offset = (current_page - 1) * size;
			if(offset < 0) offset = 0;
			
			List<MapDTO> list = dao.listMap(offset, size, info.getUserId());

			// 페이징 처리
			String listUrl = cp + "/map/list.do";
			String articleUrl = cp + "/map/article.do?page=" + current_page;
			String paging = util.paging(current_page, total_page, listUrl);

			// 포워딩할 list.jsp에 넘길 값
			req.setAttribute("list", list);
			req.setAttribute("dataCount", dataCount);
			req.setAttribute("articleUrl", articleUrl);
			req.setAttribute("page", current_page);
			req.setAttribute("total_page", total_page);
			req.setAttribute("paging", paging);
			
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
		// 글 보기
		// 게시물 보기
		MapDAO dao = new MapDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");

		String cp = req.getContextPath();
		String page = req.getParameter("page");

		try {
			long num = Long.parseLong(req.getParameter("num"));

			MapDTO dto = dao.readMap(num);
			if (dto == null || !dto.getUserId().equals(info.getUserId())) {
				resp.sendRedirect(cp + "/sphoto/list.do?page=" + page);
				return;
			}
			dto.setContent(dto.getContent().replaceAll("\n", "<br>"));
			
			//
			// MapDTO preReadDto = dao.preReadPhoto(num, info.getUserId());
			// MapDTO nextReadDto = dao.nextReadPhoto(num, info.getUserId());

			// List<MapDTO> listFile = dao.listPhotoFile(num);
			// List<MapDTO> listFile = dao.li

			req.setAttribute("dto", dto);
			// req.setAttribute("preReadDto", preReadDto);
			// req.setAttribute("nextReadDto", nextReadDto);
			// req.setAttribute("listFile", listFile);
			req.setAttribute("page", page);

			forward(req, resp, "/WEB-INF/views/map/article.jsp");
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		forward(req, resp, "/WEB-INF/views/map/article.jsp");
	}
	protected void updateForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 글 수정
	}
	protected void updateSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 글 수정 완료
	}
	protected void deleteFile(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 수정에서 파일 삭제
	}
	protected void delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 게시글 삭제
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
