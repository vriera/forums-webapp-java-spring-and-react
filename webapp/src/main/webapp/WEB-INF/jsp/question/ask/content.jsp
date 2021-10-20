<%----%><!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

	<title><spring:message code="question.contentTitle"/></title>


	<!-- Icons -->
	<%--    <link href="/assets/vendor/nucleo/css/nucleo-icons.css" rel="stylesheet">--%>
	<link href="https://use.fontawesome.com/releases/v5.0.6/css/all.css" rel="stylesheet">

	<!-- BLK• CSS -->
	<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta3/dist/css/bootstrap.min.css" rel="stylesheet"
		  integrity="sha384-eOJMYsd53ii+scO/bJGFsiCZc+5NDVN2yr8+0RDqr0Ql0h+rP48ckxlpbzKgwra6" crossorigin="anonymous">
	<link type="text/css" href="<c:url value="/resources/styles/argon-design-system.css"/>" rel="stylesheet">
	<link type="text/css" href="<c:url value="/resources/styles/general.css"/>" rel="stylesheet">
	<link type="text/css" href="<c:url value="/resources/styles/stepper.css"/>" rel="stylesheet">
	<link rel="icon" href="<c:url value="/resources/images/favicon.ico"/>">

	<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta3/dist/js/bootstrap.bundle.min.js"
			integrity="sha384-JEW9xMcG8R+pH31jmWH6WWP0WintQrMb4s7ZOdauHnUtxwoG2vI5DkLtS3qm9Ekf"
			crossorigin="anonymous"></script>

</head>
<body>

<c:choose>
	<c:when test="${is_user_present == true}">
		<jsp:include page="/WEB-INF/jsp/components/navbarLogged.jsp">
			<jsp:param name="user_name" value="${user.getUsername()}"/>
			<jsp:param name="user_email" value="${user.getEmail()}"/>
			<jsp:param name="user_notifications" value="${notifications.getTotal()}"/>
		</jsp:include>
	</c:when>
	<c:otherwise>
		<jsp:include page="/WEB-INF/jsp/components/navbar.jsp"/>
	</c:otherwise>
</c:choose>

<div class="wrapper">
	<div class="section section-hero section-shaped">
		<div class="shape shape-style-1 shape-default shape-skew viewheight-90">
			<span class="span-150"></span>
			<span class="span-50"></span>
			<span class="span-50"></span>
			<span class="span-75"></span>
			<span class="span-100"></span>
			<span class="span-75"></span>
			<span class="span-50"></span>
			<span class="span-100"></span>
			<span class="span-50"></span>
			<span class="span-100"></span>
		</div>
		<%--Tarjeta--%>
		<div class="container">
			<div class="white-pill">
				<div class="d-flex justify-content-center">
					<div class="h1 text-primary"><spring:message code="question.chooseContent"/></div>
				</div>
				<hr>
				<div class="p"><spring:message code="question.contentCallToAction" arguments="${community.name}"/></div>
				<c:url value="/question/ask/content" var="postPath"/>
				<form:form modelAttribute="questionForm" action="${postPath}" method="post" enctype="multipart/form-data">
					<%--Título--%>
					<div class="form-group mt-3">
						<form:label path="title"  class="text-black"><spring:message code="title"/></form:label>
						<spring:message code="placeholder.question.title" var="placeholderTitle"/>
						<form:input path="title" class="form-control" placeholder="${placeholderTitle}" id="title"/>
						<form:errors path="title" cssClass="error" element="p"/>
					</div>
					<%--Foro--%>
					<div class="form-group invisible position-absolute">
						<form:label path="forum" for="forum"><spring:message code="forum"/></form:label>
						<form:select  path="forum" class="form-control" id="forum">
							<c:forEach items="${forumList}" var="forum">
								<form:option value="${forum.id}"><c:out value="${forum.name}"/></form:option>
							</c:forEach>
						</form:select>
					</div>
					<%--Cuerpo--%>
					<div class="form-group">
						<form:label path="body"><spring:message code="body"/></form:label>
						<spring:message code="placeholder.question.body" var="bodyPlaceholder"/>
						<form:textarea path="body" class="form-control" id="body" rows="3" placeholder="${bodyPlaceholder}"/>
						<form:errors path="body" cssClass="error" element="p"/>
					</div>
					<div class="row mb-3">
						<form:label path="image" class="left"><spring:message code="general.label.image" /></form:label>
						<input name="image" class="form-control" type="file"  accept="image/png, image/jpeg" />
						<form:errors path="image" cssClass="formError" element="p"/>
					</div>
					<%--Botones--%>
					<div class="d-flex justify-content-center">
						<a class="btn btn-light align-self-start" href="<c:url value="/question/ask/community"/>"><spring:message code="back"/></a>
						<input class="btn btn-primary mb-3" type="submit" value="<spring:message code="button.continue"/>"/>
					</div>
					<hr>
					<%--Stepper--%>
					<div class="stepper-wrapper">
						<div class="stepper-item completed">
							<div class="step-counter">1</div>
							<div class="step-name"><spring:message code="question.community"/></div>
						</div>
						<div class="stepper-item active">
							<div class="step-counter">2</div>
							<div class="step-name"><spring:message code="question.content"/></div>
						</div>
						<div class="stepper-item">
							<div class="step-counter">3</div>
							<div class="step-name"><spring:message code="question.wrapup"/></div>
						</div>
					</div>
					<form:input path="community" value="${community.id}" cssClass="invisible"/>
				</form:form>
			</div>
		</div>

	</div>
</div>


</body>