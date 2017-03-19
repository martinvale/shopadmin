package com.ibiscus.shopnchek.web.controller.domain;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ibiscus.shopnchek.application.ResultSet;
import com.ibiscus.shopnchek.application.proveedor.DeleteProveedorCommand;
import com.ibiscus.shopnchek.application.proveedor.GetProveedorCommand;
import com.ibiscus.shopnchek.application.proveedor.SaveProveedorCommand;
import com.ibiscus.shopnchek.application.proveedor.SearchProveedorCommand;
import com.ibiscus.shopnchek.domain.admin.Proveedor;
import com.ibiscus.shopnchek.domain.security.User;

@Controller
@RequestMapping(value="/proveedores")
public class ProveedorController {

  /** The maximum users to retrieve per page. */
  private final static int PAGE_SIZE = 20;

  @Autowired
  private GetProveedorCommand getProveedorCommand;

  @Autowired
  private SaveProveedorCommand saveProveedorCommand;

  @Autowired
  private DeleteProveedorCommand deleteProveedorCommand;

  @Autowired
  private SearchProveedorCommand searchProveedorCommand;

  @RequestMapping(value = "/suggest", method = RequestMethod.GET)
  public @ResponseBody List<Proveedor> suggest(@RequestParam String term) {
    searchProveedorCommand.setPage(1);
    searchProveedorCommand.setPageSize(PAGE_SIZE);
    searchProveedorCommand.setName(term);
    ResultSet<Proveedor> rsProveedores = searchProveedorCommand.execute();
    return rsProveedores.getItems();
  }

  @RequestMapping(value = "/", method = RequestMethod.GET)
  public String list(@ModelAttribute("model") final ModelMap model) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    model.addAttribute("user", user);

    searchProveedorCommand.setPage(1);
    searchProveedorCommand.setPageSize(PAGE_SIZE);
    searchProveedorCommand.setName(null);
    ResultSet<Proveedor> rsProveedores = searchProveedorCommand.execute();
    model.addAttribute("proveedores", rsProveedores);
    model.addAttribute("start", 1);
    model.addAttribute("page", 1);
    model.addAttribute("pageSize", PAGE_SIZE);
    return "proveedores";
  }

  @RequestMapping(value = "/search")
  public String search(@ModelAttribute("model") final ModelMap model,
      int page, String name) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    model.addAttribute("user", user);

    model.addAttribute("name", name);
    searchProveedorCommand.setPage(page);
    searchProveedorCommand.setPageSize(PAGE_SIZE);
    searchProveedorCommand.setName(name);
    ResultSet<Proveedor> rsProveedores = searchProveedorCommand.execute();
    model.addAttribute("proveedores", rsProveedores);
    int start = ((page - 1) * PAGE_SIZE) + 1;
    model.addAttribute("start", start);
    model.addAttribute("page", page);
    model.addAttribute("pageSize", PAGE_SIZE);
    return "proveedores";
  }

  @RequestMapping(value = "/new", method = RequestMethod.GET)
  public String get(@ModelAttribute("model") final ModelMap model) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    model.addAttribute("user", user);

    return "proveedor";
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  public String get(@ModelAttribute("model") final ModelMap model,
      @PathVariable long id) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    model.addAttribute("user", user);

    getProveedorCommand.setId(id);
    Proveedor proveedor = getProveedorCommand.execute();
    model.addAttribute("proveedor", proveedor);
    return "proveedor";
  }

  @RequestMapping(value = "/proveedor/{id}", method = RequestMethod.DELETE)
  public @ResponseBody boolean delete(@ModelAttribute("model") final ModelMap model,
      @PathVariable long id) {
    deleteProveedorCommand.setId(id);
    return deleteProveedorCommand.execute();
  }

  @RequestMapping(value = "/create", method = RequestMethod.POST)
  public String create(@ModelAttribute("model") final ModelMap model, String descripcion) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    model.addAttribute("user", user);

    saveProveedorCommand.setId(null);
    saveProveedorCommand.setDescription(descripcion);
    Proveedor proveedor = saveProveedorCommand.execute();
    model.addAttribute("proveedor", proveedor);
    return "redirect:" + proveedor.getId();
  }

  @RequestMapping(value = "/update", method = RequestMethod.POST)
  public String update(@ModelAttribute("model") final ModelMap model,
      long id, String descripcion) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    model.addAttribute("user", user);

    saveProveedorCommand.setId(id);
    saveProveedorCommand.setDescription(descripcion);
    Proveedor proveedor = saveProveedorCommand.execute();
    model.addAttribute("proveedor", proveedor);
    return "redirect:" + id;
  }

}
