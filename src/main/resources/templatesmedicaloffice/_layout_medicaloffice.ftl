<#macro header>
    <!DOCTYPE html>
    <html lang="en">
    <head>
        <title>Praxis Sommergarten</title>
        <style>
            table.center {
                margin-left: auto;
                margin-right: auto;
            }

            td, th {
                padding: 0 10px;
                text-align: left;
            }
        </style>
    </head>
    <body style="text-align: center; font-family: sans-serif">
    <img src="/static/Krankenhaus_Blau_gematik.svg" alt="medical office" width="100">
    <h2>Praxis Sommergarten</h2>
    <hr>
    <#nested>
    <hr>
    <a href="/medicaloffice">Back to the main page</a>
    </body>
    </html>
</#macro>