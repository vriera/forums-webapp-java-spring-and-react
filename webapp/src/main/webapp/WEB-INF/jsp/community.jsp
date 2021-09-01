<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!-- TODO: meter el utf:8-->
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<html>

<head>
    <meta charset="utf-8">
    <title>Communities</title>
    <!-- Argon CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta3/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-eOJMYsd53ii+scO/bJGFsiCZc+5NDVN2yr8+0RDqr0Ql0h+rP48ckxlpbzKgwra6" crossorigin="anonymous">
    <link rel="stylesheet" href="/resources/styles/argon-design-system.min.css" type="text/css">
    <link rel="stylesheet" href="/resources/styles/general.css" type="text/css">
</head>

<body class="bg-secondary section section-hero section-shaped">
    <div class="shape shape-style-1 shape-default h-50"></div>
        <div class="container-xl">
            <div class="row">
                <div class="col-3">
                    <div class="card rounded-0 bg-white top-0 start-0">
                        <div class="sidenav-header d-flex justify-content-center my-3">
                            <p class="h1 text-primary "><strong>Ask Away</strong></p>
                        </div>
                        <p class="h3 px-3">Foros</p>
                        <hr class="my-1">
                        <ul class="navbar-nav px-3">
                            <!--Cuando lo cambie a un for each, hacer que sean h5 y agregarle un lindo mb-3-->
                            <li class="nav-item">
                                <div class="btn-block">
                                    <p class="h4">Foro numero 1</p>
                                </div>

                            </li>
                            <li class="nav-item">
                                <p class="h4">Foro numero 2</p>
                            </li>
                            <li class="nav-item">
                                <p class="h4">Foro numero 3</p>
                            </li>
                            <li class="nav-item">
                                <p class="h4">Foro numero 4</p>
                            </li>
                        </ul>
                    </div>
                </div>


                <div class="col-6">
                    <div class="card bg-white h-75 ">
                        <div class="align-items-center d-flex justify-content-center my-3">
                            <p class="h1 text-primary bold">COMUNIDAD</p>
                        </div>
                        <div class="overflow-auto">
                            <c:forEach begin="1" end="7">
                                <div class="m-3">
                                    <jsp:include page="/WEB-INF/jsp/components/questionCard.jsp"/>
                                </div>

                            </c:forEach>
                        </div>

                    </div>
                </div>

                <div class="col-3">
                    <div class="card bg-white">
                        <div class="align-items-center d-flex justify-content-center my-3">
                            <h1 class="text-primary bold">INFORMACIÓN</h1>
                        </div>
                    </div>
                </div>



            </div>

    </div>

</body>

</html>
