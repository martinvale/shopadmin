package com.ibiscus.shopnchek.web.controller.site;

import java.io.InputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import com.ibiscus.shopnchek.application.shopmetrics.ImportService;
import com.ibiscus.shopnchek.application.shopmetrics.ShopmetricsUserDto;

@Controller
@RequestMapping("/import")
public class ImportController {

  /** Service to import external data. */
  @Autowired
  private ImportService importService;

  @RequestMapping(value="/shopmetrics")
  public String renderShopmetrics(
      @ModelAttribute("model") final ModelMap model) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    model.addAttribute("user", user);

    return "importShopmetrics";
  }

  @RequestMapping(value="/shopmetrics", method=RequestMethod.POST)
  public String importShopmetrics(@ModelAttribute("model") final ModelMap model,
      MultipartFile file) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    model.addAttribute("user", user);
    InputStream inputStream = null;
    try {
      inputStream = file.getInputStream();
      List<ShopmetricsUserDto> users = importService.process(inputStream);
      inputStream.close();
      model.addAttribute("users", users);
      model.addAttribute("status", "El archivo se importó correctamente.");
    } catch (Exception e) {
      throw new RuntimeException("Cannot import the file", e);
    }

    return "importShopmetrics";
  }

}
