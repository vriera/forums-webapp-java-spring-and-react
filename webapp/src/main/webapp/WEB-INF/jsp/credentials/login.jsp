<%----%><!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>

<html>
<head>

	<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
	<meta charset="utf-8">
	<title> <spring:message code="login.title"></spring:message></title>


	<!-- Icons -->
	<%--    <link href="/assets/vendor/nucleo/css/nucleo-icons.css" rel="stylesheet">--%>
	<link href="https://use.fontawesome.com/releases/v5.0.6/css/all.css" rel="stylesheet">

	<!-- BLK• CSS -->
	<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta3/dist/css/bootstrap.min.css" rel="stylesheet"
		  integrity="sha384-eOJMYsd53ii+scO/bJGFsiCZc+5NDVN2yr8+0RDqr0Ql0h+rP48ckxlpbzKgwra6" crossorigin="anonymous">
	<link type="text/css" href="<c:url value="/resources/styles/argon-design-system.css"/>" rel="stylesheet">
	<link type="text/css" href="<c:url value="/resources/styles/blk-design-system.css"/>" rel="stylesheet">
	<link type="text/css" href="<c:url value="/resources/styles/general.css"/>" rel="stylesheet">
	<link rel="icon" href="<c:url value="/resources/images/favicon.ico"/>">

	<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta3/dist/js/bootstrap.bundle.min.js"
			integrity="sha384-JEW9xMcG8R+pH31jmWH6WWP0WintQrMb4s7ZOdauHnUtxwoG2vI5DkLtS3qm9Ekf"
			crossorigin="anonymous"></script>

</head>
<body>

<jsp:include page="/WEB-INF/jsp/components/navbar.jsp"/>

<div class="wrapper">
	<div class="section section-hero section-shaped">
		<div class="shape shape-style-1 shape-default shape-skew viewheight-90">
			<span class="span-150 square1"></span>
			<span class="span-50 square2"></span>
			<span class="span-50 square3"></span>
			<span class="span-75 square4"></span>
			<span class="span-100 square5"></span>
			<span class="span-75 square6"></span>
			<span class="span-50 square7"></span>
			<span class="span-100 square3"></span>
			<span class="span-50 square2"></span>
			<span class="span-100 square4"></span>
		</div>
		<div class="container">
			<div class="white-pill">
				<div class="d-flex justify-content-center">
					<div class="h1 text-primary"><spring:message code="logIn"></spring:message></div>
				</div>
				<hr>
				<c:url value="/credentials/login" var="postPath"/>
				<form:form modelAttribute="loginForm" action="${postPath}" method="post">
					<%--Email--%>
					<div class="form-group mt-3">
						<form:label path="email" class="text-black"><spring:message code="email"></spring:message></form:label>
						<form:input path="email" type="email" class="form-control" placeholder="ejemplo@email.com" id="email"/>
						<form:errors path="email" cssClass="error" element="p"/>
					</div>
					<%--Contraseña--%>
					<div class="form-group mt-3">
						<form:label path="password" class="text-black"><spring:message code="password"></spring:message></form:label>
						<form:input path="password" type="password" class="form-control" placeholder="Contraseña" id="password"/>
						<form:errors path="password" cssClass="error" element="p"/>
					</div>

					<div class="p"><spring:message code="withoutAccount"></spring:message>
						<a class="link-primary" href="<c:url value="/credentials/register"/>"><spring:message code="register.register"></spring:message></a>
					</div>
					<%--Botones--%>
					<div class="d-flex justify-content-center">
						<a class="btn btn-light align-self-start" href="<c:url value="/"/>"><spring:message code="back"></spring:message></a>
						<input type="submit" class="btn btn-primary mb-3" value="Iniciar sesión"/>
					</div>

				</form:form>
			</div>
		</div>
	</div>
</div>


</body>
</html>
