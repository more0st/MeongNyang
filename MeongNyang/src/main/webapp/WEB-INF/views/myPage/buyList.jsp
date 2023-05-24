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
			<h2> 나의구매내역 </h2>
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
			
			<div class="market-box">
				<c:forEach var="dto" items="${list}" varStatus="status">
					<div class="item" title="${dto.subject}"
						onclick="location.href='${articleUrl}&marketnum=${dto.marketnum}';">
						<img src="${pageContext.request.contextPath}/uploads/market/${dto.imageFilename}">
						<div class="border">
						<div>제목 : ${dto.subject}</div>
						<div>판매자 : ${dto.sellerid}</div>
						<div>거래지 : ${dto.addr}</div>
						<div>가격 : ${dto.price}원</div>
						<div>구매날자 : ${dto.pay_date}</div>
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
						<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/myPage/myPage/buyList.do';" title="새로고침"><i class="fa-solid fa-arrow-rotate-right"></i></button>
					</td>
					<td align="center">
						<form name="searchForm" action="${pageContext.request.contextPath}/myPage/buyList.do" method="post">
							<select name="condition" class="form-select">
								<option value="all"      ${condition=="all"?"selected='selected'":"" }>제목+내용</option>
								<option value="seller" ${condition=="userName"?"selected='selected'":"" }>판매자</option>
								<option value="pay_date"  ${condition=="reg_date"?"selected='selected'":"" }>구매날짜</option>
								<option value="subject"  ${condition=="subject"?"selected='selected'":"" }>제목</option>
								<option value="content"  ${condition=="content"?"selected='selected'":"" }>내용</option>
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