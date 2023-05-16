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

.reply { clear: both; padding: 20px 0 10px; }
.reply .bold { font-weight: 600; }

.reply .form-header { padding-bottom: 7px; }
.reply-form  tr>td { padding: 2px 0 2px; }
.reply-form textarea { width: 100%; height: 75px; }
.reply-form button { padding: 8px 25px; }

.reply .reply-info { padding-top: 25px; padding-bottom: 7px; }
.reply .reply-info  .reply-count { color: #3EA9CD; font-weight: 700; }

.reply .reply-list tr>td { padding: 7px 5px; }
.reply .reply-list .bold { font-weight: 600; }

.reply .deleteReply, .reply .deleteReplyAnswer { cursor: pointer; }
.reply .notifyReply { cursor: pointer; }

.reply-list .list-header { border: 1px solid #ccc; background: #f8f8f8; }
.reply-list tr>td { padding-left: 7px; padding-right: 7px; }

.reply-answer { display: none; }
.reply-answer .answer-left { float: left; width: 5%; }
.reply-answer .answer-right { float: left; width: 95%; }
.reply-answer .answer-list { border-top: 1px solid #ccc; padding: 0 10px 7px; }
.reply-answer .answer-form { clear: both; padding: 3px 10px 5px; }
.reply-answer .answer-form textarea { width: 100%; height: 75px; }
.reply-answer .answer-footer { clear: both; padding: 0 13px 10px 10px; text-align: right; }

.answer-article { clear: both; }
.answer-article .answer-article-header { clear: both; padding-top: 5px; }
.answer-article .answer-article-body { clear:both; padding: 5px 5px; border-bottom: 1px solid #ccc; }

.photo-layout img { width: 570px; height: 450px; }
</style>
<script type="text/javascript">
<c:if test="${sessionScope.member.userId==dto.sellerId || sessionScope.member.userId=='admin'}">
	function deleteBoard() {
	    if(confirm("게시글을 삭제 하시 겠습니까 ? ")) {
		    let query = "marketNum=${dto.marketNum}&${query}";
		    let url = "${pageContext.request.contextPath}/market/delete.do?" + query;
	    	location.href = url;
	    }
	}
</c:if>

function modal() {
	const viewer = $(".buymodal");
	let userId = "${dto.sellerId}";
	let sessionId = "${sessionScope.member.userId}";
	let s;
	if(userId === sessionId){
		s="<button class='btn'>판매확정</button> <button class='btn'>취소</button>";
	}else{
		s="<button>결재하기</button>";
	}
	viewer.html(s);
	
	$(".dialog-modal").dialog({
		title: "결재",
		modal: true
	});
};

function imageViewer(img) {
	const viewer = $(".photo-layout");
	let s="<img src='"+img+"'>";
	viewer.html(s);
	
	$(".dialog-photo").dialog({
		title:"판매물품",
		width: 600,
		height: 530,
		modal: true
	});
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
					<tr>
						<td width="50%">
							가격 : ${dto.price}
						</td>
						<td align="right">
							거래지 : ${dto.addr}
						</td>
					</tr>
				</thead>
				
				<tbody>
					<tr>
						<td width="50%">
							이름 : ${dto.sellerId}
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
					
					<tr style="border-bottom: none;">
						<td colspan="2" height="110">
							<div class="img-box">
								<c:forEach var="vo" items="${listFile}">
									<img src="${pageContext.request.contextPath}/uploads/market/${vo.imageFilename}" onclick="imageViewer('${pageContext.request.contextPath}/uploads/market/${vo.imageFilename}');">
								</c:forEach>
							</div>
						</td>	
					</tr>
					<tr>
						<td colspan="2">
							이전글 :
							<c:if test="${not empty preReadDto}">
								<a href="${pageContext.request.contextPath}/market/article.do?page=${page}&marketNum=${preReadDto.marketNum}">${preReadDto.subject}</a>
							</c:if>
						</td>
					</tr>
					<tr>
						<td colspan="2">
							다음글 :
							<c:if test="${not empty nextReadDto}">
								<a href="${pageContext.request.contextPath}/market/article.do?page=${page}&marketNum=${nextReadDto.marketNum}">${nextReadDto.subject}</a>
							</c:if>
						</td>
					</tr>
				</tbody>
			</table>
			
			<table class="table">
				<tr>
					<td width="50%">
						<c:choose>
							<c:when test="${sessionScope.member.userId==dto.sellerId}">
								<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/market/update.do?marketNum=${dto.marketNum}&page=${page}';">수정</button>
							</c:when>
							<c:otherwise>
								<button type="button" class="btn" disabled="disabled">수정</button>
							</c:otherwise>
						</c:choose>
				    	
						<c:choose>
				    		<c:when test="${sessionScope.member.userId==dto.sellerId || sessionScope.member.userId=='admin'}">
				    			<button type="button" class="btn" onclick="deleteBoard();">삭제</button>
				    		</c:when>
				    		<c:otherwise>
				    			<button type="button" class="btn" disabled="disabled">삭제</button>
				    		</c:otherwise>
				    	</c:choose>
					</td>
					<td align="right">
					<c:choose>
						<c:when test="${sessionScope.member.userId==dto.sellerId}">
							<button type="button" class="btn" onclick="modal();">판매시작</button>
						</c:when>
						<c:otherwise>
							<button type="button" class="btn" onclick="modal();">카드결재</button>
						</c:otherwise>
					</c:choose>	
						<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/market/list.do?page=${page}';">리스트</button>
					</td>
				</tr>
			</table>
			
			<div class="reply">
				<form name="replyForm" method="post">
					<div class='form-header'>
						<span class="bold">댓글쓰기</span><span> - 타인을 비방하거나 개인정보를 유출하는 글의 게시를 삼가해 주세요.</span>
					</div>
					
					<table class="table reply-form">
						<tr>
							<td>
								<textarea class='form-control' name="content"></textarea>
							</td>
						</tr>
						<tr>
						   <td align='right'>
								<button type='button' class='btn btnSendReply'>댓글 등록</button>
							</td>
						 </tr>
					</table>
				</form>
				
				<div id="listReply"></div>
			</div>

	    </div>
	    </div>
	</div>
</main>

<footer>
    <jsp:include page="/WEB-INF/views/layout/footer.jsp"></jsp:include>
</footer>

<div class="dialog-photo">
      <div class="photo-layout"></div>
</div>

<div class="dialog-modal">
      <div class="buymodal"></div>
</div>

<jsp:include page="/WEB-INF/views/layout/staticFooter.jsp"/>
</body>
</html>