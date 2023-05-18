package com.event;

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
@WebServlet("/event/*")
public class EventServlet extends MyUploadServlet{

	private static final long serialVersionUID = 1L;
	
	private String pathname;

	@Override
	protected void execute(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");
		
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
		} else if(uri.indexOf("join.do")!=-1) {
			join(req, resp);
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
			
			//검색 (전체:2/진행:1/종료:0)
			int eventStatus=2;
			String status=req.getParameter("eventStatus");
			if(status!=null) {
				eventStatus=Integer.parseInt(status);
			}
	
			int size=6;
			
			//데이터개수
			int dataCount = 0;

			if(eventStatus==2) {//전체 이벤트
				dataCount=dao.dataCount();
			} else {
				dataCount=dao.dataCount(eventStatus);
			}
			
			int total_page=util.pageCount(dataCount, size);
			if(current_page>total_page) {
				current_page=total_page;
			}
			
			int offset=(current_page-1)*size;
			if(offset<0) offset=0;
			
			
			List<EventDTO> list=null;
			if(eventStatus==2) {
				list=dao.listEvent(offset, size);
			} else {
				list=dao.listEvent(offset, size, eventStatus);
			}
			
			String query="eventStatus="+eventStatus;
			String listUrl=cp+"/event/list.do";
			String articleUrl=cp+"/event/article.do?page="+current_page;
			
			if(query.length()!=0) {
				listUrl+="?"+query;
				articleUrl+="&"+query;
			}
			String paging=util.paging(current_page, total_page, listUrl);
			
			req.setAttribute("list", list);
			req.setAttribute("page", current_page);
			req.setAttribute("total_page", total_page);
			req.setAttribute("dataCount", dataCount);
			req.setAttribute("size", size);
			req.setAttribute("articleUrl", articleUrl);
			req.setAttribute("paging", paging);
			req.setAttribute("eventStatus", eventStatus);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		forward(req, resp, "/WEB-INF/views/event/list.jsp");
	}
	protected void writeForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//이벤트 등록 폼
		HttpSession session=req.getSession();
		SessionInfo info=(SessionInfo)session.getAttribute("member");
		
		String cp=req.getContextPath();
		String size=req.getParameter("size");
		
		if(!info.getUserId().equals("admin")) {
			resp.sendRedirect(cp+"/event/list.do?size="+size);
			return;
		}
		
		req.setAttribute("mode", "write");
		req.setAttribute("size", size);
		
