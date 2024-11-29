package br.com.ucsal.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

import br.com.ucsal.annotation.Rota;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/view/*")  // Mapeia todas as requisições com "/view/*"
public class ProdutoController extends HttpServlet {

    private Map<String, Command> commands = new HashMap<>();
    
    @Override
    public void init() {
    	Reflections reflections = new Reflections("br.com.ucsal.controller");
    	
    	Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Rota.class);
    	
    	Class<?>[] classArray = classes.toArray(new Class<?>[0]);
    	
    	for(Class<?> clazz : classArray) {
    		try {
    			
    			Rota rota = clazz.getAnnotation(Rota.class);
    			Object instance = clazz.getDeclaredConstructor().newInstance();
        		
    			for (String path : rota.value()) {
                    commands.put(path, (Command) instance);
                }
        		
    		} catch (Exception e) {
				e.printStackTrace();
			}
    	}
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getPathInfo();
        System.out.println(path);
        Command command = commands.get(path);

        if (command != null) {
            command.execute(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Página não encontrada");
        }
    }

	


}
