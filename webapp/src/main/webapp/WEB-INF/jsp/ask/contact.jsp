<%----%><!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<html>
<head>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

	<title>AskAway | Contacto</title>


	<!-- Icons -->
	<%--    <link href="/assets/vendor/nucleo/css/nucleo-icons.css" rel="stylesheet">--%>
	<link href="https://use.fontawesome.com/releases/v5.0.6/css/all.css" rel="stylesheet">

	<!-- BLK• CSS -->
	<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta3/dist/css/bootstrap.min.css" rel="stylesheet"
		  integrity="sha384-eOJMYsd53ii+scO/bJGFsiCZc+5NDVN2yr8+0RDqr0Ql0h+rP48ckxlpbzKgwra6" crossorigin="anonymous">
	<link type="text/css" href="<c:url value="/resources/styles/argon-design-system.css"/>" rel="stylesheet">
	<link type="text/css" href="<c:url value="/resources/styles/general.css"/>" rel="stylesheet">
	<link type="text/css" href="<c:url value="/resources/styles/stepper.css"/>" rel="stylesheet">
</head>
<body>

<jsp:include page="/WEB-INF/jsp/components/navbar.jsp"/>

<div class="wrapper">
	<div class="section section-hero section-shaped">
		<div class="shape shape-style-1 shape-default shape-skew">
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
		<div class="container">
			<div class="white-pill">
				<div class="d-flex justify-content-center">
					<div class="h1 text-primary">CONTACTO</div>
				</div>
				<hr>
				<div class="p">¡Ya casi estás! Sólo falta que nos des una manera de contactarte para mantenerte al tanto de tu pregunta</div>
				<c:url value="/ask/contact" var="postPath"/>
				<form:form modelAttribute="userForm" action="${postPath}" method="post">
				<%--Email--%>
				<div class="form-group mt-3">
					<form:label path="email" class="text-black">Email</form:label>
					<form:input path="email" type="email" class="form-control" placeholder="ejemplo@gmail.com" id="email"/>
				</div>
				<%--Nombre--%>
				<div class="form-group mt-3">
					<form:label path="name" class="text-black">Usuario</form:label>
					<form:input path="name" class="form-control" placeholder="Tu nombre de usuario acá" id="username"/>
				</div>
				<%--Publicar--%>
				<div class="d-flex justify-content-center">
					<input type="submit" class="btn btn-primary mb-3" value="Publicar"/>
				</div>
					<form:input path="key" value="${key}" cssClass="invisible"/>
				</form:form>
				<hr>
				<%--Stepper--%>
				<div class="stepper-wrapper">
					<div class="stepper-item completed">
						<div class="step-counter">1</div>
						<div class="step-name">Comunidad</div>
					</div>
					<div class="stepper-item completed">
						<div class="step-counter">2</div>
						<div class="step-name">Pregunta</div>
					</div>
					<div class="stepper-item active">
						<div class="step-counter">3</div>
						<div class="step-name">Contacto</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

</body>