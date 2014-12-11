<!DOCTYPE html>
<html>
  <head>
    <meta charset="utf-8">
    <title>Shopnchek</title>
    <meta http-equiv="cleartype" content="on">

    <link rel="stylesheet" href="../css/jquery-ui/jquery-ui.css">

    <link rel="stylesheet" href="../css/base.css">
    <link rel="stylesheet" href="../css/shop.css">
    <link rel="stylesheet" href="../css/custom.css">

<style>
@media print {
  #print-button {
    display: none;
  }
}

#print-button {
  text-align: center;
}

.table-form td.total {
  text-align: left;
}

.totals .label {
  font-weight: bold;
  width: 90%;
  text-align: right;
}

</style>
  </head>
  <body>
    <p id="print-button"><button onclick="window.print()">Imprimir</button></p>

    <div class="container-box-plantilla">
      <h2 class="container-tit">Listado de items adeudados</h2>
      <table summary="Listado de items adeudados" class="table-form">
        <tbody>
          <#assign totalHonorarios = 0 />
          <#assign totalReintegros = 0 />
          <#assign totalOtrosGastos = 0 />
          <#list model["items"] as item>
            <tr>
              <td>${item.empresa!''}</td>
              <td>${item.local!''}</td>
              <td>${item.anio?c}</td>
              <td>${item.mes?c}</td>
              <td>${item.fecha}</td>
              <td>${item.descripcion}</td>
              <td>${item.importe?string.currency}</td>
              <#if item.descripcion == 'HONORARIOS'>
                <#assign totalHonorarios = totalHonorarios + item.importe />
              <#elseif item.descripcion == 'REINTEGROS'>
                <#assign totalReintegros = totalReintegros + item.importe />
              <#else>
                <#assign totalOtrosGastos = totalOtrosGastos + item.importe />
              </#if>
            </tr>
          </#list>
        </tbody>
      </table>
      <hr />
      <table class="table-form totals">
        <tbody>
          <tr>
            <td class="label">Honorarios:</td>
            <td>${totalHonorarios?string.currency}</td>
          </tr>
          <tr>
            <td class="label">Reintegros:</td>
            <td>${totalReintegros?string.currency}</td>
          </tr>
          <tr>
            <td class="label">Otros gastos:</td>
            <td>${totalOtrosGastos?string.currency}</td>
          </tr>
          <tr>
            <td>&nbsp;</td>
            <td>&nbsp;</td>
          </tr>
          <tr>
            <td class="label">Total:</td>
            <td>${(totalHonorarios + totalReintegros + totalOtrosGastos)?string.currency}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </body>
</html>