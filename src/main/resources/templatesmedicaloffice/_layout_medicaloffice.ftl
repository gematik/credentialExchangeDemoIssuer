<#macro header>
    <!DOCTYPE html>
    <html lang="en">
    <head>
        <title>Medical Office</title>
        <style>
             table.center {
                margin-left: auto;
                margin-right: auto;
            }
            td, th {
                padding: 0 10px ;
                text-align: left;
            }
        </style>
    </head>
    <body style="text-align: center; font-family: sans-serif">
    <img src="/static/Vaccination.svg" alt="medical office">
    <h1>Medical Office</h1>
    <hr>
    <#nested>
    <hr>
    <a href="/medicaloffice">Back to the main page</a>
    </body>
    </html>
</#macro>