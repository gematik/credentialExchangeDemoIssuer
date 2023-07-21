<#import "_layout_insurance.ftl" as layout />
<@layout.header>
    <div style = "overflow: auto">
        <table class="center">
            <tr style="background-color: lightcyan">
                <th>name</th>
                <th>birth date</th>
                <th>gender</th>
                <th>email</th>
            </tr>
            <#assign x = 0>
            <#list customers as customer>
                <#assign x++>
                <tr <#if  x%2 = 0>style="background-color: lightcyan"</#if> >
                    <td><a href="/insurance/${customer.id}">${customer.givenName} ${customer.name}</a></td>
                    <td>${customer.birthDate?date}</td>
                    <td>${customer.gender}</td>
                    <td><#if customer.email??><a href="mailto:${customer.email}">${customer.email}</a></#if></td>
                </tr>
            </#list>
        </table>
    </div>
<p>
    <a href="/insurance/new" style="padding: 10px">Create insurant</a><br>
</p>
</@layout.header>