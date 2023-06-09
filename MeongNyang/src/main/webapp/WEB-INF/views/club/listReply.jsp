<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:if test="${not empty listReply }">
	<div class='reply-info'>
		<span class='reply-count'>댓글 ${replyCount }개</span>
		<span>[목록 ${pageNo }/${total_page} 페이지]</span>
	</div>
</c:if>

<table class='table reply-list'>
	<c:forEach var="vo" items="${listReply }">
		<tr class='list-header'>
			<td width='50%'>
				<span class='bold'>${vo.userName }</span>
			</td>
			<td width='50%' align='right'>
				<span>${vo.reg_date }</span> |
				
				<c:choose>
					<c:when test="${sessionScope.member.userId == 'admin' || sessionScope.member.userId == vo.userId }">
						<span class='deleteReply' data-replyNum='${vo.replyNum }' data-pageNo='${pageNo}'>삭제</span>
					</c:when>
					<c:otherwise>
						<span class="notifyReply">신고</span>
					</c:otherwise>
				</c:choose>
				
			</td>
		</tr>
		<tr>
			<td colspan='2' valign='top'>${vo.content }</td>
		</tr>

		<tr>
			<td>
				<button type='button' class='btn btnReplyAnswerLayout' data-replyNum='${vo.replyNum }'>답글 <span id="answerCount${vo.replyNum }">${vo.answerCount }</span></button>
			</td>
		</tr>
	
	    <tr class='reply-answer'>
	        <td colspan='2'>
	            <div id='listReplyAnswer${vo.replyNum }' class='answer-list'></div>
	            <div class="answer-form">
	                <div class='answer-left'>└</div>
	                <div class='answer-right'><textarea class='form-control'></textarea></div>
	            </div>
	             <div class='answer-footer'>
	                <button type='button' class='btn btnSendReplyAnswer' data-replyNum='${vo.replyNum }'>답글 등록</button>
	            </div>
			</td>
	    </tr>
	</c:forEach>
</table>

<div class="page-navigation">
	${paging }
</div>			
