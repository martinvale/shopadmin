<!DOCTYPE html>
<html>
    <head>
      <meta charset="utf-8">
      <title>Login</title>
      <meta http-equiv="cleartype" content="on">

      <link rel="stylesheet" href="css/base.css">
      <link rel="stylesheet" href="css/shop.css">

    </head>
    <body>
        <div class="header">
            <h1>Shopncheck</h1>
        </div>
        <div class="container-box">
            <form class="form-shop form-shop-big" action="j_spring_security_check" method="POST">
              <fieldset>
                  <legend>Ingresar a <b>shopncheck </b></legend>
                  <div class="form-shop-row">
                      <label for="input_user">Usuario:</label>
                      <input type="text" size="50" name="username" id="input_user" class="form-shop-icon-input">
                  </div>
                  <div class="form-shop-row">
                      <label for="input_pass">Clave:</label>
                      <#assign className="form-shop-icon-input">
                      <#if model["error"]?? >
                        <#assign className="validation-error">
                      </#if>
                      <input type="password" size="50" name="password" id="input_pass" class="${className}">
                      <#if model["error"]?? >
                        <div role="alert" class="form-error ch-cone" aria-hidden="false">
                          <i class=""></i><div class="ch-popover-content">La clave es incorrecta.</div>
                        </div>
                      </#if>
                  </div>
                  <div class="form-shop-actions">
                      <input type="submit" class="btn-shop" value="Ingresar" name="confirmation">
                  </div>
              </fieldset>
            </form>
        </div>
    </body>
</html>