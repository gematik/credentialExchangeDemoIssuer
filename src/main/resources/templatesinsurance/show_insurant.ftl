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
                ${customer.insurance.insurantId}<br>
                ${customer.insurance.insuranceType}<br>
                coverage since: ${customer.insurance.start?date}<br>
                <a href="/insurance/${customer.insurance.invitation.id}/invitation">${customer.insurance.invitation.id}</a>
            </p>
        <#else>
            <p style="color:red">no active insurance</p>
        </#if>
        <p>
            <a href="/insurance/${customer.id}/edit" style="padding: 10px">Edit profile</a>
        </p>
    </div>
</@layout.header>