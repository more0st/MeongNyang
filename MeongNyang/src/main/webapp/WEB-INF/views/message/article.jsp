<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>쪽지함 :: 멍냥마켓</title>
<jsp:include page="/WEB-INF/views/layout/staticHeader.jsp"/>
<style type="text/css">
.body-main {
	max-width: 700px;
	padding-top: 15px;
}

.table-article tr>td { padding-left: 5px; padding-right: 5px; }
</style>
<script type="text/javascript">
	function deleteBoard() {
	    if(confirm("게시글을 삭제 하시 겠습니까 ? ")) {
		    let query = "num=${dto.messageNum}&category=${category}&page=${page}";
		    let url = "${pageContext.request.contextPath}/message/delete.do?" + query;
	    	location.href = url;
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
	    <div class="body-title" style="text-align: center;">
			<c:choose>
	   			<c:when test="${category == 'receive' }">
					<a href="${pageContext.request.contextPath}/message/list_receive.do">
					<img src="${pageContext.request.contextPath}/resource/images/receiveham.png" style="width: 280px;" >
	    			</a>
				</c:when>
				<c:otherwise>
					<a href="${pageContext.request.contextPath}/message/list_send.do">
					<img src="${pageContext.request.contextPath}/resource/images/sendham.png" style="width: 280px;" >
	    			</a>
				</c:otherwise>
			</c:choose>
	    </div>
	    <div style="box-shadow: 0 0 15px 0 rgb(2 59 109 / 10%);border-radius: 30px; margin: 0 auto ; width: 70%; margin-bottom: 50px;">
	    <div class="body-main mx-auto">
			<table class="table  table-article">
				<thead>
					<tr style="border-top: 2px solid #eee; border-bottom: 2px solid #eee;">
						<td colspan="2" align="center">
							<h3>${dto.subject}</h3>
						</td>
					</tr>
				</thead>
				
				<tbody>
					<tr>
						<td width="50%">
							보낸사람 : ${dto.sendName} &nbsp; | &nbsp; 받은사람 : ${dto.receiveName}
						</td>
						<td align="right">
							${dto.send_date}
						</td>
					</tr>
					
					<tr>
						<td colspan="2" valign="top" height="200">
							${dto.content}
						</td>
					</tr>
					
					
				</tbody>
			</table>
			
			<table class="table">
				<tr>
					<td width="50%">
				    	<button type="button" class="btn" onclick="deleteBoard();">삭제</button>
				    	
				    	<!-- 
						<c:choose>
				    		<c:when test="${sessionScope.member.userId==( category=='receive' ? dto.receiveId : dto.sendId ) || sessionScope.member.userId=='admin'}">
				    			<button type="button" class="btn" onclick="deleteBoard();">삭제</button>
				    		</c:when>
				    		<c:otherwise>
				    			<button type="button" class="btn" disabled="disabled">삭제</button>
				    		</c:otherwise>
				    	</c:choose>
				    	 -->
					</td>
					<td align="right">
					<c:if test="${category == 'receive'}">
						<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/message/write.do?name=${dto.sendName}';">답장</button>
					</c:if>
						<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/message/list_${category}.do?';">리스트</button>
					</td>
				</tr>
			</table>

	    </div>
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