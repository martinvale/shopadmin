<!DOCTYPE html>
<html>
  <head>
    <meta charset="utf-8">
    <title>Shopnchek</title>
    <meta http-equiv="cleartype" content="on">

    <link rel="stylesheet" href="../../css/jquery-ui/jquery-ui.css">

    <link rel="stylesheet" href="../../css/base.css">
    <link rel="stylesheet" href="../../css/shop.css">
    <link rel="stylesheet" href="../../css/custom.css">

  </head>
  <body>
    <#include "header.ftl" />

    <#assign endpoint = "create"/>
    <#if model["client"]??>
      <#assign editionClient = model["client"] />
      <#assign endpoint = "update"/>
    </#if>
    <div class="container-box-plantilla">
      <h2 class="container-tit">Usuario</h2>
      <form class="form-shop form-shop-big" action="save" method="POST">
        <#if editionClient??>
          <input type="hidden" name="id" value="${editionClient.id?c}" />
        </#if>
        <div class="cell">
          <div class="box-green">
            <div class="field">
              <label class="field-name" for="name">Nombre: </label>
              <input type="text" id="name" name="name" value="${(editionClient.name)!''}" />
            </div>
            <div class="field">
              <label class="field-name">Pais:</label>
              <select id="country" name="country">
              <#list model["countries"] as country>
                <option value="${country}" <#if editionClient?? && editionClient.country!'' == country>selected="selected"</#if>>${country}</option>
              </#list>
              <select>
            </div>
          </div>
        </div>
        <ul class="action-columns">
          <li><a href="." class="btn-shop-small">Cancelar</a></li>
          <li><input type="submit" value="Guardar" class="btn-shop-small"></li>
        </ul>
      </form>
    </div>
  </body>
</html>