package lite.scheduler.core.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Table
@Entity
@Accessors(chain = true)
public class Task {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	Integer rowid;

	@Column(unique = true, nullable = false)
	String id;

	@Column(unique = true, nullable = false)
	String name;

	@Type(type = "text")
	@Column(nullable = false)
	String cronExp;

	@Type(type = "text")
	@Column(nullable = false)
	String taskClass;

	@Type(type = "text")
	@Column(nullable = false)
	String description;

	@Column(nullable = false)
	Boolean enabled;
	
	@OrderBy("sdate desc")
	@OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
	List<TaskHistory> histories;

	@OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
	List<TaskParameter> parameters;

}
