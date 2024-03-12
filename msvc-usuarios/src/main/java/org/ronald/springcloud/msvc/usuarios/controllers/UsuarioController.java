package org.ronald.springcloud.msvc.usuarios.controllers;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import org.ronald.springcloud.msvc.usuarios.models.entity.Usuario;
import org.ronald.springcloud.msvc.usuarios.service.UsuariosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UsuarioController {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private UsuariosService service;

	@Autowired
	private ApplicationContext context;

	@Autowired
	private Environment env;

	@GetMapping("/crash")
	public void crash() {
		((ConfigurableApplicationContext) context).close();
	}

	@GetMapping
	public ResponseEntity<?> listar() {
		Map<String, Object> body = new HashMap<>();
		body.put("users", service.listar());
		body.put("pod_info", env.getProperty("MY_POD_NAME") + ": " + env.getProperty("MY_POD_IP"));
		body.put("texto", env.getProperty("config.texto"));
		// return Collections.singletonMap("users", service.listar());
		return ResponseEntity.ok(body);
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> detalle(@PathVariable Long id) {
		Optional<Usuario> usuarioOptional = service.porId(id);
		if (usuarioOptional.isPresent()) {
			return ResponseEntity.ok(usuarioOptional.get());
		}
		return ResponseEntity.notFound().build();
	}

	@PostMapping
	public ResponseEntity<?> crear(@Valid @RequestBody Usuario usuario, BindingResult result) {

		if (result.hasErrors()) {
			return validar(result);
		}

		if (!usuario.getEmail().isEmpty() && service.existePorEmail(usuario.getEmail())) {
			return ResponseEntity.badRequest()
					.body(Collections.singletonMap("mensaje", "Ya existe un usuario con ese correo electronico"));
		}

		usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

		return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(usuario));
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> editar(@Valid @RequestBody Usuario usuario, BindingResult result, @PathVariable Long id) {

		if (result.hasErrors()) {
			return validar(result);
		}

		Optional<Usuario> o = service.porId(id);
		if (o.isPresent()) {
			Usuario usuarioDb = o.get();

			if (!usuario.getEmail().isEmpty() && !usuario.getEmail().equalsIgnoreCase(usuarioDb.getEmail())
					&& service.porEmail(usuario.getEmail()).isPresent()) {
				return ResponseEntity.badRequest()
						.body(Collections.singletonMap("mensaje", "Ya existe un usuario con ese correo electronico"));
			}

			usuarioDb.setNombre(usuario.getNombre());
			usuarioDb.setEmail(usuario.getEmail());
			usuarioDb.setPassword(passwordEncoder.encode(usuario.getPassword()));

			return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(usuarioDb));
		}
		return ResponseEntity.notFound().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> eliminar(@PathVariable Long id) {
		Optional<Usuario> o = service.porId(id);
		if (o.isPresent()) {
			service.eliminar(id);
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.notFound().build();
	}

	@GetMapping("/usuarios-por-curso")
	public ResponseEntity<?> obteneralumnosPorCurso(@RequestParam List<Long> ids) {
		return ResponseEntity.ok(service.listarPorIds(ids));
	}

	@GetMapping("/authorized")
	public Map<String, Object> authorized(@RequestParam(name = "code") String code) {
		return Collections.singletonMap("code", code);
	}

	@GetMapping("/login")
	public ResponseEntity<?> loginByEmail(@RequestParam(name = "email") String email) {
		Optional<Usuario> o = service.porEmail(email);
		if (o.isPresent()) {
			return ResponseEntity.ok(o.get());
		}
		return ResponseEntity.notFound().build();
	}

	private ResponseEntity<Map<String, String>> validar(BindingResult result) {
		Map<String, String> errores = new HashMap<>();
		result.getFieldErrors().forEach(err -> {
			errores.put(err.getField(), "El campo " + err.getField() + " " + err.getDefaultMessage());
		});
		return ResponseEntity.badRequest().body(errores);
	}

}
