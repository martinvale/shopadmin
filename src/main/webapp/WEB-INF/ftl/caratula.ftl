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

.detail {
  font-size: 20px;
  font-style: italic;
  margin-bottom: 5px;
}

.clients table {
  font-size: 16px;
  margin: 0 auto;
  width: 85%;
}

.clients table tr {
  height: 35px;
}

.clients table .check div {
  border: 1px solid #000000;
  height: 20px;
  width: 20px;
}

</style>
  </head>
  <body>
    <p id="print-button"><button onclick="window.print()">Imprimir</button></p>
    <div class="page">
      <p class="title">Orden de pago</p>
      <p class="subtitle">Car&aacute;tula</p>
      <p class="heading">
        <span class="numero"><span class="name">Nro</span> ${model['ordenPago'].numero?c}</span>
        <span class="fechaPago"><span class="name">Fecha de pago</span> ${model['ordenPago'].fechaPago?string('dd/MM/yyyy')}</span>
      </p>
      <p class="detail">Clientes</p>
      <hr/>
      <div class="clients">
        <table>
          <#list model["clients"] as client>
            <tr>
              <td class="client">${client}</td>
              <td class="check"><div /></td>
            </tr>
          </#list>
        </table>
      </div>
      <hr/>
    </div>
  </body>
</html>