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

img-box {
	max-width: 700px;
	padding: 10px;
	box-sizing: border-box;
	display: flex;
	text-align : center;
	justify-content:center;
	flex-direction: row;
	flex-wrap: nowrap;
	overflow-x: auto;
}

.img-box img {
	width: 400px; height: 400px;
	display: block;
	margin: auto;
}



</style>
<script type="text/javascript">
<c:if test="${sessionScope.member.userId==dto.userId || sessionScope.member.userId=='admin'}">
	function deleteBoard() {
	    if(confirm("게시글을 삭제 하시 겠습니까 ? ")) {
		    let query = "eNum=${dto.eNum}&${query}";
		    let url = "${pageContext.request.contextPath}/event/delete.do?" + query;
	    	location.href = url;
	    }
	}
</c:if>

function passEvent(){
	if(confirm("추첨을 진행하시겠습니까 ? (추첨은 단 한번만 가능하며, 당첨자 변경이 불가합니다.)")){
		let url="${pageContext.request.contextPath}/event/passEvent.do?eNum=${dto.eNum}&passCount=${dto.passCount}";
		location.href=url;
	}
}


//해당되는 객체가 숨겨져있는지 아닌지 확인하는 함수
const isHidden = ele => {
	const styles = window.getComputedStyle(ele); //인자로 넘겨 받은 요소의 모든 css 속성 값을 담은 객체 반환
	return styles.display === 'none' || styles.visibility === 'hidden'; //숨겨져있는지 아닌지 확인
	
};



</script>

</head>
<body>

<header>
	<jsp:include page="/WEB-INF/views/layout/header.jsp"></jsp:include>
</header>
	
<main>
	<div class="container body-container">
	    <div class="body-title">
			<h2> 글보기 </h2>
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
				</thead>
				
				<tbody>
					<tr>
						<td width="50%">
							기간 : ${dto.start_date} ~ ${dto.end_date}
						</td>
						<td align="right">
							추첨인원 : ${dto.passCount}
						</td>
					</tr>
					<tr>
						<td colspan="2" valign="top" style="min-height: 50px;">
							${dto.content}
						</td>
					</tr>
					<tr>
						<td colspan="2" height="110">
							<div class="img-box">
								<c:forEach var="file" items="${listFile}">
									<img src="${pageContext.request.contextPath}/uploads/event/${file.imageFileName}">
								</c:forEach>
							</div>
						</td>
					</tr>
					<tr>
						<td colspan="2">
							이전글 :
							<c:if test="${not empty preReadDTO}">
								<a href="${pageContext.request.contextPath}/event/article.do?${query}&eNum=${preReadDTO.eNum}">${preReadDTO.subject}</a>
							</c:if>
						</td>
					</tr>
					<tr>
						<td colspan="2">
							다음글 :
							<c:if test="${not empty nextReadDTO}">
								<a href="${pageContext.request.contextPath}/event/article.do?${query}&eNum=${nextReadDTO.eNum}">${nextReadDTO.subject}</a>
							</c:if>
						</td>
					</tr>
				</tbody>
			</table>
			
			<table class="table">
				<tr>
					<td width="50%">
						<c:choose>
							<c:when test="${sessionScope.member.userId=='admin'}">
								<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/event/update.do?eNum=${dto.eNum}&page=${page}';">수정</button>
							</c:when>
							<c:otherwise>
								<button type="button" class="btn" disabled="disabled">수정</button>
							</c:otherwise>
						</c:choose>
				    	
						<c:choose>
				    		<c:when test="${sessionScope.member.userId==dto.userId || sessionScope.member.userId=='admin'}">
				    			<button type="button" class="btn" onclick="deleteBoard();">삭제</button>
				    		</c:when>
				    		<c:otherwise>
				    			<button type="button" class="btn" disabled="disabled">삭제</button>
				    		</c:otherwise>
				    	</c:choose>
					</td>
					<td align="right">
						<button type="button" class="btn" onclick="passEvent();">추첨</button>
						<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/event/list.do?${query}';">리스트</button>
					</td>
				</tr>
			</table>
					<input type="hidden" name="eNum" value="${dto.eNum}">
					<input type="hidden" name="eventStatus" value="${eventStatus}">
					<input type="hidden" name="passCount" value="${dto.passCount}">
					

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