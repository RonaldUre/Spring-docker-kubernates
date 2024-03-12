package org.ronald.springcloud.msvc.clients;

import java.util.List;

import org.ronald.springcloud.msvc.models.Usuario;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "msvc-usuarios")
public interface UsuarioClientRest {

	@GetMapping("/{id}")
	Usuario detalle(@PathVariable Long id);
	
	@PostMapping("/")
	Usuario crear(@RequestBody Usuario usuario);
	
	@GetMapping("/usuarios-por-curso")
	List<Usuario> obteneralumnosPorCurso(@RequestParam Iterable<Long> ids,
			@RequestHeader(value = "Authorization", required = true) String token);
	
	
	
}
