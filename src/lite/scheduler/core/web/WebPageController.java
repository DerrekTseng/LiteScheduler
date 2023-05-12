package lite.scheduler.core.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping
public class WebPageController {

	@Autowired
	WebService service;

	@GetMapping
	public ModelAndView index() {
		ModelAndView view = new ModelAndView("index");
		return view;
	}

	@GetMapping("openGlobleParameter")
	public ModelAndView openGlobleParameter() {
		ModelAndView view = new ModelAndView("globleParameter");
		return view;
	}

	@GetMapping("openCreateSchedule")
	public ModelAndView openCreateSchedule() {
		ModelAndView view = new ModelAndView("createSchedule");
		return view;
	}

	@GetMapping("openScheduleDetail")
	public ModelAndView openScheduleDetail(@RequestParam(required = true) String id) {
		ModelAndView view = new ModelAndView("scheduleDetail");
		view.addObject("id", id);
		return view;
	}

}
