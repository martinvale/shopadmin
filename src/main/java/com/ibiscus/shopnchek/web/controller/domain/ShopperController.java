package com.ibiscus.shopnchek.web.controller.domain;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ibiscus.shopnchek.domain.admin.Shopper;
import com.ibiscus.shopnchek.domain.admin.ShopperRepository;

@Controller
@RequestMapping(value="/services/shoppers")
public class ShopperController {

  /** Repository of shoppers. */
  @Autowired
  private ShopperRepository shopperRepository;

  @RequestMapping(value = "/suggest", method = RequestMethod.GET)
  public @ResponseBody List<Shopper> suggest(@RequestParam String term) {
    return shopperRepository.find(term);
  }

}
