package com.example.demo.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.insert.ProjectTask;

@Repository
public interface ProjectTaskRepository  extends CrudRepository<ProjectTask,Long> {

	public List<ProjectTask> findByProjectIdentifierOrderByPriority(String id);
	
	ProjectTask findByProjectSequence(String pt_id);
	
	
}