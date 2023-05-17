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
	max-width: auto;
}

.grid-box {
	margin-top: 30px; margin-bottom: 50px;
	display: grid;
	/* auto-fill :  남는 공간(빈 트랙)을 그대로 유지, minmax : '최소, 최대 크기'를 정의 */
	grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
	grid-column-gap: 50px;
	grid-row-gap: 150px;
	
}
.grid-box .item { border: 1px solid #DAD9FF; height: 250px; cursor: pointer;  border-radius: 40px; }
.item > img {  width: 100%; height: 100%; cursor: pointer;  border-radius: 40px; }

.item > .desc-area > h3 {
	font-size: 17px;
	color: #333;
	text-align: center;
	font-weight: 900;
}

.item > .desc-area > .info > span {
	font-size: 15px;
	color: #777;
	font-weight: 600;
}

.item > .desc-area > .info {
	text-align: right;
}

.item > .desc-area > .info > span:first-child {
	margin-right: 10px;
}

.body-title {
    color: tomato;
    padding-top: 35px;
    padding-bottom: 7px;
    margin: 0 0 25px 0;
    border-bottom: 2px solid rgb(251, 167, 138);
}
.body-title h2 {
    font-size: 24px;
    min-width: 300px;
    font-family:"Malgun Gothic", "맑은 고딕", NanumGothic, 나눔고딕, 돋움, sans-serif;
    color:tomato;
    font-weight: 700;
    padding-bottom: 10px;
    display: inline-block;
    margin: 0 0 -7px 0;
    border-bottom: 3px solid tomato;
}

</style>

</head>
<body>

<header>
	<jsp:include page="/WEB-INF/views/layout/header.jsp"></jsp:include>
</header>



<main>
	<div class="container body-container">
	    <div class="body-title">
			<img src="${pageContext.request.contextPath}/resource/images/gallerypage.png" style="width: 200px;" >
	    </div>
	    
	    <div class="body-main mx-auto">
			<table class="table">
				<tr>
					<td width="50%">
						${dataCount}개(${page}/${total_page} 페이지)
					</td>
					
					<td align="right">
						<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/gallery/write.do';">등록</button>
					</td>
					
				</tr>
			</table>
			
			<div class="grid-box">
				<c:forEach var="dto" items="${list}" varStatus="status">
					<div class="item" title="${dto.subject}"
						onclick="location.href='${articleUrl}&num=${dto.photoNum}';">
						<img src="${pageContext.request.contextPath}/uploads/gallery/${dto.imageFilename}">
						
						<div class="desc-area">
							<div class="info">
								<span>${dto.subject}</span>
								<span><img src="${pageContext.request.contextPath}/resource/images/like2.png" style="width: 25px;">${dto.boardLikeCount}</span>
								<span><img src="${pageContext.request.contextPath}/resource/images/hitCount.jpg" style="width: 25px; margin-bottom: 5px;" >${dto.hitCount}</span>
							</div>
						</div>
					</div>
					
				</c:forEach>
			</div>
			
			<div class="page-navigation">
				${dataCount == 0 ? "등록된 게시물이 없습니다." : paging}
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