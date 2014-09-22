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
import org.springframework.web.bind.annotation.ResponseBody;

import com.ibiscus.shopnchek.domain.security.User;
import com.ibiscus.shopnchek.domain.security.UserRepository;
import com.ibiscus.shopnchek.domain.util.ResultSet;

@Controller
@RequestMapping(value="/users")
public class UsersController {

  /** The maximum users to retrieve per page. */
  private final static int PAGE_SIZE = 20;

  @Autowired
  private UserRepository userRepository;

  @RequestMapping(value = "/", method = RequestMethod.GET)
  public String list(@ModelAttribute("model") final ModelMap model) {
    org.springframework.security.core.userdetails.User user;
    user = (org.springframework.security.core.userdetails.User) SecurityContextHolder
        .getContext().getAuthentication().getPrincipal();
    model.addAttribute("user", user);

    List<User> users = userRepository.find(1, PAGE_SIZE, null);
    int size = userRepository.getUsersCount(null);
    model.addAttribute("users", new ResultSet<User>(users, size));
    model.addAttribute("start", 1);
    model.addAttribute("page", 1);
    model.addAttribute("pageSize", PAGE_SIZE);
    return "users";
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
    List<User> users = userRepository.find(start, PAGE_SIZE, name);
    int size = userRepository.getUsersCount(name);
    model.addAttribute("users", new ResultSet<User>(users, size));
    model.addAttribute("start", start);
    model.addAttribute("page", page);
    model.addAttribute("pageSize", PAGE_SIZE);
    return "users";
  }

  @RequestMapping(value = "/new", method = RequestMethod.GET)
  public String get(@ModelAttribute("model") final ModelMap model) {
    org.springframework.security.core.userdetails.User user;
    user = (org.springframework.security.core.userdetails.User) SecurityContextHolder
        .getContext().getAuthentication().getPrincipal();
    model.addAttribute("user", user);

    return "user";
  }

  @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
  public String get(@ModelAttribute("model") final ModelMap model,
      @PathVariable long userId) {
    org.springframework.security.core.userdetails.User user;
    user = (org.springframework.security.core.userdetails.User) SecurityContextHolder
        .getContext().getAuthentication().getPrincipal();
    model.addAttribute("user", user);

    User editionUser = userRepository.get(userId);
    model.addAttribute("editionUser", editionUser);
    return "user";
  }

  @RequestMapping(value = "/user/{userId}", method = RequestMethod.DELETE)
  public @ResponseBody boolean delete(@ModelAttribute("model") final ModelMap model,
      @PathVariable long userId) {
    userRepository.delete(userId);
    return true;
  }

  @RequestMapping(value = "/create", method = RequestMethod.POST)
  public String create(@ModelAttribute("model") final ModelMap model,
      String username, String name, int profile) {
    org.springframework.security.core.userdetails.User user;
    user = (org.springframework.security.core.userdetails.User) SecurityContextHolder
        .getContext().getAuthentication().getPrincipal();
    model.addAttribute("user", user);

    User newUser = new User(username, name, profile);
    long id = userRepository.save(newUser);
    model.addAttribute("editionUser", newUser);
    return "redirect:" + id;
  }

  @RequestMapping(value = "/update", method = RequestMethod.POST)
  public String update(@ModelAttribute("model") final ModelMap model,
      long id, String username, String name, int profile) {
    org.springframework.security.core.userdetails.User user;
    user = (org.springframework.security.core.userdetails.User) SecurityContextHolder
        .getContext().getAuthentication().getPrincipal();
    model.addAttribute("user", user);

    User editionUser = userRepository.get(id);
    editionUser.update(username, name, profile);
    userRepository.update(editionUser);
    model.addAttribute("editionUser", editionUser);
    return "redirect:" + id;
  }
}
