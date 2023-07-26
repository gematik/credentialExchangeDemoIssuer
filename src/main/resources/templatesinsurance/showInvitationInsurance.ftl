<!DOCTYPE html>
<html lang="en">
<head>
    <title>Check In</title>
</head>
<body style="text-align: center; font-family: sans-serif">
<img src="/static/Krankenhaus_Blau_gematik.svg" alt="medical office" width="100">
<h3>
    Welcome ${invitation.givenName} ${invitation.name}.
</h3>
<p>Click or scan qr-code to request your insurance credential for ${invitation.insurantId}.</p>
<a href="${invitation.url}">
    <img style='width:256px; height:256px;' id='base64image' alt="qr-code-invite"
         src='data:image/jpeg;base64, ${invitation.qrCode}'/>
</a>
</body>