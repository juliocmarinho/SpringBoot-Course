package br.com.juliocesar.TodoList.Task.TaskController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.juliocesar.TodoList.Task.TaskModel.TaskModel;
import br.com.juliocesar.TodoList.Task.TaskRepository.TaskRepository;
import br.com.juliocesar.TodoList.Utils.Utils;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    
    @Autowired
    private TaskRepository taskRepository;

    @PostMapping("/")
    public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request){


        var user = request.getAttribute("userId");
        taskModel.setUserId((UUID) user);

        var currentDate = LocalDateTime.now();
        if(currentDate.isAfter(taskModel.getStartAt()) || currentDate.isAfter(taskModel.getEndAt())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(("A data de início/término deve ser maior que a data atual"));
        }

        if(taskModel.getStartAt().isAfter(taskModel.getEndAt())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de início deve ser menor que a data de término");
        }
        taskRepository.save(taskModel);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(taskModel);
    }

    @GetMapping("/")
    public List<TaskModel> list(HttpServletRequest request){
        var user = request.getAttribute("userId");
        return taskRepository.findByUserId((UUID) user);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity update(@RequestBody TaskModel taskModel, @PathVariable UUID id, HttpServletRequest request){
        var task = taskRepository.findById(id).orElse(null);

        var user = request.getAttribute("userId");

        if(task == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tarefa não encontrada.");
        }

        if(!task.getUserId().equals(user)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário não tem permissão para alterar essa tarefa");
        }

        Utils.copyNonNullProperties(taskModel, task);
        
        var taskUpdated = taskRepository.save(task);

        return  ResponseEntity.ok().body(taskRepository.save(taskUpdated));
    }
}
