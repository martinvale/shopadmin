package com.ibiscus.shopnchek.web.controller.site;

import java.io.InputStream;
import java.util.List;

import com.ibiscus.shopnchek.application.shopmetrics.ImportShopmetricsService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.ibiscus.shopnchek.application.shopmetrics.ImportService;
import com.ibiscus.shopnchek.domain.security.User;
import com.ibiscus.shopnchek.domain.tasks.BatchTaskStatus;
import com.ibiscus.shopnchek.domain.tasks.BatchTaskStatusRepository;

@Controller
@RequestMapping("/import")
public class ImportController {

  /** Service to import external data. */
  @Autowired
  private ImportService importService;

  @Autowired
  private ImportShopmetricsService importShopmetricsService;

  @Autowired
  private BatchTaskStatusRepository batchTaskStatusRepository;

  @RequestMapping(value="/shopmetrics")
  public String renderShopmetrics(
      @ModelAttribute("model") final ModelMap model) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    model.addAttribute("user", user);

    List<BatchTaskStatus> tasks = batchTaskStatusRepository.find();
    model.addAttribute("tasks", tasks);

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
      importShopmetricsService.importFile(file.getOriginalFilename(), inputStream);
      IOUtils.closeQuietly(inputStream);
      List<BatchTaskStatus> tasks = batchTaskStatusRepository.find();
      model.addAttribute("tasks", tasks);
      //model.addAttribute("processName", processName);
    } catch (Exception e) {
      throw new RuntimeException("Cannot import the file", e);
    }

    return "importShopmetrics";
  }

  @RequestMapping(value="/tasks")
  public @ResponseBody List<BatchTaskStatus> getTasks() {
    List<BatchTaskStatus> tasks = batchTaskStatusRepository.find();
    return tasks;
  }
}
