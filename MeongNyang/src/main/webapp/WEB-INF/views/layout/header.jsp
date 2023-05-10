<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

	<div class="header-top">
		<div class="header-left">
			&nbsp;
		</div>
		<div class="header-center">
			<h1 class="logo"><a href="#">SPRING</a><span>.</span></h1>
		</div>
		<div class="header-right">
            <c:if test="${empty sessionScope.member}">
                <a href="${pageContext.request.contextPath}/member/login.do" title="로그인"><i class="fa-solid fa-arrow-right-to-bracket"></i></a>
				&nbsp;
                <a href="#" title="회원가입"><i class="fa-solid fa-user-plus"></i></a>
            </c:if>
            <c:if test="${not empty sessionScope.member}">
            	<a href="#" title="알림"><i class="fa-regular fa-bell"></i></a>
            	&nbsp;
				<a href="${pageContext.request.contextPath}/member/logout.do" title="로그아웃"><i class="fa-solid fa-arrow-right-from-bracket"></i></a>
            </c:if>
            <c:if test="${sessionScope.member.userId == 'admin'}">
            	&nbsp;
				<a href="#" title="관리자"><i class="fa-solid fa-gear"></i></a>
            </c:if>
		</div>
	</div>

	<nav>
		<ul class="main-menu">
			<li><a href="${pageContext.request.contextPath}/">홈</a></li>
			
			<li><a href="#">회사소개</a></li>
			
			<li><a href="#">커뮤니티</a>
				<ul class="sub-menu">
					<li><a href="#" aria-label="submenu">방명록</a></li>
					<li><a href="#" aria-label="submenu">게시판</a></li>
					<li><a href="#" aria-label="submenu">포토갤러리</a></li>
					<li><a href="#" aria-label="submenu">채팅</a></li>
					<li><a href="#" aria-label="submenu">자료실</a></li>
				</ul>
			</li>

			<li><a href="#">스터디룸</a>
				<ul class="sub-menu">
					<li><a href="#" aria-label="submenu">프로그래밍</a></li>
					<li><a href="#" aria-label="submenu">데이터베이스</a></li>
					<li><a href="#" aria-label="submenu">웹프로그래밍</a></li>
					<li><a href="#" aria-label="submenu">질문과 답변</a></li>
				</ul>
			</li>

			<li><a href="#">고객센터</a>
				<ul class="sub-menu">
					<li><a href="#" aria-label="submenu">자주하는 질문</a></li>
					<li><a href="#" aria-label="submenu">공지사항</a></li>
					<li><a href="#" aria-label="submenu">질문과 답변</a></li>
					<li><a href="#" aria-label="submenu">1:1 문의</a></li>
					<li><a href="#" aria-label="submenu">이벤트</a></li>
				</ul>
			</li>

			<li><a href="#">마이페이지</a>
				<ul class="sub-menu">
					<li><a href="#" aria-label="submenu">포토앨범</a></li>
					<li><a href="#" aria-label="submenu">일정관리</a></li>
					<li><a href="#" aria-label="submenu">쪽지</a></li>
					<li><a href="#" aria-label="submenu">이메일</a></li>
					<li><a href="#" aria-label="submenu">정보수정</a></li>
				</ul>
			</li>

		</ul>
	</nav>
