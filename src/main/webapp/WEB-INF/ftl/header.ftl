    <#assign user = model["user"] />
    <header>
      <div class="header-box">
        <h1>Shopnchek<span class="tag-intranet">intranet</span></h1>
        <nav>
          <ul class="menu">
            <li><a href="#"><i class="icon-home"></i>Orden de pago</a>
              <ul class="sub-menu">
                <li><a href="../orden/">Nueva</a></li>
                <li><a href="../orden/search">Buscar</a></li>
              </ul>
            </li>
            <li><a href="#"><i class="icon-user"></i>Items de orden de pago</a>
              <ul class="sub-menu">
                <li><a href="../item/search">Buscar</a></li>
              </ul>
            </li>
            <li><a href="#"><i class="icon-user"></i>Administraci&oacute;n</a>
              <ul class="sub-menu">
                <li><a href="../users/">Usuarios</a></li>
                <li><a href="../proveedores/">Proveedores</a></li>
              </ul>
            </li>
          </ul>
        </nav>
        <p class="user"> ${user.username} <a href="../j_spring_security_logout">Salir</a></p>
      </div>
    </header>
