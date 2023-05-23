package com.club;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

import com.member.SessionInfo;
import com.util.FileManager;
import com.util.MyUploadServlet;
import com.util.MyUtil;

@MultipartConfig
@WebServlet("/club/*")
public class ClubServlet extends MyUploadServlet {
	private static final long serialVersionUID = 1L;

	private String pathname;
	
	@Override
	protected void execute(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");

		String uri = req.getRequestURI();
		String cp = req.getContextPath();

		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");
		
		//헤더 정보
		String ajax = req.getHeader("AJAX");
		
		if(ajax != null && uri.indexOf("list.do") == -1 && info == null) {
			resp.sendError(403);
			return;
		}else if (uri.indexOf("list.do") == -1 && info == null) { // 로그인되지 않은 경우
			resp.sendRedirect(cp + "/member/login.do");
			return;
		}
		
		

		// 이미지를 저장할 경로(pathname)
		String root = session.getServletContext().getRealPath("/");
		pathname = root + "uploads" + File.separator + "club";
		
		// uri에 따른 작업 구분
		if (uri.indexOf("list.do") != -1) {
			list(req, resp);
		} else if (uri.indexOf("my.do") != -1) {
			mylist(req, resp);
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
		} else if (uri.indexOf("deleteFile") != -1) {
			deleteFile(req, resp);
		} else if (uri.indexOf("delete.do") != -1) {
			delete(req, resp);
		} else if (uri.indexOf("signUp.do") != -1) {
			signUp(req, resp);
		} else if (uri.indexOf("byebye.do") != -1) {
			byebye(req, resp);
		}  else if (uri.indexOf("insertBoardLike.do") != -1) {
			//게시글 공감
			insertBoardLike(req, resp);
		} else if (uri.indexOf("insertReply.do") != -1) {
			//댓글 등록
			insertReply(req, resp);
		} else if (uri.indexOf("listReply.do") != -1) {
			//댓글 리스트
			listReply(req, resp);
		} else if (uri.indexOf("deleteReply.do") != -1) {
			//댓글 삭제
			deleteReply(req, resp);
		} else if (uri.indexOf("insertReplyAnswer.do") != -1) {
			//댓글의 답글 등록
			insertReplyAnswer(req, resp);
		} else if (uri.indexOf("listReplyAnswer.do") != -1) {
			//댓글의 답글 삭제 리스트
			listReplyAnswer(req, resp);
		} else if (uri.indexOf("deleteReplyAnswer.do") != -1) {
			//댓글의 답글 삭제
			deleteReplyAnswer(req, resp);
		} else if (uri.indexOf("countReplyAnswer.do") != -1) {
			//댓글의 답글 개수
			countReplyAnswer(req, resp);
		}
	}
		
	
	
	protected void list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 모임 리스트
		ClubDAO dao = new ClubDAO();
		MyUtil util = new MyUtil();
		
		
		String cp = req.getContextPath();
		
