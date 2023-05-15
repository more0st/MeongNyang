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


.table-border thead > tr { border-top: 1px solid #eee; border-bottom: 1px solid #eee; }


/* 댓글 */
.reply {
	clear: both; padding: 20px 0 10px;
}
.reply .bold {
	font-weight: 900;
}

.reply .form-header {
	padding-bottom: 7px;
}
.reply-form tr>td {
	padding: 2px 0 2px;
}
.reply-form textarea {
	width: 100%; height: 75px; resize: none;
	border-radius: 30px;
}
.reply-form button {
	padding: 8px 25px;
}

.reply .reply-info {
	padding-top: 25px; padding-bottom: 7px;
}
.reply .reply-info  .reply-count {
	color: tomato; font-weight: 900;
}

.reply .reply-list tr>td {
	padding: 7px 5px;
}
.reply .reply-list .bold {
	font-weight: 600;
}

.reply .deleteReply, .reply .deleteReplyAnswer {
	cursor: pointer;
}
.reply .notifyReply {
	cursor: pointer;
}

.reply-list .list-header {
	border: 1px solid white; background: #ffedea;
	border-radius: 30px;
	
}
.reply-list tr>td {
	padding-left: 7px; padding-right: 7px;
}

.reply-answer {
	display: none;
}
.reply-answer .answer-left {
	float: left; width: 5%;
}
.reply-answer .answer-right {
	float: left; width: 95%;
	border-radius: 30px;
}
.reply-answer .answer-list {
	border-top: 1px solid #cccccc; padding: 0 10px 7px;
}
.reply-answer .answer-form {
	clear: both; padding: 3px 10px 5px;
}
.reply-answer .answer-form textarea {
	width: 100%; height: 75px; resize: none;
	border-radius: 30px;
}
.reply-answer .answer-footer {
	clear: both; padding: 0 13px 10px 10px; text-align: right;
}

.answer-article {
	clear: both;
}
.answer-article .answer-article-header {
	clear: both; padding-top: 5px;
}
.answer-article .answer-article-body {
	clear:both; padding: 5px 5px; border-bottom: 1px solid #cccccc;
}



</style>
<script type="text/javascript">
<c:if test="${sessionScope.member.userId==dto.userId || sessionScope.member.userId=='admin'}">
	function deleteBoard() {
	    if(confirm("게시글을 삭제 하시 겠습니까 ? ")) {
		    let query = "noticeNum=${dto.noticeNum}&${query}";
		    let url = "${pageContext.request.contextPath}/notice/delete.do?" + query;
	    	location.href = url;
	    }
	}
</c:if>

//해당되는 객체가 숨겨져있는지 아닌지 확인하는 함수
const isHidden = ele => {
	const styles = window.getComputedStyle(ele); //인자로 넘겨 받은 요소의 모든 css 속성 값을 담은 객체 반환
	return styles.display === 'none' || styles.visibility === 'hidden'; //숨겨져있는지 아닌지 확인
	
};

//댓글 등록
window.addEventListener('load',()=>{
	const btnEL = document.querySelector('.btnSendReply');
	
	btnEL.addEventListener('click', e =>{
		const El = e.target.closest('table');
		let content = El.querySelector('textarea').value.trim();
		if(!content) {
			alert('내용을입력하세요');
			El.querySelector('textarea').focus();
			return;
		}
		
		content = encodeURIComponent(content);
		alert('등록할 댓글 :' + content);
	});
	
});

//댓글 삭제
window.addEventListener('load',() => {
	const listReplyEL = document.querySelector('#listReply');	

	listReplyEL.addEventListener('click',e =>{
		if(e.target.matches('.deleteReply')){
			
			if(! confirm('게시글을 삭제하시겠습니까 ? ')){
				return;
			}
			let pageNo = e.target.getAttribute('data-pageNo');
			let replyNum = e.target.getAttribute('data-replyNum');
			
			alert('삭제할 댓글번호 :'+replyNum+",페이지번호:"+pageNo);
		}
	
	});
});



//답글 버튼 : 댓글별 답글 등록폼 및 답글 리스트 표시/숨김
window.addEventListener('load',() => {
	const listReplyEL = document.querySelector('#listReply');	
	
	listReplyEL.addEventListener('click',e =>{
		if(e.target.matches('.btnReplyAnswerLayout')||e.target.parentElement.matches('.btnReplyAnswerLayout')){
			let $El = e.target.closest('tr').nextElementSibling;//다음형제
			
			//$El.classList.toggle('reply-answer'); //tr태그라 화면이 이상하게나옴
			//$El.style.display = 'block'; //tr태그엔 block속성 사용안됨, 화면이 이상하게 나옴
			
			if(isHidden($El)){//숨겨져있으면 table-row, 아니면 none
				$El.style.display = 'table-row'; //tr태그에 보이게할수있는 속성은 table-row!!!
			}else{
				$El.style.display = 'none';
			}
		}
	});
});

//답글 등록 버튼

window.addEventListener('load',() => {
	const listReplyEL = document.querySelector('#listReply');	
	
	listReplyEL.addEventListener('click',e =>{
		if(e.target.matches('.btnSendReplyAnswer')){
			let replyNum = e.target.getAttribute('data-replyNum');
			
			let El = e.target.closest('td');
			let content = El.querySelector('textarea').value.trim();
			if( ! content){
				alert('내용을 입력해주세요');
				El.querySelector('textarea').focus();
				return;
			}
			
			content = encodeURIComponent(content);//서버로 데이터 보내는것
			
			alert('댓글번호 : '+ replyNum+', 등록할 답글 : '+content);
			
			
		}
		
	});
});


</script>

</head>
<body>

<header>
	<jsp:include page="/WEB-INF/views/layout/header.jsp"></jsp:include>
</header>
	
<main>
	<div class="container body-container">
	    <div class="body-title">
			<h2><img src="${pageContext.request.contextPath}/resource/images/noticepage.png" style="width: 250px;"></h2>
	    </div>
	    <div style="box-shadow: 0 0 15px 0 rgb(2 59 109 / 10%);border-radius: 30px; margin: 0 auto ; width: 70%; margin-bottom: 50px;">
	    <div class="body-main mx-auto" >
			<table class="table table-border table-article">
				<thead>
					<tr>
						<td colspan="2" align="left" style="font-weight: 900; color: tomato; font-size: 20px;">
							${dto.subject}
						</td>
					</tr>
				</thead>
				
				<tbody>
					<tr style="color : gray; font-size: 12px;">
						<td width="50%">
							이름 : ${dto.userName}
						</td>
						<td align="right">
							${dto.reg_date} | 조회 ${dto.hitCount}
						</td>
					</tr>
					
					<tr>
						<td colspan="2" valign="top" height="200" style="font-size: 13px;">
							${dto.content}
						</td>
					</tr>
					
					<tr>
						<td colspan="2">
							이전글 :
							<c:if test="${not empty preReadDTO}">
								<a href="${pageContext.request.contextPath}/notice/article.do?${query}&noticeNum=${preReadDTO.noticeNum}">${preReadDTO.subject}</a>
							</c:if>
						</td>
					</tr>
					<tr>
						<td colspan="2">
							다음글 :
							<c:if test="${not empty nextReadDTO}">
								<a href="${pageContext.request.contextPath}/notice/article.do?${query}&noticeNum=${nextReadDTO.noticeNum}">${nextReadDTO.subject}</a>
							</c:if>
						</td>
					</tr>
				</tbody>
			</table>
			
			<table class="table">
				<tr>
					<td width="50%">
						<c:choose>
							<c:when test="${sessionScope.member.userId==dto.userId}">
								<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/notice/update.do?noticeNum=${dto.noticeNum}&page=${page}';">수정</button>
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
						<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/notice/list.do?${query}';">리스트</button>
					</td>
				</tr>
			</table>

	    </div>
	    </div>
	</div>
	
	
	<!-- 댓글 폼 -->
	<div class="body-container">

	<div class="reply">
		<form name="replyForm" method="post">
			<div class='form-header'>
				<span class="bold">댓글쓰기</span><span> - 타인을 비방하거나 개인정보를 유출하는 글의 게시를 삼가해 주세요.</span>
			</div>
			
			<table class="table reply-form" >
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
		
		<div id="listReply">
		
			<div class='reply-info'>
				<span class='reply-count'>댓글 15개</span>
				<span style="font-size: 12px;">[목록, 1/3 페이지]</span>
			</div>
			
			<table class='table reply-list'>
			
					<tr class='list-header'>
						<td width='50%'>
							<span class='bold'>홍길동</span>
						</td>
						<td width='50%' align='right'>
							<span>2021-11-01</span> |
							<span class='deleteReply' data-replyNum='10' data-pageNo='1'>삭제</span>
						</td>
					</tr>
					<tr>
						<td colspan='2' valign='top'>댓글입니다댓글입니다<br>댓글입니다댓글입니다</td>
					</tr>
			
					<tr>
						<td>
							<button type='button' class='btn btnReplyAnswerLayout' data-replyNum='10'>답글 <span id="answerCount10">3</span></button>
						</td>
						<td align='right'>
							<button type='button' class='btn btnSendReplyLike' data-replyNum='10' data-replyLike='1' title="좋아요">좋아요 <span>3</span></button>
							<button type='button' class='btn btnSendReplyLike' data-replyNum='10' data-replyLike='0' title="싫어요">싫어요 <span>1</span></button>	        
						</td>
					</tr>
				
				    <tr class='reply-answer'>
				        <td colspan='2'>
				            <div id='Answer10' class='answer-list'>
				            
								<div class='answer-article'>
									<div class='answer-article-header'>
										<div class='answer-left'>└</div>
										<div class='answer-right'>
											<div style='float: left;'><span class='bold'>스프링</span></div>
											<div style='float: right;'>
												<span>2021-11-01</span> |
												<span class='deleteReplyAnswer' data-replyNum='10' data-answer='15'>삭제</span>
											</div>
										</div>
									</div>
									<div class='answer-article-body'>
										답글입니다.
									</div>
								</div>
												            
				            </div>
				            <div class="answer-form">
				                <div class='answer-left'>└</div>
				                <div class='answer-right'><textarea class='form-control' ></textarea></div>
				            </div>
				             <div class='answer-footer'>
				                <button type='button' class='btn btnSendReplyAnswer' data-replyNum='10'>답글 등록</button>
				            </div>
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