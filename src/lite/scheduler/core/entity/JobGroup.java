package lite.scheduler.core.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

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
	String name;

	@Type(type = "text")
	String description;

	Integer sequence;

	Boolean enable;

	@ManyToOne
	Schedule schedule;

	@OneToMany(mappedBy = "jobGroup", cascade = CascadeType.ALL)
	List<Job> jobs;

	@OneToMany(mappedBy = "jobGroup", cascade = CascadeType.ALL)
	List<JobGroupParameter> jobGroupParameters;
	
	@OneToMany(mappedBy = "jobGroup", cascade = CascadeType.ALL)
	List<ExecutionHistory> executionHistories;
}
