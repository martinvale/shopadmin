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
    <#assign user = model["user"] />
    <header>
      <div class="header-box">
        <h1>Shopnchek<span class="tag-intranet">intranet</span></h1>
        <#include "header.ftl" />
        <p class="user"> ${user.username} <a href="../j_spring_security_logout">Salir</a></p>
      </div>
    </header>
    <#if model["editionUser"]??>
      <#assign editionUser = model["editionUser"] />
    </#if>
    <div class="container-box-plantilla">
      <h2 class="container-tit">Usuario</h2>
      <#assign username = ""/>
      <#assign name = ""/>
      <#assign profile = 2 />
      <#assign endpoint = "create"/>
      <#if editionUser??>
        <#assign username = "${editionUser.username}"/>
        <#assign name = "${editionUser.name}"/>
        <#assign profile = editionUser.perfil />
        <#assign endpoint = "update"/>
      </#if>
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
              <input type="text" id="username" name="username" value="${username}" />
            </div>
            <div class="field">
              <label class="field-name" for="name">Nombre: </label>
              <input type="text" id="name" name="name" value="${name}" />
            </div>
            <div class="field">
              <label class="field-name">Tipo de usuario:</label>
              <input type="radio" id="profile-standard" name="profile" value="1" <#if profile == 1>checked=checked</#if>/>
              <label for="profile-standard">Standard</label>
              <input type="radio" id="profile-admin" name="profile" value="2" <#if profile == 2>checked=checked</#if>/>
              <label for="profile-admin">Administrador</label>
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