<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>1:1문의::멍냥마켓</title>
<jsp:include page="/WEB-INF/views/layout/staticHeader.jsp"/>
<style type="text/css">
.body-main {
	max-width: 700px;
}

.table-article tr > td { padding-left: 5px; padding-right: 5px; }
</style>

<script type="text/javascript">
<c:if test="${sessionScope.member.userId==dto.userId || sessionScope.member.userId=='admin'}">
	function deleteBoard() {
	    if(confirm("게시글을 삭제 하시 겠습니까 ? ")) {
	        let query = "qesNum=${dto.qesNum}&${query}";
	        let url = "${pageContext.request.contextPath}/qna/delete.do?" + query;
	    	location.href = url;
	    }
	}
</c:if>
</script>
</head>
<body>

<header>
	<jsp:include page="/WEB-INF/views/layout/header.jsp"></jsp:include>
</header>
	
<main>
	<div class="container body-container">
	    <div class="body-title" style="text-align: center;">
			<img src="${pageContext.request.contextPath}/resource/images/questionPage.png" style="width: 250px;">
	    </div>
	    
	    <div class="body-main mx-auto">
			<table class="table table-border table-article">
				<thead>
					<tr style="border-top: 2px solid #eee; border-bottom: 2px solid #eee;">
						<td colspan="2" align="center">
							<h3> ${dto.subject} </h3> 
						</td>
					</tr>
				</thead>
				
				<tbody>
					<tr>
						<td width="50%">
							이름 : ${dto.userName}
						</td>
						<td align="right">
							 ${dto.reg_date}
						</td>
					</tr>
					
					<tr>
						<td colspan="2" valign="top" height="200">
							${dto.content}
						</td>
					</tr>
					
					
				</tbody>
			</table>
			
    	<c:if test="${empty dto.replyContent}">
			<table class="table">
				<tr >
					<td width="50%">
						<c:choose>
							<c:when test="${sessionScope.member.userId=='admin'}">
						<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/qna/writeReply.do?qesNum=${dto.qesNum}&page=${page}';">답변</button>
							</c:when>
							<c:otherwise>
								<button type="button" class="btn" style="display: none;" >답변</button>
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
						<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/qna/list.do?${query}';">리스트</button>
					</td>
				</tr>
			</table>
		</c:if>
	    </div>
	    
	    
	    <%-- 답변 --%>
			
			
		<c:if test="${not empty dto.replyContent}">
	    <div class="body-main mx-auto">
			<table class="table table-border table-article" style="border-bottom: 1px solid; ">
				<thead>
					<tr style="border-top: 2px solid #eee; border-bottom: 2px solid #eee;">
						<td colspan="2" align="center">
						<h3>'--${dto.subject}--'에 대한 답변입니다! </h3>	
						</td>
					</tr>
				</thead>
				
				<tbody>
					<tr>
						<td width="50%">
							이름 : 관리자
						</td>
						<td align="right">
							답변시간 : ${dto.replyReg_date}
						</td>
					</tr>
					
					<tr>
						<td colspan="2" valign="top" height="200">
							${dto.replyContent}
						</td>
					</tr>
					
				</tbody>
			</table>
			
			<c:if test="${not empty dto.replyContent}">
			<table class="table">
				<tr>
					<td width="50%">
						<c:choose>
							<c:when test="${sessionScope.member.userId=='admin'}">
						<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/qna/writeReply.do?qesNum=${dto.qesNum}&page=${page}';">답변</button>
							</c:when>
							<c:otherwise>
								<button type="button" class="btn" style="display: none;" >답변</button>
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
						<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/qna/list.do?${query}';">리스트</button>
					</td>
				</tr>
			</table>
		</c:if>
			
			
	    </div>
		</c:if>
	    
	</div>
</main>

<footer>
    <jsp:include page="/WEB-INF/views/layout/footer.jsp"></jsp:include>
</footer>

<jsp:include page="/WEB-INF/views/layout/staticFooter.jsp"/>
</body>
</html>