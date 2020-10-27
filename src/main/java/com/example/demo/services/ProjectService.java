package com.example.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.exception.ProjectIdException;
import com.example.demo.exception.ProjectNotFoundException;
import com.example.demo.insert.Backlog;
import com.example.demo.insert.Project;
import com.example.demo.insert.User;
import com.example.demo.repository.BacklogRepository;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.UserRepository;

@Service
public class ProjectService {

	@Autowired
	private ProjectRepository projectRepository;
	
	@Autowired
	private BacklogRepository backlogRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	public Project saveOrUpdateProject(Project project,String username) {
		
		if(project.getId() != null) {
			Project existingProject = projectRepository.findByProjectIdentifier(project.getProjectIdentifier());
			
			if(existingProject != null && (!existingProject.getProjectLeader().equals(username))) {
				throw new ProjectNotFoundException("Project Not found in your account");
			} else if(existingProject == null) {
				throw new ProjectNotFoundException("Project with Id " +project.getProjectIdentifier()+" cannot be updated because it doesnot exist");
			}
		}		
		
		try {
			
			User user = userRepository.findByUsername(username);
			project.setUser(user);
			project.setProjectLeader(user.getUsername());
			
            project.setProjectIdentifier(project.getProjectIdentifier().toUpperCase());
            
            if(project.getId()==null) {
            	Backlog backlog = new Backlog();
            	project.setBacklog(backlog);
            	backlog.setProject(project);
            	backlog.setProjectIdentifier(project.getProjectIdentifier().toUpperCase());
            }
            
            if(project.getId()!=null) {
            	project.setBacklog(backlogRepository.findByProjectIdentifier(project.getProjectIdentifier().toUpperCase()));
            }
            
			return projectRepository.save(project);
		}
		catch(Exception e) {
			throw new ProjectIdException("Project Id " + project.getProjectIdentifier().toUpperCase() + " already exists");
		}
		
	}
	
	public Project findProjectByIdentifier(String projectId,String username) {
		
		Project project =  projectRepository.findByProjectIdentifier(projectId.toUpperCase());
		
		if(project == null) {
			throw new ProjectIdException("Project Id " + projectId + " doesn't exist");
		}
		
		if(!project.getProjectLeader().equals(username)) {
			throw new ProjectNotFoundException("project not found in your account");
		}
		
		return project;
	}
	
	public Iterable<Project> findAllProjects(String username){
		return projectRepository.findAllByProjectLeader(username);
	}
	
	public void deleteProjectByIdentifier(String projectId,String username) {
//		Project project = projectRepository.findByProjectIdentifier(projectId.toUpperCase());
//		
//		
//		if(project == null) {
//			throw new ProjectIdException("cannot Project with ID " + projectId + ". This Project doesnt exist.");
//		}
		
		projectRepository.delete(findProjectByIdentifier(projectId, username));
	}
}
