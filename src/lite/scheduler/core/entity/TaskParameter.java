package lite.scheduler.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Table
@Entity
@Accessors(chain = true)
public class TaskParameter {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	Integer rowid;
	
	@Column(nullable = false)
	String name;
	
	@Column(nullable = false)
	String data;
	
	String description;

	@ManyToOne
	Task task;

}
