<!DOCTYPE html>
<html lang="en">
<head>
    <title>Issue vaccination certificate</title>
</head>
<body style="text-align: center; font-family: sans-serif">
<img src="/static/Vaccination.svg" alt="medical office" width="100">
<h3>
    Welcome ${invitation.givenName} ${invitation.name}.
</h3>
<p>Click or scan qr-code to request your vaccination credential from ${invitation.dateOfVaccination?date("yyyy-MM-dd'T'hh:mm:ssX")}.</p>
<a href="${invitation.url}">
    <img style='width:256px; height:256px;' id='base64image' alt="qr-code-invite"
         src='data:image/jpeg;base64, ${invitation.qrCode}'/>
</a>
</body>