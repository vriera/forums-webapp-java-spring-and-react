<%----%><!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>

<html>
<head>

	<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
	<meta charset="utf-8">
	<title>AskAway | Registrarse</title>


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
					<div class="h1 text-primary">REGISTRARSE</div>
				</div>
				<hr>
				<div class="p">Registrate para contribuir en nuestras comunidades</div>
				<c:url value="/credentials/register" var="postPath"/>
				<form:form modelAttribute="userForm" action="${postPath}" method="post">
					<%--Email--%>
					<div class="form-group mt-3">
						<form:label path="email" class="text-black">Email</form:label>
						<form:input path="email" type="email" class="form-control" placeholder="ejemplo@email.com" id="email"/>
						<form:errors path="email" cssClass="error" element="p"/>
					</div>
					<%--Username--%>
					<div class="form-group mt-3">
						<form:label path="username" class="text-black">Nombre</form:label>
						<form:input path="username" type="text" class="form-control" placeholder="Nombre de usuario" id="username"/>
						<form:errors path="username" cssClass="error" element="p"/>
					</div>
					<%--Contraseña--%>
					<div class="form-group mt-3">
						<form:label path="password" class="text-black">Contraseña</form:label>
						<form:input path="password" type="password" class="form-control" placeholder="Contraseña" id="password"/>
						<form:errors path="password" cssClass="error" element="p"/>
					</div>
					<%--Repetir contraseña--%>
					<div class="form-group mt-3">
						<form:label path="repeatPassword" class="text-black">Repetir contraseña</form:label>
						<form:input path="repeatPassword" type="password" class="form-control" placeholder="Contraseña" id="repeatPassword"/>
						<form:errors path="repeatPassword" cssClass="error" element="p"/>
					</div>

					<div class="p">¿Ya tenés una cuenta?
						<a class="link-primary" href="<c:url value="/credentials/login"/>">Ingresá</a>
					</div>
					<%--Botones--%>
					<div class="d-flex justify-content-center">
						<a class="btn btn-light align-self-start" href="<c:url value="/"/>">Volver</a>
						<input type="submit" class="btn btn-primary mb-3" value="Registrarse"/>
					</div>



				</form:form>
			</div>
		</div>
	</div>
</div>


</body>
</html>