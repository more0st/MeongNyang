package com.notice;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
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
@WebServlet("/notice/*")
public class NoticeServlet extends MyUploadServlet {
	private static final long serialVersionUID = 1L;

	private String pathname;

	@Override
	protected void execute(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");

		String uri = req.getRequestURI();
		String cp = req.getContextPath();

		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");

		if (uri.indexOf("list.do") == -1 && info == null) {
			resp.sendRedirect(cp + "/member/login.do");
			return;
		}


		// uri에 따른 작업 구분
		if (uri.indexOf("list.do") != -1) {
			list(req, resp);
		} else if (uri.indexOf("write.do") != -1) {
			writeForm(req, resp);
		} else if (uri.indexOf("write_ok.do") != -1) {
			writeSubmit(req, resp);
		} else if (uri.indexOf("article.do") != -1) {
			article(req, resp);
		} else if (uri.indexOf("update.do") != -1) {
			updateForm(req, resp);
		} else if (uri.indexOf("update_ok.do") != -1) {
			updateSubmit(req, resp);
		} else if (uri.indexOf("delete.do") != -1) {
			delete(req, resp);
		} else if (uri.indexOf("deleteList.do") != -1) {
			deleteList(req, resp);
		}
	}

	protected void list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 게시물 리스트
		NoticeDAO dao = new NoticeDAO();
		MyUtil util = new MyUtil();
		
		String cp = req.getContextPath();

		try {
			String page = req.getParameter("page");
			int current_page = 1;
			if (page != null) {
				current_page = Integer.parseInt(page);
			}

			String condition = req.getParameter("condition");
			String keyword = req.getParameter("keyword");
			if (condition == null) {
				condition = "all";
				keyword = "";
			}
			if (req.getMethod().equalsIgnoreCase("GET")) {
				keyword = URLDecoder.decode(keyword, "utf-8");
			}

			// 한페이지 표시할 데이터 개수
			String pageSize = req.getParameter("size");
			int size = pageSize == null ? 10 : Integer.parseInt(pageSize);

			int dataCount, total_page;

			if (keyword.length() != 0) {
				dataCount = dao.dataCount(condition, keyword);
			} else {
				dataCount = dao.dataCount();
			}
			total_page = util.pageCount(dataCount, size);

			if (current_page > total_page) {
				current_page = total_page;
			}

			int offset = (current_page - 1) * size;
			if(offset < 0) offset = 0;
			
			List<NoticeDTO> list;
			if (keyword.length() != 0) {
				list = dao.listNotice(offset, size, condition, keyword);
			} else {
				list = dao.listNotice(offset, size);
			}

			//시간의 차이를 구해 new 게시물 표시처리
			long gap;
			Date curDate = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			for (NoticeDTO dto : list) {
				Date date = sdf.parse(dto.getReg_date());
				// gap = (curDate.getTime() - date.getTime()) / (1000*60*60*24); // 일자
				gap = (curDate.getTime() - date.getTime()) / (1000 * 60 * 60); // 시간
				dto.setGap(gap);

				dto.setReg_date(dto.getReg_date().substring(0, 10));
			}

			String query = "";
			String listUrl;
			String articleUrl;

			listUrl = cp + "/notice/list.do?size=" + size;
			articleUrl = cp + "/notice/article.do?page=" + current_page + "&size=" + size;
			if (keyword.length() != 0) {
				query = "condition=" + condition + "&keyword=" + URLEncoder.encode(keyword, "utf-8");

				listUrl += "&" + query;
				articleUrl += "&" + query;
			}

			String paging = util.paging(current_page, total_page, listUrl);

			// 포워딩 jsp에 전달할 데이터
			req.setAttribute("list", list);
			req.setAttribute("articleUrl", articleUrl);
			req.setAttribute("dataCount", dataCount);
			req.setAttribute("size", size);
			req.setAttribute("page", current_page);
			req.setAttribute("total_page", total_page);
			req.setAttribute("paging", paging);
			req.setAttribute("condition", condition);
			req.setAttribute("keyword", keyword);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		// JSP로 포워딩
		forward(req, resp, "/WEB-INF/views/notice/list.jsp");
	}

	protected void writeForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 글쓰기 폼
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");
		
		String cp = req.getContextPath();
		
		String size = req.getParameter("size");

		// admin만 글을 등록
		if (!info.getUserId().equals("admin")) {
			resp.sendRedirect(cp + "/notice/list.do?size=" + size);
			return;
		}

		req.setAttribute("mode", "write");
		req.setAttribute("size", size);
		
