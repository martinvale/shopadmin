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

    <#assign endpoint = "create"/>
    <#if model["editionUser"]??>
      <#assign editionUser = model["editionUser"] />
      <#assign endpoint = "update"/>
    </#if>
    <div class="container-box-plantilla">
      <h2 class="container-tit">Usuario</h2>
      <form class="form-shop form-shop-big" action="${endpoint}" method="POST">
          <!--div role="alert" class="form-error-txt" aria-hidden="false"><i class="ch-icon-remove"></i>
                  <div class="ch-popover-content">
                      Revisa los datos. Debes completar campos "Número" y "Factura".
                  </div>
          </div-->
        <#if editionUser??>
          <input type="hidden" name="id" value="${editionUser.id?c}" />
        </#if>
        <div class="cell">
          <div class="box-green">
            <div class="field">
              <label class="field-name" for="username">Usuario: </label>
              <input type="text" id="username" name="username" value="${(editionUser.username)!''}" />
              <input type="checkbox" id="state-enabled" name="enabled" value="true" <#if editionUser?? && editionUser.enabled>checked="checked"</#if>/>
              <label for="state-enabled">Habilitado</label>
            </div>
            <div class="field">
              <label class="field-name" for="name">Nombre: </label>
              <input type="text" id="name" name="name" value="${(editionUser.name)!''}" />
            </div>
            <div class="field">
              <label class="field-name" for="password">Password: </label>
              <input type="password" id="password" name="password" value="${(editionUser.password)!''}" />
            </div>
            <div class="field">
              <label class="field-name">Roles:</label>
              <select name="roles" multiple="multiple">
              <#list model["roles"] as role>
                <option value="${role.id?c}" <#if editionUser?? && editionUser.hasRole(role)>selected="selected"</#if>>${role.name}</option>
              </#list>
              <select>
            </div>
          </div>
        </div>
        <ul class="action-columns">
          <li><a href="list" class="btn-shop-small">Cancelar</a></li>
          <li><input type="submit" value="Guardar" class="btn-shop-small"></li>
        </ul>
      </form>
    </div>
  </body>
</html>