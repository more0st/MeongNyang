package com.qna;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.member.SessionInfo;
import com.util.MyUploadServlet;

@MultipartConfig
@WebServlet("/qna/*")
public class QnaServlet extends MyUploadServlet{
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
		
		
		// uri에 따른 작업 구분
		if(uri.indexOf("list.do") != -1) {
			list(req, resp);
		} else if(uri.indexOf("write.do") != -1) {
			writeForm(req,resp);
		} else if(uri.indexOf("write_ok.do") != -1) {
			writeSubmit(req,resp);
		} else if(uri.indexOf("article.do") != -1) {
			article(req,resp);
		} else if(uri.indexOf("delete.do") != -1) {
			delete(req, resp);
		} else if(uri.indexOf("insertReply.do") != -1) {
			insertReply(req, resp);
		}
	}
	
	protected void list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 게시글 리스트
		forward(req, resp, "/WEB-INF/views/qna/list.jsp");
	}
	protected void writeForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 글쓰기 폼
		
		forward(req, resp, "/WEB-INF/views/map/write.jsp");
	}

	protected void writeSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 글 저장
		
	}
	protected void wirteReplyForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 답글쓰기
	}
	protected void writeReplySubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 답글 저장
	}
	protected void article(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
	}
	protected void delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
	}

	protected void insertReply(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
	}


}

	