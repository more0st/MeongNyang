<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

	<div class="header-top">

		<div class="header-center">
			<h1 class="logo"><a href="${pageContext.request.contextPath}/main.do"><img src="${pageContext.request.contextPath}/resource/images/mainlogo.png" style="width: 300px;"></a></h1>
		</div>
		<div class="header-right">
            <c:if test="${empty sessionScope.member}">
				&nbsp;
                <a href="${pageContext.request.contextPath}/member/login.do" title="로그인"><img src="${pageContext.request.contextPath}/resource/images/loginICON.png" style="width: 30px;"></a>
            </c:if>
            <c:if test="${not empty sessionScope.member}">
            	<a href="${pageContext.request.contextPath}/message/list_receive.do" title="알림"><img src="${pageContext.request.contextPath}/resource/images/message2.PNG" style="width: 30px;"></a>
            	&nbsp;
				<a href="${pageContext.request.contextPath}/member/logout.do" title="로그아웃"><img src="${pageContext.request.contextPath}/resource/images/logoutICON.png" style="width: 30px;"></a>
            </c:if>
		</div>
	</div>

	<nav>
		<ul class="main-menu">
			<li><a href="${pageContext.request.contextPath}/main.do">홈</a></li>
			
			<li><a href="${pageContext.request.contextPath}/market/list.do"> 멍냥마켓 </a></li>

			<li><a href="#">커뮤니티</a>
			   	<ul class="sub-menu">
               		<li><a href="${pageContext.request.contextPath}/map/list.do">멍냥지도</a></li>
               		<li><a href="${pageContext.request.contextPath}/gallery/list.do">멍냥갤러리</a></li>
               		<li><a href="${pageContext.request.contextPath}/club/list.do">멍냥모임</a></li>
               		<li><a href="${pageContext.request.contextPath}/event/list.do">이벤트</a></li>
            	</ul>

			<li><a href="#">고객센터</a>
			   	<ul class="sub-menu">
					<li><a href="${pageContext.request.contextPath}/notice/list.do">공지사항</a></li>
					<li><a href="${pageContext.request.contextPath}/qna/list.do">1:1 문의</a></li>
            	</ul>
			</li>
			<c:if test="${sessionScope.member.userId != null && sessionScope.member.userId != 'admin'}">
			<li><a href="#">마이페이지</a>
			   	<ul class="sub-menu">
					<li><a href="${pageContext.request.contextPath}/myPage/buyList.do">나의 구매내역</a></li>
					<li><a href="${pageContext.request.contextPath}/myPage2/salesList.do">나의 판매내역</a></li>
					<li><a href="${pageContext.request.contextPath}/myPage3/writingList.do">작성글목록</a></li>
					<li><a href="${pageContext.request.contextPath}/myPage4/likeList.do">찜 목록</a></li>
					<li><a href="${pageContext.request.contextPath}/message/list_receive.do">쪽지함</a></li>
					<li><a href="${pageContext.request.contextPath}/member/pwd.do?mode=update">회원정보수정</a></li>
            	</ul>
			</li>
            </c:if>
			<c:if test="${sessionScope.member.userId == 'admin' && sessionScope.member.userId != null}">
			<li><a href="#">관리페이지</a>
			   	<ul class="sub-menu">
					<li><a href="${pageContext.request.contextPath}/admin/userList.do">전체 사용자 조회</a></li>
					<li><a href="${pageContext.request.contextPath}/admin2/userFix.do">사용자 관리</a></li>
            	</ul>
			</li>
            </c:if>
		</ul>
	</nav>


