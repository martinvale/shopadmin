<!DOCTYPE html>
<html>
  <head>
    <meta charset="utf-8">
    <title>Shopnchek</title>

<style>
@media print {
  #print-button {
    display: none;
  }
}

#print-button {
  text-align: center;
}

.page {
  border: 1px solid #000000;
  font-family: Arial;
  min-height: 800px;
  padding: 10px;
  width: 95%;
}

.title {
  font-size: 35px;
  line-height: 35px;
  margin-top: 10px;
  text-decoration: underline;
  text-align: center;
}

.subtitle {
  font-size: 25px;
  line-height: 35px;
  text-decoration: underline;
  text-align: center;
}

.heading {
  font-size: 25px;
  line-height: 35px;
}

.name {
  font-style: italic;
  text-decoration: underline;
}

.fechaPago {
  float: right;
}

table {
  font-size: 12px;
  width: 100%;
}

table tr {
  height: 35px;
}

table tr.header {
  font-size: 14px;
  font-weight: bold;
}

table tr.header td {
  border-bottom: 1px solid #000000;
  border-top: 1px solid #000000;
}

table td.value {
  text-align: right;
}

</style>
  </head>
  <body>
    <p id="print-button"><button onclick="window.print()">Imprimir</button></p>
    <div class="page">
      <p class="title">Orden de pago</p>
      <p class="heading">
        <span class="numero"><span class="name">Nro:</span> ${model['ordenPago'].numero?c}</span>
        <span class="fechaPago"><span class="name">Fecha de pago:</span> ${model['ordenPago'].fechaPago?string('dd/MM/yyyy')}</span>
      </p>
      <p class="heading">
        <span class="name">Titular:</span> ${model['titularNombre']}
      </p>
      <div class="items">
        <#assign totalHonorarios = model['ordenPago'].honorarios />
        <#assign totalReintegros = model['ordenPago'].reintegros />
        <#assign totalOtrosGastos = model['ordenPago'].otrosGastos />
        <#assign total = (totalReintegros + totalOtrosGastos + totalHonorarios) />
        <table>
          <tr class="header">
            <td>Shopper</td>
            <td>Mes</td>
            <td>A&ntilde;o</td>
            <td>Cliente</td>
            <td>Sucursal</td>
            <td>Pago</td>
            <td>Importe</td>
          </tr>
        <#list model["ordenPago"].items as item>
          <tr>
            <#if item.shopper??>
              <#assign shopperDescription = "${item.shopper.name}">
            </#if>
            <td>${shopperDescription!'No encontrado'}</td>
            <td>${item.mes!''}</td>
            <td>${item.anio?c!''}</td>
            <td>${item.cliente!''}</td>
            <td>${item.sucursal!''}</td>
            <td>${item.tipoPago.description}</td>
            <td>${item.importe?string.currency}</td>
          </tr>
        </#list>
          <tr class="header">
            <td>Total:</td>
            <td>&nbsp;</td>
            <td>&nbsp;</td>
            <td>&nbsp;</td>
            <td>&nbsp;</td>
            <td>&nbsp;</td>
            <td>${total?string.currency}</td>
          </tr>
        </table>
      </div>
    </div>
  </body>
</html>