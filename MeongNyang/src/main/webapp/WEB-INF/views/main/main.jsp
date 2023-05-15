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
.market-box {
	margin-top: 3px; margin-bottom: 70px;
	display: grid;
	/* auto-fill :  남는 공간(빈 트랙)을 그대로 유지, minmax : '최소, 최대 크기'를 정의 */
	/*grid-template-columns: repeat(auto-fill, minmax(180px, 3fr));*/
	/*grid-template-columns: repeat(3, 180px);
	grid-column-gap: 10px;
	grid-row-gap: 100px;*/	display: flex;
	justify-content: flex-start;
	align-items: center;
	flex-wrap: wrap;
	gap: 10px;
}
.market-box .item { cursor: pointer; max-width: 300px; max-height: 370px; }
.item > img {  width: 300px; height: 300px; object-fit: cover; cursor: pointer; border-radius : 20px; }
.border{
	background: #ffedea;
	border-radius : 10px;
	color: black;
	border: none;
	font-weight: bold;
	
}
</style>
</head>
<body>

<header>
    <jsp:include page="/WEB-INF/views/layout/header.jsp"></jsp:include>
</header>
	
<main>
	<div class="container body-container">
	    <div class="body-main mx-auto">
	    <div class="inner-page mx-auto" style="text-align: center; font-size: 30px; margin-bottom: 15px; font-weight: 900;" >
			실시간 인기 매물
	    </div>
			
			<div class="market-box">
				<c:forEach var="dto" items="${list}" begin="1" end="6" step="1"	>
					<div class="item" title="${dto.subject}"
						onclick="location.href='${articleUrl}&marketNum=${dto.marketNum}';">
						<img src="${pageContext.request.contextPath}/uploads/market/${dto.imageFilename}">
						<div class="border">
						<div style="margin-left: 10px">
						<div>제목 : ${dto.subject}</div>
						<div>거래지 : ${dto.addr}</div>
						<div>가격 : ${dto.price}원</div>
						</div>
						</div>
					</div>
				</c:forEach>
			</div>
	    </div>
	</div>
</main>

<footer>
	<jsp:include page="/WEB-INF/views/layout/footer.jsp"></jsp:include>
</footer>

<jsp:include page="/WEB-INF/views/layout/staticFooter.jsp"/></body>
</html>