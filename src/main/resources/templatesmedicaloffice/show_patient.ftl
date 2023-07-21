<#import "_layout_medicaloffice.ftl" as layout />
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
                ${customer.insurance.costCenter}<br>
                coverage since: ${customer.insurance.start?date}
            </p>
        <#else>
            <p style="color:red">no insurance information available yet</p>
        </#if>
        <h3>
            Vaccination
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
                    <td><a href="/medicaloffice/${vaccination.invitation.id}/invitation">${vaccination.invitation.id}</a>
                    </td>
                </tr>
            </#list>
        </table>

        <hr>
        <p>
            <a href="/medicaloffice/${customer.id}/edit" style="padding: 10px">Edit profile</a>
            <a href="/medicaloffice/${customer.id}/addVaccination" style="padding: 10px">Add vaccination</a>
        </p>
    </div>
</@layout.header>