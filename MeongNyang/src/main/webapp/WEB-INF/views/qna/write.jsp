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

.table-form td { padding: 7px 0; }
.table-form p { line-height: 200%; }
.table-form tr:first-child { border-top: 2px solid #212529; }
.table-form tr > td:first-child { width: 110px; text-align: center; background: #f8f8f8; }
.table-form tr > td:nth-child(2) { padding-left: 10px; }

.table-form input[type=text], .table-form input[type=file], .table-form textarea {
	width: 96%; }
.table-form input[type=checkbox] { vertical-align: middle; }
</style>

<script type="text/javascript">
function sendOk() {
    const f = document.noticeForm;
	let str;
	
    str = f.subject.value.trim();
    if(!str) {
        alert("제목을 입력하세요. ");
        f.subject.focus();
        return;
    }

    str = f.content.value.trim();
    if(!str) {
        alert("내용을 입력하세요. ");
        f.content.focus();
        return;
    }

    f.action = "${pageContext.request.contextPath}/notice/${mode}_ok.do";
    f.submit();
}

<c:if test="${mode=='update'}">
	function deleteFile(fileNum) {
		if(! confirm("파일을 삭제 하시겠습니까 ?")) {
			return;
		}
		
		let query = "num=${dto.num}&fileNum=" + fileNum + "&page=${page}&size=${size}";
		let url = "${pageContext.request.contextPath}/notice/deleteFile.do?" + query;
		location.href = url;
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
	    <div class="body-title">
			<h2><i class="fas fa-clipboard-list"></i> 공지사항 </h2>
	    </div>
	    
	    <div class="body-main mx-auto">
			<form name="noticeForm" method="post" enctype="multipart/form-data">
				<table class="table table-border table-form">
					<tr>
						<td>제&nbsp;&nbsp;&nbsp;&nbsp;목</td>
						<td> 
							<input type="text" name="subject" maxlength="100" class="form-control" value="${dto.subject}">
						</td>
					</tr>
	
					<tr>
						<td>공지여부</td>
						<td> 
							<p><input type="checkbox" name="notice" value="1" ${dto.notice==1 ? "checked='checked' ":"" }> <label>공지</label></p>
						</td>
					</tr>
	
					<tr> 
						<td>작성자</td>
						<td> 
							<p>${sessionScope.member.userName}</p>
						</td>
					</tr>
					
					<tr> 
						<td valign="top">내&nbsp;&nbsp;&nbsp;&nbsp;용</td>
						<td> 
							<textarea name="content" class="form-control">${dto.content}</textarea>
						</td>
					</tr>
					
					<tr>
						<td>첨&nbsp;&nbsp;&nbsp;&nbsp;부</td>
						<td> 
							<input type="file" name="selectFile" class="form-control" multiple="multiple">
						</td>
					</tr>
	
					<c:if test="${mode=='update'}">
						<c:forEach var="vo" items="${listFile}">
							<tr>
								<td>첨부된파일</td>
								<td> 
									<p>
										<a href="javascript:deleteFile('${vo.fileNum}');"><i class="far fa-trash-alt"></i></a>
										${vo.originalFilename}
									</p>
								</td>
							</tr>
						</c:forEach> 
					</c:if>
				</table>
					
				<table class="table">
					<tr> 
						<td align="center">
							<button type="button" class="btn" onclick="sendOk();">${mode=='update'?'수정완료':'등록하기'}</button>
							<button type="reset" class="btn">다시입력</button>
							<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/notice/list.do?size=${size}';">${mode=='update'?'수정취소':'등록취소'}</button>
							<input type="hidden" name="size" value="${size}">
							<c:if test="${mode=='update'}">
								<input type="hidden" name="num" value="${dto.num}">
								<input type="hidden" name="page" value="${page}">
							</c:if>
						</td>
					</tr>
				</table>
			</form>
	    </div>
	</div>
</main>

<footer>
    <jsp:include page="/WEB-INF/views/layout/footer.jsp"></jsp:include>
</footer>

<jsp:include page="/WEB-INF/views/layout/staticFooter.jsp"/>
</body>
</html>