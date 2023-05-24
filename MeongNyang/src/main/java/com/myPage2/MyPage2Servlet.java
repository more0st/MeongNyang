package com.myPage2;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.member.SessionInfo;
import com.util.MyServlet;
import com.util.MyUtil;

@MultipartConfig
@WebServlet("/myPage2/*")
public class MyPage2Servlet extends MyServlet{
	private static final long serialVersionUID = 1L;


	@Override
	protected void execute(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");

		String uri = req.getRequestURI();

		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");

		if (info == null) {
			forward(req, resp, "/WEB-INF/views/member/login.jsp");
			return;
		}


		if (uri.indexOf("salesList.do") != -1) {	
			list(req, resp);
		} else if(uri.indexOf("salesArticle.do") != -1) {
			article(req, resp);
		}
		
	}

	private void list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		MyPage2DAO dao = new MyPage2DAO();
		MyUtil util = new MyUtil();
		
		String cp = req.getContextPath();
		
		try {
			
			HttpSession session = req.getSession();
			SessionInfo info = (SessionInfo) session.getAttribute("member");

			// 페이지번호
			String page = req.getParameter("page");
			int current_page = 1;
			if(page != null) {
				current_page = Integer.parseInt(page);
			}
			
			// 검색
			String condition = req.getParameter("condition");
			String keyword = req.getParameter("keyword");
			if(condition == null) {	// 검색상태가 아니면
				condition = "all";
				keyword = "";
			}
			
			// GET 방식이면 디코딩
			if(req.getMethod().equalsIgnoreCase("GET")) {
				keyword = URLDecoder.decode(keyword, "utf-8");
			}
			
			// 전체 데이터 개수
			int dataCount;
			if(keyword.length() == 0) {	// 검색이 아닐때
				dataCount = dao.dataCount(info.getUserId());
			} else {
				dataCount = dao.dataCount(info.getUserId(), condition, keyword);
			}
			
			// 전체 페이지수
			int size = 9;
			int total_page = util.pageCount(dataCount, size);
			if(current_page > total_page) {
				current_page = total_page;
			}
			
			// 게시글 가져오기
			int offset = (current_page - 1) * size;
			if(offset < 0) offset = 0;
			
			List<myPage2DTO> list = null;
			
			if(keyword.length() == 0) {
				list = dao.listBoard(info.getUserId(), offset, size);		// 게시물 리스트
			} 
			  else {
				list = dao.listBoard(info.getUserId(), offset, size, condition, keyword);		// 검색에서 게시물 리스트
			} 
			
			// 페이징 처리
			String query = "";
			if(keyword.length() != 0) {
				query = "condition=" + condition + "&keyword=" + URLEncoder.encode(keyword, "utf-8");
			}
			
			String listUrl = cp + "/myPage2/salesList.do";
			String articleUrl = cp + "/myPage2/salesArticle.do?";
			if(query.length() != 0) {
				listUrl += "?" + query;
				articleUrl += "&" + query;
			}
			
			String paging = util.paging(current_page, total_page, listUrl);

			req.setAttribute("list", list);
			req.setAttribute("page", current_page);
			req.setAttribute("dataCount", dataCount);
			req.setAttribute("size", size);
			req.setAttribute("total_page", total_page);
			req.setAttribute("articleUrl", articleUrl);
			req.setAttribute("paging", paging);
			req.setAttribute("condition", condition);
			req.setAttribute("keyword", keyword);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		forward(req, resp, "/WEB-INF/views/myPage2/salesList.jsp");
	}
	
	private void article(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		MyPage2DAO dao = new MyPage2DAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");

		String cp = req.getContextPath();
		String page = req.getParameter("page");
		
		try {
			long marketnum = Long.parseLong(req.getParameter("marketnum"));		

			myPage2DTO dto = dao.readBoard(info.getUserId(), marketnum);
			if (dto == null) {
				resp.sendRedirect(cp + "/myPage2/salesList.do?page=" + page);
				return;
			}	
			
			dto.setContent(dto.getContent().replaceAll("\n", "<br>"));

			List<myPage2DTO> listFile = dao.listPhotoFile(marketnum);

			dao.updateHitCount(marketnum);

			myPage2DTO preReadDto = dao.preReadBoard(marketnum, info.getUserId());
			myPage2DTO nextReadDto = dao.nextReadBoard(marketnum, info.getUserId());

			req.setAttribute("dto", dto);
			req.setAttribute("page", page);
			req.setAttribute("listFile", listFile);
			req.setAttribute("preReadDto", preReadDto);
			req.setAttribute("nextReadDto", nextReadDto);
			
			forward(req, resp, "/WEB-INF/views/myPage2/salesArticle.jsp");
			return;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		forward(req, resp, "/WEB-INF/views/myPage2/salesArticle.jsp");
	}

}
