package com.member;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.util.MyServlet;

@WebServlet("/member/*")
public class MemberServlet extends MyServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void execute(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");
		
		String uri = req.getRequestURI();
		
		// uri에 따른 작업 구분
		if(uri.indexOf("login.do")!=-1) {
			loginForm(req, resp);
		} else if(uri.indexOf("login_ok.do")!=-1) {
			loginSubmit(req, resp);
		} else if(uri.indexOf("logout.do")!=-1) {
			logout(req, resp);
		} else if(uri.indexOf("member.do")!=-1) {
			memberForm(req, resp);
		} else if(uri.indexOf("member_ok.do")!=-1) {
			memberSubmit(req, resp);
		} else if(uri.indexOf("pwd.do")!=-1) {
			pwdForm(req, resp);
		} else if(uri.indexOf("pwd_ok.do")!=-1) {
			pwdSubmit(req, resp);
		} else if(uri.indexOf("update_ok.do")!=-1) {
			updateSubmit(req, resp);
		} else if(uri.indexOf("userIdCheck.do")!=-1) {
			userIdCheck(req, resp);
		}
	}

	protected void loginForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 로그인 폼
		String path = "/WEB-INF/views/member/login.jsp";
		forward(req, resp, path);
	}

	protected void loginSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 로그인 처리
		// 세션객체. 세션 정보는 서버에 저장(로그인 정보, 권한등을 저장)
		HttpSession session = req.getSession();
		
		MemberDAO dao=new MemberDAO();
		String cp = req.getContextPath();

		if(req.getMethod().equalsIgnoreCase("GET")) {
			resp.sendRedirect(cp + "/");
			return;
		}
		
		String userId = req.getParameter("userId");
		String userPwd = req.getParameter("userPwd");
		
		MemberDTO dto = dao.loginMember(userId, userPwd);
		if(dto != null) {
			// 로그인 성공 : 로그인정보를 서버에 저장
			// 세션의 유지시간을 20분설정(기본 30분)
			session.setMaxInactiveInterval(20*60);
			
			// 세션에 저장할 내용
			SessionInfo info = new SessionInfo();
			info.setUserId(dto.getUserId());
			info.setUserName(dto.getUserName());
			
			// 세션에 member이라는 이름으로 저장
			session.setAttribute("member", info);
			
			// 메인화면으로 리다이렉트
			resp.sendRedirect(cp+"/");
			return;
		}
		
		// 로그인 실패인 경우(다시 로그인 폼으로)
		String msg = "아이디 또는 패스워드가 일치하지 않습니다.";
		req.setAttribute("message", msg);
		
		forward(req, resp, "/WEB-INF/views/member/login.jsp");
	}
	
	protected void logout(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 로그아웃
		HttpSession session = req.getSession();
		String cp = req.getContextPath();

		// 세션에 저장된 정보를 지운다.
		session.removeAttribute("member");
		
		// 세션에 저장된 모든 정보를 지우고 세션을 초기화 한다.
		session.invalidate();
		
		// 루트로 리다이렉트
		resp.sendRedirect(cp+"/");
	}
	
	protected void memberForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//회원가입 폼 불러오기
		req.setAttribute("title", "회원가입");
		req.setAttribute("mode", "member");
		
		forward(req, resp, "/WEB-INF/views/member/member.jsp");
	}

	protected void memberSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//회원가입 처리
		MemberDAO dao=new MemberDAO();
		
		String cp=req.getContextPath();
		if(req.getMethod().equalsIgnoreCase("GET")) {
			resp.sendRedirect(cp+"/");
			return;
		}
		
		String message="";
		
		try {
			MemberDTO dto=new MemberDTO();
			dto.setUserId(req.getParameter("userId"));
			dto.setUserPwd(req.getParameter("userPwd"));
			dto.setUserName(req.getParameter("userName"));
			dto.setBirth(req.getParameter("birth"));
		
			String tel1=req.getParameter("tel1");
			String tel2=req.getParameter("tel2");
			String tel3=req.getParameter("tel3");
			dto.setTel(tel1+"-"+tel2+"-"+tel3);
			
			dto.setPostNum(req.getParameter("postNum"));
			
			String addr1=req.getParameter("addr1");
			String addr2=req.getParameter("addr2");
			dto.setAddr(addr1+" "+addr2);
			
			String email1=req.getParameter("email1");
			String email2=req.getParameter("email2");
			dto.setEmail(email1+"@"+email2);
			
			dao.insertMember(dto);
			resp.sendRedirect(cp+"/");
			return;
			
		} catch (SQLException e) {
			if (e.getErrorCode() == 1)
				message = "아이디 중복으로 회원 가입이 실패 했습니다.";
			else if (e.getErrorCode() == 1400)
				message = "필수 사항을 입력하지 않았습니다.";
			else if (e.getErrorCode() == 1840 || e.getErrorCode() == 1861)
				message = "날짜 형식이 일치하지 않습니다.";
			else
				message = "회원 가입이 실패 했습니다.";
			// 기타 - 2291:참조키 위반, 12899:폭보다 문자열 입력 값이 큰경우
		} catch (Exception e) {
			message = "회원 가입이 실패 했습니다.";
			e.printStackTrace();
		}
		
		req.setAttribute("title", "회원가입");
		req.setAttribute("mode", "member");
		req.setAttribute("message", message);
		forward(req, resp, "/WEB-INF/views/member/member.jsp");

	}
	
	protected void pwdForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

	}

	protected void pwdSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

	}

	protected void updateSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

	}

	protected void userIdCheck(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

	}	
}
