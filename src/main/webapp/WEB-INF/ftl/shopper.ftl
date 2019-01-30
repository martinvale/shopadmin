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

    <script src="../../script/jquery.js"></script>
    <script src="../../script/jquery-ui.js"></script>

    <script type="text/javascript">

      jQuery(document).ready(function() {
        jQuery(".js-fecha" ).datepicker({
          dateFormat: 'dd/mm/yy',
          onSelect: function(dateText, datePicker) {
            $(this).attr('value', dateText);
          }
        });
      });

    </script>

  </head>
  <body>
    <#include "header.ftl" />

    <#assign endpoint = "create"/>
    <#if model["shopper"]??>
      <#assign editionShopper = model["shopper"] />
      <#assign endpoint = "update"/>
    </#if>
    <div class="container-box-plantilla">
      <h2 class="container-tit">Shopper</h2>
      <form class="form-shop form-shop-big" action="${endpoint}" method="POST" modelAttribute="shopper">
        <#if editionShopper??>
          <input type="hidden" name="id" value="${editionShopper.id?c}" />
        </#if>
        <div class="cell">
          <div class="box-green">
            <fieldset>
              <legend>Datos personales:</legend>
              <div class="field">
                <label class="field-name" for="surname">Apellido: </label>
                <input type="text" id="surname" name="surname" required="true" value="${(editionShopper.surname)!''}" />
              </div>
              <div class="field">
                <label class="field-name" for="firstName">Nombre: </label>
                <input type="text" id="firstName" name="firstName" required="true" value="${(editionShopper.firstName)!''}" />
              </div>
              <div class="field">
                <label class="field-name" for="identityId">DNI: </label>
                <input type="text" id="identityId" name="identityId" required="true" value="${(editionShopper.identityId)!''}" />
                <input type="hidden" name="identityType" value="DNI" />
              </div>
              <div class="field">
                <label class="field-name" for"enabled">Habilitado: </label>
                <input type="checkbox" id="enabled" name="enabled" value="true" <#if (editionShopper.enabled)!true>checked="checked"</#if>/>
              </div>
              <div class="field">
                <label class="field-name" for="birthDate">Fecha de nac.: </label>
                <input type="text" id="birthDate" name="birthDate" required="true" value="${(editionShopper.birthDate?string('dd/MM/yyyy'))!''}" class="js-fecha" />
              </div>
              <div class="field">
                <label class="field-name" for="gender">G&eacute;nero: </label>
                <select id="gender" name="gender">
                  <#assign gender = (editionShopper.gender)!'MALE' />
                  <option value="FEMALE" <#if gender == 'FEMALE'>selected="selected"</#if>>Femenino</option>
                  <option value="MALE" <#if gender == 'MALE'>selected="selected"</#if>>Masculino</option>
                </select>
              </div>
            </fieldset>
            <fieldset>
              <legend>Direcci&oacute;n:</legend>
              <div class="field">
                <label class="field-name" for="address">Calle: </label>
                <input type="text" id="address" name="address" required="true" value="${(editionShopper.address)!''}" />
              </div>
              <div class="field">
                <label class="field-name" for="neighborhood">Barrio: </label>
                <input type="text" id="neighborhood" name="neighborhood" value="${(editionShopper.neighborhood)!''}" />
              </div>
              <div class="field">
                <label class="field-name" for="postalCode">C&oacute;digo postal: </label>
                <input type="text" id="postalCode" name="postalCode" value="${(editionShopper.postalCode)!''}" />
              </div>
              <div class="field">
                <label class="field-name" for="region">Localidad: </label>
                <input type="text" id="region" name="region" value="${(editionShopper.region)!''}" />
              </div>
              <div class="field">
                <label class="field-name" for="state">Provincia: </label>
                <input type="text" id="state" name="state" required="true" value="${(editionShopper.state)!''}" />
              </div>
              <input type="hidden" name="country" value="1" />
            </fieldset>
            <fieldset>
              <legend>Contacto:</legend>
              <div class="field">
                <label class="field-name" for="workPhone">Tel&eacute;fono laboral: </label>
                <input type="text" id="workPhone" name="workPhone" value="${(editionShopper.workPhone)!''}" />
              </div>
              <div class="field">
                <label class="field-name" for="particularPhone">Tel&eacute;fono particular: </label>
                <input type="text" id="particularPhone" name="particularPhone" value="${(editionShopper.particularPhone)!''}" />
              </div>
              <div class="field">
                <label class="field-name" for="cellPhone">Celular: </label>
                <input type="text" id="cellPhone" name="cellPhone" value="${(editionShopper.workPhone)!''}" />
              </div>
              <div class="field">
                <label class="field-name" for="email">E-mail: </label>
                <input type="text" id="email" name="email" required="true" value="${(editionShopper.email)!''}" />
              </div>
              <div class="field">
                <label class="field-name" for="email2">E-mail adicional: </label>
                <input type="text" id="email2" name="email2" value="${(editionShopper.email2)!''}" />
              </div>
            </fieldset>
            <fieldset>
              <legend>Otros datos:</legend>
              <div class="field">
                <label class="field-name" for="education">Educaci&oacute;n: </label>
                <input type="text" id="education" name="education" value="${(editionShopper.education)!''}" />
              </div>
              <div class="field">
                <label class="field-name" for="cobraSf">Cobra SF: </label>
                <input type="checkbox" id="cobraSf" name="cobraSf" value="true" <#if (editionShopper.cobraSf)!false>checked="checked"</#if>/>
              </div>
              <div class="field">
                <label class="field-name" for="confidentiality">Confidencialidad: </label>
                <input type="checkbox" id="confidentiality" name="confidentiality" value="true" <#if (editionShopper.confidentiality)!false>checked="checked"</#if>/>
              </div>
              <div class="field">
                <label class="field-name" for="referrer">Referido: </label>
                <input type="text" id="referrer" name="referrer" value="${(editionShopper.referrer)!''}" />
              </div>
              <div class="field">
                <label class="field-name" for="loginShopmetrics">ID Shopmetrics: </label>
                <input type="text" id="loginShopmetrics" name="loginShopmetrics" value="${(editionShopper.loginShopmetrics)!''}" />
              </div>
              <div class="field">
                <label class="field-name" for="observations">Observaciones: </label>
                <textarea id="observations" name="observations" class="item-field">${(editionShopper.observations)!''}</textarea>
              </div>
            </fieldset>
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