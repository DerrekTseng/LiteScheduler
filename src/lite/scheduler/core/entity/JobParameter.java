package lite.scheduler.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Table
@Entity
@Accessors(chain = true)
public class JobParameter {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	Integer id;

	@Type(type = "text")
	String name;

	@Type(type = "text")
	String data;

	@ManyToOne
	Job job;

	@Column(nullable = false)
	Boolean enabled;

}
