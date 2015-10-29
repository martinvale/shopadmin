package com.ibiscus.shopnchek.web.controller.domain;

import java.util.Set;

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
import com.ibiscus.shopnchek.application.security.GetUserCommand;
import com.ibiscus.shopnchek.application.security.SaveUserCommand;
import com.ibiscus.shopnchek.application.security.SearchUserCommand;
import com.ibiscus.shopnchek.domain.security.User;

@Controller
@RequestMapping(value="/users")
public class UsersController {

  @Autowired
  private SearchUserCommand searchUserCommand;

  @Autowired
  private GetUserCommand getUserCommand;

  @Autowired
  private SaveUserCommand saveUserCommand;

  @RequestMapping(value = "/list")
  public String list(@ModelAttribute("model") final ModelMap model,
			@RequestParam(required = false, defaultValue = "1") Integer page,
			@RequestParam(required = false, defaultValue = "name") String orderBy,
			@RequestParam(required = false, defaultValue = "true") Boolean ascending,
			String name) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    model.addAttribute("user", user);

	searchUserCommand.setPage(page);
	searchUserCommand.setOrderBy(orderBy, ascending);
	searchUserCommand.setName(name);
	ResultSet<User> resultSet = searchUserCommand.execute();
	model.put("result", resultSet);
	model.put("page", page);
	model.put("pageSize", 25);
	model.put("name", name);

    return "users";
  }

  @RequestMapping(value = "/new", method = RequestMethod.GET)
  public String get(@ModelAttribute("model") final ModelMap model) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    model.addAttribute("user", user);

    return "user";
  }

  @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
  public String get(@ModelAttribute("model") final ModelMap model,
      @PathVariable long userId) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    model.addAttribute("user", user);

    getUserCommand.setUserId(userId);
    User editionUser = getUserCommand.execute();
    model.addAttribute("editionUser", editionUser);
    return "user";
  }

  @RequestMapping(value = "/user/{userId}", method = RequestMethod.DELETE)
  public @ResponseBody boolean delete(@ModelAttribute("model") final ModelMap model,
      @PathVariable long userId) {
    //userRepository.delete(userId);
    return true;
  }

  @RequestMapping(value = "/create", method = RequestMethod.POST)
  public String create(@ModelAttribute("model") final ModelMap model,
      String username, String name, String password, boolean enabled,
      Set<Long> roleIds) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    model.addAttribute("user", user);

    saveUserCommand.setUserId(null);
    saveUserCommand.setUsername(username);
    saveUserCommand.setName(name);
    saveUserCommand.setPassword(password);
    saveUserCommand.setEnabled(enabled);
    saveUserCommand.setRoleIds(roleIds);
    User newUser = saveUserCommand.execute();
    model.addAttribute("editionUser", newUser);
    return "redirect:../list";
  }

  @RequestMapping(value = "/update", method = RequestMethod.POST)
  public String update(@ModelAttribute("model") final ModelMap model,
      long id, String username, String name, boolean enabled, Set<Long> roleIds) {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    model.addAttribute("user", user);

    saveUserCommand.setUserId(id);
    saveUserCommand.setUsername(username);
    saveUserCommand.setName(name);
    //saveUserCommand.setPassword(password);
    saveUserCommand.setEnabled(enabled);
    saveUserCommand.setRoleIds(roleIds);
    User newUser = saveUserCommand.execute();
    model.addAttribute("editionUser", newUser);
    return "redirect:../list";
  }
}
