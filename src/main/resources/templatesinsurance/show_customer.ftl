<#import "_layout_insurance.ftl" as layout />
<@layout.header>
    <div>
        <h2>
            ${customer.givenName} ${customer.name} - ${customer.birthDate?date} - ${customer.gender}
            - <#if customer.email??>${customer.email}</#if>
        </h2>
        <h3>
            Insurance
        </h3>
        <#if customer.insurance??>
            <p>
                ${customer.insurance.insurant.insurantId}<br>
                ${customer.insurance.coverage.insuranceType}<br>
                coverage since: ${customer.insurance.coverage.start?date}<br>
            </p>
        <#else>
            <p style="color:red">no active insurance</p>
        </#if>
        <p>
            <a href="/insurance/${customer.id}/edit" style="padding: 10px">Edit profile</a>
            <a href="/insurance/${customer.invitation.id}/invitation"
               target="popup"
               onclick="window.open('/insurance/${customer.invitation.id}/invitation','popup','width=320,height=520'); return false;"
            >Send invitation</a>
        </p>
    </div>
</@layout.header>