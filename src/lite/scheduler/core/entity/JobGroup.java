package lite.scheduler.core.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
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
public class JobGroup {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	Integer id;

	@Type(type = "text")
	@Column(nullable = false)
	String name;

	@Type(type = "text")
	@Column(nullable = false)
	String description;

	@Column(nullable = false)
	Integer sequence;

	@Column(nullable = false)
	ScheduledState state;

	@ManyToOne
	Schedule schedule;

	@OrderBy("sequence")
	@OneToMany(mappedBy = "jobGroup", cascade = CascadeType.ALL)
	List<Job> jobs;

	@OneToMany(mappedBy = "jobGroup", cascade = CascadeType.ALL)
	List<JobGroupParameter> jobGroupParameters;

	@OrderBy("startDt desc")
	@OneToMany(mappedBy = "jobGroup", cascade = CascadeType.ALL)
	List<ExecutionHistory> executionHistories;

}
