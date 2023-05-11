package lite.scheduler.core.web;

import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping
public class WebController {

	@Autowired
	WebService service;

	/**
	 * 首頁排程列表
	 * 
	 * @return
	 * @throws SchedulerException
	 */
	@GetMapping
	public ModelAndView index() {
		ModelAndView view = new ModelAndView("index");
		view.addObject("items", service.getGridScheduleRows());
		return view;
	}

	/**
	 * 建立排程頁面
	 * 
	 * @return
	 */
	@GetMapping("createSchedule")
	public ModelAndView createSchedule() {
		ModelAndView view = new ModelAndView("createSchedule");
		// TODO
		return view;
	}

	/**
	 * 建立排程
	 * 
	 * @return
	 */
	@PostMapping("createScheduleSave")
	public ModelAndView createScheduleSave() {
		ModelAndView view = new ModelAndView("scheduleDetail");
		// TODO
		return view;
	}

	/**
	 * 排程明細頁面
	 * 
	 * @param id
	 * @return
	 */
	@GetMapping("scheduleDetail")
	public ModelAndView scheduleDetail(@RequestParam(required = true) String id) {
		ModelAndView view = new ModelAndView("scheduleDetail");
		view.addObject("data", service.getScheduleDetail(id));
		return view;
	}

	/**
	 * 排程明細儲存
	 * 
	 * @param id
	 * @return
	 */
	@PostMapping("scheduleDetailSave")
	public ModelAndView scheduleDetailSave(String id) {
		ModelAndView view = new ModelAndView("scheduleDetail");
		// TODO
		return view;
	}

	/**
	 * 建立新的群組
	 * 
	 * @return
	 */
	public ModelAndView createJobGroup() {
		ModelAndView view = new ModelAndView("createJobGroup");
		// TODO
		return view;
	}

	/**
	 * 建立新的群組儲存
	 * 
	 * @return
	 */
	public ModelAndView createJobGroupSave() {
		ModelAndView view = new ModelAndView("scheduleDetail");
		// TODO
		return view;
	}

	/**
	 * 群組明細
	 * 
	 * @return
	 */
	@GetMapping("jobGroupDetail")
	public ModelAndView jobGroupDetail(@RequestParam(required = true) Integer id) {
		ModelAndView view = new ModelAndView("jobGroupDetail");
		view.addObject("data", service.getJobGroupDetail(id));
		return view;
	}

	/**
	 * 群組明細儲存
	 * 
	 * @return
	 */
	public ModelAndView jobGroupDetailSave() {
		ModelAndView view = new ModelAndView("scheduleDetail");
		// TODO
		return view;
	}

	/**
	 * 工作明細
	 * 
	 * @param id
	 * @return
	 */
	@GetMapping("jobDetail")
	public ModelAndView jobDetail(@RequestParam(required = true) Integer id) {
		ModelAndView view = new ModelAndView("jobDetail");
		view.addObject("data", service.getJobDetail(id));
		return view;
	}

}
