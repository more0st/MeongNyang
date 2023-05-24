package com.myPage;

import java.io.File;
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

	private String pathname;

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

		String root = session.getServletContext().getRealPath("/");
		pathname = root + "uploads" + File.separator + "market";

		// uri에 따른 작업 구분
		if (uri.indexOf("buyList.do") != -1) {		// 나의 구매내역 리스트
			list(req, resp);
		} else if(uri.indexOf("buyArticle.do") != -1) {
			article(req, resp);
		}
		
	}


	private void list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 게시물 리스트
		MyPageDAO dao = new MyPageDAO();	
		MyUtil util = new MyUtil();
		
		String cp = req.getContextPath();
		
		try {
			// 파라미터 : [페이지번호], [검색컬럼,검색값]          (페이지번호가 올수도 있고 오지않을수도잇음)
		
			
			// 로그인한 아이디로만 글 뜨게
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
			
			List<MyPageDTO> list = null;
			
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
			
			String listUrl = cp + "/myPage/buyList.do";
			String articleUrl = cp + "/myPage/buyArticle.do?";
			if(query.length() != 0) {
				listUrl += "?" + query;
				articleUrl += "&" + query;
			}

			String paging = util.paging(current_page, total_page, listUrl);
	
			
			
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
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		// 포워딩에서/ 는 cp까지를 의미한다.
		forward(req,resp,"/WEB-INF/views/myPage/buyList.jsp");
	}



	private void article(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 글 보기
		MyPageDAO dao = new MyPageDAO();

		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");

		String cp = req.getContextPath();
		String page = req.getParameter("page");
		
		try {
			long marketnum = Long.parseLong(req.getParameter("marketnum"));		

			MyPageDTO dto = dao.readBoard(info.getUserId(), marketnum);
			if (dto == null) {
				resp.sendRedirect(cp + "/myPage/buyList.do?page=" + page);
				return;
			}
			
			// 글내용 엔터를 <br>로
			dto.setContent(dto.getContent().replaceAll("\n", "<br>"));

			// 사진
			List<MyPageDTO> listFile = dao.listPhotoFile(marketnum);

			/// 조회수 증가
			dao.updateHitCount(marketnum);
			
			// 이전글 다음글
			MyPageDTO preReadDto = dao.preReadBoard(marketnum, info.getUserId());
			MyPageDTO nextReadDto = dao.nextReadBoard(marketnum, info.getUserId());

			// 포워딩할 JSP에 넘겨줄 속성
			req.setAttribute("dto", dto);
			req.setAttribute("page", page);
			req.setAttribute("listFile", listFile);
			req.setAttribute("preReadDto", preReadDto);
			req.setAttribute("nextReadDto", nextReadDto);
			
			// 포워딩
			forward(req,resp,"/WEB-INF/views/myPage/buyArticle.jsp");
			return;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		forward(req, resp, "/WEB-INF/views/myPage/buyArticle.jsp");
	}


	
}