		forward(req, resp, "/WEB-INF/views/event/write.jsp");
		
	}
	protected void writeSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//이벤트 저장
		EventDAO dao=new EventDAO();
		
		HttpSession session=req.getSession();
		SessionInfo info=(SessionInfo)session.getAttribute("member");
		
		String cp=req.getContextPath();
		if(req.getMethod().equalsIgnoreCase("GET")) {
			resp.sendRedirect(cp+"/event/list.do");
			return;
		}
		
		if(!info.getUserId().equals("admin")) {
			resp.sendRedirect(cp+"/event/list.do");
			return;
		}
		
		try {
			EventDTO dto=new EventDTO();
			
			dto.setUserId(info.getUserId());
			
			dto.setSubject(req.getParameter("subject"));
			dto.setContent(req.getParameter("content"));
			dto.setStart_date(req.getParameter("start_date"));
			dto.setEnd_date(req.getParameter("end_date"));
			dto.setPassCount(Long.parseLong(req.getParameter("passCount")));
			
			Map<String, String[]> map = doFileUpload(req.getParts(), pathname);
			if (map != null) {
				String[] saveFiles = map.get("saveFilenames");
				dto.setImageFiles(saveFiles);
			}
			
			dao.insertEvent(dto);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		resp.sendRedirect(cp+"/event/list.do");
	}
	
	protected void article(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	      //이벤트 글보기
	      
	      String cp=req.getContextPath();
	      
	      String page=req.getParameter("page");
	      int size=6;
	      String query="page="+page;
	      
	      EventDAO dao=new EventDAO();
	      try {
	         long eNum=Long.parseLong(req.getParameter("eNum"));
	         
	         int eventStatus=2;
	         String status=req.getParameter("eventStatus");
	         
	         if(status!=null) {
	            eventStatus=Integer.parseInt(status);
	         }


	         query+="&eventStatus="+eventStatus;
	         
	         EventDTO dto=dao.readEvent(eNum);
	         if(dto==null) {
	            resp.sendRedirect(cp+"/event/list.do?"+query);
	            return;
	         }
	         
	         dto.setContent(dto.getContent().replaceAll("\n", "<br>"));
	         
	         //이전글다음글
	         EventDTO preReadDTO=dao.preReadEvent(dto.geteNum(), eventStatus);
	         EventDTO nextReadDTO=dao.nextReadEvent(dto.geteNum(), eventStatus);
	         
	         
	         req.setAttribute("dto", dto);
	         req.setAttribute("query", query);
	         req.setAttribute("page", page);
	         req.setAttribute("size", size);
	         req.setAttribute("eventStatus",eventStatus );
	         req.setAttribute("eNum",eNum);
			 req.setAttribute("preReadDTO", preReadDTO);
			 req.setAttribute("nextReadDTO", nextReadDTO);
	         
	         forward(req, resp, "/WEB-INF/views/event/article.jsp");
	         return;
	         
	      } catch (Exception e) {
	         e.printStackTrace();
	      }
	      
	      resp.sendRedirect(cp+"/event/list.do?"+query);

	}
	protected void updateForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//이벤트 수정
		EventDAO dao=new EventDAO();
		
		HttpSession session=req.getSession();
		SessionInfo info=(SessionInfo)session.getAttribute("member");
		
		String cp=req.getContextPath();
		String page=req.getParameter("page");
		
		try {
			long eNum=Long.parseLong(req.getParameter("eNum"));
			EventDTO dto=dao.readEvent(eNum);
			
			if(dto==null) {
				resp.sendRedirect(cp+"/event/list.do?page="+page);
				return;
			}
			
			if(!info.getUserId().equals("admin")) {
				resp.sendRedirect(cp+"/event/list.do?page="+page);
				return;
			}
			
			//파일처리
			
			req.setAttribute("dto", dto);
			req.setAttribute("page", page);
			req.setAttribute("mode", "update");
			
			forward(req, resp, "/WEB-INF/views/event/write.jsp");
			return;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		resp.sendRedirect(cp+"/event/list.do?page="+page);
		
		
	}
	protected void updateSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//이벤트 수정 완료
		EventDAO dao= new EventDAO();
		
		HttpSession session=req.getSession();
		SessionInfo info=(SessionInfo)session.getAttribute("member");
		
		String cp=req.getContextPath();
		String page=req.getParameter("page");
		
		if(req.getMethod().equalsIgnoreCase("GET")) {
			resp.sendRedirect(cp+"/event/list.do");
			return;
		}
		
		if(!info.getUserId().equals("admin")) {
			resp.sendRedirect(cp+"/event/list.do?page="+page);
			return;
		}
		
		try {
			
			EventDTO dto=new EventDTO();
			
			dto.seteNum(Long.parseLong(req.getParameter("eNum")));
			dto.setSubject(req.getParameter("subject"));
			dto.setContent(req.getParameter("content"));
			dto.setStart_date(req.getParameter("start_date"));
			dto.setEnd_date(req.getParameter("end_date"));
			dto.setUserId(info.getUserId());
			
			//파일처리
			
			dao.updateEvent(dto);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		resp.sendRedirect(cp+"/event/list.do?page="+page);
		
		
	}
	protected void deleteFile(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//수정에서 사진만 삭제
	}
	protected void delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//이벤트 삭제
		EventDAO dao=new EventDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");
		
		String cp = req.getContextPath();
		
		if(!info.getUserId().equals("admin")) {
			resp.sendRedirect(cp+"/event/list.do");
			return;
		}
		
		String page = req.getParameter("page");
		String query = "page=" + page;
		
		try {
			long eNum=Long.parseLong(req.getParameter("eNum"));
			
			int eventStatus=2;
			String status=req.getParameter("eventStatus");
			
			if(status!=null) {
				eventStatus=Integer.parseInt(status);
			}
			
			query+="&eventStatus="+eventStatus;
			
			EventDTO dto=dao.readEvent(eNum);
			if(dto==null) {
				resp.sendRedirect(cp+"/event/list.do?page="+page);
				return;
			}
			
			//이미지 파일 지우기
			
			dao.deleteEvent(eNum);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		resp.sendRedirect(cp+"/event/list.do?"+query);
		
		
	}
	protected void join(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//이벤트 참여
		EventDAO dao=new EventDAO();
		
		String cp=req.getContextPath();
		
		HttpSession session=req.getSession();
		SessionInfo info=(SessionInfo)session.getAttribute("member");
		
		try {
			long eNum=Long.parseLong(req.getParameter("eNum"));
			
			dao.insertParticipant(eNum,info.getUserId());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		resp.sendRedirect(cp+"/event/list.do");
		
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
