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

.img-box{
	width:80%;
	height: auto;
}

.img-box img{
	max-width:100%;
	display: block;
}

.table-article tr>td { padding-left: 5px; padding-right: 5px; }
</style>
<script type="text/javascript">
<c:if test="${sessionScope.member.userId==dto.userId || sessionScope.member.userId=='admin'}">
	function deleteBoard() {
	    if(confirm("게시글을 삭제 하시 겠습니까 ? ")) {
		    let query = "num=${dto.photoNum}&page=${page}";
		    let url = "${pageContext.request.contextPath}/gallery/delete.do?" + query;
	    	location.href = url;
	    }
	}
</c:if>

//해당되는 객체가 숨겨져있는지 아닌지 확인하는 함수
const isHidden = ele => {
	const styles = window.getComputedStyle(ele); //인자로 넘겨 받은 요소의 모든 css 속성 값을 담은 객체 반환
	return styles.display === 'none' || styles.visibility === 'hidden'; //숨겨져있는지 아닌지 확인
	
};


function ajaxFun(url, method, query, dataType, fn) {
	$.ajax({
		type:method,	// 메소드(get, post, put, delete)
		url:url,		// 요청받을 서버주소 
		data:query,		// 서버에 전송할 파라미터 
		dataType:dataType,	// 서버에서 응답할 형식(json, xml, text)
		success:function(data){
			fn(data);
		},
		beforeSend:function(jqXHR){
			jqXHR.setRequestHeader("AJAX", true);	// 사용자 정의 헤더
		},
		error:function(jqXHR){
			if(jqXHR.status === 403) {
				login();
				return false;
			} else if(jqXHR.status === 400) {
				alert("요청 처리가 실패했습니다.");
				return false;
			}
			console.log(jqXHR.responseText);
		}
	});
}

//게시글 공감 여부 
$(function() {
	$(".btnSendBoardLike").click(function() {
		const $i = $(this).find("i");
		let isNoLike = $i.css("color") == "rgb(0, 0, 0)";
		let msg = isNoLike ? "게시글에 공감하십니까 ?" : "게시글 공감을 취소하시겠습니까 ?"; 
		
		if(! confirm(msg)) {
			return false; 
		}
		
		let url = "${pageContext.request.contextPath}/gallery/insertBoardLike.do";
		let num = "${dto.photoNum}"; 
		let qs = "num=" + num + "&isNoLike=" + isNoLike;
		
		const fn = function(data) {
			let state = data.state;
			if(state === "true") {
				let color = "black";
				if( isNoLike) {
					color = "blue";
				}
				$i.css("color", color);
				
				let count = data.boardLikeCount;
				$("#boardLikeCount").text(count);
				
			} else if (state === "liked") {
				alert("좋아요는 한번만 가능합니다.");
			}
		};
		
		ajaxFun(url, "post", qs, "json", fn);
		
	});
});


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




/*
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
*/

</script>

</head>
<body>

<header>
	<jsp:include page="/WEB-INF/views/layout/header.jsp"></jsp:include>
</header>
	
<main>
	<div class="container body-container">
	    <div class="body-title">
			<h2> 멍냥갤러리 </h2>
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
							이름 : ${dto.userName}
						</td>
						<td align="right" style="border-bottom: 2px;">
							${dto.reg_date} | 조회 ${dto.hitCount}
						</td>
					</tr>
					
					<tr style="border-bottom: none;">
						<td colspan="2" style="padding-bottom: 0;">
							<div class="img-box">
								<c:forEach var="vo" items="${listFile}">
								<div>
									<img src="${pageContext.request.contextPath}/uploads/gallery/${vo.imageFilename}">
								</div>
								</c:forEach>
							</div>
						</td>
					</tr>
					
					<tr>
						<td colspan="2" valign="top" style="margin-bottom:100px;">
							${dto.content}
						</td>
					</tr>
					
					<tr>
						<td colspan="2" align="center" style="border-bottom: 20px;">
							<button type="button" class="btn btnSendBoardLike" title="좋아요"><i class="fas fa-thumbs-up" style="color:${isUserLike?'blue':'black'}"></i>&nbsp;&nbsp;<span id="boardLikeCount">${dto.boardLikeCount}</span></button>
						</td>
					</tr>
					
					<tr>
						<td colspan="2">
							이전글 :
							<c:if test="${not empty preReadDto}">
								<a href="${pageContext.request.contextPath}/gallery/article.do?${query}&num=${preReadDto.photoNum}">${preReadDto.subject}</a>
							</c:if>
						</td>
					</tr>
					<tr>
						<td colspan="2">
							다음글 :
							<c:if test="${not empty nextReadDto}">
								<a href="${pageContext.request.contextPath}/gallery/article.do?${query}&num=${nextReadDto.photoNum}">${nextReadDto.subject}</a>
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
								<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/gallery/update.do?num=${dto.photoNum}&page=${page}';">수정</button>
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
						<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/gallery/list.do?${query}';">리스트</button>
					</td>
				</tr>
			</table>

	    </div>
	    </div>
	</div>
	
	
	<!-- 댓글 폼 -->
	<div class="body-container" >

	<div class="reply" >
		<form name="replyForm" method="post">
			<div class='form-header'>
				<span class="bold">댓글쓰기</span><span> - 타인을 비방하거나 개인정보를 유출하는 글의 게시를 삼가해 주세요.</span>
			</div>
			
			<table class="table reply-form">
				<tr>
					<td>
						<textarea class='form-control' name="content" style="height: 120px;"></textarea>
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
				<span>[목록, 1/3 페이지]</span>
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
						<td colspan='2' valign='top'>내용입니다.</td>
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