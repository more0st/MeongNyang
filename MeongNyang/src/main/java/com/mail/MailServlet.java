package com.mail;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.member.SessionInfo;
import com.util.MyServlet;

@WebServlet("/mail/*")
public class MailServlet  extends MyServlet{


	private static final long serialVersionUID = 1L;

	@Override
	protected void execute(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");
		
		String uri=req.getRequestURI();
		String cp=req.getContextPath();
		
		HttpSession session=req.getSession();
		SessionInfo info=(SessionInfo)session.getAttribute("member");
		
		if(info==null) {
			resp.sendRedirect(cp+"/member/login.do");
			return;
		}
		
		if(uri.indexOf("send.do") !=-1) {
			sendForm(req, resp);
		} else if(uri.indexOf("send_ok.do")!=-1) {
			sendSubmit(req, resp);
		} else if(uri.indexOf("complete.do")!=-1) {
			complete(req, resp);
		}
				
	}
	
	
	protected void sendForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		forward(req, resp, "/WEB-INF/views/mail/write.jsp");
	}
	
	protected void sendSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String cp=req.getContextPath();
		
		if(req.getMethod().equalsIgnoreCase("GET")) {
			resp.sendRedirect(cp+"/");
			return;
		}
		
		HttpSession session=req.getSession();
		SessionInfo info=(SessionInfo)session.getAttribute("member");
		
		String url=cp+"/mail/complete.do";
		
		try {
			Mail dto=new Mail();
			dto.setSenderName(info.getUserName());
			dto.setSenderEmail(req.getParameter("senderEmail"));
			dto.setReceiverEmail(req.getParameter("receiverEmail"));
			dto.setSubject(req.getParameter("subject"));
			dto.setContent(req.getParameter("content"));
			
			session.setAttribute("receiver", dto.getReceiverEmail());
			//세션에 receiver의 메일을 저장해둠
			
			MailSender sender=new MailSender();
			
			boolean b=sender.mailSend(dto);
			if(!b) {
				url+="?fail";
			}
		} catch (Exception e) {
			//e.printStackTrace();
			url+="?fail";
		}
		resp.sendRedirect(url);
		
	}
	
	protected void complete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session=req.getSession();
		
		String fail=req.getParameter("fail");
		
		String receiver=(String)session.getAttribute("receiver");
		session.removeAttribute("receiver");
		//값을 가져온 뒤 session에 값을 지워버림
		
		String msg="<span style='color:blue;'>"+receiver+"</span> 님에게 <br>";
		if(fail == null) {
			msg+="메일을 성공적으로 전송했습니다.";
		} else {
			msg+="메일 전송이 실패 했습니다.";
		}
		
		req.setAttribute("message", msg);
		
		forward(req, resp, "/WEB-INF/views/mail/complete.jsp");
	}

}
