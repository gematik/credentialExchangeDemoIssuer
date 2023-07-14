<#import "_layout.ftl" as layout />
<@layout.header>
    <div>
        <h3>
            ${customer.givenName} ${customer.name} - ${customer.birthDate?date} - ${customer.gender} - <#if customer.email??>${customer.email}</#if>
        </h3>

        <table class="center">
            <tr style="background-color: lightcyan">
                <th>date of vaccination</th>
                <th>atc code</th>
                <th>vaccine</th>
                <th>batchNumber</th>
                <th>order</th>
                <th>invitation</th>
            </tr>
            <#assign x = 0>
            <#list customer.vaccinations?reverse as vaccination>
                <#assign x++>
                <tr <#if  x%2 = 0>style="background-color: lightcyan"</#if> >
                    <td>${vaccination.dateOfVaccination?date}</td>
                    <td>${vaccination.atcCode}</td>
                    <td>${vaccination.vaccine}</td>
                    <td>${vaccination.batchNumber}</td>
                    <td>${vaccination.order}/3</td>
                    <td><a href="/admin/${vaccination.invitation.id}/invitation">${vaccination.invitation.id}</a></td>
                </tr>
            </#list>
        </table>

        <hr>
        <p>
            <a href="/admin/${customer.id}/edit">Edit profile</a>
            <a href="/admin/${customer.id}/addVaccination">Add vaccination</a>
        </p>
    </div>
</@layout.header>