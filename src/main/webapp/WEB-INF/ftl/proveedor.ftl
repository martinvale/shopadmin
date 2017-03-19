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

    <script src="../script/jquery.js"></script>
    <script src="../script/jquery-ui.js"></script>
    <script src="../script/pure.min.js"></script>
    <script src="../script/livevalidation.js"></script>

    <script type="text/javascript">

      window.App = window.App || {};

      App.widget = App.widget || {};

      jQuery(document).ready(function() {
      });

    </script>

    <!--script src="../script/ShopperSelector.js"></script-->

  </head>
  <body>
    <#include "header.ftl" />

    <#if model["proveedor"]??>
      <#assign proveedor = model["proveedor"] />
    </#if>
    <div class="container-box-plantilla">
      <h2 class="container-tit">Proveedor</h2>
      <#assign description = ""/>
      <#assign endpoint = "create" />
      <#if proveedor??>
        <#assign description = "${proveedor.description}"/>
        <#assign endpoint = "update" />
      </#if>
      <form class="form-shop form-shop-big" action="${endpoint}" method="POST">
          <!--div role="alert" class="form-error-txt" aria-hidden="false"><i class="ch-icon-remove"></i>
                  <div class="ch-popover-content">
                      Revisa los datos. Debes completar campos "Número" y "Factura".
                  </div>
          </div-->
        <#if proveedor??>
          <input type="hidden" name="id" value="${proveedor.id?c}" />
        </#if>
        <div class="cell">
          <div class="box-green">
            <div class="field">
              <label class="field-name" for="description">Nombre: </label>
              <input type="text" id="description" name="descripcion" value="${description}" />
            </div>
          </div>
        </div>
        <ul class="action-columns">
          <li><a href="." class="btn-shop-small">Volver a la lista</a></li>
          <li><input type="submit" value="Guardar" class="btn-shop-small"></li>
        </ul>
      </form>
    </div>
  </body>
</html>