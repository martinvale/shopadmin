package com.ibiscus.shopnchek.web.controller.domain;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ibiscus.shopnchek.domain.admin.Proveedor;
import com.ibiscus.shopnchek.domain.admin.ProveedorRepository;

@Controller
@RequestMapping(value="/services/proveedores")
public class ProveedorController {

  /** Repository of shoppers. */
  @Autowired
  private ProveedorRepository proveedorRepository;

  @RequestMapping(value = "/suggest", method = RequestMethod.GET)
  public @ResponseBody List<Proveedor> suggest(@RequestParam String term) {
    return proveedorRepository.find(term);
  }

}
