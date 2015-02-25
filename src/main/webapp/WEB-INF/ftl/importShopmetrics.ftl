<!DOCTYPE html>
<html>
  <head>
    <meta charset="utf-8">
    <title>Shopnchek</title>
    <meta http-equiv="cleartype" content="on">

    <link rel="stylesheet" href="../css/base.css">
    <link rel="stylesheet" href="../css/shop.css">
    <link rel="stylesheet" href="../css/custom.css">

    <script src="../script/jquery.js"></script>
    <script src="../script/jquery-ui.js"></script>
    <script src="../script/livevalidation.js"></script>

    <script src="../script/spin.js"></script>
    <script src="../script/jquery.spin.js"></script>

    <script type="text/javascript">

      window.App = window.App || {};

      App.widget = App.widget || {};

      jQuery(document).ready(function() {
        var form = jQuery(".form-shop");
        var loadingIndicator = new App.widget.LoadingIndicator(form);
        form.find("input[type=submit]").click(function () {
          loadingIndicator.start();
        });
      });

    </script>

    <script src="../script/LoadingIndicator.js"></script>

  </head>
  <body>
    <#include "header.ftl" />

    <div class="container-box-plantilla">
      <h2 class="container-tit">Importaci&oacute;n de Shopmetrics</h2>
      <form class="form-shop form-shop-big" action="shopmetrics" enctype="multipart/form-data" method="POST">
      <#if model["status"]??>
        <div role="success" class="form-success-txt" aria-hidden="false">
          <i class="ch-icon-remove"></i>
          <div class="ch-popover-content">${model["status"]}</div>
        </div>
      </#if>
        <!--div role="alert" class="form-error-txt" aria-hidden="false">
          <i class="ch-icon-remove"></i>
          <div class="ch-popover-content">
            Revisa los datos. Debes completar campos "Número" y "Factura".
          </div>
        </div-->
        <div class="box-green">
          <label for="file">Archivo a importar: </label>
          <input id="file" type="file" name="file" />
          <p class="note">El tama&ntilde;o del archivo debe ser menor a 5Mb</p>
        </div>
        <ul class="action-columns">
          <li><input type="submit" value="Importar" class="btn-shop-small"></li>
        </ul>
      </form>
    <#if model["users"]??>
      <p>Los siguientes usuarios no pudieron ser identificados en la tabla de shoppers</p>
      <table summary="Listado de shoppers no identificados" class="table-form">
        <thead>
          <tr>
            <th scope="col" style="width:33%">Login</th>
            <th scope="col" style="width:33%">Apellido</th>
            <th scope="col" style="width:34%">Nombre</th>
          </tr>
        </thead>
        <tbody>
          <#list model["users"] as user>
          <tr>
            <td>${user.login!''}</td>
            <td>${user.lastName!''}</td>
            <td>${user.name!''}</td>
          </tr>
          </#list>
        </tbody>
      </table>
    </#if>
    </div>
  </body>
</html>