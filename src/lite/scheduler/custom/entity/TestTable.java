package lite.scheduler.custom.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Table
@Entity
public class TestTable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	Integer rowid;
	
	String name;
	
	String data;
}
