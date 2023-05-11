package lite.scheduler.core.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import lite.scheduler.core.enums.ExecutionStatus;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Table
@Entity
@Accessors(chain = true)
public class ExecutionHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	Integer id;

	String executionId;

	@ManyToOne
	Schedule schedule;

	@ManyToOne
	JobGroup jobGroup;

	@ManyToOne
	Job job;

	@Type(type = "text")
	String parameter;

	@Type(type = "text")
	String message;

	ExecutionStatus executionStatus;

	Date startDt;

	Date endDt;

}
