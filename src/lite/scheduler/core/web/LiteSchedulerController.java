package lite.scheduler.core.web;

import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import lite.scheduler.custom.TestService;

@Controller
@RequestMapping
public class LiteSchedulerController {

	@Autowired
	TestService service;

	@GetMapping
	public ModelAndView index() throws SchedulerException {

		return new ModelAndView("index");
	}

}
