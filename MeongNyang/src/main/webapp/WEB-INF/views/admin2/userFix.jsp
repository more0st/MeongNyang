<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>관리페이지 :: 사용자 관리</title>
<jsp:include page="/WEB-INF/views/layout/staticHeader.jsp"/>
<style type="text/css">
.body-main {
	max-width: 1000px;
}

.table-list thead > tr:first-child{ background: #ffedea; }
.table-list th, .table-list td { text-align: center; }
.table-list .left { text-align: left; padding-left: 5px; }

.table-list .userid { width: 60px; color: #787878; }
.table-list .num { width: 60px; color: #787878; }
.table-list .birth { width: 100px; color: #787878; }
.table-list .tel { width: 150px; color: #787878; }
.table-list .addr { width: 200px; color: #787878; }
.table-list .email { width: 200px; color: #787878; }
.table-list .sell { width: 30px; color: #787878; }
.table-list .buy { width: 30px; color: #787878; }
.table-list .state { width: 70px; color: #787878; }
.table-list .statechange { width: 70px; color: #787878; }
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

function changeState1(userId,enabled) {
	if(confirm("회원을 복구하시겠습니까 ? ")) {
		const f = document.stateForm;
		f.userId.value = userId;
		f.enabled.value = enabled;
		f.submit();
	}
}

function changeState2(userId,enabled) {
	if(confirm("회원을 정지시키겠습니까 ? ")) {
		const f = document.stateForm;
		f.userId.value = userId;
		f.enabled.value = enabled;
		f.submit();
	}
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
			<h2><img src="${pageContext.request.contextPath}/resource/images/managerPage.png" style="width: 250px;"></h2>
	    </div>
	   <div style="box-shadow: 0 0 15px 0 rgb(2 59 109 / 10%);border-radius: 30px; margin: 0 auto ; width: 100%;">
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
						<th class="userid">회원ID</th>
						<th class="name">이름</th>
						<th class="birth">생년월일</th>
						<th class="tel">전화번호</th>
						<th class="addr">주소</th>
						<th class="email">이메일</th>
						<th class="state">회원상태</th>
						<th class="statechange"></th>
						<th class="statechange"></th>
					</tr>
				</thead>
				
				<tbody>
					<c:forEach var="dto" items="${list}" varStatus="status">
						<tr>
							<td>${dataCount - (page-1) * size - status.index}</td>
							<td class="left">${dto.userId}</td>
							<td>${dto.userName}</td>
							<td>${dto.birth}</td>
							<td>${dto.tel}</td>
							<td>${dto.addr}</td>
							<td>${dto.email}</td>
							<td>${dto.enabled == 1 ? '활동중' : '정지' }</td>
							<td>
									<button type="button" name="recovery" value="${recovery}" onclick="changeState1('${dto.userId}', 1);"><img src="${pageContext.request.contextPath}/resource/images/able.png" style="width: 50px;"></button>
							</td>
							<td>
									<button type="button" name="pause" value="${pause}" onclick="changeState2('${dto.userId}', 0);"><img src="${pageContext.request.contextPath}/resource/images/enable.png" style="width: 50px;"></button>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			
			<form name="stateForm" action="${pageContext.request.contextPath}/admin2/userFix.do" method="post">
				<input type="hidden" name="userId">
				<input type="hidden" name="enabled">
			</form>
			
			<div class="page-navigation">
				${dataCount == 0 ? "등록된 게시물이 없습니다." : paging}
			</div>
			
			<table class="table">
				<tr>
					<td width="100">
						<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/admin2/userFix.do';" title="새로고침"><i class="fa-solid fa-arrow-rotate-right"></i></button>
					</td>
					<td align="center">
						<form name="searchForm" action="${pageContext.request.contextPath}/admin2/userFix.do" method="post">
							<select name="condition" class="form-select">
								<option value="userId"      ${condition=="userId"?"selected='selected'":"" }>회원ID</option>
								<option value="userName" ${condition=="userName"?"selected='selected'":"" }>회원이름</option>
							</select>
							<input type="text" name="keyword" value="${keyword}" class="form-control" style="border-radius: 20px;">
							<button type="button" class="btn" onclick="searchList();">검색</button>
						</form>
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