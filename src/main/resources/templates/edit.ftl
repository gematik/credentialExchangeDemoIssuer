<#import "_layout.ftl" as layout />
<@layout.header>
    <div>
        <h3>Edit customer</h3>
        <form action="/admin/${customer.id}" method="post">
            <p>
                <input type="text" name="name" value="${customer.name}">
            </p>
            <p>
                <input type="text" name="givenname" value="${customer.givenName}">
            </p>
            <p>
                <input type="date" name="birthDate" value="${customer.birthDate?date?iso_utc}">
            </p>
            <p>
                <input type="text" name="email" <#if customer.email??>value="${customer.email}"</#if>>
            </p>
            <p>
                <input type="submit" name="_action" value="update">
            </p>
        </form>
    </div>
    <div>
        <form action="/admin/${customer.id}" method="post">
            <p>
                <input type="submit" name="_action" value="delete">
            </p>
        </form>
    </div>
</@layout.header>