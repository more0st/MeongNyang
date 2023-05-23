package com.market;

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
@WebServlet("/market/*")
public class MarketServlet extends MyUploadServlet{

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

		if (uri.indexOf("list.do") != -1) {
			list(req, resp);
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
		} else if (uri.indexOf("delete.do") != -1) {
			delete(req, resp);
		} else if (uri.indexOf("deleteFile.do") != -1) {
			deleteFile(req, resp);
		} else if (uri.indexOf("buy_ok.do") != -1) {
			buy(req, resp);
		} else if (uri.indexOf("cardbuy_ok.do") != -1) {
			cardbuy(req, resp);
		} else if(uri.indexOf("insertBoardLike.do") != -1) {
			insertBoardLike(req, resp);
		} else if(uri.indexOf("insertReply.do") != -1) {
			insertReply(req, resp);
		} else if(uri.indexOf("listReply.do") != -1) {
			listReply(req, resp);
		} else if(uri.indexOf("listReplyAnswer.do") != -1) {
			listReplyAnswer(req, resp);
		} else if(uri.indexOf("countReplyAnswer.do") != -1) {
			countReplyAnswer(req, resp);
		} else if(uri.indexOf("deleteReply.do") != -1) {
			// 댓글 삭제
			deleteReply(req, resp);
		}
		
	}
	
	protected void list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		MarketDAO dao = new MarketDAO();
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
			int size = 9;
			int total_page = util.pageCount(dataCount, size);
			if (current_page > total_page) {
				current_page = total_page;
			}

			// 게시물 가져오기
			int offset = (current_page - 1) * size;
			if(offset < 0) offset = 0;
			
			List<MarketDTO> list = null;
			if (keyword.length() == 0) {
				list = dao.listMarket(offset, size);
			} else {
				list = dao.listMarket(offset, size, condition, keyword);
			}

			String query = "";
			if (keyword.length() != 0) {
				query = "condition=" + condition + "&keyword=" + URLEncoder.encode(keyword, "utf-8");
			}

			// 페이징 처리
			String listUrl = cp + "/market/list.do";
			String articleUrl = cp + "/market/article.do?page=" + current_page;
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

		// JSP로 포워딩
		forward(req, resp, "/WEB-INF/views/market/list.jsp");
	}
	
	protected void writeForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		// JSP로 포워딩
		req.setAttribute("mode", "write");
		forward(req, resp, "/WEB-INF/views/market/write.jsp");
	}
	
	protected void writeSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		MarketDAO dao = new MarketDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		String cp = req.getContextPath();
		if (req.getMethod().equalsIgnoreCase("GET")) {
			resp.sendRedirect(cp + "/market/list.do");
			return;
		}
		
		try {
			MarketDTO dto = new MarketDTO();
			
			dto.setSellerId(info.getUserId());
			dto.setSubject(req.getParameter("subject"));
			dto.setContent(req.getParameter("content"));
			dto.setAddr(req.getParameter("addr"));
			dto.setPrice(Integer.parseInt(req.getParameter("price")));
			
			Map<String, String[]> map = doFileUpload(req.getParts(), pathname);
			if (map != null) {
				String[] saveFiles = map.get("saveFilenames");
				dto.setImageFiles(saveFiles);
			}
			
			dao.insertMarket(dto);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		resp.sendRedirect(cp + "/market/list.do");
	}
	
	protected void article(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		MarketDAO dao = new MarketDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");

		String cp = req.getContextPath();
		String page = req.getParameter("page");

		try {
			long marketNum = Long.parseLong(req.getParameter("marketNum"));

			MarketDTO dto = dao.readMarket(marketNum);
			if (dto == null) {
				resp.sendRedirect(cp + "/market/list.do?page=" + page);
				return;
			}
			dto.setContent(dto.getContent().replaceAll("\n", "<br>"));
			
			List<MarketDTO> listFile = dao.listPhotoFile(marketNum);
			dao.updateHitCount(marketNum);
			
			boolean isUserLike = dao.isUserBoardLike(marketNum, info.getUserId());
			
			MarketDTO preReadDto = dao.preReadPhoto(marketNum);
			MarketDTO nextReadDto = dao.nextReadPhoto(marketNum);

			req.setAttribute("dto", dto);
			req.setAttribute("listFile", listFile);
			req.setAttribute("page", page);
			req.setAttribute("preReadDto", preReadDto);
			req.setAttribute("nextReadDto", nextReadDto);

			req.setAttribute("isUserLike", isUserLike);
			
			forward(req, resp, "/WEB-INF/views/market/article.jsp");
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
		forward(req, resp, "/WEB-INF/views/market/article.jsp");
	}
	
	protected void updateForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		MarketDAO dao = new MarketDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");

		String cp = req.getContextPath();

		String page = req.getParameter("page");
		try {
			long marketNum = Long.parseLong(req.getParameter("marketNum"));
			MarketDTO dto = dao.readMarket(marketNum);

			if (dto == null) {
				resp.sendRedirect(cp + "/market/list.do?page=" + page);
				return;
			}

			// 게시물을 올린 사용자가 아니면
			if (!dto.getSellerId().equals(info.getUserId())) {
				resp.sendRedirect(cp + "/market/list.do?page=" + page);
				return;
			}

			List<MarketDTO> listFile = dao.listPhotoFile(marketNum);

			req.setAttribute("dto", dto);
			req.setAttribute("page", page);
			req.setAttribute("listFile", listFile);

			req.setAttribute("mode", "update");

			forward(req, resp, "/WEB-INF/views/market/write.jsp");
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}

		resp.sendRedirect(cp + "/market/list.do?page=" + page);
	}
	
	protected void updateSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		MarketDAO dao = new MarketDAO();
		
		try {
			String cp = req.getContextPath();
			
			if (req.getMethod().equalsIgnoreCase("GET")) {
				resp.sendRedirect(cp + "/market/list.do");
				return;
			}

			String page = req.getParameter("page");
			try {
				MarketDTO dto = new MarketDTO();
				dto.setMarketNum(Long.parseLong(req.getParameter("marketNum")));
				dto.setPrice(Integer.parseInt(req.getParameter("price")));
				dto.setSubject(req.getParameter("subject"));
				dto.setContent(req.getParameter("content"));
				dto.setAddr(req.getParameter("addr"));

				Map<String, String[]> map = doFileUpload(req.getParts(), pathname);
				if (map != null) {
					String[] saveFiles = map.get("saveFilenames");
					dto.setImageFiles(saveFiles);
				}

				dao.updateMarket(dto);
			} catch (Exception e) {
				e.printStackTrace();
			}

			resp.sendRedirect(cp + "/market/list.do?page=" + page);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	protected void delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		MarketDAO dao = new MarketDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");

		String cp = req.getContextPath();
		
		String page = req.getParameter("page");

		try {
			long marketNum = Long.parseLong(req.getParameter("marketNum"));

			MarketDTO dto = dao.readMarket(marketNum);
			if (dto == null) {
				resp.sendRedirect(cp + "/market/list.do?page=" + page);
				return;
			}

			// 게시물을 올린 사용자가 아니면
			if ( ! dto.getSellerId().equals(info.getUserId()) && ! info.getUserId().equals("admin")) {
				resp.sendRedirect(cp + "/market/list.do?page=" + page);
				return;
			}

			// 이미지 파일 지우기
			List<MarketDTO> listFile = dao.listPhotoFile(marketNum);
			for (MarketDTO vo : listFile) {
				FileManager.doFiledelete(pathname, vo.getImageFilename());
			}
			dao.deletePhotoFile("all", marketNum);

			// 테이블 데이터 삭제
			dao.deletePhoto(marketNum);
			dao.deleteZZIM(marketNum);
			dao.deleteMarket(marketNum);
		} catch (Exception e) {
			e.printStackTrace();
		}

		resp.sendRedirect(cp + "/market/list.do?page=" + page);
	}
	
	protected void deleteFile(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 수정에서 파일만 삭제
		MarketDAO dao = new MarketDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");

		String cp = req.getContextPath();

		String page = req.getParameter("page");

		try {
			long marketNum = Long.parseLong(req.getParameter("marketNum"));
			long fileNum = Long.parseLong(req.getParameter("fileNum"));
			
			MarketDTO dto = dao.readMarket(marketNum);

			if (dto == null) {
				resp.sendRedirect(cp + "/market/list.do?page=" + page);
				return;
			}

			if (!info.getUserId().equals(dto.getSellerId())) {
				resp.sendRedirect(cp + "/market/list.do?page=" + page);
				return;
			}
			
			MarketDTO vo = dao.readPhotoFile(fileNum);
			if(vo != null) {
				FileManager.doFiledelete(pathname, vo.getImageFilename());
				
				dao.deletePhotoFile("one", fileNum);
			}

			resp.sendRedirect(cp + "/market/update.do?marketNum=" + marketNum + "&page=" + page);
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}

		resp.sendRedirect(cp + "/market/list.do?page=" + page);
	}
	
	protected void insertBoardLike(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 게시글 공감 저장 : AJAX-JSON
		MarketDAO dao = new MarketDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		String state = "false";
		int boardLikeCount = 0;
		
		try {
			long num = Long.parseLong(req.getParameter("marketNum"));
			String isNoLike = req.getParameter("isNoLike");
			
			if(isNoLike.equals("true")) {
				dao.insertBoardLike(num, info.getUserId()); // 공감
			}else {
				dao.deleteBoardLike(num, info.getUserId()); // 공감취소
			}
			
			// 공감 개수
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
		
		PrintWriter out = resp.getWriter();
		out.print(job.toString());
	}
	
	
	protected void insertReply(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 게시글 댓글/답글 저장 : AJAX-JSON
		MarketDAO dao = new MarketDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		String state = "false";
		try {
			ReplyDTO dto = new ReplyDTO();
			
			long num = Long.parseLong(req.getParameter("marketNum"));
			dto.setMarketNum(num);
			dto.setUserId(info.getUserId());
			dto.setContent(req.getParameter("content"));
			String rereplynum = req.getParameter("rereplynum");
			if(rereplynum != null) {
				dto.setAnswer(Long.parseLong(rereplynum));
			}
			
			dao.insertReply(dto);
			
			state = "true";
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JSONObject job = new JSONObject();
		job.put("state", state);
		
		resp.setContentType("text/html;charset=utf-8");
		PrintWriter out = resp.getWriter();
		out.print(job.toString());
	}
	
	protected void listReply(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 게시글 댓글 리스트 : AJAX-Text
		MarketDAO dao = new MarketDAO();
		MyUtil util = new MyUtil();
		try {
			long num = Long.parseLong(req.getParameter("marketNum"));
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
			
			int offset = (current_page - 1) * size;
			if(offset < 0) offset = 0;
			
			List<ReplyDTO> listReply = dao.listReply(num, offset, size);
			
			for(ReplyDTO dto : listReply) {
				dto.setContent(dto.getContent().replaceAll("\n", "<bR>"));
			}
			
			String paging = util.pagingMethod(current_page, total_page, "listPage");
			
			req.setAttribute("listReply", listReply);
			req.setAttribute("pageNo", pageNo);
			req.setAttribute("replyCount", replyCount);
			req.setAttribute("total_page", total_page);
			req.setAttribute("paging", paging);
			
			forward(req, resp, "/WEB-INF/views/market/listReply.jsp");
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		resp.sendError(400);
	}
	
	protected void buy(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		MarketDAO dao = new MarketDAO();
		
		String cp = req.getContextPath();
		
		try {
			
			String buyerId = req.getParameter("buyerId");
			long marketNum = Long.parseLong(req.getParameter("marketNum"));
			
			dao.buy(marketNum, buyerId);
			
			resp.sendRedirect(cp + "/market/list.do");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void cardbuy(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		MarketDAO dao = new MarketDAO();
		
		String cp = req.getContextPath();
		
		try {
			
			String buyerId = req.getParameter("buyerId");
			long marketNum = Long.parseLong(req.getParameter("marketNum"));
			
			dao.cardbuy(marketNum, buyerId);
			
			resp.sendRedirect(cp + "/market/list.do");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void listReplyAnswer(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 게시글 댓글의 답글 리스트 : AJAX-JSON
		MarketDAO dao = new MarketDAO();
		
		try {
			long answer = Long.parseLong(req.getParameter("answer"));
			
			List<ReplyDTO> listReplyAnswer = dao.listReplyAnswer(answer);
			
			for(ReplyDTO dto : listReplyAnswer) {
				dto.setContent(dto.getContent().replaceAll("\n", "<br>"));
			}
			
			req.setAttribute("listReplyAnswer", listReplyAnswer);
			
			forward(req, resp, "/WEB-INF/views/market/listReplyAnswer.jsp");
			return;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		resp.sendError(400);
	}
	
	protected void countReplyAnswer(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 게시글 댓글의 답글 개수 : AJAX-JSON
		MarketDAO dao = new MarketDAO();
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

	protected void deleteReply(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 게시글 댓글 또는 댓글의 답글 삭제 : AJAX-JSON
		MarketDAO dao = new MarketDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		String state = "false";
		
		try {
			long replyNum = Long.parseLong(req.getParameter("replyNum"));
			dao.deleteReply(replyNum, info.getUserId());
			state = "ture";
		} catch (Exception e) {
			e.printStackTrace();
		}
		JSONObject job = new JSONObject();
		job.put("state", state);
		
		resp.setContentType("text/html; charset=utf-8");
		PrintWriter out = resp.getWriter();
		out.print(job.toString());
		
	}
}
