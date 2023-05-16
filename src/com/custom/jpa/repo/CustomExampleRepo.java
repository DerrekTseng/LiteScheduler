package com.custom.jpa.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.custom.jpa.entity.CustomExample;

@Repository
public interface CustomExampleRepo extends JpaRepository<CustomExample, Integer> {

}
