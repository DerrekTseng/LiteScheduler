package lite.scheduler.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import lite.scheduler.cmp.ExecutionResult;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Table
@Entity
@Accessors(chain = true)
public class TaskHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	Integer rowid;

	@Type(type = "text")
	String parameter;

	@Type(type = "text")
	String message;

	@Column(nullable = false)
	ExecutionResult result;

	@Column(nullable = false)
	Date sdate;

	Date edate;

	@ManyToOne
	Task task;

}
