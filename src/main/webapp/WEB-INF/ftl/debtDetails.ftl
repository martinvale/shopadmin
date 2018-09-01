<!DOCTYPE html>
<html>
  <head>
    <meta charset="utf-8">
    <title>Shopnchek</title>
    <meta http-equiv="cleartype" content="on">

    <link rel="stylesheet" href="../css/base.css">
    <link rel="stylesheet" href="../css/shop.css">
    <link rel="stylesheet" href="../css/custom.css">

    <link rel="stylesheet" href="../font-awesome/css/font-awesome.min.css">

  </head>
  <body>
    <div class="container-box-plantilla">
      <h2 class="container-tit">Detalle de visitas de ${model["shopper"].name} a ${model["client"].name}</h2>
      <table summary="Lista de adicionales" class="table-form js-results">
        <thead>
          <tr>
            <th scope="col">Sucursal</th>
            <th scope="col">Tipo de Pago</th>
            <th scope="col">Importe</th>
            <th scope="col">F. Visita</th>
            <th scope="col">Estado</th>
          </tr>
        </thead>
        <tbody>
          <#list model["items"] as item>
          <tr>
            <td>${(item.branch.address)!(item.branchDescription)!''}</td>
            <td>${item.tipoPago}</td>
            <td>$ ${item.importe?string["0.##"]?replace(',', '.')}</td>
            <td>${item.fecha?string('dd/MM/yyyy')}</td>
            <td>${(item.estado)!''}</td>
          </tr>
          </#list>
        </tbody>
      </table>

    </div>
  </body>
</html>