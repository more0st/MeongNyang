<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

	<div class="header-top">

		<div class="header-center">
			<h1 class="logo"><a href="${pageContext.request.contextPath}/main.do"><img src="${pageContext.request.contextPath}/resource/images/logo.png" style="width: 300px;"></a><span></span></h1>
		</div>
		<div class="header-right">
            <c:if test="${empty sessionScope.member}">
				&nbsp;
                <a href="${pageContext.request.contextPath}/member/login.do" title="로그인">로그인</a>
            </c:if>
            <c:if test="${not empty sessionScope.member}">
            	<a href="#" title="알림"><i class="fa-regular fa-bell"></i></a>
            	&nbsp;
				<a href="${pageContext.request.contextPath}" title="로그아웃"><i class="fa-solid fa-arrow-right-from-bracket"></i></a>
            </c:if>
            <c:if test="${sessionScope.member.userId == 'admin'}">
            	&nbsp;
				<a href="#" title="관리자"><i class="fa-solid fa-gear"></i></a>
            </c:if>
		</div>
	</div>

	<nav>
		<ul class="main-menu">
			<li><a href="${pageContext.request.contextPath}/main.do">홈</a></li>
			
			<li><a href="#"> 멍냥마켓 </a></li>

			<li><a href="#">커뮤니티</a>
			   	<ul class="sub-menu">
               		<li><a href="${pageContext.request.contextPath}/bbs/list.do">멍냥지도</a></li>
               		<li><a href="#">멍냥갤러리</a></li>
               		<li><a href="#">멍냥모임</a></li>
            	</ul>

			<li><a href="#">고객센터</a>
			   	<ul class="sub-menu">
					<li><a href="#">공지사항</a></li>
					<li><a href="#">1:1 문의</a></li>
            	</ul>
			</li>

			<li><a href="#">마이페이지</a>
			   	<ul class="sub-menu">
					<li><a href="#">나의 구매내역</a></li>
					<li><a href="#">나의 판매내역</a></li>
					<li><a href="#">회원정보수정</a></li>
					<li><a href="#">쪽지함</a></li>
					<li><a href="#">찜 목록</a></li>
            	</ul>
			</li>
			<!-- 만약 관리자계정으로 로그인하는 경우 마이페이지->관리페이지로 보이게 -->
			<li><a href="#">관리페이지</a>
			   	<ul class="sub-menu">
					<li><a href="#">관리페이지 접근</a></li>
					<li><a href="#">전체 사용자 조회</a></li>
					<li><a href="#">사용자 관리</a></li>
            	</ul>
			</li>


		</ul>
	</nav>


