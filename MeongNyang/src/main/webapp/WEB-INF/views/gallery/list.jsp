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
	grid-column-gap: 40px;
	grid-row-gap: 60px;
	
}
.grid-box .item { border: 1px solid #DAD9FF; height: 250px; cursor: pointer;  border-radius: 40px; }
.item > img {  width: 100%; height: 100%; cursor: pointer;  border-radius: 40px; }

.item > .desc-area > h3 {
	font-size: 17px;
	color: #333;
	text-align: center;
	font-weight: 900;
}

.item > .desc-area > .info > div span {
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
	    <div class="body-title" style="text-align: center;">
	    	<a href="${pageContext.request.contextPath}/gallery/list.do">
			<img src="${pageContext.request.contextPath}/resource/images/gallerypage2.png" style="width: 280px;" >
	    	</a>
	    </div>
	    
	    <div class="body-main mx-auto" style="box-shadow: 0 0 15px 0 rgb(2 59 109 / 10%); border-radius: 30px; padding: 15px;">
			<table class="table">
				<tr>
					<td width="50%">
						${dataCount}개(${page}/${total_page} 페이지)
					</td>
					
					<!-- 
					<td align="right">
						<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/gallery/write.do';">등록</button>
					</td>
					 -->
				</tr>
			</table>
			
			<div class="grid-box">
				<c:forEach var="dto" items="${list}" varStatus="status">
					<div class="item" title="${dto.subject}"
						onclick="location.href='${articleUrl}&num=${dto.photoNum}';">
						<img src="${pageContext.request.contextPath}/uploads/gallery/${dto.imageFilename}">
						
						<div class="desc-area">
							<div class="info">
								<div style="text-align: center;">
								<span>${dto.subject}</span>
								</div> 
								<div>
								<span><img src="${pageContext.request.contextPath}/resource/images/heart.png" style="width: 23px;">${dto.boardLikeCount}</span>
								<span><img src="${pageContext.request.contextPath}/resource/images/view.png" style="width: 25px;">${dto.hitCount}</span>
								<span><img src="${pageContext.request.contextPath}/resource/images/reply.png" style="width: 23px;">${dto.replyCount}</span>
								</div>
								
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
						<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/gallery/list.do';" title="새로고침"><i class="fa-solid fa-arrow-rotate-right"></i></button>
					</td>
					<td align="center">
						<form name="searchForm" action="${pageContext.request.contextPath}/gallery/list.do" method="post">
							<select name="condition" class="form-select">
								<option value="all"      ${condition=="all"?"selected='selected'":"" }>제목+내용</option>
								<option value="userName" ${condition=="userName"?"selected='selected'":"" }>작성자</option>
								<option value="reg_date"  ${condition=="reg_date"?"selected='selected'":"" }>등록일</option>
								<option value="subject"  ${condition=="subject"?"selected='selected'":"" }>제목</option>
								<option value="content"  ${condition=="content"?"selected='selected'":"" }>내용</option>
							</select>
							<input type="text" name="keyword" value="${keyword}" class="form-control">
							<button type="button" class="btn" onclick="searchList();">검색</button>
						</form>
					</td>
					<td align="right" width="100">
						<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/gallery/write.do';">글올리기</button>
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