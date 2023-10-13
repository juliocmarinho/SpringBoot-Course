package br.com.juliocesar.TodoList.Task.TaskRepository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.juliocesar.TodoList.Task.TaskModel.TaskModel;
import java.util.List;


public interface TaskRepository extends JpaRepository<TaskModel, UUID>{
    List<TaskModel> findByUserId(UUID userId);
}
