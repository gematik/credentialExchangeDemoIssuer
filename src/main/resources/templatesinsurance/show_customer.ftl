<#import "_layout_insurance.ftl" as layout />
<@layout.header>
    <div>
        <h3>
            Customer
        </h3>
        <table class="center">
            <tr style="background-color: lightcyan; font-size: xx-small">
                <th>family name</th>
                <th>given name</th>
                <th>birth date</th>
                <th>gender</th>
                <th>phone</th>
                <th>email</th>
                <th>preferred contact</th>
            </tr>
            <tr>
                <td>${customer.name}</td>
                <td>${customer.givenName}</td>
                <td>${customer.birthDate?date("yyyy-MM-dd'T'hh:mm:ssX")}</td>
                <td>${customer.gender}</td>
                <td>${customer.phone!"---"}</td>
                <td>${customer.email!"---"}</td>
                <td>${customer.preferredContact}</td>
            </tr>
        </table>
        <h3>
            Insurance
        </h3>
        <#if customer.insurance??>
            <h4>
                Insurant
            </h4>
            <#if customer.insurance.insurant.portrait??>
                <img src="data:image/png;base64,${customer.insurance.insurant.portrait}" alt="Portrait" width="100"
                     height="128"/>
            </#if>
            <table class="center">
                <tr style="background-color: lightcyan; font-size: xx-small">
                    <th>insurant id</th>
                    <th>family name</th>
                    <th>given name</th>
                    <th>extension</th>
                    <th>title</th>
                    <th>birth date</th>
                    <th>gender</th>
                </tr>
                <tr>
                    <td>${customer.insurance.insurant.insurantId}</td>
                    <td>${customer.insurance.insurant.familyName}</td>
                    <td>${customer.insurance.insurant.givenName}</td>
                    <td>${customer.insurance.insurant.nameExtension!"---"}</td>
                    <td>${customer.insurance.insurant.academicTitle!"---"}</td>
                    <td>${customer.insurance.insurant.birthDate?date("yyyy-MM-dd'T'hh:mm:ssX")}</td>
                    <td>${customer.insurance.insurant.gender}</td>
                </tr>
                <#if customer.insurance.insurant.streetAddress??>
                    <tr style="background-color: lightcyan; font-size: xx-small">
                        <th>street</th>
                        <th>number</th>
                        <th>city</th>
                        <th>zip</th>
                        <th>country</th>
                    </tr>
                    <tr>
                        <td>${customer.insurance.insurant.streetAddress.street}</td>
                        <td>${customer.insurance.insurant.streetAddress.streetNumber}</td>
                        <td>${customer.insurance.insurant.streetAddress.location}</td>
                        <td>${customer.insurance.insurant.streetAddress.postalCode?c}</td>
                        <td>${customer.insurance.insurant.streetAddress.country}</td>
                    </tr>
                </#if>
                <#if customer.insurance.insurant.postBoxAddress??>
                    <tr style="background-color: lightcyan; font-size: xx-small">
                        <th colspan="2">post box number</th>
                        <th>city</th>
                        <th>zip</th>
                        <th>country</th>
                    </tr>
                    <tr>
                        <td colspan="2">${customer.insurance.insurant.postBoxAddress.postBoxNumber}</td>
                        <td>${customer.insurance.insurant.postBoxAddress.location}</td>
                        <td>${customer.insurance.insurant.postBoxAddress.postalCode?c}</td>
                        <td>${customer.insurance.insurant.postBoxAddress.country}</td>
                    </tr>
                </#if>
            </table>
            <h4>
                Coverage
            </h4>
            <table class="center">
                <tr style="background-color: lightcyan; font-size: xx-small">
                    <th>start</th>
                    <th>ende</th>
                    <th>type</th>
                    <th>residency principle</th>
                    <th>special group of person</th>
                    <th>disease management plan</th>
                </tr>
                <tr>
                    <td>${customer.insurance.coverage.start?date("yyyy-MM-dd'T'hh:mm:ssX")}</td>
                    <td>
                        <#if customer.insurance.coverage.end??>
                            ${customer.insurance.coverage.end?date("yyyy-MM-dd'T'hh:mm:ssX")}
                        <#else>
                            --.--.----
                        </#if>
                    </td>
                    <td>${customer.insurance.coverage.insuranceType!"---"}</td>
                    <td>${customer.insurance.coverage.residencyPrinciple!"---"}</td>
                    <td>${customer.insurance.coverage.specialGroupOfPersons!"---"}</td>
                    <td>${customer.insurance.coverage.dmpMark!"---"}</td>
                </tr>
            </table>
            <#if customer.insurance.coverage.reimbursement??>
                <h4>
                    Reimbursement
                </h4>
                <table class="center">
                    <tr style="background-color: lightcyan; font-size: xx-small">
                        <th>medical care</th>
                        <th>dental care</th>
                        <th>inpatient sector</th>
                        <th>initiated services</th>
                    </tr>
                    <tr>
                        <td>${customer.insurance.coverage.reimbursement.medicalCare?string('yes', 'no')}</td>
                        <td>${customer.insurance.coverage.reimbursement.dentalCare?string('yes', 'no')}</td>
                        <td>${customer.insurance.coverage.reimbursement.inpatientSector?string('yes', 'no')}</td>
                        <td>${customer.insurance.coverage.reimbursement.initiatedServices?string('yes', 'no')}</td>
                    </tr>
                </table>
            </#if>
            <#if customer.insurance.coverage.selectiveContracts??>
                <h4>
                    Selective Contracts
                </h4>
                <table class="center">
                    <tr style="background-color: lightcyan; font-size: xx-small">
                        <th>medical</th>
                        <th>dental</th>
                        <th colspan="3">type</th>
                    </tr>
                    <tr style="background-color: lightcyan; font-size: xx-small">
                        <th></th>
                        <th></th>
                        <th>general practioner care</th>
                        <th>structured treatment program</th>
                        <th>integrated care</th>
                    </tr>
                    <tr>
                        <td>${customer.insurance.coverage.selectiveContracts.medical}</td>
                        <td>${customer.insurance.coverage.selectiveContracts.dental}</td>
                        <td>${customer.insurance.coverage.selectiveContracts.contractType.generalPractionerCare?string('yes', 'no')}</td>
                        <td>${customer.insurance.coverage.selectiveContracts.contractType.structuredTreatmentProgram?string('yes', 'no')}</td>
                        <td>${customer.insurance.coverage.selectiveContracts.contractType.integratedCare?string('yes', 'no')}</td>
                    </tr>
                </table>
            </#if>
            <h4>
                Others
            </h4>
            <table class="center">
                <tr style="background-color: lightcyan; font-size: xx-small">
                    <th colspan="2">co-payment</th>
                    <th colspan="3">dormant benefits entitlement</th>
                </tr>
                <tr style="background-color: lightcyan; font-size: xx-small">
                    <th>exemption</th>
                    <th>valid until</th>
                    <th>start</th>
                    <th>end</th>
                    <th>type</th>
                </tr>
                <tr>
                    <td>${customer.insurance.coverage.coPayment.status?string('yes', 'no')}</td>
                    <td>
                        <#if customer.insurance.coverage.coPayment.validUntil??>
                            ${customer.insurance.coverage.coPayment.validUntil?date("yyyy-MM-dd'T'hh:mm:ssX")}
                        <#else>
                            --.--.----
                        </#if>
                    </td>
                    <#if customer.insurance.coverage.dormantBenefitsEntitlement??>
                        <td>${customer.insurance.coverage.dormantBenefitsEntitlement.start?date("yyyy-MM-dd'T'hh:mm:ssX")}</td>
                        <td>${customer.insurance.coverage.dormantBenefitsEntitlement.end?date("yyyy-MM-dd'T'hh:mm:ssX")}</td>
                        <td>${customer.insurance.coverage.dormantBenefitsEntitlement.dormancyType}</td>
                    <#else>
                        <td>--.--.----</td>
                        <td>--.--.----</td>
                        <td>---</td>
                    </#if>
                </tr>
            </table>
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