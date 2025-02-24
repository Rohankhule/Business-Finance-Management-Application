package com.bfms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.bfms.model.Event;
import com.bfms.model.Project;
import com.bfms.repository.EventRepository;
import com.bfms.repository.ProjectRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProjectService {
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private EventRepository eventRepository;

    public Project addNewProject(Project project) {
        project.setStatus(Project.ProjectStatus.PLANNING);
        return projectRepository.save(project);
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Optional<Project> getProjectById(String projectId) {
        return projectRepository.findById(projectId);
    }
    public List<Project> getProjectByStatus(String status) {
        return projectRepository.findByStatus(Project.ProjectStatus.valueOf(status));
    }

    public Project updateProject(String projectId, Project updatedProject) {
        return projectRepository.findById(projectId)
            .map(existingProject -> {
                existingProject.setTitle(updatedProject.getTitle());
                existingProject.setDescription(updatedProject.getDescription());
                existingProject.setStartDate(updatedProject.getStartDate());
                existingProject.setEndDate(updatedProject.getEndDate());
                existingProject.setStatus(updatedProject.getStatus());
                return projectRepository.save(existingProject);
            })
            .orElseThrow(() -> new RuntimeException("Project not found"));
    }

    public void deleteProject(String projectId) {
        projectRepository.deleteById(projectId);
    }
    
    public List<Event> getUpcomingEvents() {
        LocalDateTime now = LocalDateTime.now();
        List<Project> projects = projectRepository.findProjectsWithUpcomingEvents(now);
        
        return projects.stream()
                .flatMap(project -> project.getEvents().stream()
                        .filter(event -> event.getEventDate().isAfter(now)))
                .collect(Collectors.toList());
    }
}