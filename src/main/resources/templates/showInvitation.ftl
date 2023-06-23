<#import "_layout_public.ftl" as layout />
<@layout.header>
    <div>
        <h3>
            Invitation
        </h3>
        <p>Click invitation or scan qr-code to request your credential.</p>
        <p>
            <a href="${invitation.url}">Invitation</a>
        </p>
        <img style='display:block; width:256px; height:256px; margin-left: auto; margin-right: auto' id='base64image' alt="qr-code-invite"
             src='data:image/jpeg;base64, ${invitation.qrCode}' />
    </div>
</@layout.header>