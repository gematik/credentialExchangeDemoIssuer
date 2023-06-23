<#import "_layout.ftl" as layout />
<@layout.header>
    <div>
        <h3>
            ${customer.givenName} ${customer.name}
        </h3>
        <p>
            ${customer.birthDate?date}
        </p>
        <p>
            <#if customer.email??>${customer.email}</#if>
        </p>
        <hr>
        <p>
            <a href="/admin/${customer.id}/edit">Edit customer</a>
        </p>
    </div>
</@layout.header>