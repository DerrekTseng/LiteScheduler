package com.custom.jpa.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Table
@Entity
public class CustomExample {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	Integer rowid;
	
	String data;
	
}
