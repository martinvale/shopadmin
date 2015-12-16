package com.ibiscus.shopnchek.web.controller.site;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.ibiscus.shopnchek.application.feed.GetFeedCommand;
import com.ibiscus.shopnchek.application.feed.SaveFeedCommand;
import com.ibiscus.shopnchek.domain.debt.Feed;
import com.ibiscus.shopnchek.domain.security.User;

@Controller
@RequestMapping("/feed")
public class FeedController {

	@Autowired
	private GetFeedCommand getFeedCommand;

	@Autowired
	private SaveFeedCommand saveFeedCommand;

	@RequestMapping(value="/")
	public String listFeed(@ModelAttribute("model") final ModelMap model) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		model.addAttribute("user", user);

		getFeedCommand.setCode("mcd_items_debt");
		Feed feed = getFeedCommand.execute();
		model.put("feed", feed);

		return "listFeed";
	}

	@RequestMapping(value="/{code}", method = RequestMethod.POST)
	public String update(@ModelAttribute("model") final ModelMap model,
			@PathVariable("code") String code,
			@DateTimeFormat(pattern="dd/MM/yyyy") Date from) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		model.addAttribute("user", user);

		saveFeedCommand.setCode("mcd_items_debt");
		saveFeedCommand.setFrom(from);
		Feed feed = saveFeedCommand.execute();
		model.put("feed", feed);

		return "listFeed";
	}

}
