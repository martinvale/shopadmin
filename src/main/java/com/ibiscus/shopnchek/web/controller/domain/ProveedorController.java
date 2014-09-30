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

import com.ibiscus.shopnchek.domain.admin.Proveedor;
import com.ibiscus.shopnchek.domain.admin.ProveedorRepository;
import com.ibiscus.shopnchek.domain.util.ResultSet;

@Controller
@RequestMapping(value="/proveedores")
public class ProveedorController {

  /** The maximum users to retrieve per page. */
  private final static int PAGE_SIZE = 20;

  /** Repository of shoppers. */
  @Autowired
  private ProveedorRepository proveedorRepository;

  @RequestMapping(value = "/suggest", method = RequestMethod.GET)
  public @ResponseBody List<Proveedor> suggest(@RequestParam String term) {
    return proveedorRepository.find(1, PAGE_SIZE, term);
  }

  @RequestMapping(value = "/", method = RequestMethod.GET)
  public String list(@ModelAttribute("model") final ModelMap model) {
    org.springframework.security.core.userdetails.User user;
    user = (org.springframework.security.core.userdetails.User) SecurityContextHolder
        .getContext().getAuthentication().getPrincipal();
    model.addAttribute("user", user);

    List<Proveedor> proveedores = proveedorRepository.find(1, PAGE_SIZE, null);
    int size = proveedorRepository.getProveedoresCount(null);
    model.addAttribute("proveedores", new ResultSet<Proveedor>(proveedores, size));
    model.addAttribute("start", 1);
    model.addAttribute("page", 1);
    model.addAttribute("pageSize", PAGE_SIZE);
    return "proveedores";
  }

  @RequestMapping(value = "/search")
  public String search(@ModelAttribute("model") final ModelMap model,
      int page, String name) {
    org.springframework.security.core.userdetails.User user;
    user = (org.springframework.security.core.userdetails.User) SecurityContextHolder
        .getContext().getAuthentication().getPrincipal();
    model.addAttribute("user", user);

    model.addAttribute("name", name);
    int start = ((page - 1) * PAGE_SIZE) + 1;
    List<Proveedor> proveedores = proveedorRepository.find(start, PAGE_SIZE, name);
    int size = proveedorRepository.getProveedoresCount(name);
    model.addAttribute("proveedores", new ResultSet<Proveedor>(proveedores, size));
    model.addAttribute("start", start);
    model.addAttribute("page", page);
    model.addAttribute("pageSize", PAGE_SIZE);
    return "proveedores";
  }

  @RequestMapping(value = "/new", method = RequestMethod.GET)
  public String get(@ModelAttribute("model") final ModelMap model) {
    org.springframework.security.core.userdetails.User user;
    user = (org.springframework.security.core.userdetails.User) SecurityContextHolder
        .getContext().getAuthentication().getPrincipal();
    model.addAttribute("user", user);

    return "proveedor";
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  public String get(@ModelAttribute("model") final ModelMap model,
      @PathVariable long id) {
    org.springframework.security.core.userdetails.User user;
    user = (org.springframework.security.core.userdetails.User) SecurityContextHolder
        .getContext().getAuthentication().getPrincipal();
    model.addAttribute("user", user);

    Proveedor proveedor = proveedorRepository.get(id);
    model.addAttribute("proveedor", proveedor);
    return "proveedor";
  }

  @RequestMapping(value = "/proveedor/{id}", method = RequestMethod.DELETE)
  public @ResponseBody boolean delete(@ModelAttribute("model") final ModelMap model,
      @PathVariable long id) {
    proveedorRepository.delete(id);
    return true;
  }

  @RequestMapping(value = "/create", method = RequestMethod.POST)
  public String create(@ModelAttribute("model") final ModelMap model,
      String cuit, String descripcion, String factura) {
    org.springframework.security.core.userdetails.User user;
    user = (org.springframework.security.core.userdetails.User) SecurityContextHolder
        .getContext().getAuthentication().getPrincipal();
    model.addAttribute("user", user);

    Proveedor proveedor = new Proveedor(cuit, descripcion, factura);
    long id = proveedorRepository.save(proveedor);
    model.addAttribute("proveedor", proveedor);
    return "redirect:" + id;
  }

  @RequestMapping(value = "/update", method = RequestMethod.POST)
  public String update(@ModelAttribute("model") final ModelMap model,
      long id, String cuit, String descripcion, String factura) {
    org.springframework.security.core.userdetails.User user;
    user = (org.springframework.security.core.userdetails.User) SecurityContextHolder
        .getContext().getAuthentication().getPrincipal();
    model.addAttribute("user", user);

    Proveedor proveedor = proveedorRepository.get(id);
    proveedor.update(cuit, descripcion, factura);
    proveedorRepository.update(proveedor);
    model.addAttribute("proveedor", proveedor);
    return "redirect:" + id;
  }

}
