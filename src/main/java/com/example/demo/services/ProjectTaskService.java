package com.example.demo.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.example.demo.exception.ProjectNotFoundException;
import com.example.demo.insert.Backlog;
import com.example.demo.insert.Project;
import com.example.demo.insert.ProjectTask;
import com.example.demo.repository.BacklogRepository;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.ProjectTaskRepository;

@Service
public class ProjectTaskService {

	@Autowired
	private BacklogRepository backlogRepository;
	
	@Autowired
	private ProjectTaskRepository projectTaskRepository;
	
	@Autowired
	private ProjectRepository projectRepository;
	
	@Autowired
	private ProjectService projectService;

	public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask,String username) {
		
		//try {
		//PTs to be added to a specific project, project != null , BL exists
		Backlog backlog = projectService.findProjectByIdentifier(projectIdentifier, username).getBacklog();// backlogRepository.findByProjectIdentifier(projectIdentifier);
		
		//set the bl to pt
		projectTask.setBacklog(backlog);
		//we want our project sequence to be like this IDPRO-1 .....
		Integer BacklogSequence = backlog.getPTSequence();
		//Update BL Sequence
		BacklogSequence++;
		
		backlog.setPTSequence(BacklogSequence);
		
		//Add Sequence to Project Task
		projectTask.setProjectSequence(projectIdentifier+"-"+BacklogSequence);
		projectTask.setProjectIdentifier(projectIdentifier);
		
		//Initial Priority when priority null // issue projectTask.getPriority()==0
		if(projectTask.getPriority()==null||projectTask.getPriority()==0) {
			projectTask.setPriority(3);
		}
		//Initial status when status is null
		if(projectTask.getStatus()==""||projectTask.getStatus()==null) {
			projectTask.setStatus("TO_DO");
		}
		
		return projectTaskRepository.save(projectTask);

		//		}catch(Exception e) {
//			throw new ProjectNotFoundException("Project Not Found");
//		}
	}


	public Iterable<ProjectTask> findBacklogById(String id,String username) {
		
		
		projectService.findProjectByIdentifier(id, username);
		
//		Project project = projectRepository.findByProjectIdentifier(id);
//		
//		if(project==null) {
//			throw new ProjectNotFoundException("Project with Id " +id+ " does not exist" );
//		}
		
		return projectTaskRepository.findByProjectIdentifierOrderByPriority(id);
	}
	
	
	public ProjectTask findPTByProjectSequence(String backlog_id,String pt_id,String username) {
		
		
		projectService.findProjectByIdentifier(backlog_id, username);
		//make sure we are searching on right backlog
//		Backlog backlog = backlogRepository.findByProjectIdentifier(backlog_id);
//		if(backlog==null) {
//			throw new ProjectNotFoundException("Project with Id "+backlog_id+ " doesnot exist");
//		}
		
		//make sure that our task exists
		ProjectTask projectTask =projectTaskRepository.findByProjectSequence(pt_id);
		if(projectTask==null) {
			throw new ProjectNotFoundException("Project Task " +pt_id + "not found");
		}
		
		//make sure that the backlog/project id in the path corresponds to right project
		if(!projectTask.getProjectIdentifier().equals(backlog_id)) {
			throw new ProjectNotFoundException("Project Task "+pt_id+" does not exist in project "+backlog_id);
		}
		
		return projectTask;
	}
	
	
	public ProjectTask updateByProjectSequence(ProjectTask updatedTask,String backlog_id,String pt_id,String username) {
		ProjectTask projectTask = findPTByProjectSequence(backlog_id, pt_id,username);
		
		projectTask=updatedTask;
		
		return projectTaskRepository.save(projectTask);
	}
	
	public void deletePTByProjectSequence(String backlog_id,String pt_id,String username) {
		ProjectTask projectTask = findPTByProjectSequence(backlog_id, pt_id,username);
		
//		Backlog backlog = projectTask.getBacklog();
//		List<ProjectTask> pts = backlog.getProjectTasks();
//		pts.remove(projectTask);
//		backlogRepository.save(backlog);
		
		projectTaskRepository.delete(projectTask);
	}
}
