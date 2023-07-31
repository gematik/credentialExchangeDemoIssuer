<#import "_layout_medicaloffice.ftl" as layout />
<@layout.header>
    <div style="overflow: auto">
        <table class="center">
            <tr style="background-color: lightcyan">
                <th>name</th>
                <th>birth date</th>
                <th>gender</th>
                <th>email</th>
                <th>insurance check</th>
            </tr>
            <#assign x = 0>
            <#list customers as customer>
                <#assign x++>
                <tr <#if  x%2 = 0>style="background-color: lightcyan"</#if> >
                    <td><a href="/medicaloffice/${customer.id}">${customer.givenName} ${customer.name}</a></td>
                    <td>${customer.birthDate?date}</td>
                    <td>${customer.gender}</td>
                    <td><#if customer.email??><a href="mailto:${customer.email}">${customer.email}</a></#if></td>
                    <#if customer.insurance??>
                        <td>${customer.insurance.lastStatusCheck?datetime}</td>
                    <#else>
                        <td style="color:red">no insurance data</td>
                    </#if>
                </tr>
            </#list>
        </table>
    </div>
    <p>
        <a href="/medicaloffice/new" style="padding: 10px">Create patient</a>
        <a href="/medicaloffice/checkin" style="padding: 10px"
           target="popup"
           onclick="window.open('/medicaloffice/checkin','popup','width=320,height=520'); return false;"
        >Check in with insurance credential</a>
    </p>
</@layout.header>