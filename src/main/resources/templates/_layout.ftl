<#macro header>
    <!DOCTYPE html>
    <html lang="en">
    <head>
        <title>Customer Administration</title>
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
    <img src="/static/IdentityCard.svg" alt="identity card">
    <h1>Customer Administration </h1>
    <hr>
    <#nested>
    <a href="/admin">Back to the main page</a>
    </body>
    </html>
</#macro>