package lite.scheduler.core.web;

import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping
public class LiteSchedulerController {

	@Autowired
	LiteSchedulerService service;

	@GetMapping
	public ModelAndView index() throws SchedulerException {
		ModelAndView view = new ModelAndView("index");
		return view;
	}

	@PostMapping("createSchedule")
	public ModelAndView createSchedule() {
		ModelAndView view = new ModelAndView("scheduleDetail");
		return view;
	}

	@PostMapping("createScheduleSave")
	public ModelAndView createScheduleSave() {
		ModelAndView view = new ModelAndView("scheduleDetail");
		return view;
	}

	@PostMapping("scheduleDetail")
	public ModelAndView scheduleDetail() {
		ModelAndView view = new ModelAndView("scheduleDetail");
		return view;
	}

	@PostMapping("scheduleDetailSave")
	public ModelAndView scheduleDetailSave() {
		ModelAndView view = new ModelAndView("scheduleDetail");
		return view;
	}
	
	public ModelAndView jobGroup() {
		ModelAndView view = new ModelAndView("scheduleDetail");
		return view;
	} 
	
	public ModelAndView createJobGroup() {
		ModelAndView view = new ModelAndView("scheduleDetail");
		return view;
	} 
	
	public ModelAndView createJobGroupSave() {
		ModelAndView view = new ModelAndView("scheduleDetail");
		return view;
	} 
	
	public ModelAndView jobGroupDetail() {
		ModelAndView view = new ModelAndView("scheduleDetail");
		return view;
	} 
	
	public ModelAndView jobGroupDetailSave() {
		ModelAndView view = new ModelAndView("scheduleDetail");
		return view;
	} 
	
	

}
