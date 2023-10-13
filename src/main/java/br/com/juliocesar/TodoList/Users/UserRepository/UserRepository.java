package br.com.juliocesar.TodoList.Users.UserRepository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.juliocesar.TodoList.Users.UserModel.UserModel;

public interface UserRepository extends JpaRepository<UserModel, UUID> {
 
    UserModel findByUsername(String name);

}
