<#import "_layout_medicaloffice.ftl" as layout />
<@layout.header>
    <div>
        <h3>Add vaccination</h3>
        <form action="/medicaloffice/${data.customer.id}/addVaccination" method="post">
            <table class="center">
                <tr>
                    <td style="text-align: right"><label for="dateOfVaccination">date of vaccination:</label></td>
                    <td style="text-align: left"><input type="date" name="dateOfVaccination" id="dateOfVaccination">
                    </td>
                </tr>
                <tr>
                    <td style="text-align: right"><label for="atcCode">atc code:</label></td>
                    <td style="text-align: left"><input type="text" name="atcCode" id="atcCode" value="J07BX03"> </td>
                </tr>
                <tr>
                    <td style="text-align: right"><label for="vaccine">vaccine:</label></td>
                    <td style="text-align: left">
                        <select name="vaccine" id="vaccine">
                            <#list data.vaccines as vaccine>
                                <option>${vaccine}</option>
                            </#list>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: right"><label for="batchNumber">batch number:</label></td>
                    <td style="text-align: left"><input type="text" name="batchNumber" id="batchNumber"></td>
                </tr>
                <tr>
                    <td style="text-align: right"><label for="order">gender:</label></td>
                    <td style="text-align: left">
                        <select name="order" id="order">
                            <option>1</option>
                            <option>2</option>
                            <option>3</option>
                        </select>
                    </td>
                </tr>
            </table>
            <p>
                <input type="submit" value="Add">
            </p>
        </form>
    </div>
</@layout.header>