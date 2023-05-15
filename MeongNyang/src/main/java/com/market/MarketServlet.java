package com.market;

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
		}
	}
	
	protected void list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		MarketDAO dao = new MarketDAO();
		MyUtil util = new MyUtil();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		String cp = req.getContextPath();
		
		try {
			String page = req.getParameter("page");
			int current_page = 1;
			if(page != null) {
				current_page = Integer.parseInt(page);
			}
			
			// 전체데이터 개수
			int dataCount = dao.dataCount();

			// 전체페이지수
			int size = 9;
			int total_page = util.pageCount(dataCount, size);
			if (current_page > total_page) {
				current_page = total_page;
			}

			// 게시물 가져오기
			int offset = (current_page - 1) * size;
			if(offset < 0) offset = 0;
			
			List<MarketDTO> list = dao.listMarket(offset, size);
			
			// 페이징 처리
			String listUrl = cp + "/market/list.do";
			String articleUrl = cp + "/market/article.do?page=" + current_page;
			String paging = util.paging(current_page, total_page, listUrl);

			// 포워딩할 list.jsp에 넘길 값
			req.setAttribute("list", list);
			req.setAttribute("dataCount", dataCount);
			req.setAttribute("articleUrl", articleUrl);
			req.setAttribute("page", current_page);
			req.setAttribute("total_page", total_page);
			req.setAttribute("paging", paging);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
			
			MarketDTO preReadDto = dao.preReadPhoto(marketNum, info.getUserId());
			MarketDTO nextReadDto = dao.nextReadPhoto(marketNum, info.getUserId());

			req.setAttribute("dto", dto);
			req.setAttribute("listFile", listFile);
			req.setAttribute("page", page);
			req.setAttribute("preReadDto", preReadDto);
			req.setAttribute("nextReadDto", nextReadDto);

			forward(req, resp, "/WEB-INF/views/market/article.jsp");
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
		forward(req, resp, "/WEB-INF/views/market/article.jsp");
	}
	
	protected void updateForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		req.setAttribute("mode", "update");
		forward(req, resp, "/WEB-INF/views/market/write.jsp");
	}
	
	protected void updateSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		MarketDAO dao = new MarketDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
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
				dto.setSubject(req.getParameter("subject"));
				dto.setContent(req.getParameter("content"));

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
			// TODO: handle exception
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
			if (!dto.getSellerId().equals(info.getUserId())) {
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
		} catch (Exception e) {
			e.printStackTrace();
		}

		resp.sendRedirect(cp + "/market/list.do?page=" + page);
	}

}
