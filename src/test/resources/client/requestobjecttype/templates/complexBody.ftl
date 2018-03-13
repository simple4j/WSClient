{
    "salesPersonId":"${salesPersonId}",
    <#list orders>
    "orders":
    [
        <#items as order>
        {
            "orderDate":"${order.orderDate?string('dd.MM.yyyy HH:mm:ss')}",
            "orderId":"${order.orderId}",
            "orderItems":
            [
                <#list order.orderItems as product, orderItem>
                {
                    "productId":"${product.productId}",
                    "productDescription":"${product.productDescription}",
                    "orderItemId":"${orderItem.orderItemId}",
                    "quantity":"${orderItem.quantity}",
                    "status":"${orderItem.status}"
                }<#sep>,</#sep>
                </#list>
            ]
        }<#sep>,</#sep>
        </#items>
    ]
    </#list>
}
