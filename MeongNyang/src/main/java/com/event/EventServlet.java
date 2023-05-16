package com.event;

import java.io.File;
import java.io.IOException;

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
@WebServlet("/event/*")
public class EventServlet extends MyUploadServlet{

	private static final long serialVersionUID = 1L;
	
	private String pathname;

	@Override
	protected void execute(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.getParameter("utf-8");
		
		String uri=req.getRequestURI();
		String cp=req.getContextPath();
		
		HttpSession session=req.getSession();
		SessionInfo info=(SessionInfo)session.getAttribute("member");
		
		if(uri.indexOf("list.do")==-1 && info ==null) {
			resp.sendRedirect(cp+"/member/login.do");
			return;
		}
		
		String root=session.getServletContext().getRealPath("/");
		pathname=root+"uploads"+File.separator+"event";
		
		if(uri.indexOf("list.do")!=-1) {
			list(req, resp);
		} else if(uri.indexOf("write.do")!=-1) {
			writeForm(req, resp);
		} else if(uri.indexOf("write_ok.do")!=-1) {
			writeSubmit(req, resp);
		} else if(uri.indexOf("article.do")!=-1) {
			article(req, resp);
		} else if(uri.indexOf("update.do")!=-1) {
			updateForm(req, resp);
		} else if(uri.indexOf("update_ok.do")!=-1) {
			updateSubmit(req, resp);
		} else if(uri.indexOf("deleteFile.do")!=-1) {
			deleteFile(req, resp);
		} else if(uri.indexOf("delete.do")!=-1) {
			delete(req, resp);
		} else if(uri.indexOf("participant.do")!=-1) {
			eventParticipant(req, resp);
		} else if(uri.indexOf("disable.do")!=-1) {
			eventDisable(req, resp);
		} else if(uri.indexOf("pass.do")!=-1) {
			eventPass(req, resp);
		}
		
	}
	
	protected void list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//이벤트 리스트
		EventDAO dao=new EventDAO();
		MyUtil util=new MyUtil();
		
		String cp=req.getContextPath();
		
		try {
			String page=req.getParameter("page");
			int current_page=1;
			if(page!=null) {
				current_page=Integer.parseInt(page);
			}
			
			String pageSize=req.getParameter("size");
			int size=pageSize==null? 10 : Integer.parseInt(pageSize);
			
			int dataCount, total_page;
			//컨디션에 진행/종료/전체 설정해야할듯
			int enabled=Integer.parseInt(req.getParameter("enabled"));
			if(enabled==1) {//진행중인 이벤트
				dataCount=dao.dataCount();
			}
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	protected void writeForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//이벤트 등록 폼
	}
	protected void writeSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//이벤트 저장
	}
	protected void article(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//이벤트 글보기
	}
	protected void updateForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//이벤트 수정
	}
	protected void updateSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//이벤트 수정 완료
	}
	protected void deleteFile(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//수정에서 사진만 삭제
	}
	protected void delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//이벤트 삭제
	}
	protected void eventParticipant(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//이벤트 참여
	}
	protected void eventDisable(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//이벤트 활성화/비활성화
	}
	protected void eventPass(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//이벤트 추첨
	}
	

}
