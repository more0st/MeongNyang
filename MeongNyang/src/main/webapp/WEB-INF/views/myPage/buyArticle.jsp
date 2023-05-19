<!-- 안쓰는 파일 -->

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
	padding-top: 15px;
}

.table-article tr>td { padding-left: 5px; padding-right: 5px; }

.img-box {
	max-width: 700px;
	padding: 5px;
	box-sizing: border-box;
	border: 1px solid #ccc;
	display: flex; /* 자손요소를 flexbox로 변경 */
	flex-direction: row; /* 정방향 수평나열 */
	flex-wrap: nowrap;
	overflow-x: auto;
}
.img-box img {
	width: 100px; height: 100px;
	margin-right: 5px;
	flex: 0 0 auto;
	cursor: pointer;
}

</style>
<script type="text/javascript">


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
	    <div style="box-shadow: 0 0 15px 0 rgb(2 59 109 / 10%);border-radius: 30px; margin: 0 auto ; width: 70%; margin-bottom: 50px;">
	    <div class="body-main mx-auto">
			<table class="table table-border table-article">
				<thead>
					<tr>
						<td colspan="2" align="center">
							${dto.subject}
						</td>
					</tr>
					<tr>
						<td colspan="2" align="center">
							${dto.price}
						</td>
					</tr>
				</thead>
				
				<tbody>
					<tr>
						<td width="50%">
							판매자 : ${dto.sellerid}
						</td>
						<td align="right">
							${dto.reg_date} | 조회 ${dto.hitCount}
						</td>
					</tr>
					
					<tr>
						<td colspan="2" valign="top" height="200">
							${dto.content}
						</td>
					</tr>

					<tr>
						<td colspan="2" valign="top" height="200">
							판매날짜 : ${dto.pay_date}
						</td>
					</tr>
					
					<tr style="border-bottom: none;">
						<td colspan="2" height="110">
							<div class="img-box">
								<c:forEach var="vo" items="${listFile}">
									<img src="${pageContext.request.contextPath}/uploads/market/${vo.imageFilename}"
										onclick="imageViewer('${pageContext.request.contextPath}/uploads/market/${vo.imageFilename}');">
								</c:forEach>
							</div>
						</td>	
					</tr>
					<tr>
						<td colspan="2">
							이전구매글 :
							<c:if test="${not empty preReadDto}">
								<a href="${pageContext.request.contextPath}/myPage/buyArticle.do?page=${page}&marketnum=${preReadDto.marketnum}">${preReadDto.subject}</a>
							</c:if>
						</td>
					</tr>
					<tr>
						<td colspan="2">
							다음구매글 :
							<c:if test="${not empty nextReadDto}">
								<a href="${pageContext.request.contextPath}/myPage/buyArticle.do?page=${page}&marketnum=${nextReadDto.marketnum}">${nextReadDto.subject}</a>
							</c:if>
						</td>
					</tr>
				</tbody>
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