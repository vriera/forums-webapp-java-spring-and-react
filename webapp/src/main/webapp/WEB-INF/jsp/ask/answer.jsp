<%----%><!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <html>
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

        <title>AskAway-Answers</title>

        <!-- Icons -->
        <%--    <link href="/assets/vendor/nucleo/css/nucleo-icons.css" rel="stylesheet">--%>
        <link href="https://use.fontawesome.com/releases/v5.0.6/css/all.css" rel="stylesheet">
        <!-- css bootstrap -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta3/dist/css/bootstrap.min.css" rel="stylesheet"
              integrity="sha384-eOJMYsd53ii+scO/bJGFsiCZc+5NDVN2yr8+0RDqr0Ql0h+rP48ckxlpbzKgwra6" crossorigin="anonymous">
        <link type="text/css" href="<c:url value="/resources/styles/argon-design-system.css"/>" rel="stylesheet">
        <link type="text/css" href="<c:url value="/resources/styles/general.css"/>" rel="stylesheet">
        <link type="text/css" href="<c:url value="/resources/styles/stepper.css"/>" rel="stylesheet">
</head>
<body>
    <jsp:include page="/WEB-INF/jsp/components/navbar.jsp"/>
    <div class="wrapper" >
        <div class="m-4 d-flex justify-content-center">
            <p class="h2 text-primary">¿Como se hace para programar?</p>
        </div>
        <hr/>
        <div class="container">
            <form>
                <div class="form-group">
                    <label for="answerArea">Tu Respuesta</label>
                    <textarea class="form-control" id="answerArea" rows="3"></textarea>
                </div>
            </form>

            <div>
                <div class="mt-3 d-flex justify-content-center">
                    <p class="h4 text-primary">Contacto</p>
                </div>
                <form class="row g-3 d-flex justify-content-center">
                    <div class="col-auto">
                        <label for="name" >Nombre</label>
                        <input  class="form-control" id="name">
                    </div>
                </form>
                <form class="row g-3 d-flex justify-content-center">
                    <div class="col-auto mt-4">
                     <label for="email" >Email</label>
                     <input  class="form-control" id="email">
                    </div>
                </form>
            </div>
            <div class="d-flex justify-content-center mb-3 mt-3">
                <a class="btn btn-primary" href="<c:url value="/"/>">Enviar</a>
            </div>
        </div>
    </div>
</body>
</html>
