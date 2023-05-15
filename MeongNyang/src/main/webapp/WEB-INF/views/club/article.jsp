﻿<%@ page contentType="text/html; charset=UTF-8" %>
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
<link rel="stylesheet" href="https://use.fontawesome.com/releases/v6.3.0/css/all.css">

<style type="text/css">
.body-main {
	max-width: 700px;
	padding-top: 15px;
}

/* 모달대화상자 */
.ui-widget-header { /* 타이틀바 */
	background: none;
	border: none;
	border-bottom: 1px solid #ccc;
	border-radius: 0;
}
.ui-dialog .ui-dialog-title {
	padding-top: 5px; padding-bottom: 5px;
}
.ui-widget-content { /* 내용 */
   /* border: none; */
   border-color: #ccc; 
}

.table-article tr>td { padding-left: 5px; padding-right: 5px; }

.img-box {
	max-width: 700px;
	padding: 5px;
	box-sizing: border-box;
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

.photo-layout img { width: 570px; height: 450px; }

.bold { font-weight: bold;}

</style>
<script type="text/javascript" src="https://code.jquery.com/jquery-3.6.4.min.js"></script>
<script type="text/javascript">
<c:if test="${sessionScope.member.userId==dto.userId || sessionScope.member.userId=='admin'}">
	function deleteBoard() {
	    if(confirm("게시글을 삭제 하시 겠습니까 ? ")) {
		    let query = "num=${dto.clubNum}&${query}";
		    let url = "${pageContext.request.contextPath}/club/delete.do?" + query;
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
		
		f.action = "${pageContext.request.contextPath}/club/replyWrite_ok.do";
		f.submit();
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

$(function(){
	$(".showMember").click(function(){
		$(".popup-dialog").dialog({
			title:"현재 가입한 멤버"
		});
	});
});

function imageViewer(img) {
	const viewer = $(".photo-layout");
	let s="<img src='"+img+"'>";
	viewer.html(s);
	
	$(".dialog-photo").dialog({
		title:"이미지",
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
	<div class="container body-container" >
	    <div class="body-title" style="text-align: center;">
			<img src="${pageContext.request.contextPath}/resource/images/clubpage.png" style="width: 250px;">
	    </div>
	    <div style="box-shadow: 0 0 15px 0 rgb(2 59 109 / 10%);border-radius: 30px; margin: 0 auto ; width: 70%; margin-bottom: 50px;">
	    <div class="body-main mx-auto">
			<table class="table table-border table-article">
				<thead>
					<tr>
						<td colspan="2" align="center">
							<h3>${dto.subject}</h3>
						</td>
					</tr>
				</thead>
				
				<tbody>
					<tr>
						<td><span class="bold">모임명</span> : ${dto.clubName }</td>
						<td align="right">
							${dto.reg_date} | 조회수 ${dto.hitCount}
						</td>
					</tr>
					<tr>
						<td width="50%">
							<span class="bold">모임장</span> : ${dto.userName}
						</td>
						
					</tr>
					
					<tr>
						<td colspan="2" valign="top" height="200">
							${dto.content}
						</td>
					</tr>
					
					<tr>
						<td width="50%">
							<span class="bold">인원</span> : ${memberCount}/${dto.maxMember }
						</td>
						
					</tr>
					
					<tr style="border-bottom: none;">
						<td colspan="2" height="110">
							<div class="img-box">
								<c:forEach var="vo" items="${listFile}">
									<img src="${pageContext.request.contextPath}/uploads/club/${vo.imageFilename}"
										onclick="imageViewer('${pageContext.request.contextPath}/uploads/club/${vo.imageFilename}');">
								</c:forEach>
							</div>
						</td>	
					</tr>
					
					<tr>
						<td colspan="2">
							<span class="bold">이전글 : </span>
							<c:if test="${not empty preReadDto}">
								<a href="${pageContext.request.contextPath}/club/article.do?${query}&num=${preReadDto.clubNum}">${preReadDto.subject}</a>
							</c:if>
						</td>
					</tr>
					<tr>
						<td colspan="2">
							<span class="bold">다음글 : </span>
							<c:if test="${not empty nextReadDto}">
								<a href="${pageContext.request.contextPath}/club/article.do?${query}&num=${nextReadDto.clubNum}">${nextReadDto.subject}</a>
							</c:if>
						</td>
					</tr>
					
					
				
				</tbody>
			</table>
			
			<table class="table">
				<tr>
					<td width="30%">
						<c:choose>
							<c:when test="${sessionScope.member.userId==dto.userId}">
								<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/club/update.do?num=${dto.clubNum}&page=${page}';">수정</button>
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
						<c:choose>
				    		<c:when test="${ result }">
								<button type="button" class="btn showMember" >멤버보기</button>
				    		</c:when>
				    	</c:choose>
						
						<!--가입하기버튼 정원수초과하면 비활성화 -> 현재수>=정원수, 멤버일 경우 비활성화->멤버리스트가 있으면(not empty)
						  -->
						<c:choose>
				    		<c:when test="${ memberCount < dto.maxMember && !result }">
				    			<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/club/signUp.do?num=${dto.clubNum}';">가입하기</button>
				    		</c:when>
				    	</c:choose>
							<!-- 탈퇴하기 버튼 가입한멤버이면(리더빼고) 활성화 해야함 -->
							
						<c:choose>
				    		<c:when test="${ result && status != 1 }">
				    			<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/club/byebye.do?num=${dto.clubNum}';">탈퇴하기</button>
				    		</c:when>
				    	</c:choose>
				    	
					</td>
					<td align="right">
						<button type="button" class="btn" >좋아요</button>
						<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/club/list.do?${query}';">리스트</button>
					</td>
					
				</tr>
			</table>
			
				<input type="hidden" name="num" value="${dto.clubNum }">
					<!-- 멤버리스트 -->
					<div class=" popup-dialog" style="display: none;">
							<c:forEach var="list" items="${list }">
								<p><i class="fa-solid fa-user" style="color: #fd855d;"></i>${list.userName }</p>
							</c:forEach>
					</div>

	    </div>
	    </div>
	</div>
	
	
	<!-- 댓글 쓰기폼 -->
	<div class="body-container">

	<div class="reply">
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
							<!-- 댓글리스트 -->
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
					</tr>
				
						<!-- 대댓글 리스트-->
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
				            
				            <!-- 대댓글 쓰기 -->
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

<div class="dialog-photo">
      <div class="photo-layout"></div>
</div>

<jsp:include page="/WEB-INF/views/layout/staticFooter.jsp"/>
</body>
</html>