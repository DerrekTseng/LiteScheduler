package lite.scheduler.core.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import lite.scheduler.core.enums.SchedulerStatus;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Table
@Entity
@Accessors(chain = true)
public class Schedule {

	@Id
	String id;

	@Type(type = "text")
	String name;
	
	@Type(type = "text")
	String description;

	@Type(type = "text")
	String cronExp;

	SchedulerStatus status;
	
	@OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL)
	List<JobGroup> jobGroups;	
	
	@OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL)
	List<ScheduleParameter> scheduleParameters;
	
	@OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL)
	List<ExecutionHistory> executionHistories;

}
