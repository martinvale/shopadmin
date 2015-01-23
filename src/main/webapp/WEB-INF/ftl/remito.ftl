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

.totales table {
  font-size: 20px;
  margin: 0 auto;
  width: 85%;
}

.totales table tr {
  height: 35px;
}

.totales table .total td {
  border-bottom: 1px solid #000000;
}

.totales table td.value {
  text-align: right;
}

.left {
  display: inline-block;
  float: right;
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
      <hr/>
      <div class="totales">
        <#assign totalHonorarios = model['ordenPago'].honorarios />
        <#assign totalReintegros = model['ordenPago'].reintegros />
        <#assign totalOtrosGastos = model['ordenPago'].otrosGastos />
        <#assign ivaHonorarios = (totalHonorarios * (model['ordenPago'].iva / 100)) />
        <#assign totalHonorariosConIva = (totalHonorarios + ivaHonorarios) />
        <#assign total = (totalReintegros + totalOtrosGastos + totalHonorariosConIva) />
        <table>
          <tr>
            <td class="name">Honorarios:</td>
            <td class="value">${totalHonorarios?string.currency}</td>
          </tr>
          <tr>
            <td class="name">IVA ${model['ordenPago'].iva}%:</td>
            <td class="value">${ivaHonorarios?string.currency}</td>
          </tr>
          <tr class="total">
            <td class="name">Subtotal Honorarios:</td>
            <td class="value">${totalHonorariosConIva?string.currency}</td>
          </tr>
          <tr class="total">
            <td class="name">Subtotal Reintegros:</td>
            <td class="value">${totalReintegros?string.currency}</td>
          </tr>
          <tr class="total">
            <td class="name">Subtotal Otros Gastos:</td>
            <td class="value">${totalOtrosGastos?string.currency}</td>
          </tr>
          <tr>
            <td>&nbsp;</td>
            <td>&nbsp;</td>
          </tr>
          <tr class="total">
            <td class="name">Total General:</td>
            <td class="value">${total?string.currency}</td>
          </tr>
        </table>
      </div>
      <hr/>
      <p class="heading">
        <#if model["ordenPago"].idTransferencia??>
          <span class="name">ID Transfer:</span> ${model["ordenPago"].idTransferencia}
        </#if>
        <#if model["ordenPago"].numeroChequera??>
          <span class="name">Chequera Nro:</span> ${model["ordenPago"].numeroChequera}
        </#if>
        <#if model["ordenPago"].numeroCheque??>
          <span class="left"><span class="name">Cheque Nro:</span> ${model["ordenPago"].numeroCheque}</span>
        </#if>
      </p>
      <p class="heading">
        <#if model["ordenPago"].medioPago??>
          ${model["ordenPago"].medioPago.description}
        </#if>
        <#if model["ordenPago"].localidad??>
          <span class="left">${model["ordenPago"].localidad}</span>
        </#if>
      </p>
      <hr/>
      <p class="heading">Recib&iacute; conforme</p>
      <p class="heading"><span class="name">Firma</span></p>
      <p class="heading"><span class="name">Aclaraci&oacute;n</span></p>
    </div>
  </body>
</html>