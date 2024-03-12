package org.ronald.springcloud.msvc.services;

import java.util.List;
import java.util.Optional;

import org.ronald.springcloud.msvc.models.Usuario;
import org.ronald.springcloud.msvc.models.entity.Curso;

public interface CursoService {

	List<Curso> listar();

	Optional<Curso> porId(Long id);
	
	Optional<Curso> porIdConUsuarios(Long id, String token);

	Curso guardar(Curso curso);

	void eliminar(Long id);
	
	void eliminaCursoUsuarioPorId(Long id);
	
	
	
	Optional<Usuario> asignarUsuario(Usuario usuario, Long cursoId); 
	Optional<Usuario> crearUsuario(Usuario usuario, Long cursoId);
	Optional<Usuario> eliminarUsuario(Usuario usuario, Long cursoId);
}