		try {
			String page = req.getParameter("page");
			int current_page = 1;
			if (page != null) {
				current_page = Integer.parseInt(page);
			}
			
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
				dataCount = dao.dataCount();
			} else {
				dataCount = dao.dataCount(condition, keyword);
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
			
			List<ClubDTO> list = null;
			if (keyword.length() == 0) {
				//list = dao.myClubList(offset, size, userId);
				list = dao.listClub(offset, size);
			} else if(keyword.length()>=1) {
				list = dao.listClub(offset, size, condition, keyword);
			}

			String query = "";
			if (keyword.length() != 0) {
				query = "condition=" + condition + "&keyword=" + URLEncoder.encode(keyword, "utf-8");
			}
			
			// 페이징 처리
			String listUrl = cp + "/club/list.do";
			String articleUrl = cp + "/club/article.do?page=" + current_page;
			if (query.length() != 0) {
				listUrl += "?" + query;
				articleUrl += "&" + query;
			}			
			
			String paging = util.paging(current_page, total_page, listUrl);
			
			
			
			
			// 포워딩할 JSP에 전달할 속성
			req.setAttribute("list", list);
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
		forward(req, resp, "/WEB-INF/views/club/list.jsp");
		
	}
	
	
	protected void mylist(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 모임 리스트
		ClubDAO dao = new ClubDAO();
		MyUtil util = new MyUtil();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");
		
		String cp = req.getContextPath();
		
		try {
			String userId = info.getUserId();
			String page = req.getParameter("page");
			int current_page = 1;
			if (page != null) {
				current_page = Integer.parseInt(page);
			}
			
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
				dataCount = dao.dataCount(userId);
			} else {
				dataCount = dao.dataCount(condition, keyword,userId);
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
			
			
			List<ClubDTO> list = null;
			if (keyword.length() == 0) {
				list =  dao.myClubList(offset, size,  userId);
			} else if(keyword.length()>=1) {
				list =  dao.myClubList(offset, size, condition, keyword, userId);
			}
			
			// 페이징 처리
			String listUrl = cp + "/club/mylist.do";
			String articleUrl = cp + "/club/article.do?page=" + current_page;
			
			String paging = util.paging(current_page, total_page, listUrl);
			
			
			// 포워딩할 JSP에 전달할 속성
			req.setAttribute("list", list);
			req.setAttribute("page", current_page);
			req.setAttribute("total_page", total_page);
			req.setAttribute("dataCount", dataCount);
			req.setAttribute("size", size);
			req.setAttribute("articleUrl", articleUrl);
			req.setAttribute("condition", condition);
			req.setAttribute("keyword", keyword);
			req.setAttribute("paging", paging);
			req.setAttribute("val", "true");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		forward(req, resp, "/WEB-INF/views/club/my.jsp");
		
		
	}
	
	protected void writeForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//글쓰기 폼
		req.setAttribute("mode", "write");
		forward(req, resp, "/WEB-INF/views/club/write.jsp");
		
	}
	
	protected void writeSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//글 저장
		ClubDAO dao = new ClubDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");

		String cp = req.getContextPath();
		if (req.getMethod().equalsIgnoreCase("GET")) {
			resp.sendRedirect(cp + "/club/list.do");
			return;
		}
		
		try {
			ClubDTO dto = new ClubDTO();
			
			// userId는 세션에 저장된 정보
			dto.setUserId(info.getUserId());
			
			// 제목, 모임명, 모임장(session), 내용, 모임정원수 
			dto.setSubject(req.getParameter("subject"));
			dto.setClubName(req.getParameter("clubName"));
			dto.setContent(req.getParameter("content"));
			dto.setMaxMember(Integer.parseInt(req.getParameter("maxMember")));

			Map<String, String[]> map = doFileUpload(req.getParts(), pathname);
			if (map != null) {
				String[] saveFiles = map.get("saveFilenames");
				dto.setImageFiles(saveFiles);
			}
			
			dao.insertClub(dto);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		resp.sendRedirect(cp + "/club/list.do");
	}
	
	protected void article(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 글보기
		ClubDAO dao = new ClubDAO();
		MyUtil util = new MyUtil();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");
		
		String cp = req.getContextPath();
		
		String page = req.getParameter("page");
		String query = "page=" + page;

		try {
			String val = req.getParameter("val");
			String userId = info.getUserId();
			long num = Long.parseLong(req.getParameter("num"));
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
			dao.updateHitCount(num);

			// 게시물 가져오기
			ClubDTO dto = dao.readClub(num);
			if (dto == null) { // 게시물이 없으면 다시 리스트로
				resp.sendRedirect(cp + "/club/list.do?" + query);
				return;
			}
			dto.setContent(util.htmlSymbols(dto.getContent()));
			
			
			//로그인 유저의 게시글 공감 여부(좋아요)
			boolean isUserLike = dao.isUserBoardLike(num, info.getUserId());
			

			// 이전글 다음글
			ClubDTO preReadDto = dao.preReadBoard(dto.getClubNum(), condition, keyword);
			ClubDTO nextReadDto = dao.nextReadBoard(dto.getClubNum(), condition, keyword);

			List<ClubDTO> listFile = dao.listPhotoFile(num);
			
			int memberCount = dao.memberCount(num);
			
			List<ClubDTO> list = dao.memberList(num);
			
			boolean result = dao.isMemberCheck(num, userId);
			
			int status = dao.statusCheck(num, userId);
			
			// JSP로 전달할 속성
			req.setAttribute("dto", dto);
			req.setAttribute("page", page);
			req.setAttribute("query", query);
			req.setAttribute("preReadDto", preReadDto);
			req.setAttribute("nextReadDto", nextReadDto);
			req.setAttribute("listFile", listFile);
			req.setAttribute("memberCount", memberCount);
			req.setAttribute("list", list);
			req.setAttribute("result", result);
			req.setAttribute("status", status);
			req.setAttribute("mode", "update");
			req.setAttribute("isUserLike", isUserLike );
			req.setAttribute("val", val );

			// 포워딩
			forward(req, resp, "/WEB-INF/views/club/article.jsp");
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}

		resp.sendRedirect(cp + "/club/list.do?" + query);
	}
	
