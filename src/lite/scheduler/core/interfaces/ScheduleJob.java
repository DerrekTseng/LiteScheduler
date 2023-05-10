package lite.scheduler.core.interfaces;

import lite.scheduler.core.bean.ExecuteParamenter;

public interface ScheduleJob {
	void execute(ExecuteParamenter executeParamenter) throws Exception;
}
