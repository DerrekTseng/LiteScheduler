package lite.scheduler.core.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import lite.scheduler.core.enums.ScheduledState;
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
	@Column(nullable = false)
	String name;

	@Type(type = "text")
	@Column(nullable = false)
	String description;

	@Type(type = "text")
	@Column(nullable = false)
	String cronExp;

	@Column(nullable = false)
	ScheduledState state;

	@OrderBy("sequence")
	@OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL)
	List<JobGroup> jobGroups;

	@OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL)
	List<ScheduleParameter> scheduleParameters;

}
