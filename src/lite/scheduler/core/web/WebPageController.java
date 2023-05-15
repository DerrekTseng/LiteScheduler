package lite.scheduler.core.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping
public class WebPageController {

	@GetMapping
	public ModelAndView index() {
		return new ModelAndView("index");
	}

	@GetMapping("cronExpGenerator")
	public ModelAndView cronExpGenerator(@RequestParam(required = true) String cronExp) {
		return new ModelAndView("cronExpGenerator").addObject("cronExp", cronExp);
	}

	@GetMapping("openTaskCreate")
	public ModelAndView openTaskCreate() {
		return new ModelAndView("taskCreate");
	}

	@GetMapping("openGlobleParameter")
	public ModelAndView openGlobleParameter() {
		return new ModelAndView("globleParameter");
	}

	@GetMapping("openGlobleParameterCreate")
	public ModelAndView openGlobleParameterCreate() {
		return new ModelAndView("globleParameterCreate");
	}

	@GetMapping("openGlobleParameterEdit")
	public ModelAndView openGlobleParameterEdit(@RequestParam(required = true) Integer rowid) {
		return new ModelAndView("globleParameterEdit").addObject("rowid", rowid);
	}

	@GetMapping("openTaskHistory")
	public ModelAndView openHistory(@RequestParam(required = true) Integer rowid) {
		return new ModelAndView("taskHistory").addObject("rowid", rowid);
	}

	@GetMapping("openTaskDetail")
	public ModelAndView openDetail(@RequestParam(required = true) Integer rowid) {
		return new ModelAndView("taskDetail").addObject("rowid", rowid);
	}

	@GetMapping("openTaskParameterCreate")
	public ModelAndView openTaskParameterCreate(@RequestParam(required = true) Integer rowid) {
		return new ModelAndView("taskParameterCreate").addObject("rowid", rowid);
	}

	@GetMapping("openTaskParameterEdit")
	public ModelAndView openTaskParameterEdit(@RequestParam(required = true) Integer rowid) {
		return new ModelAndView("taskParameterEdit").addObject("rowid", rowid);
	}

}
