package br.com.juliocesar.TodoList.Filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.juliocesar.TodoList.Users.UserRepository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@Component
public class FilterTaskAuth extends OncePerRequestFilter{


    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

            var servPath = request.getServletPath();
            if(servPath.startsWith("/tasks/")){
            //Pegando autenticação e decodando.
            var authorization = request.getHeader("Authorization");
            //Vem no formato Basic xxxxxx
            var autoEncoded = authorization.substring("Basic".length()).trim(); 
            //Cortamos para tirar o Basic e sobrar apenas o xxxx
            byte[] autoDecoded = Base64.getDecoder().decode(autoEncoded);
            //Decodificamos o xxx e jogamos para um array de byte
            var authString = new String(autoDecoded);
            //Convertemos o array de bytes para uma string
            String[] arrayString = authString.split(":");
            //A string original é nome:senha, cortamos em duas partes separadas por :
            String username = arrayString[0]; 
            String password = arrayString[1];

            //Verificação de usuário

            var userVerify = userRepository.findByUsername(username);

            if(userVerify == null){
                System.out.println("Usuário não localizado no banco de dados.");
                response.sendError(401);
            }else{
                //Verificação de senha
               var flagPassword = BCrypt.verifyer().verify(password.toCharArray(), userVerify.getPassword());
               if(flagPassword.verified){
                request.setAttribute("userId", userVerify.getId());
                filterChain.doFilter(request, response);
               }else{
                response.sendError(401);
               }
            }
        }else{
            filterChain.doFilter(request, response);
        }
            
    }

   
    
}
