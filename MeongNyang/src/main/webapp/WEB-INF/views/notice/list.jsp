<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>spring</title>
<jsp:include page="/WEB-INF/views/layout/staticHeader.jsp"/>
<style type="text/css">
.body-main {
	max-width: 700px;
}

.table-list thead > tr:first-child{ background: #ffedea; border-top: white;}
.table-list th, .table-list td { text-align: center; }
.table-list .left { text-align: left; padding-left: 5px; }

.table-list .num { width: 60px; color: #787878; }
.table-list .subject { color: #787878; }
.table-list .name { width: 100px; color: #787878; }
.table-list .date { width: 100px; color: #787878; }
.table-list .hit { width: 70px; color: #787878; }
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
			<h2><img src="${pageContext.request.contextPath}/resource/images/noticePage.png" style="width: 250px;"></h2>
	    </div>
	   <div style="box-shadow: 0 0 15px 0 rgb(2 59 109 / 10%);border-radius: 30px; margin: 0 auto ; width: 70%;">
	    <div class="body-main mx-auto">
			<table class="table">
				<tr>
					<td width="50%">
						${dataCount}개(${page}/${total_page} 페이지)
					</td>
					<td align="right">&nbsp;</td>
				</tr>
			</table>
			
			<table class="table table-border table-list">
				<thead>
					<tr>
						<th class="num">번호</th>
						<th class="subject">제목</th>
						<th class="name">작성자</th>
						<th class="date">작성일</th>
						<th class="hit">조회수</th>
					</tr>
				</thead>
				
				<tbody>
					<c:forEach var="dto" items="${list}" varStatus="status">
						<tr>
							<td>${dataCount - (page-1) * size - status.index}</td>
							<td class="left">
								<a href="${articleUrl}&noticeNum=${dto.noticeNum}">${dto.subject}</a>
								<c:if test="${dto.gap<1}"><img src="${pageContext.request.contextPath}/resource/images/new.gif"></c:if>
							</td>
							<td>${dto.userName}</td>
							<td>${dto.reg_date}</td>
							<td>${dto.hitCount}</td>
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
						<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/notice/list.do';" title="새로고침"><i class="fa-solid fa-arrow-rotate-right"></i></button>
					</td>
					<td align="center">
						<form name="searchForm" action="${pageContext.request.contextPath}/notice/list.do" method="post">
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
					<td align="right" width="100">
					<c:if test="${sessionScope.member.userId == 'admin'}">
						<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/notice/write.do';">글올리기</button>
					</c:if>
					</td>
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