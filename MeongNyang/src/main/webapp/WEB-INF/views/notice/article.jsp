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
			<h2><img src="${pageContext.request.contextPath}/resource/images/noticePage.png" style="width: 250px;"></h2>
	    </div>
	    <div style="box-shadow: 0 0 15px 0 rgb(2 59 109 / 10%);border-radius: 30px; margin: 0 auto ; width: 70%; margin-bottom: 50px;">
	    <div class="body-main mx-auto" >
			<table class="table table-border table-article">
				<thead>
					<tr style="border-top: 2px solid #eee; border-bottom: 2px solid #eee;">
						<td colspan="2" align="center">
							<h3>${dto.subject}</h3>
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
								<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/notice/update.do?noticeNum=${dto.noticeNum}&page=${page}&size=${size}';">수정</button>
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
	

	
	
</main>

<footer>
    <jsp:include page="/WEB-INF/views/layout/footer.jsp"></jsp:include>
</footer>

<jsp:include page="/WEB-INF/views/layout/staticFooter.jsp"/>
</body>
</html>