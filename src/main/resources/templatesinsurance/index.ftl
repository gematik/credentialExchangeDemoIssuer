<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Overview Insurance Company</title>
    <style>
        table.center {
            margin-left: auto;
            margin-right: auto;
            padding-top: 40px;
        }

        td, th {
            padding-top: 10px;
            text-align: center;
        }
    </style>
</head>
<body style="text-align: center; font-family: sans-serif; overflow: auto">
<table class="center">
    <tr>
        <td colspan="2"></td>
        <td>Mobile Wallet</td>
        <td colspan="2"><img src="/static/Gematik_Logo_Flag_Tagline_right_RGB.png" alt="gematik logo" width="190"></td>
    </tr>
    <tr>
        <td colspan="2"></td>
        <td><img src="/static/Online-Check-in_Blau_gematik.svg" alt="mobile wallet" width="150"></td>
        <td colspan="2">
            <#if url.lastCallingRemoteAddress??>
                <a href="http://${url.lastCallingRemoteAddress}:9091" target="Mobile Wallet Log">
                    Examine Log<br><img src="/static/Test_Blau_gematik.svg" alt="Log" width="40">
                </a>
            </#if>
        </td>
    </tr>
    <tr>
        <td colspan="5">Issue Credential / Presentation Exchange</td>
    </tr>
    <tr>
        <td></td>
        <td><img src="/static/Verbinder-TR-BL_Blau_gematik.svg" alt="Verbinder" width="75"></td>
        <td><img src="/static/Verbinder-TC-BC_Blau_gematik.svg" alt="Verbinder" width="75"></td>
        <td><img src="/static/Verbinder-TL-BR_Blau_gematik.svg" alt="Verbinder" width="75"></td>
        <td></td>
    </tr>
    <tr>
        <td>Insurance Company</td>
        <td></td>
        <td>Medical Office</td>
        <td></td>
        <td>Admission Control</td>
    </tr>
    <tr>
        <td>
            <a href="http://${url.address}:8080/insurance" target="Insurance Company">
                <img src="/static/Krankenkasse_Blau_gematik.svg" alt="insurance company" width="150">
            </a>
        </td>
        <td></td>
        <td>
            <a href="http://${url.address}:8081/medicaloffice" target="Medical Office">
                <img src="/static/Krankenhaus_Blau_gematik.svg" alt="medical office" width="150">
            </a>
        </td>
        <td></td>
        <td>
            <img src="/static/Huerde_Blau_gematik.svg" alt="admission control" width="150">
        </td>
    </tr>
</table>
<p style="padding-top: 40px">&copy;2023 gematik GmbH</p>
</body>
</html>
