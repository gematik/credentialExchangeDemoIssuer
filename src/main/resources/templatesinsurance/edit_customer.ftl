<#import "_layout_insurance.ftl" as layout />
<@layout.header>
    <div>
        <h3>Edit customer</h3>
        <form action="/insurance/${customer.id}/edit" method="post">
            <p>
                <input type="text" name="name" value="${customer.name}">
            </p>
            <p>
                <input type="text" name="givenname" value="${customer.givenName}">
            </p>
            <p>
                <input type="date" name="birthDate" value="${customer.birthDate?date("yyyy-MM-dd'T'hh:mm:ssX")?iso_utc}">
            </p>
            <p>
                <select name="gender" id="gender">
                    <option <#if customer.gender == "Female">selected</#if>>Female</option>
                    <option <#if customer.gender == "Male">selected</#if>>Male</option>
                    <option <#if customer.gender == "Undefined">selected</#if>>Undefined</option>
                </select>
            </p>
            <p>
                <input type="text" name="email" <#if customer.email??>value="${customer.email}"</#if>>
            </p>
            <p>
                <input type="submit" name="_action" value="update">
            </p>
            <p>
                <input type="submit" name="_action" value="delete">
            </p>
        </form>
    </div>
</@layout.header>