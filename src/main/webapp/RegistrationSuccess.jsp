<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ca">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css">
    <link rel="stylesheet" href="css/style.css">
    <title>Registre Completat</title>
</head>
<body>

<div class="main-container">
    <div class="w3-card-4 w3-white">
        <div class="w3-container w3-green">
            <h2>Registre completat</h2>
        </div>

        <div class="w3-container w3-padding-24">
            <p class="w3-text-dark w3-large">
                L'usuari <strong>${user.name}</strong> s'ha registrat correctament.
            </p>
            <p class="w3-text-grey">
                Si us plau, guarda aquesta informació per iniciar sessió més endavant.
            </p>
            <p>
                <a href="Login.jsp" class="w3-button w3-teal w3-margin-right">Anar a l'inici de sessió</a>
                <a href="Register" class="w3-button w3-light-grey">Registrar un altre usuari</a>
            </p>
        </div>
    </div>
</div>

</body>
</html>
