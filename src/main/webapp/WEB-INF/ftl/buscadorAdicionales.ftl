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

    <script src="../script/spin.js"></script>
    <script src="../script/jquery.spin.js"></script>

    <script type="text/javascript">

      window.App = window.App || {};

      App.widget = App.widget || {};

      jQuery(document).ready(function() {
        var shopperSelector = new App.widget.ShopperSelector(jQuery(".js-shopper-selector"), true);
        shopperSelector.render();

        var form = jQuery(".js-search-form");
        var loadingIndicator = new App.widget.LoadingIndicator(form);
        form.find("input[type=submit]").click(function () {
          loadingIndicator.start();
        });
      });

    </script>

    <script src="../script/ShopperSelector.js"></script>
    <script src="../script/LoadingIndicator.js"></script>

  </head>
  <body>
    <#include "header.ftl" />

    <div class="container-box-plantilla">
      <h2 class="container-tit">B&uacute;squeda de adicionales</h2>
      <form action="search" class="form-shop form-shop-big js-search-form" method="GET">
          <!--div role="alert" class="form-error-txt" aria-hidden="false"><i class="ch-icon-remove"></i>
                  <div class="ch-popover-content">
                      Revisa los datos. Debes completar campos "Número" y "Factura".
                  </div>
          </div-->
        <div class="cell box-green buscador">
          <div class="field shopper-widget js-shopper-selector">
            <label for="shopper">Shooper: </label>
            <input type="text" id="shopper" name="shopper" value="${(model['shopper'].name)!''}" class="js-shopper" />
            <a id="clear-shopper" href="#" class="clear js-clear">limpiar</a>
            <input type="hidden" name="shopperId" value="" class="js-shopper-id" />
            <input type="hidden" name="shopperDni" value="${model['shopperDni']!''}" class="js-shopper-dni" />
          </div>
          <div class="field">
            <label for="mes">Mes: </label>
            <select id="mes" name="mes">
              <option value=""></option>
            <#assign mesVisita = model["mesVisita"]!-1 />
            <#list 1..12 as mes>
              <option value="${mes}" <#if mes == mesVisita>selected="true"</#if>>${mes}</option>
            </#list>
            </select>
          </div>
          <div class="field">
            <label for="anio">A&ntilde;o: </label>
            <select id="anio" name="anio">
              <option value=""></option>
            <#assign anioVisita = model["anioVisita"]!-1 />
            <#list 2006..2015 as anio>
              <option value="${anio?c}" <#if anio == anioVisita>selected="true"</#if>>${anio?c}</option>
            </#list>
            </select>
          </div>
          <div class="field">
            <label for="usuarioId">Usuario: </label>
            <select id="usuarioId" name="usuarioId">
              <option value=""></option>
            <#list model["users"] as usuario>
              <option value="${usuario.id?c}" <#if usuario.id == model["usuarioId"]!-1>selected="true"</#if>>${usuario.name!''}</option>
            </#list>
            </select>
          </div>
        </div>
        <ul class="action-columns">
          <li><input type="submit" value="Buscar" class="btn-shop-small"></li>
        </ul>
      </form>
      <table summary="Lista de adicionales" class="table-form">
        <thead>
          <tr>
            <th scope="col">Shopper</th>
            <th scope="col">Cliente</th>
            <th scope="col">Sucursal</th>
            <th scope="col">Pago</th>
            <th scope="col">Importe</th>
            <th scope="col">F. Visita</th>
            <th scope="col">F. Cobro</th>
            <th scope="col">Autoriza</th>
            <th scope="col">Nro OP</th>
          </tr>
        </thead>
        <tbody>
          <#list model["items"].data as item>
          <tr>
            <td>${(item.shopper.name)!''} <a href="autorizacion?groupId=${item.group?c}&itemId=${item.id?c}">editar</a></td>
            <td>${item.clienteNombre}</td>
            <td>${item.sucursalNombre}</td>
            <td>${item.tipoPago.description}</td>
            <td>${item.importe?string.currency}</td>
            <td>${item.fecha?string('dd/MM/yyyy')}</td>
            <td>${item.fechaCobro?string('dd/MM/yyyy')}</td>
            <td>${item.username}</td>
            <td>${(item.nroOperacion?c)!''}</td>
          </tr>
          </#list>
        </tbody>
      </table>

      <div class="paginator">
        <#assign parameters = "shopperDni=${model['shopperDni']!''}&mes=${model['mesVisita']!''}&anio=${model['anioVisita']!''}&usuarioId=${model['usuarioId']!''}" />
        <a href="search?page=1&${parameters}">&lt;&lt;</a>
        <#assign start = 0 />
        <#if model["items"].size &gt; 0>
          <#assign start = model["start"] />
          <#list 1..model["items"].size as i>
            <#assign page = (i - 1) / model["pageSize"] />
            <#if (i - 1) % model["pageSize"] == 0>
              <a href="search?page=${page + 1}&${parameters}">${page + 1}</a>
            </#if>
          </#list>
        </#if>
        <#assign lastPage = (model["items"].size / model["pageSize"])?int />
        <#if model["items"].size % model["pageSize"] &gt; 0>
          <#assign lastPage = lastPage + 1 />
        </#if>
        <a href="search?page=${lastPage}&${parameters}">&gt;&gt;</a>

        <#assign maxIndex = model["page"] * model["pageSize"] />
        <#if maxIndex &gt; model["items"].size>
          <#assign maxIndex = model["items"].size />
        </#if>
        <span class="resultset">Adicionales de ${start?c} a ${maxIndex} de ${model["items"].size}</span>
      </div>

    </div>
  </body>
</html>