<#import "_layout.ftl" as layout />
<@layout.header>
    <div style = "overflow: auto">
        <table class="center">
            <tr style="background-color: lightcyan">
                <th>name</th>
                <th>birth date</th>
                <th>email</th>
                <th>invitation</th>
            </tr>
            <#assign x = 0>
            <#list customers?reverse as customer>
                <#assign x++>
                <tr <#if  x%2 = 0>style="background-color: lightcyan"</#if> >
                    <td><a href="/admin/${customer.id}">${customer.givenName} ${customer.name}</a></td>
                    <td>${customer.birthDate?date}</td>
                    <td><#if customer.email??><a href="mailto:${customer.email}">${customer.email}</a></#if></td>
                    <td><a href="/admin/${customer.id}/invitation">${customer.invitation.id}</a></td>
                </tr>
            </#list>
        </table>
    </div>
<p>
    <a href="/admin/new">Create customer</a><br>
</p>
</@layout.header>