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

    <#assign tipoCliente = 2 />
    <#assign groupId = "" />
    <#assign clienteId = "" />
    <#assign clienteNombre = "" />
    <#assign tipoSucursal = "lista" />
    <#assign sucursalId = "" />
    <#assign sucursalNombre = "" />
    <#assign shopperDni = "" />
    <#assign mesVisita = "" />
    <#assign anioVisita = "" />
    <#assign fecha = "" />
    <#if model["adicionales"]??>
      <#assign adicional = model["adicionales"][0] />
      <#assign tipoCliente = adicional.tipoItem />
      <#assign groupId = "${adicional.group?c}" />
      <#assign clienteId = "${adicional.clienteId?c}" />
      <#assign clienteNombre = "${adicional.clienteNombre}" />
      <#if adicional.sucursalId == "-1">
        <#assign tipoSucursal = "otra" />
      </#if>
      <#assign sucursalId = "${adicional.sucursalId}" />
      <#assign sucursalNombre = "${adicional.sucursalNombre}" />
      <#assign shopperDni = "${adicional.shopperDni}" />
      <#assign mesVisita = "${adicional.mes}" />
      <#assign anioVisita = "${adicional.anio?c}" />
      <#assign fecha = "${adicional.fecha?string('dd/MM/yyyy')}" />
    </#if>

    <#assign itemId = "" />
    <#assign fechaCobro = "" />
    <#assign tipoPago = "" />
    <#assign importe = "" />
    <#assign observacion = "" />
    <#if model["adicional"]??>
      <#assign item = model["adicional"] />
      <#assign itemId = "${item.id?c}" />
      <#assign fechaCobro = "${item.fechaCobro?string('dd/MM/yyyy')}" />
      <#assign tipoPago = "${item.tipoPago.id?c}" />
      <#assign importe = "${item.importe?c}" />
      <#assign observacion = "${item.observacion!''}" />
    </#if>