		forward(req, resp, "/WEB-INF/views/notice/write.jsp");
	}

	protected void writeSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 글 저장
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");

		String cp = req.getContextPath();
		
		if (req.getMethod().equalsIgnoreCase("GET")) {
			resp.sendRedirect(cp + "/notice/list.do");
			return;
		}
		
		// admin만 글을 등록
		if (!info.getUserId().equals("admin")) {
			resp.sendRedirect(cp + "/notice/list.do");
			return;
		}
		
		NoticeDAO dao = new NoticeDAO();
		
		String size = req.getParameter("size");
		try {
			NoticeDTO dto = new NoticeDTO();
			
			dto.setUserId(info.getUserId());
			dto.setSubject(req.getParameter("subject"));
			dto.setContent(req.getParameter("content"));

			dao.insertNotice(dto);
		} catch (Exception e) {
			e.printStackTrace();
		}

		//resp.sendRedirect(cp + "/notice/list.do?size=" + size);
		resp.sendRedirect(cp + "/notice/list.do");
		}

	protected void article(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 글보기
		
		String cp=req.getContextPath();
		
		String page=req.getParameter("page");
		String size=req.getParameter("size");
		String query = "page=" + page + "&size=" + size;
		
		NoticeDAO dao=new NoticeDAO();
		
		try {
			long noticeNum=Long.parseLong(req.getParameter("noticeNum"));
			
			String condition=req.getParameter("condition");
			String keyword=req.getParameter("keyword");
			if(condition==null) {
				condition="all";
				keyword="";
			}
			keyword=URLDecoder.decode(keyword,"utf-8");
			
			if(keyword.length()!=0) {
				query+="&condition="+condition+"&keyword="+URLEncoder.encode(keyword,"utf-8");
			}
			
			dao.updateHitCount(noticeNum);
			
			NoticeDTO dto=dao.readNotice(noticeNum);
			if(dto==null) {
				resp.sendRedirect(cp+"/notice/list.do?"+query);
				return;
			}
			
			dto.setContent(dto.getContent().replaceAll("\n", "<br>"));
			
			NoticeDTO preReadDTO=dao.preReadNotice(dto.getNoticeNum(), condition, keyword);
			NoticeDTO nextReadDTO=dao.nextReadNotice(dto.getNoticeNum(), condition, keyword);
			
			req.setAttribute("dto", dto);
			req.setAttribute("preReadDTO", preReadDTO);
			req.setAttribute("nextReadDTO", nextReadDTO);
			req.setAttribute("query", query);
			req.setAttribute("page", page);
			req.setAttribute("size", size);
			
			forward(req, resp, "/WEB-INF/views/notice/article.jsp");
			return;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		resp.sendRedirect(cp+"/notice/list.do?"+query);
		
	}

	protected void updateForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 수정 폼
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");

		String cp = req.getContextPath();

		if (!info.getUserId().equals("admin")) {
			resp.sendRedirect(cp + "/notice/list.do");
			return;
		}
		
		NoticeDAO dao = new NoticeDAO();

		String page = req.getParameter("page");
		String size = req.getParameter("size");

		try {
			long noticeNum = Long.parseLong(req.getParameter("noticeNum"));

			NoticeDTO dto = dao.readNotice(noticeNum);
			if (dto == null) {
				resp.sendRedirect(cp + "/notice/list.do?page=" + page + "&size=" + size);
				return;
			}

			// 파일

			req.setAttribute("dto", dto);
			req.setAttribute("page", page);
			req.setAttribute("size", size);

			req.setAttribute("mode", "update");

			forward(req, resp, "/WEB-INF/views/notice/write.jsp");
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}

		resp.sendRedirect(cp + "/notice/list.do?page=" + page + "&size=" + size);

	}

	protected void updateSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 수정 완료
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");
		
		String cp = req.getContextPath();

		if (req.getMethod().equalsIgnoreCase("GET")) {
			resp.sendRedirect(cp + "/notice/list.do");
			return;
		}
		
		if (!info.getUserId().equals("admin")) {
			resp.sendRedirect(cp + "/notice/list.do");
			return;
		}

		NoticeDAO dao = new NoticeDAO();
		
		String page = req.getParameter("page");
		String size = req.getParameter("size");

		try {
			NoticeDTO dto = new NoticeDTO();
			
			dto.setNoticeNum(Long.parseLong(req.getParameter("noticeNum")));
			dto.setSubject(req.getParameter("subject"));
			dto.setContent(req.getParameter("content"));

			dao.updateNotice(dto);

		} catch (Exception e) {
			e.printStackTrace();
		}

		//resp.sendRedirect(cp + "/notice/list.do?page=" + page + "&size=" + size);
		resp.sendRedirect(cp + "/notice/list.do?page=" + page);

	}


	protected void delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 삭제
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");

		String cp = req.getContextPath();
		
		if (!info.getUserId().equals("admin")) {
			resp.sendRedirect(cp + "/notice/list.do");
			return;
		}
		
		NoticeDAO dao = new NoticeDAO();

		String page = req.getParameter("page");
		String size = req.getParameter("size");
		String query = "page=" + page + "&size=" + size;

		try {
			long noticeNum = Long.parseLong(req.getParameter("noticeNum"));
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

			NoticeDTO dto = dao.readNotice(noticeNum);
			if (dto == null) {
				resp.sendRedirect(cp + "/notice/list.do?" + query);
				return;
			}

			// 게시글 삭제
			dao.deleteNotice(noticeNum);

		} catch (Exception e) {
			e.printStackTrace();
		}

		resp.sendRedirect(cp + "/notice/list.do?" + query);
		
		
	}

	protected void deleteList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	}

}
