package com.myPage3;

import java.io.IOException;
import java.net.URLDecoder;
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
@WebServlet("/myPage3/*")
public class MyPage3Servlet extends MyServlet{
	private static final long serialVersionUID = 1L;

	@Override
	protected void execute(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");

		String uri = req.getRequestURI();

		// 세션 정보
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");

		if (info == null) {
			forward(req, resp, "/WEB-INF/views/member/login.jsp");
			return;
		}
		
		// uri에 따른 작업 구분
		if (uri.indexOf("writingList.do") != -1) {		// 나의 구매내역 리스트
			list(req, resp);
		}
		
	}

	
	private void list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		MyPage3DAO dao = new MyPage3DAO();
		MyUtil util = new MyUtil();
		
		String cp = req.getContextPath();
		
		try {

			HttpSession session = req.getSession();
			SessionInfo info = (SessionInfo) session.getAttribute("member");

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
			
			
			
			// 카테코리 
			String category = req.getParameter("category");
			
			
			// 전체 데이터 개수
			int dataCount = 0;
			if(category==null && keyword.length() == 0) { 	// 카테고리검색이 선택되지 않았을때
				category = "map";
				dataCount = dao.writeCount(info.getUserId(), category);
			} else if(category == null && keyword.length() != 0) {
				category = "map";
				dataCount = dao.writeCount(info.getUserId(), condition, keyword, category);
			} else if(category != null && keyword.length() == 0) {
				dataCount = dao.writeCount(info.getUserId(), category);
			} else if(category != null && keyword.length() != 0) {
				dataCount = dao.writeCount(info.getUserId(), condition, keyword, category);
			}
			
			// 전체 페이지수
			int size = 10;
			int total_page = util.pageCount(dataCount, size);
			if(current_page > total_page) {
				current_page = total_page;
			}
			
			
					
			// 카테고리 검색 게시글 가져오기
			int offset = (current_page - 1) * size;
			if(offset <0) offset = 0;
			
			List<MyPage3DTO> list = null;
			
			if(category==null && keyword.length() == 0) {
				category = "map";
				list = dao.categoryListBoard(info.getUserId(), offset, size, category);
			} else if(category == null && keyword.length() != 0) {
				category = "map";
				list = dao.categoryListBoard(info.getUserId(), offset, size, condition, keyword, category);
			} else if (category != null && keyword.length() == 0) {
				list = dao.categoryListBoard(info.getUserId(), offset, size, category);
			} else if (category != null && keyword.length() != 0) {
				list = dao.categoryListBoard(info.getUserId(), offset, size, condition, keyword, category);
			}
			
			
			
			
			
			// 카테고리 페이징 처리
			String query = "";
			if(category != null ) {
				query = "category=" + category;
			}
			
			String listUrl = cp + "/myPage3/writingList.do";
			String articleUrl = ""; // 클릭해서 다른조원이 만든 글로 이동하게 수정하기
			
			if(category== null || category.equals("map")) {
				articleUrl = cp + "/map/article.do?";
			} else if(category.equals("gallery")) {
				articleUrl = cp + "/gallery/article.do?";
			} else if(category.equals("club")) {
				articleUrl = cp + "/club/article.do?";
			}


			if(query.length() != 0) {
				listUrl += "?" + query;
				articleUrl += "&" + query;
			}
			
			
			String paging = util.paging(current_page, total_page, listUrl);

			
			/*
			// 카테고리 페이징 처리
			String query = "categroy=" + category;
			
			String listUrl = cp + "/myPage3/writingList.do";
			String articleUrl = cp + "/market/article.do?";		// market 수정
			if(query.length() != 0) {
				listUrl += "?" + query;
				articleUrl += "&" + query;
			}
			
			String paging = util.paging(current_page, total_page, listUrl);
			*/
			
			
			
			// 포워딩할 JSP에 전달할 속성(attribute)
			req.setAttribute("list", list);
			req.setAttribute("page", current_page);
			req.setAttribute("dataCount", dataCount);
			req.setAttribute("size", size);
			req.setAttribute("total_page", total_page);
			req.setAttribute("articleUrl", articleUrl);
			req.setAttribute("paging", paging);
			req.setAttribute("condition", condition);
			req.setAttribute("keyword", keyword);
			req.setAttribute("userId", info.getUserId());
			req.setAttribute("category", category);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	
		forward(req, resp, "/WEB-INF/views/myPage3/writingList.jsp");

	}
	
	
	
	

}
