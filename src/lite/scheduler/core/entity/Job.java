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
public class Job {

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
	JobGroup jobGroup;
	
	@OneToMany(mappedBy = "job", cascade = CascadeType.ALL)
	List<JobParameter> jobParameter;
	
	@OneToMany(mappedBy = "job", cascade = CascadeType.ALL)
	List<ExecutionHistory> executionHistories;

}
