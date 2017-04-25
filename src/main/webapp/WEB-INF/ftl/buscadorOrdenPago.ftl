<!DOCTYPE html>
<html>
  <head>
    <#import "/spring.ftl" as spring />
    <meta charset="utf-8">
    <title>Shopnchek</title>
    <meta http-equiv="cleartype" content="on">

    <link rel="stylesheet" href="../css/jquery-ui/jquery-ui.css">

    <link rel="stylesheet" href="../css/base.css">
    <link rel="stylesheet" href="../css/shop.css">
    <link rel="stylesheet" href="../css/custom.css">

    <link rel="stylesheet" href="../font-awesome/css/font-awesome.min.css">

    <script src="../script/jquery.js"></script>
    <script src="../script/jquery-ui.js"></script>
    <script src="../script/pure.min.js"></script>
    <script src="../script/livevalidation.js"></script>

    <script src="../script/spin.js"></script>
    <script src="../script/jquery.spin.js"></script>

    <script type="text/javascript">

      window.App = window.App || {};

      App.widget = App.widget || {};

App.widget.Buscador = function (container) {

  var tabs = container.find(".js-filters").tabs();

  var titularSelector;

  var initEventListeners = function () {
    var loadingIndicator = new App.widget.LoadingIndicator(container);
    container.find("input[type=submit]").click(function () {
      loadingIndicator.start();
    });
  };

  return {
    render: function() {
      titularSelector = new App.widget.TitularSelector(
          container.find(".js-titular-selector"), "<@spring.url '/titular/suggest'/>");
      titularSelector.render();

      var shopperSelector = new App.widget.ShopperSelector(jQuery(".js-shopper-selector"), true);
      shopperSelector.render();

      initEventListeners();
    }
  }
}

      jQuery(document).ready(function() {
        var buscador = new App.widget.Buscador(jQuery(".js-search"));
        buscador.render();
      });
    </script>

    <script src="../script/TitularSelector.js"></script>
    <script src="../script/ShopperSelector.js"></script>
    <script src="../script/LoadingIndicator.js"></script>

  </head>
  <body>
    <#include "header.ftl" />
    <#setting locale="es_AR">

    <div class="container-box-plantilla">
      <h2 class="container-tit">Buscar Orden de pago</h2>
      <form action="searchByNumber" method="POST" class="form-shop form-shop-big js-search">
        <!-- FILA 1 -->
        <div class="cell box-green buscador">
          <fieldset>
            <legend>B&uacute;squeda simple</legend>
            <div class="form-shop-row-left">
              <label for="number">N&uacute;mero</label>
              <input type="text" name="numeroOrden" id="number" value="${(model['numeroOrden']?c)!""}" />
            </div>
          </fieldset>
        </div>
        <!-- FIN FILA 1 -->
        <!-- FIN FILA 2 -->
        <ul class="action-columns">
          <li><input type="submit" class="btn-shop-small" value="Buscar"></li>
        </ul>
        <!-- FIN FILA 2 -->
      </form>
      <form action="search" method="GET" class="form-shop form-shop-big js-search">
        <!-- FILA 1 -->
        <div class="cell box-green buscador">
          <fieldset>
            <legend>B&uacute;squeda avanzada</legend>
            <div class="field js-titular-selector">
              <label for="titularNombre">Titular</label>
              <#assign titularId = "${(model['titularId']?c)!''}" />
              <#assign titularNombre = "${model['titularNombre']!''}" />
              <input id="titularNombre" type="text" value="${titularNombre}" class="nombre-titular js-titular-nombre" />
              <input type="hidden" name="titularId" value="${titularId}" class="js-titular-id" />
              <input type="hidden" name="tipoTitular" value="${model['tipoTitular']!1}" class="js-titular-tipo" />
            </div>
            <div class="field js-shopper-selector">
              <label for="shopper">Shopper incluido en la orden</label>
              <input type="text" id="shopper" name="shopper" value="${model['shopper']!''}" class="js-shopper" />
              <a id="clear-shopper" href="#" class="clear js-clear">limpiar</a>
              <input type="hidden" name="shopperId" value="${(model['shopperId']?c)!''}" class="js-shopper-id" />
              <input type="hidden" name="shopperDni" value="${model['shopperDni']!''}" class="js-shopper-dni" />
            </div>
            <div class="field">
              <label for="numeroCheque">Nro de cheque</label>
              <input id="numeroCheque" type="text" name="numeroCheque" value="${model["numeroCheque"]!""}" />
            </div>
            <div class="field">
              <label for="state">Estado de la orden</label>
              <select id="state" name="estadoId">
                <option value=""></option>
                <#assign state = model["state"]!0 />
                <#list model["orderStates"] as orderState>
                  <option value="${orderState.id}" <#if orderState.id == state>selected="selected"</#if>>${orderState.description}</option>
                </#list>
              </select>
            </div>
          </fieldset>
        </div>
        <!-- FIN FILA 1 -->
        <!-- FIN FILA 2 -->
        <ul class="action-columns">
          <li><input type="submit" class="btn-shop-small" value="Buscar"></li>
        </ul>
        <!-- FIN FILA 2 -->
      </form>

      <h2 class="subtitulo">Ordenes de pago</h2>
      <#assign orderBy = model['orderBy']!'fechaPago' />
      <#assign ascending = model['ascending']!false />
      <#assign direction = 'down' />
      <#if model['ascending']>
        <#assign direction = 'up' />
      </#if>
      <#assign parameters = "shopperDni=${model['shopperDni']!''}&tipoTitular=${model['tipoTitular']!''}&titularId=${(model['titularId']?c)!''}&estadoId=${model['state']!''}&numeroCheque=${model['numeroCheque']!''}" />
      <table summary="Listado de ordenes de pago" class="table-form">
        <thead>
          <tr>
            <th scope="col"><a href="search?${parameters}&orderBy=numero&ascending=${(!ascending)?c}" <#if orderBy == 'numero'>class="selected"</#if>>Nro de Orden <#if orderBy == 'numero'><i class="fa fa-angle-${direction}"></i></#if></a></th>
            <th scope="col">Titular</th>
            <th scope="col">Importe</th>
            <th scope="col"><a href="search?${parameters}&orderBy=fechaPago&ascending=${(!ascending)?c}" <#if orderBy == 'fechaPago'>class="selected"</#if>>Fecha de Pago <#if orderBy == 'fechaPago'><i class="fa fa-angle-${direction}"></i></#if></a></th>
            <th scope="col"><a href="search?${parameters}&orderBy=estado&ascending=${(!ascending)?c}" <#if orderBy == 'estado'>class="selected"</#if>>Estado <#if orderBy == 'estado'><i class="fa fa-angle-${direction}"></i></#if></a></th>
          </tr>
        </thead>
        <tbody>
          <#assign resultSet = model["result"] />
          <#list resultSet.items as orden>
          <tr>
            <td>${orden.numero?c} <a href="${orden.numero?c}">editar</a></td>
            <td>${orden.titular!''}</td>
            <td class="js-importe">$ ${orden.importe}</td>
            <td class="js-fecha">${orden.fechaPago}</td>
            <td class="js-estado">${orden.state}</td>
          </tr>
          </#list>
        </tbody>
      </table>

      <div class="paginator">
        <#if model["page"] &gt; 1>
          <a href="?page=${(model['page'] - 1)}&${parameters}&orderBy=${orderBy}&ascending=${ascending?c}">&lt;&lt;</a>
        <#else>
          <span>&lt;&lt;</span>
        </#if>

        <#assign maxIndex = model["page"] * model["pageSize"] />
        <#if maxIndex &gt; resultSet.count>
          <#assign maxIndex = resultSet.count />
          <span>&gt;&gt;</span>
        <#else>
          <a href="?page=${(model['page'] + 1)}&${parameters}&orderBy=${orderBy}&ascending=${ascending?c}">&gt;&gt;</a>
        </#if>

        <#assign start = 0 />
        <#if resultSet.count &gt; 0>
          <#assign start = ((model["page"] - 1) * model["pageSize"]) + 1 />
        </#if>
        <span class="resultset">Adicionales de ${(start)?c} a ${maxIndex} de ${resultSet.count}</span>
        <a class="tool" href="download?${parameters}">descargar</a>
      </div>

    </div>
  </body>
</html>