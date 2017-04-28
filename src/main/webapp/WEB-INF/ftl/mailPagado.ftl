<html>
   <body>
     <#if order.observacionesShopper??>
      <p>${order.observacionesShopper}</p>
     </#if>
      <p>Listado de items pagados:</p>
      <table style="text-align: center;">
         <tr style="background-color: #FFF2CC; font-weight: bold;">
            <td width="200px">Shopper</td>
            <td width="100px">Fecha</td>
            <td width="200px">Cliente</td>
            <td width="200px">Sucursal</td>
            <td width="100px">Pago</td>
            <td width="100px">Importe</td>
         </tr>
        <#list model["items"] as item>
         <tr <#if item?index %  2 == 1>style="background-color: #EDEDED;"<#/if>>
            <td>${(item.shopper.name)!-}</td>
            <td>${item.fecha}</td>
            <td>${item.cliente!''}</td>
            <td>${item.sucursal!''}</td>
            <td>${item.tipoPago.description}</td>
            <td>${item.importe}</td>
         </tr>
        </#list>
         <tr>
            <td>&nbsp;</td>
            <td>&nbsp;</td>
            <td>&nbsp;</td>
            <td>&nbsp;</td>
            <td>&nbsp;</td>
            <td>Total: $ model["total"]</td>
         </tr>
      </table>
   </body>
</html>