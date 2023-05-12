package com.myPage;

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
@WebServlet("/myPage/*")
public class MyPageServlet extends MyServlet{
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
		if (uri.indexOf("buyList.do") != -1) {		// 나의 구매내역 리스트
			list(req, resp);
		} else if (uri.indexOf("buyArticle.do") != -1) {	// 클릭하면 글로 이동?
			article(req, resp);
		} 
	}

	private void list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 게시물 리스트
		MyPageDAO dao = new MyPageDAO();	////////////////////////////////// 이름 수정
		MyUtil util = new MyUtil();

		String cp = req.getContextPath();
		
		try {
			String gubun = req.getParameter("category");
			int category = 1;
			if(gubun != null) {
				category = Integer.parseInt(gubun);
			}
			
			String page = req.getParameter("page");
			int current_page = 1;
			if (page != null)
				current_page = Integer.parseInt(page);

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
				dataCount = dao.dataCount(category);
			} else {
				dataCount = dao.dataCount(category, condition, keyword);
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
			
			List<MyPageDTO> list = null;
			if (keyword.length() == 0) {
				list = dao.listBoard(category, offset, size);
			} else {
				list = dao.listBoard(category, offset, size, condition, keyword);
			}

			String query = "";
			if (keyword.length() != 0) {
				query = "condition=" + condition + "&keyword=" + URLEncoder.encode(keyword, "utf-8");
			}

			// 페이징 처리
			String listUrl = cp + "/myPage/buyList.do?category=" + category;
			String articleUrl = cp + "/myPage/buyArticle.do?category=" + category + "&page=" + current_page;
			if (query.length() != 0) {
				listUrl += "&" + query;
				articleUrl += "&" + query;
			}

			String paging = util.paging(current_page, total_page, listUrl);

			// 포워딩할 JSP로 넘길 속성
			req.setAttribute("list", list);
			req.setAttribute("category", category);
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

		// JSP로 포워딩
		forward(req, resp, "/WEB-INF/views/myPage/buyList.jsp");
	}


	private void article(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 글 보기
		MyPageDAO dao = new MyPageDAO();
		MyUtil util = new MyUtil();
		
		String cp = req.getContextPath();
		
		String category = req.getParameter("category");
		String page = req.getParameter("page");
		String query = "category=" + category + "&page=" + page;

		try {
			long marketnum = Long.parseLong(req.getParameter("marketnum"));
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
			dao.updateHitCount(marketnum);

			// 게시물 가져오기
			MyPageDTO dto = dao.readBoard(marketnum);
			if (dto == null) { // 게시물이 없으면 다시 리스트로
				resp.sendRedirect(cp + "/myPage/buyList.do?" + query);
				return;
			}
			dto.setContent(util.htmlSymbols(dto.getContent()));

			// 이전글 다음글
			int nCategory = Integer.parseInt(category);
			MyPageDTO preReadDto = dao.preReadBoard(nCategory, dto.getMarketnum(), condition, keyword);
			MyPageDTO nextReadDto = dao.nextReadBoard(nCategory, dto.getMarketnum(), condition, keyword);

			// JSP로 전달할 속성
			req.setAttribute("category", category);
			req.setAttribute("dto", dto);
			req.setAttribute("page", page);
			req.setAttribute("query", query);
			req.setAttribute("preReadDto", preReadDto);
			req.setAttribute("nextReadDto", nextReadDto);

			// 포워딩
			forward(req, resp, "/WEB-INF/views/myPage/buyArticle.jsp");
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}

		resp.sendRedirect(cp + "/myPage/buyList.do?" + query);
	}

	
}


