<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<!DOCTYPE html>
<html lang="ca">

<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css">
    <link rel="stylesheet" href="css/style.css">
    <title>Registre d'Usuari (Manual)</title>
</head>

<body>

    <div class="main-container">
        <div class="w3-card-4 w3-white">
            <div class="w3-container w3-teal">
                <h2>Registre (Validació Manual)</h2>
            </div>

            <form id="registerForm" action="Register" method="POST" class="w3-container w3-padding-24">

                <p>
                    <label class="w3-text-grey">Nom complet</label>
                    <input class="w3-input w3-border" type="text" id="name" name="name" required minlength="2" maxlength="60"
                        value="${user.name}" title="Nom entre 2 i 60 caràcters." />
                </p>

                <p>
                    <label class="w3-text-grey">Correu electrònic</label>
                    <input class="w3-input w3-border" type="email" id="email" name="email" required
                        value="${user.email}" title="Introdueix un correu electrònic vàlid." />
                </p>

                <p>
                    <label class="w3-text-grey">Telèfon (opcional)</label>
                    <input class="w3-input w3-border" type="tel" id="phone" name="phone"
                        pattern="\d{7,15}" value="${user.phone}"
                        title="Telèfon de 7 a 15 dígits." />
                </p>

                <p>
                    <label class="w3-text-grey">Prefix del telèfon</label>
                    <select class="w3-select w3-border" id="phoneCountry" name="phoneCountry">
                        <option value="">Selecciona prefix</option>
                        <option value="+34" <c:if test="${user.phoneCountry == '+34'}">selected</c:if>>+34</option>
                        <option value="+1" <c:if test="${user.phoneCountry == '+1'}">selected</c:if>>+1</option>
                        <option value="+44" <c:if test="${user.phoneCountry == '+44'}">selected</c:if>>+44</option>
                        <option value="+33" <c:if test="${user.phoneCountry == '+33'}">selected</c:if>>+33</option>
                    </select>
                </p>

                <p>
                    <label class="w3-text-grey">DNI</label>
                    <input class="w3-input w3-border" type="text" id="dni" name="dni" required maxlength="9"
                        pattern="\d{8}[A-Z]" value="${user.dni}" title="8 dígits seguits d'una lletra majúscula." />
                </p>

                <p>
                    <label class="w3-text-grey">Codi postal (ubicació subscrita)</label>
                    <input class="w3-input w3-border" type="text" id="zip" name="zip" required maxlength="5"
                        pattern="\d{5}" value="${user.zip}" title="Codi postal (5 dígits) d'una ubicació subscrita a BubbleNet." />
                </p>

                <p>
                    <label class="w3-text-grey">Ciutat</label>
                    <input class="w3-input w3-border" type="text" id="city" name="city" required
                        value="${user.city}" title="Introdueix la teva ciutat." />
                </p>

                <p>
                    <label class="w3-text-grey">País</label>
                    <input class="w3-input w3-border" type="text" id="country" name="country" required maxlength="2"
                        pattern="[A-Za-z]{2}" value="${user.country}" title="Codi ISO 3166-1 alpha-2 de 2 lletres (per exemple, ES, GB, US)." />
                </p>

                <p>
                    <label class="w3-text-grey">Gènere</label>
                    <select class="w3-select w3-border" id="gender" name="gender">
                        <option value="">No especificat</option>
                        <option value="Male" <c:if test="${user.gender == 'Male'}">selected</c:if>>Home</option>
                        <option value="Female" <c:if test="${user.gender == 'Female'}">selected</c:if>>Dona</option>
                        <option value="Other" <c:if test="${user.gender == 'Other'}">selected</c:if>>Altres</option>
                    </select>
                </p>

                <p>
                    <label class="w3-text-grey">Nom d'usuari</label>
                    <input class="w3-input w3-border" type="text" id="username" name="username" required minlength="4" maxlength="30"
                        pattern="[A-Za-z0-9_]{4,30}" value="${user.username}"
                        title="4–30 caràcters, només lletres, números i guions baixos." />
                </p>

                <p>
                    <label class="w3-text-grey">Contrasenya</label>
                    <input class="w3-input w3-border" type="password" id="password" name="password" required
                        pattern="^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*]).{8,}$"
                        title="Mínim 8 caràcters, amb majúscula, número i caràcter especial." />
                </p>

                <p>
                    <label class="w3-text-grey">Repetir contrasenya</label>
                    <input class="w3-input w3-border" type="password" id="confirmPassword" name="confirmPassword" required
                        title="Les contrasenyes han de coincidir." />
                </p>

                <button type="submit" class="w3-button w3-teal w3-block w3-section w3-padding">Enviar Registre</button>

            </form>
        </div>
    </div>

    <script>
        // Injectem els errors del servidor (Format Map K/V) per al JS
        const serverErrors = {
            <c:forEach var="error" items="${errors}">
                "${error.key}": "${error.value}",
            </c:forEach>
        };
    </script>
    <script src="js/validation.js"></script>

</body>

</html>