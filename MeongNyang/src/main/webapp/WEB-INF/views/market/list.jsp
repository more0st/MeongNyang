﻿<%@ page contentType="text/html; charset=UTF-8" %>
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

.market-box {
	margin-top: 3px; margin-bottom: 70px;
	display: grid;
	/* auto-fill :  남는 공간(빈 트랙)을 그대로 유지, minmax : '최소, 최대 크기'를 정의 */
	/*grid-template-columns: repeat(auto-fill, minmax(180px, 3fr));*/
	/*grid-template-columns: repeat(3, 180px);
	grid-column-gap: 10px;
	grid-row-gap: 100px;*/
	display: flex;
	justify-content: flex-start;
	align-items: center;
	flex-wrap: wrap;
	gap: 10px;
	
}
.market-box .item { cursor: pointer; max-width: 220px;  }
.item > img {  width: 220px; height: 220px; object-fit: cover; cursor: pointer; border-radius : 20px; }
.border{
	background: #ffedea;
	border-radius : 10px;
}

.list{
height : 18px;
overflow : hidden;
text-overflow : ellipsis;
white-space : nowrap;
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
			<h2><img src="${pageContext.request.contextPath}/resource/images/marketPage.png" style="width: 250px;"></h2>
	    </div>
	    
	    <div class="body-main mx-auto">
			<table class="table">
				<tr>
					<td width="50%">
						${dataCount}개(${page}/${total_page} 페이지) 
					</td>
					<td align="right">
						<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/market/write.do';">내 물건 팔기</button>
					</td>
				</tr>
			</table>
			
			<div class="market-box">
				<c:forEach var="dto" items="${list}" varStatus="status">
					<div class="item" title="${dto.subject}"
						onclick="location.href='${articleUrl}&marketNum=${dto.marketNum}';">
					<c:choose>
						<c:when test="${dto.state == 2 || dto.state == 1}">
							<img src="${pageContext.request.contextPath}/resource/images/buy.jpg">
						</c:when>
						<c:otherwise>
							<img src="${pageContext.request.contextPath}/uploads/market/${dto.imageFilename}">
						</c:otherwise>
					</c:choose>	
						<div>
							<div class="border">
								<div class="list">제목 : ${dto.subject}</div>
								<div class="list">거래지 : ${dto.addr}</div>
								<div class="list">가격 : ${dto.price}원</div>
							</div>
						</div>
					</div>
				</c:forEach>
			</div>
			
			<div class="page-navigation">
				${dataCount == 0 ? "등록된 게시물이 없습니다." : paging}
			</div>
			
			<table class="table">
				<tr>
					<td width="100">
						<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/market/list.do';" title="새로고침"><i class="fa-solid fa-arrow-rotate-right"></i></button>
					</td>
					<td align="center">
						<form name="searchForm" action="${pageContext.request.contextPath}/market/list.do" method="post">
							<select name="condition" class="form-select">
								<option value="all"      ${condition=="all"?"selected='selected'":"" }>제목+내용</option>
								<option value="userName" ${condition=="sellerId"?"selected='selected'":"" }>작성자</option>
								<option value="reg_date"  ${condition=="reg_date"?"selected='selected'":"" }>등록일</option>
								<option value="subject"  ${condition=="subject"?"selected='selected'":"" }>제목</option>
								<option value="content"  ${condition=="content"?"selected='selected'":"" }>내용</option>
							</select>
							<input type="text" name="keyword" value="${keyword}" class="form-control">
							<button type="button" class="btn" onclick="searchList();">검색</button>
						</form>
					</td>
				</tr>
			</table>

	    </div>
	</div>
</main>

<footer>
    <jsp:include page="/WEB-INF/views/layout/footer.jsp"></jsp:include>
</footer>

<jsp:include page="/WEB-INF/views/layout/staticFooter.jsp"/>
</body>
</html>