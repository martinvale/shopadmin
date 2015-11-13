<!DOCTYPE html>
<html>
  <head>
    <meta charset="utf-8">
    <title>Shopnchek</title>

    <#setting locale="es_AR">

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
  position: relative;
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

.observacion {
  border: 2px solid #000000;
  padding: 2px;
}

.important {
  text-decoration: underline;
}

.note {
  font-weight: bold;
}

.horario {
  float: right;
}

.footer {
  bottom: 0;
  position: absolute;
  width: 98%;
}

.footer p, .footer table {
  margin: 0 auto;
  width: 500px;
}

.footer table tr {
  height: 17px;
}

.footer p.observacion {
  margin-bottom: 10px;
}

</style>
  </head>
  <body>
    <p id="print-button"><button onclick="window.print()">Imprimir</button></p>
    <div class="page">
      <div class="items">
        <#assign totalHonorarios = model['ordenPago'].honorarios />
        <#assign totalReintegros = model['ordenPago'].reintegros />
        <#assign totalOtrosGastos = model['ordenPago'].otrosGastos />
        <#assign total = (totalReintegros + totalOtrosGastos + totalHonorarios) />
        <table>
          <tr class="header">
            <td>Fecha</td>
            <td>Cliente</td>
            <td>Sucursal</td>
            <td>Pago</td>
            <td>Importe</td>
          </tr>
        <#list model["ordenPago"].items?sort_by(["tipoPago", "description"]) as item>
          <tr>
            <td>${item.fecha!''}</td>
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
            <td>${total?string.currency}</td>
          </tr>
        </table>
      </div>
      <p class="observacion">${model["ordenPago"].observacionesShopper!''}</p>
      <p>&nbsp;</p>
      <p class="observacion note"><span class="important">IMPORTANTE:</span><br>
        Cualquier duda o aclaraci&oacute;n sobre este pago, deber&aacute; efectuarse dentro de
        los <span class="important">90 d&iacute;as</span> desde la emisi&oacute;n del cheque
        o de realizaci&oacute;n de la transferencia bancaria.
      </p>
      <hr>
      <p class="observacion note important">ATENCI&Oacute;N: A partir del 1/6/2012 el
        horario de la ma&ntilde;ana es de 10:00 hs a 11:30 hs
      </p>
      <table>
        <tr>
          <td><span class="important">D&iacute;as y horarios de pago, sin excepci&oacute;n:</span> Martes y Jueves</td>
          <td>10:00 hs a 11:30 hs</td>
        </tr>
        <tr>
          <td>&nbsp;</td>
          <td>15:00 hs a 16:30 hs</td>
        </tr>
      </table>
      <p>&nbsp;</p>
    </div>
  </body>
</html>