App.widget.AdicionalEditor = function (container, enabled, sucursalesMCD,
    sucursalesGAC, sucursalesShopmetrics) {

  var tipoCliente = 2;

  var selectorSucursal = container.find(".js-sucursales");

  var formVisita = container.find(".js-visita");

  var programaValidation;

  var clientShopmetricsValidation;

  var otroClientValidation;

  var sucursalValidation;

  var otraSucursalValidation;

  var shopperValidation;

  var mesValidation;

  var anioValidation;

  var fechaValidation;

  var fechaCobroValidation;

  var importeValidation;

  var getSucursalesShopmetrics = function (clientId) {
    var sucursales = [];
    jQuery.each(sucursalesShopmetrics, function (index, sucursal) {
      if (sucursal.client === clientId) {
        sucursales.push(sucursal);
      }
    });
    return sucursales;
  }

  var updateSucursales = function (sucursales) {
    selectorSucursal.removeAttr("disabled");
    selectorSucursal.empty();
    selectorSucursal.append("<option value=\"Seleccionar\">Seleccione una sucursal"
        +  "</option>");
    jQuery.each(sucursales, function (index, sucursal) {
      selectorSucursal.append("<option value=\"" + sucursal.id + "\">"
          + sucursal.description + "</option>");
    });
  };

  var initValidations = function() {
    programaValidation = new LiveValidation("program");
    programaValidation.add(Validate.Exclusion, {
      within: ["Seleccionar"],
      failureMessage: "Seleccione un programa"
    });

    clientShopmetricsValidation = new LiveValidation("clientShopmetrics");
    clientShopmetricsValidation.add(Validate.Exclusion, {
      within: ["Seleccionar"],
      failureMessage: "Seleccione un cliente"
    });

    otroClientValidation = new LiveValidation("otherClient");
    otroClientValidation.add(Validate.Presence, {
      failureMessage: "Ingrese el nombre del cliente"
    });

    sucursalValidation = new LiveValidation("sucursal");
    sucursalValidation.add(Validate.Exclusion, {
      within: ["Seleccionar"],
      failureMessage: "La sucursal es obligatoria"
    });

    shopperValidation = new LiveValidation("shopper");
    shopperValidation.add(Validate.Exclusion, {
      within: ["Seleccionar"],
      failureMessage: "Seleccione un shopper"
    });

    mesValidation = new LiveValidation("mes");
    mesValidation.add(Validate.Exclusion, {
      within: ["Seleccionar"],
      failureMessage: "Obligatorio"
    });

    anioValidation = new LiveValidation("anio");
    anioValidation.add(Validate.Exclusion, {
      within: ["Seleccionar"],
      failureMessage: "Obligatorio"
    });

    fechaValidation = new LiveValidation("fecha");
    fechaValidation.add(Validate.Presence, {
      failureMessage: "Obligatorio"
    });

    otraSucursalValidation = new LiveValidation("otraSucursalNombre");
    otraSucursalValidation.add(Validate.Presence, {
      failureMessage: "Ingrese un nombre de sucursal"
    });

    fechaCobroValidation = new LiveValidation("fechaCobro");
    fechaCobroValidation.add(Validate.Presence, {
      failureMessage: "Complete la fecha de cobro"
    });

    importeValidation = new LiveValidation("importe");
    importeValidation.add(Validate.Presence, {
      failureMessage: "Ingrese el importe"
    });
  };

  var initEventListeners = function() {
    container.find("input[name='tipoCliente']").click(function(event) {
      tipoCliente = jQuery(event.target).val();
      container.find("input[name='tipoItem']").val(tipoCliente);
      container.find(".js-sucursal-check").removeAttr("disabled");
      if (tipoCliente === "2") {
        updateSucursales(sucursalesMCD);
      } else if (tipoCliente === "4") {
        updateSucursales(sucursalesGAC);
      } else if (tipoCliente === "5") {
        var clientId = container.find(".js-client-shopmetrics").val();
        updateSucursales(getSucursalesShopmetrics(clientId));
      } else {
        container.find(".js-otra-sucursal").attr("checked", "checked");
        container.find(".js-sucursal-check").attr("disabled", true);
        selectorSucursal.attr("disabled", true);
      }
    });

    container.find(".js-client-shopmetrics").change(function(event) {
      clientId = jQuery(event.target).val();
      updateSucursales(getSucursalesShopmetrics(clientId));
    });

    container.find("input[name='tipoSucursal']").click(function(event) {
      var tipoSucursal = jQuery(event.target).val();
      if (tipoSucursal === 'lista') {
        sucursalValidation.enable();
        selectorSucursal.attr("disabled", false);
      } else {
        sucursalValidation.disable();
        selectorSucursal.attr("disabled", true);
      }
    });

    container.find(".js-date" ).datepicker({
      onSelect: function(dateText, datePicker) {
        $(this).attr('value', dateText);
      }
    });

    formVisita.find(".js-add" ).click(function () {
      resetVisitaValidations();
      var validations = getValidations();
      if (LiveValidation.massValidate(validations)) {
        disableVisita();
        enableItems();
      }
    });

  };

  var getValidations = function () {
    var validations = [];
    var tipoCliente = container.find("input[name='tipoCliente']:checked").val();
    var tipoSucursal = container.find("input[name='tipoSucursal']:checked").val();

    if (tipoCliente === "2") {
      validations.push(programaValidation);
    } else if (tipoCliente === "4") {
    } else if (tipoCliente === "5") {
      validations.push(clientShopmetricsValidation);
    } else {
      validations.push(otroClientValidation);
    }

    if (tipoSucursal === 'lista') {
      validations.push(sucursalValidation);
    } else {
      validations.push(otraSucursalValidation);
    }

    validations.push(shopperValidation);
    validations.push(mesValidation);
    validations.push(anioValidation);
    validations.push(fechaValidation);
    return validations;
  };

  var resetVisitaValidations = function () {
    formVisita.find(".LV_invalid").removeClass("LV_invalid");
    formVisita.find(".LV_invalid_field").removeClass("LV_invalid_field");
    formVisita.find(".LV_validation_message").remove();
  };

  var disableVisita = function () {
    formVisita.find("input").attr("disabled", true);
    formVisita.find("select").attr("disabled", true);

    var tipoCliente = container.find("input[name='tipoCliente']:checked").val();
    container.find("input[name='tipoItem']").val(tipoCliente);
    if (tipoCliente === "2") {
      var clienteId = container.find("select[name='program']").val();
      container.find("input[name='clienteId']").val(clienteId);
    } else if (tipoCliente === "4") {
    } else if (tipoCliente === "5") {
      var clienteId = container.find(".js-client-shopmetrics").val();
      container.find("input[name='clienteId']").val(clienteId);
    } else {
      var clienteNombre = container.find("input[name='otherClient']").val();
      container.find("input[name='clienteNombre']").val(clienteNombre);
    }

    var tipoSucursal = container.find("input[name='tipoSucursal']:checked").val();
    if (tipoSucursal === 'lista') {
      var sucursalId = container.find("select[name='sucursal']").val();
      container.find("input[name='sucursalId']").val(sucursalId);
    } else {
      var sucursalNombre = container.find("input[name='otraSucursalNombre']").val();
      container.find("input[name='sucursalNombre']").val(sucursalNombre);
    }

    var shopperDni = container.find("select[name='shopper']").val();
    container.find("input[name='shopperDni']").val(shopperDni);
    var mes = container.find("select[name='mesValue']").val();
    container.find("input[name='mes']").val(mes);
    var anio = container.find("select[name='anioValue']").val();
    container.find("input[name='anio']").val(anio);
    var fecha = container.find("input[name='fechaValue']").val();
    container.find("input[name='fecha']").val(fecha);
  }

  var enableItems = function () {
    container.find(".js-item-field").attr("disabled", false);
    container.find(".js-autorizar").removeAttr("disabled");
  }

  return {
    render: function() {
      initEventListeners();
      initValidations();
      if (!enabled) {
        disableVisita();
        enableItems();
      }
    }
  }

}

      jQuery(document).ready(function() {
        var sucursalesMCD = [
        <#list model["sucursalesMCD"] as sucursal>
          {
            id: "${sucursal.id}",
            description: "${sucursal.description}"
          }
        <#if sucursal_has_next>,</#if></#list>
        ];

        var sucursalesGAC = [];

        var sucursalesShopmetrics = [
        <#list model["sucursalesShopmetrics"] as sucursal>
          {
            id: "${sucursal.id}",
            client: "${sucursal.client.id?c}",
            description: "${sucursal.description}"
          }
        <#if sucursal_has_next>,</#if></#list>
        ];

        var editorContainer = jQuery(".js-editor-adicional");
        var enabled = <#if adicional??>false<#else>true</#if>;
        var editor = App.widget.AdicionalEditor(editorContainer, enabled,
            sucursalesMCD, sucursalesGAC, sucursalesShopmetrics);
        editor.render();
      });

    </script>

    <!--script src="../script/ShopperSelector.js"></script-->

  </head>
  <body>
    <#include "header.ftl" />

    <div class="container-box-plantilla">
      <h2 class="container-tit">Autorizaci&oacute;n de adicionales</h2>

      <form action="autorizar" method="POST" class="form-shop form-shop-big js-editor-adicional">
       <!--div role="alert" class="form-error-txt" aria-hidden="false"><i class="ch-icon-remove"></i>
          <div class="ch-popover-content">
            Revisa los datos. Debes completar campos "Número" y "Factura".
          </div>
        </div-->
        <h2 class="subtitulo">Visita</h2>
        <!-- FILA 1 -->
        <div class="cell js-visita">
          <div class="box-gray">
            <fieldset>
              <h3 class="subt-text">Cliente</h3>
              <ul class="box-client">
                <li class="form-shop">
                  <input type="radio" value="2" id="mcd" name="tipoCliente" <#if tipoCliente == 2>checked="checked"</#if>>
                  <label for="mcd">MCD</label>
                </li>
                <li class="form-shop">
                  <input type="radio" value="4" id="gac" name="tipoCliente" <#if tipoCliente == 4>checked="checked"</#if> disabled="true">
                  <label for="gac">GAC</label>
                </li>
                <li class="form-shop">
                  <input type="radio" value="5" id="metrics" name="tipoCliente" <#if tipoCliente == 5>checked="checked"</#if>>
                  <label for="metrics">Shopmetrics</label>
                </li>
                <li class="form-shop">
                  <input type="radio" value="3" id="other" name="tipoCliente" <#if tipoCliente == 3>checked="checked"</#if>>
                  <label for="other">Otro</label>
                </li>
              </ul>

              <div class="box-data-client">
                <div class="form-shop-row">
                  <label for="program">Programa:</label>
                  <select id="program" name="program">
                    <option value="Seleccionar">Seleccionar...</option>
                    <#list model["programas"] as programa>
                      <option value="${programa.id?c}" <#if tipoCliente == 2 && programa.id?c == clienteId>selected</#if>>${programa.nombre!''}</option>
                    </#list>
                  </select>
                </div>
                <div class="form-shop-row">
                  <label for="client2">Cliente:</label>
                  <select id="client2" disabled="true">
                    <option value="0">Seleccionar...</option>
                  </select>
                </div>
                <div class="form-shop-row">
                  <label for="clientShopmetrics">Cliente:</label>
                  <select id="clientShopmetrics" name="clientShopmetrics" class="js-client-shopmetrics">
                    <option value="Seleccionar">Seleccionar...</option>
                    <#list model["clientes"] as cliente>
                      <option value="${cliente.id?c}" <#if tipoCliente == 5 && cliente.id?c == clienteId>selected="true"</#if>>${cliente.nombre!''}</option>
                    </#list>
                  </select>
                </div>
                <div class="form-shop-row">
                  <label for="otherClient">Cliente:</label>
                  <input type="text" id="otherClient" name="otherClient" />
                </div>
              </div>
            </fieldset>
            <hr />
            <fieldset>
              <h3 class="subt-text">Sucursal</h3>
              <ul class="box-sucursal sucursal">
                <li class="form-shop">
                  <input type="radio" value="lista" id="lista" name="tipoSucursal" <#if tipoSucursal == 'lista'>checked="checked"</#if> class="js-sucursal-check"/>
                  <label for="lista">Lista</label>
                  <select id="sucursal" name="sucursal" class="js-sucursales">
                    <option value="Seleccionar">Seleccione una sucursal</option>
                  <#if tipoCliente == 2>
                    <#list model["sucursalesMCD"] as sucursal>
                      <option value="${sucursal.id}" <#if tipoSucursal == "lista" && sucursal.id == sucursalId>selected="true"</#if>>${sucursal.description}</option>
                    </#list>
                  </#if>
                  <#if tipoCliente == 5>
                    <#list model["sucursalesShopmetrics"] as sucursal>
                      <option value="${sucursal.id}" <#if tipoSucursal == "lista" && sucursal.id == sucursalId>selected="true"</#if>>${sucursal.description}</option>
                    </#list>
                  </#if>
                  </select>
                </li>
                <li class="form-shop">
                  <input type="radio" value="manual" id="otraSucursal" name="tipoSucursal" <#if tipoSucursal == 'otra'>checked="checked"</#if> class="js-otra-sucursal" />
                  <label for="otraSucursal">Otra</label>
                  <input id="otraSucursalNombre" type="text" size="50" name="otraSucursalNombre" <#if tipoSucursal == "otra">value="${sucursalNombre}"</#if>/>
                </li>
              </ul>
            </fieldset>
            <hr />
            <fieldset>
              <ul class="box-sucursal">
                <li class="form-shop">
                  <label for="shopper">Shopper</label>
                  <select id="shopper" name="shopper">
                    <option value="Seleccionar">Seleccione un shopper</option>
                  <#list model["shoppers"] as shopper>
                    <option value="${shopper.dni}" <#if shopper.dni == shopperDni>selected="true"</#if>>${shopper.name!''}</option>
                  </#list>
                  </select>
                </li>
                <li class="form-shop date-selector">
                  <div>
                    <label for="mes">Mes Trab.</label>
                    <select id="mes" name="mesValue">
                      <option value="Seleccionar">Mes</option>
                    <#list 1..12 as mes>
                      <option value="${mes}" <#if mes?c == mesVisita>selected="true"</#if>>${mes}</option>
                    </#list>
                    </select>
                  </div>
                  <div>
                    <label for="anio">A&ntilde;o Trab.</label>
                    <select id="anio" name="anioValue">
                      <option value="Seleccionar">A&ntilde;o</option>
                    <#list 2006..2015 as anio>
                      <option value="${anio?c}" <#if anio?c == anioVisita>selected="true"</#if>>${anio?c}</option>
                    </#list>
                    </select>
                  </div>
                  <div class="full">
                    <label for="fecha">Fecha</label>
                    <input type="text" id="fecha" name="fechaValue" class="js-date" value="${fecha}" />
                  </div>
                </li>
              </ul>
            </fieldset>
          </div>
          <ul class="action-columns">
            <!--li><input type="button" class="btn-shop-small js-reset" value="Limpiar" name="clean" /></li-->
            <li><input type="button" class="btn-shop-small js-add" value="Aceptar" name="save" /></li>
          </ul>
        </div>
        <!-- FIN FILA 1 -->

        <!-- FILA 2 -->
        <h2 class="subtitulo">Items</h2>
        <fieldset>
          <input type="hidden" name="tipoItem" />
          <input type="hidden" name="groupId" value="${groupId}" />
          <input type="hidden" name="clienteId" />
          <input type="hidden" name="clienteNombre" />
          <input type="hidden" name="sucursalId" />
          <input type="hidden" name="sucursalNombre" />
          <input type="hidden" name="shopperDni" />
          <input type="hidden" name="mes" />
          <input type="hidden" name="anio" />
          <input type="hidden" name="fecha" />
          <input type="hidden" name="itemId" value="${itemId}"/>
          <div class="form-shop-row">
            <label for="fechaCobro">Fecha de cobro:</label>
            <input type="text" id="fechaCobro" name="fechaCobro" value="${fechaCobro}" disabled="true" class="js-item-field js-date" />
          </div>
          <div class="form-shop-row">
            <label for="tipoPago">Tipo de pago:</label>
            <select id="tipoPago" name="tipoPagoId" class="js-item-field" disabled="true">
              <#list model["tiposPago"] as tipoPagoItem>
                <option value="${tipoPagoItem.id?c}" <#if tipoPagoItem.id?c == tipoPago>selected="true"</#if>>${tipoPagoItem.description}</option>
              </#list>
            </select>
          </div>
          <div class="form-shop-row">
            <label for="importe">Importe:</label>
            <input type="text" id="importe" name="importe" value="${importe}" class="js-item-field js-importe" disabled="true"/>
          </div>
          <div class="form-shop-row">
            <label for="observaciones">Observaciones:</label>
            <textarea id="observaciones" name="observacion" class="observacion js-item-field" disabled="true">${observacion}</textarea>
          </div>
          <ul class="action-columns">
            <#assign buttonName = "Autorizar" />
            <#if itemId != "">
              <#assign buttonName = "Modificar" />
            </#if>
            <li><input type="submit" class="btn-shop-small js-autorizar" value="${buttonName}" disabled="true" /></li>
          </ul>
        </fieldset>
        <table summary="Lista de items" class="table-form ">
          <thead>
            <tr>
              <th scope="col" style="width: 20%">Cliente</th>
              <th scope="col" style="width: 25%">Sucursal</th>
              <th scope="col" style="width: 15%">Pago</th>
              <th scope="col" style="width: 10%">Importe</th>
              <th scope="col" style="width: 10%">Fecha visita</th>
              <th scope="col" style="width: 10%">Fecha cobro</th>
              <th scope="col" style="width: 10%">Usuario Aut.</th>
            </tr>
          </thead>
          <tbody>
          <#if model["adicionales"]??>
            <#list model["adicionales"] as adicional>
            <tr>
              <td>${adicional.clienteNombre!''} <a href="autorizacion?groupId=${groupId}&itemId=${adicional.id?c}">editar</a> <a href="delete?groupId=${groupId}&itemId=${adicional.id?c}">borrar</a></td>
              <td>${adicional.sucursalNombre!''}</td>
              <td>${adicional.tipoPago.description}</td>
              <td>${adicional.importe?string.currency}</td>
              <td>${adicional.fecha?string('dd/MM/yyyy')}</td>
              <td>${adicional.fechaCobro?string('dd/MM/yyyy')}</td>
              <td>${adicional.username}</td>
            </tr>
            </#list>
          </#if>
          </tbody>
        </table>
        <!-- FIN FILA 2 -->
 

        <div class="actions-form">
          <ul class="action-columns">
            <li><a href="autorizacion" class="btn-shop">Nuevo grupo</a></li>
          </ul>
        </div>
      </form>
    </div>
  </body>
</html>