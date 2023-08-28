<#import "_layout_medicaloffice.ftl" as layout />
<@layout.header>
    <script type="text/javascript" src="/static/update_page.js"></script>
    <div>
        <h3>
            Patient
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
                <td>${patient.name}</td>
                <td>${patient.givenName}</td>
                <td>${patient.birthDate?date("yyyy-MM-dd'T'hh:mm:ssX")}</td>
                <td>${patient.gender}</td>
                <td>${patient.phone!"---"}</td>
                <td>${patient.email!"---"}</td>
                <td>${patient.preferredContact}</td>
            </tr>
        </table>
        <h3>
            Insurance
        </h3>
        <#if patient.insurance??>
            <h4>
                Insurant
            </h4>
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
                    <td>${patient.insurance.insurant.insurantId}</td>
                    <td>${patient.insurance.insurant.familyName}</td>
                    <td>${patient.insurance.insurant.givenName}</td>
                    <td>${patient.insurance.insurant.nameExtension!"---"}</td>
                    <td>${patient.insurance.insurant.academicTitle!"---"}</td>
                    <td>${patient.insurance.insurant.birthDate?date("yyyy-MM-dd'T'hh:mm:ssX")}</td>
                    <td>${patient.insurance.insurant.gender}</td>
                </tr>
                <#if patient.insurance.insurant.streetAddress??>
                    <tr style="background-color: lightcyan; font-size: xx-small">
                        <th>street</th>
                        <th>number</th>
                        <th>city</th>
                        <th>zip</th>
                        <th>country</th>
                    </tr>
                    <tr>
                        <td>${patient.insurance.insurant.streetAddress.street}</td>
                        <td>${patient.insurance.insurant.streetAddress.streetNumber}</td>
                        <td>${patient.insurance.insurant.streetAddress.location}</td>
                        <td>${patient.insurance.insurant.streetAddress.postalCode?c}</td>
                        <td>${patient.insurance.insurant.streetAddress.country}</td>
                    </tr>
                </#if>
                <#if patient.insurance.insurant.postBoxAddress??>
                    <tr style="background-color: lightcyan; font-size: xx-small">
                        <th colspan="2">post box number</th>
                        <th>city</th>
                        <th>zip</th>
                        <th>country</th>
                    </tr>
                    <tr>
                        <td colspan="2">${patient.insurance.insurant.postBoxAddress.postBoxNumber}</td>
                        <td>${patient.insurance.insurant.postBoxAddress.location}</td>
                        <td>${patient.insurance.insurant.postBoxAddress.postalCode?c}</td>
                        <td>${patient.insurance.insurant.postBoxAddress.country}</td>
                    </tr>
                </#if>
            </table>
            <h4>
                Coverage
            </h4>
            <table class="center">
                <tr style="background-color: lightcyan; font-size: xx-small">
                    <th colspan="3">costcenter</th>
                </tr>
                <tr style="background-color: lightcyan; font-size: xx-small">
                    <th>identification</th>
                    <th>name</th>
                    <th>country</th>
                </tr>
                <tr>
                    <td>${patient.insurance.coverage.costCenter.identification?c}</td>
                    <td>${patient.insurance.coverage.costCenter.name}</td>
                    <td>${patient.insurance.coverage.costCenter.countryCode}</td>
                </tr>
            </table>
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
                    <td>${patient.insurance.coverage.start?date("yyyy-MM-dd'T'hh:mm:ssX")}</td>
                    <td>
                        <#if patient.insurance.coverage.end??>
                            ${patient.insurance.coverage.end?date("yyyy-MM-dd'T'hh:mm:ssX")}
                        <#else>
                            --.--.----
                        </#if>
                    </td>
                    <td>${patient.insurance.coverage.insuranceType!"---"}</td>
                    <td>${patient.insurance.coverage.residencyPrinciple!"---"}</td>
                    <td>${patient.insurance.coverage.specialGroupOfPersons!"---"}</td>
                    <td>${patient.insurance.coverage.dmpMark!"---"}</td>
                </tr>
            </table>
            <#if patient.insurance.coverage.reimbursement??>
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
                        <td>${patient.insurance.coverage.reimbursement.medicalCare?string('yes', 'no')}</td>
                        <td>${patient.insurance.coverage.reimbursement.dentalCare?string('yes', 'no')}</td>
                        <td>${patient.insurance.coverage.reimbursement.inpatientSector?string('yes', 'no')}</td>
                        <td>${patient.insurance.coverage.reimbursement.initiatedServices?string('yes', 'no')}</td>
                    </tr>
                </table>
            </#if>
            <#if patient.insurance.coverage.selectiveContracts??>
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
                        <td>${patient.insurance.coverage.selectiveContracts.medical}</td>
                        <td>${patient.insurance.coverage.selectiveContracts.dental}</td>
                        <td>${patient.insurance.coverage.selectiveContracts.contractType.generalPractionerCare?string('yes', 'no')}</td>
                        <td>${patient.insurance.coverage.selectiveContracts.contractType.structuredTreatmentProgram?string('yes', 'no')}</td>
                        <td>${patient.insurance.coverage.selectiveContracts.contractType.integratedCare?string('yes', 'no')}</td>
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
                    <td>${patient.insurance.coverage.coPayment.status?string('yes', 'no')}</td>
                    <td>
                        <#if patient.insurance.coverage.coPayment.validUntil??>
                            ${patient.insurance.coverage.coPayment.validUntil?date("yyyy-MM-dd'T'hh:mm:ssX")}
                        <#else>
                            --.--.----
                        </#if>
                    </td>
                    <#if patient.insurance.coverage.dormantBenefitsEntitlement??>
                        <td>${patient.insurance.coverage.dormantBenefitsEntitlement.start?date("yyyy-MM-dd'T'hh:mm:ssX")}</td>
                        <td>${patient.insurance.coverage.dormantBenefitsEntitlement.end?date("yyyy-MM-dd'T'hh:mm:ssX")}</td>
                        <td>${patient.insurance.coverage.dormantBenefitsEntitlement.dormancyType}</td>
                    <#else>
                        <td>--.--.----</td>
                        <td>--.--.----</td>
                        <td>---</td>
                    </#if>
                </tr>
            </table>
        <#else>
            <p style="color:red">no insurance information available yet</p>
        </#if>
        <h3>
            Vaccinations
        </h3>
        <table class="center">
            <tr style="background-color: lightcyan; font-size: xx-small">
                <th>date of vaccination</th>
                <th>atc code</th>
                <th>vaccine</th>
                <th>batchNumber</th>
                <th>order</th>
                <th>invitation</th>
            </tr>
            <#assign x = 0>
            <#list patient.vaccinations?reverse as vaccination>
                <#assign x++>
                <tr <#if  x%2 = 0>style="background-color: lightcyan"</#if> >
                    <td>${vaccination.dateOfVaccination?date("yyyy-MM-dd'T'hh:mm:ssX")}</td>
                    <td>${vaccination.atcCode}</td>
                    <td>${vaccination.vaccine}</td>
                    <td>${vaccination.batchNumber}</td>
                    <td>${vaccination.order}/3</td>
                    <td><a href="/medicaloffice/${vaccination.invitation.id}/invitation"
                           target="popup"
                           onclick="window.open('/medicaloffice/${vaccination.invitation.id}/invitation','popup','width=320,height=520'); return false;"
                        >Send invitation</a>
                    </td>
                </tr>
            </#list>
        </table>

        <hr>
        <p>
            <a href="/medicaloffice/${patient.id}/edit" style="padding: 10px">Edit profile</a>
            <a href="/medicaloffice/${patient.id}/addVaccination" style="padding: 10px">Add vaccination</a>
            <a href="/medicaloffice/checkin" style="padding: 10px"
               target="popup"
               onclick="window.open('/medicaloffice/checkin','popup','width=320,height=520'); return false;"
            >Check in with insurance credential</a>
        </p>
    </div>
</@layout.header>