<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>멍냥마켓</title>
<jsp:include page="/WEB-INF/views/layout/staticHeader.jsp"/>
<style type="text/css">
.body-main {
	max-width: 700px;
}

.table-list thead > tr:first-child{ background: #ffedea; }
.table-list th, .table-list td { text-align: center; }
.table-list .left { text-align: left; padding-left: 5px; }

.table-list .num { width: 60px; color: #787878; }
.table-list .subject { color: #787878; }
.table-list .name { width: 100px; color: #787878; }
.table-list .date { width: 100px; color: #787878; }
.table-list .hit { width: 70px; color: #787878; }

.nav_sub {
	position: relative;
	top: 50;
	bottom: auto;
	left:0;
	float:left;
	padding-top: 30px;
	padding-bottom: 30px;
	text-align: center;
	
}

.nav_sub ul {
	list-style: none;
	font-weight: bold; 
}

.nav_sub ul li {
	padding: 6px 0;	
}

.nav_sub ul li a:hover {
	color: #f28011; text-decoration: underline;
}

</style>
<script type="text/javascript">
function searchList() {
	const f = document.searchForm;
	f.submit();
}
</script>
</head>
<body>

<header>
	<jsp:include page="/WEB-INF/views/layout/header.jsp"></jsp:include>
</header>
	
<main>
	<div class="container body-container">
	   <div class="body-title">
	  		<c:choose>
	   			<c:when test="${category == 'receive' }">
					<h2> 받은 쪽지함 </h2>
				</c:when>
				<c:otherwise>
					<h2> 보낸 쪽지함 </h2>
				</c:otherwise>
			</c:choose>
	   </div>
	   
	   <div class="nav_sub" style="box-shadow: 0 0 15px 0 rgb(2 59 109 / 10%); border-radius: 30px; width: 12%;">
			<ul>
				<li><a href="${pageContext.request.contextPath}/message/write.do">쪽지쓰기</a></li>
				<li><a href="${pageContext.request.contextPath}/message/list_receive.do">받은쪽지함</a></li>
				<li><a href="${pageContext.request.contextPath}/message/list_send.do">보낸쪽지함</a></li>
	   		</ul>
	   </div>
	   
	   
	   <div style="box-shadow: 0 0 15px 0 rgb(2 59 109 / 10%);border-radius: 30px; margin: 0 auto ; width: 72%;">
	    <div class="body-main mx-auto">
			<table class="table">
				<tr>
					<td width="50%">
						${dataCount}개(${page}/${total_page} 페이지)
					</td>
					<td align="right">&nbsp;</td>
				</tr>
			</table>
			
			<table class="table table-list">
				<thead>
					<tr>
						<th class="num">번호</th>
						<th class="subject">제목</th>
						<th class="name">보낸사람</th>
						<th class="name">받은사람</th>
						<th class="date">작성일</th>
					</tr>
				</thead>
				
				<tbody>
					<c:forEach var="dto" items="${list}" varStatus="status">
						<tr>
							<td>${dataCount - (page-1) * size - status.index}</td>
							<td class="left">
								<a href="${articleUrl}&num=${dto.messageNum}&category=${category}">${dto.subject}</a>
							</td>
							<td>${dto.sendName}</td>
							<td>${dto.receiveName}</td>
							<td>${dto.send_date}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			
			<div class="page-navigation">
				${dataCount == 0 ? "등록된 게시물이 없습니다." : paging}
			</div>
			
			<table class="table">
				<tr>
					<td width="100">
						<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/message/list_${category}.do';" title="새로고침"><i class="fa-solid fa-arrow-rotate-right"></i></button>
					</td>
					<!-- 
					<td align="center">
						<form name="searchForm" action="${pageContext.request.contextPath}/bbs/list.do" method="post">
							<select name="condition" class="form-select">
								<option value="all"      ${condition=="all"?"selected='selected'":"" }>제목+내용</option>
								<option value="userName" ${condition=="userName"?"selected='selected'":"" }>작성자</option>
								<option value="reg_date"  ${condition=="reg_date"?"selected='selected'":"" }>등록일</option>
								<option value="subject"  ${condition=="subject"?"selected='selected'":"" }>제목</option>
								<option value="content"  ${condition=="content"?"selected='selected'":"" }>내용</option>
							</select>
							<input type="text" name="keyword" value="${keyword}" class="form-control" style="border-radius: 20px;">
							<button type="button" class="btn" onclick="searchList();">검색</button>
						</form>
					</td>
					 -->
					<!--  
					<td align="right" width="100">
						<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/bbs/write.do';">글올리기</button>
					</td>
					-->
				</tr>
			</table>

	    </div>
	  </div>
	</div>
</main>

<footer>
    <jsp:include page="/WEB-INF/views/layout/footer.jsp"></jsp:include>
</footer>

<jsp:include page="/WEB-INF/views/layout/staticFooter.jsp"/>
</body>
</html>