	protected void updateForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 수정 폼
		ClubDAO dao = new ClubDAO();

		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");
		
		String cp = req.getContextPath();

		String page = req.getParameter("page");

		try {
			long num = Long.parseLong(req.getParameter("num"));
			ClubDTO dto = dao.readClub(num);

			if (dto == null) {
				resp.sendRedirect(cp + "/club/list.do?page=" + page);
				return;
			}

			// 게시물을 올린 사용자가 아니면
			if (! dto.getUserId().equals(info.getUserId())) {
				resp.sendRedirect(cp + "/club/list.do?page=" + page);
				return;
			}

			List<ClubDTO> listFile = dao.listPhotoFile(num);
			
			req.setAttribute("dto", dto);
			req.setAttribute("page", page);
			req.setAttribute("mode", "update");
			req.setAttribute("listFile", listFile);
			

			forward(req, resp, "/WEB-INF/views/club/write.jsp");
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}

		resp.sendRedirect(cp + "/club/list.do?page=" + page);
	
		
	}
	
	protected void updateSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 수정 완료
		ClubDAO dao = new ClubDAO();

		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");
		
		String cp = req.getContextPath();
		if (req.getMethod().equalsIgnoreCase("GET")) {
			resp.sendRedirect(cp + "/club/list.do");
			return;
		}

		String page = req.getParameter("page");
		
		try {
			ClubDTO dto = new ClubDTO();
			
			dto.setClubNum(Long.parseLong(req.getParameter("clubNum")));
			dto.setSubject(req.getParameter("subject"));
			dto.setContent(req.getParameter("content"));
			dto.setMaxMember(Integer.parseInt(req.getParameter("maxMember")));
			dto.setClubName(req.getParameter("clubName"));
			
			dto.setUserId(info.getUserId());
			
			Map<String, String[]> map = doFileUpload(req.getParts(), pathname);
			if (map != null) {
				String[] saveFiles = map.get("saveFilenames");
				dto.setImageFiles(saveFiles);
			}

			dao.updateClub(dto);
		} catch (Exception e) {
			e.printStackTrace();
		}

		resp.sendRedirect(cp + "/club/list.do?page=" + page);
		
	}
	
	protected void deleteFile(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 수정에서 파일만 삭제
		ClubDAO dao = new ClubDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");

		String cp = req.getContextPath();

		String page = req.getParameter("page");

		try {
			long num = Long.parseLong(req.getParameter("num"));
			long fileNum = Long.parseLong(req.getParameter("fileNum"));
			
			ClubDTO dto = dao.readClub(num);

			if (dto == null) {
				resp.sendRedirect(cp + "/club/list.do?page=" + page);
				return;
			}

			if (!info.getUserId().equals(dto.getUserId())) {
				resp.sendRedirect(cp + "/club/list.do?page=" + page);
				return;
			}
			
			ClubDTO vo = dao.readPhotoFile(fileNum);
			if(vo != null) {
				FileManager.doFiledelete(pathname, vo.getImageFilename());
				
				dao.deletePhotoFile("one", fileNum);
			}

			resp.sendRedirect(cp + "/club/update.do?num=" + num + "&page=" + page);
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}

		resp.sendRedirect(cp + "/club/list.do?page=" + page);
	}
	
	protected void delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//삭제완료
		ClubDAO dao = new ClubDAO();

		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");
		
		String cp = req.getContextPath();
		
		String page = req.getParameter("page");
		String query = "page=" + page;

		try {
			long num = Long.parseLong(req.getParameter("num"));
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
			
			ClubDTO dto = dao.readClub(num);
			if (dto == null) {
				resp.sendRedirect(cp + "/club/list.do?page=" + page);
				return;
			}
			
			// 게시물을 올린 사용자가 아니면
			if (!dto.getUserId().equals(info.getUserId()) && ! info.getUserId().equals("admin")) {
				resp.sendRedirect(cp + "/club/list.do?page=" + page);
				return;
			}
		
			// 이미지 파일 지우기
			List<ClubDTO> listFile = dao.listPhotoFile(num);
			for (ClubDTO vo : listFile) {
				FileManager.doFiledelete(pathname, vo.getImageFilename());
			}
			dao.deletePhotoFile("all", num);

			dao.deleteClub(num);
		
		} catch (Exception e) {
			e.printStackTrace();
		}

		resp.sendRedirect(cp + "/club/list.do?" + query);
		
		
		
		
	}
	
	protected void signUp(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//글보기에서 멤버가입하기
		
		ClubDAO dao = new ClubDAO();
		
		String cp = req.getContextPath();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");
		
		try {
			long num = Long.parseLong(req.getParameter("num"));
			
			ClubDTO dto2 = new ClubDTO();
			
			dto2.setUserId(info.getUserId());
			dto2.setClubNum(num);
			
			dto2.setNowMember(dao.memberCount(num));
			
			dao.insertClubMember(dto2);
			
			dao.updateMemberCount(num);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		resp.sendRedirect(cp + "/club/list.do");
		
		
	}
	
	protected void byebye(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//모임멤버 탈퇴
		ClubDAO dao = new ClubDAO();
		
		String cp = req.getContextPath();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");
		String val = req.getParameter("val");
		
		try {
			long num = Long.parseLong(req.getParameter("num"));
			
			String userId = info.getUserId();
			
			dao.deleteMember(num, userId);
			/*//delete가 안되도 인원수감소안되는것을 막으려고 했지만 실패
			if(dao.checkMemberList(num, userId)==null) {//clubmember테이블에 num,userId가 없으면
				dao.deleteMemberCount(num);//인원수감소
			}
			*/
			dao.deleteMemberCount(num);//인원수감소
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(val.equals("true")) {
			resp.sendRedirect(cp + "/club/my.do");
			return;
		}else
			resp.sendRedirect(cp + "/club/list.do");
		
		
	}
	
	
	protected void insertBoardLike(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//게시글 공감 저장: AJAX-JSON
		ClubDAO dao = new ClubDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");
		
		String state = "false";
		int boardLikeCount = 0;
		
		try {
			long num = Long.parseLong(req.getParameter("num"));
			String isNoLike = req.getParameter("isNoLike");
			
			if(isNoLike.equals("true")){
				dao.insertBoardLike(num, info.getUserId());//공감
			} else {
				dao.deleteBoardLike(num, info.getUserId());//공감 취소
			}
			
			//공감개수
			boardLikeCount = dao.countBoardLike(num);
			
			state = "true";
			
		} catch (SQLException e) {
			state = "liked";
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		JSONObject job = new JSONObject();
		job.put("state", state);
		job.put("boardLikeCount", boardLikeCount);
		
		resp.setContentType("text/html;charset=utf-8");//설정 안하면 한글 깨짐
		PrintWriter out = resp.getWriter();
		out.print(job.toString());
		
	}
	
	protected void insertReply(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//게시글 댓글/답글 저장 : AJAX-JSON
		ClubDAO dao = new ClubDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		String state = "false";
		
		try {
			ReplyDTO dto = new ReplyDTO();
			
			long num = Long.parseLong(req.getParameter("num"));
			dto.setClubNum(num);
			dto.setUserId(info.getUserId());
			dto.setContent(req.getParameter("content"));
			String answer = req.getParameter("answer");
			if(answer != null) {//answer가 넘어오지 않았으면 
				dto.setAnswer(Long.parseLong(answer));
			}
			
			dao.insertReply(dto);
			
			state = "true";
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JSONObject job = new JSONObject();
		job.put("state", state);
		
		resp.setContentType("text/html;charset=utf-8");//설정 안하면 한글 깨짐
		PrintWriter out = resp.getWriter();
		out.print(job.toString());
		
		
	}
	
	protected void listReply(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//게시글 댓글 리스트 : AJAX-Text
		ClubDAO dao = new ClubDAO();
		MyUtil util = new MyUtil();
		
		try {
			long num = Long.parseLong(req.getParameter("num"));
			String pageNo = req.getParameter("pageNo");
			int current_page = 1;
			
			if(pageNo != null) {
				current_page = Integer.parseInt(pageNo);
			}
			
			int size = 5;
			int total_page = 0;
			int replyCount = 0;
			
			replyCount = dao.dataCountReply(num);
			total_page = util.pageCount(replyCount, size);
			if(current_page > total_page) {
				current_page = total_page;
			}
			
			int offset = (current_page - 1)*size;
			if(offset < 0) offset = 0;
			
			
			List<ReplyDTO> listReply = dao.listReply(num, offset, size);
			
			for(ReplyDTO dto : listReply) {
				dto.setContent(dto.getContent().replaceAll("\n", "<br>"));
			}
			
			String paging = util.pagingMethod(current_page, total_page, "listPage");
			
			req.setAttribute("listReply", listReply);
			req.setAttribute("pageNo",current_page);
			req.setAttribute("replyCount",replyCount);
			req.setAttribute("total_page",total_page);
			req.setAttribute("paging",paging);
			
			forward(req, resp, "/WEB-INF/views/club/listReply.jsp");
			return;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		resp.sendError(400);//문제가 있으면 400에러 던짐
	}
	
	protected void deleteReply(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//게시글 댓글 삭제 : AJAX-Text
		ClubDAO dao = new ClubDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");
		String state = "false";
		
		try {
			long replyNum = Long.parseLong(req.getParameter("replyNum"));
			dao.deleteReply(replyNum, info.getUserId());
			
			state = "true";
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JSONObject job = new JSONObject();
		job.put("state", state);
		
		resp.setContentType("text/html; charset=utf-8");
		PrintWriter out = resp.getWriter();
		out.print(job.toString());
	}
	
	
	protected void insertReplyAnswer(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//게시글 댓글의 답글 추가 : AJAX-JSON
		
	}
	
	protected void listReplyAnswer(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//게시글 댓글의 답글 리스트 : AJAX-Text
		ClubDAO dao = new ClubDAO();
		
		try {//answer: 해당되는 아버지 번호
			long answer = Long.parseLong(req.getParameter("answer"));
			
			List<ReplyDTO> listReplyAnswer = dao.listReplyAnswer(answer);
			
			for(ReplyDTO dto : listReplyAnswer) {
				dto.setContent(dto.getContent().replaceAll("\n", "<br>"));
			}
			
			req.setAttribute("listReplyAnswer", listReplyAnswer);
			
			forward(req, resp, "/WEB-INF/views/club/listReplyAnswer.jsp");
			return;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		resp.sendError(400);
	}
	
	protected void deleteReplyAnswer(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//게시글 댓글의 답글 삭제 : AJAX-JSON
		
	}
	
	protected void countReplyAnswer(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//게시글 댓글의 답글 개수 : AJAX-JSON
		ClubDAO dao = new ClubDAO();
		int count = 0;
		
		try {
			long answer = Long.parseLong(req.getParameter("answer"));
			count = dao.dataCountReplyAnswer(answer);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JSONObject job = new JSONObject();
		job.put("count", count);
		
		resp.setContentType("text/html; charset=utf-8");
		PrintWriter out = resp.getWriter();
		out.print(job.toString());
	}
	
	
	

}
