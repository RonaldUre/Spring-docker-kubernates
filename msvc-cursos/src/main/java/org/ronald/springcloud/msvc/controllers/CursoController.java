package org.ronald.springcloud.msvc.controllers;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import org.ronald.springcloud.msvc.models.Usuario;
import org.ronald.springcloud.msvc.models.entity.Curso;
import org.ronald.springcloud.msvc.services.CursoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import feign.FeignException;

@RestController
public class CursoController {

	@Autowired
	private CursoService service;

	@GetMapping
	public ResponseEntity<List<Curso>> listar() {
		return ResponseEntity.ok(service.listar());
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> detalle(@PathVariable Long id, @RequestHeader(value = "Authorization", required = true) String token) {
		Optional<Curso> o = service.porIdConUsuarios(id, token);//service.porId(id);
		if (o.isPresent()) {
			return ResponseEntity.ok(o.get());
		}
		return ResponseEntity.notFound().build();
	}

	@PostMapping("/")
	public ResponseEntity<?> crear(@Valid @RequestBody Curso curso, BindingResult result) {

		if (result.hasErrors()) {
			return validar(result);
		}

		Curso cursoDb = service.guardar(curso);
		return ResponseEntity.status(HttpStatus.CREATED).body(cursoDb);
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> editar(@Valid @RequestBody Curso curso, BindingResult result, @PathVariable Long id) {

		if (result.hasErrors()) {
			return validar(result);
		}

		Optional<Curso> o = service.porId(id);
		if (o.isPresent()) {
			Curso cursoDb = o.get();
			cursoDb.setNombre(curso.getNombre());
			return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(cursoDb));
		}
		return ResponseEntity.notFound().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> eliminar(@PathVariable Long id) {
		Optional<Curso> o = service.porId(id);
		if (o.isPresent()) {
			service.eliminar(o.get().getId());
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.notFound().build();
	}

	@PutMapping("/asignar-usuario/{cursoId}")
	public ResponseEntity<?> asignarUsuario(@RequestBody Usuario usuario, @PathVariable Long cursoId) {
		Optional<Usuario> o;
		try {
			o = service.asignarUsuario(usuario, cursoId);
		} catch (FeignException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("mensaje",
					"no existe ese usuario o error en la comunicacion " + e.getMessage()));
		}
		if (o.isPresent()) {
			return ResponseEntity.status(HttpStatus.CREATED).body(o.get());
		}
		return ResponseEntity.notFound().build();
	}
	
	@PostMapping("/crear-usuario/{cursoId}")
	public ResponseEntity<?> crearUsuario(@RequestBody Usuario usuario, @PathVariable Long cursoId) {
		Optional<Usuario> o;
		try {
			o = service.crearUsuario(usuario, cursoId);
		} catch (FeignException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("mensaje",
					"no se pudo crear el usuario o error en la comunicacion" + e.getMessage()));
		}
		if (o.isPresent()) {
			return ResponseEntity.status(HttpStatus.CREATED).body(o.get());
		}
		return ResponseEntity.notFound().build();
	}
	
	@DeleteMapping("/eliminar-usuario/{cursoId}")
	public ResponseEntity<?> eliminarUsuario(@RequestBody Usuario usuario, @PathVariable Long cursoId) {
		Optional<Usuario> o;
		try {
			o = service.eliminarUsuario(usuario, cursoId);
		} catch (FeignException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("mensaje",
					"no existe ese usuario o error en la comunicacion " + e.getMessage()));
		}
		if (o.isPresent()) {
			return ResponseEntity.status(HttpStatus.OK).body(o.get());
		}
		return ResponseEntity.notFound().build();
	}
	
	@DeleteMapping("/eliminar-curso-usuario/{id}")
	public ResponseEntity<?> eliminarCursoUsuario(@PathVariable Long id){
		service.eliminaCursoUsuarioPorId(id);
		return ResponseEntity.noContent().build();
	}


	private ResponseEntity<Map<String, String>> validar(BindingResult result) {
		Map<String, String> errores = new HashMap<>();
		result.getFieldErrors().forEach(err -> {
			errores.put(err.getField(), "El campo " + err.getField() + " " + err.getDefaultMessage());
		});
		return ResponseEntity.badRequest().body(errores);
	}
}
