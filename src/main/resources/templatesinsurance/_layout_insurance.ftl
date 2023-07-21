<#macro header>
    <!DOCTYPE html>
    <html lang="en">
    <head>
        <title>Insurance Company</title>
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
    <img src="/static/IdentityCard.svg" alt="insurance company">
    <h1>Insurance Company</h1>
    <hr>
    <#nested>
    <hr>
    <a href="/insurance">Back to the main page</a>
    </body>
    </html>
</#macro>