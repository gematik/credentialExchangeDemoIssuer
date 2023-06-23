<#import "_layout.ftl" as layout />
<@layout.header>
    <div>
        <h3>Create customer</h3>
        <form action="/admin" method="post">
            <table class="center">
                <tr>
                    <td style="text-align: right"><label for="name">surname:</label></td>
                    <td style="text-align: left"><input type="text" name="name" id="name"></td>
                </tr>
                <tr>
                    <td style="text-align: right"><label for="givenname">given name:</label></td>
                    <td style="text-align: left"><input type="text" name="givenname" id="givenname"></td>
                </tr>
                <tr>
                    <td style="text-align: right"><label for="birthdate">birth date:</label></td>
                    <td style="text-align: left"><input type="date" name="birthdate" id="birthdate"></td>
                </tr>
                <tr>
                    <td style="text-align: right"><label for="email">email:</label></td>
                    <td style="text-align: left"><input type="text" name="email" id="email"></td>
                </tr>
            </table>
            <p>
                <input type="submit" value= "Create">
            </p>
        </form>
    </div>
</@layout.header>