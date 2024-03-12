package org.ronald.springcloud.msvc.usuarios.repositories;

import java.util.Optional;

import org.ronald.springcloud.msvc.usuarios.models.entity.Usuario;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface UsuarioRepository extends CrudRepository<Usuario, Long>{

	Optional<Usuario> findByEmail(String email);
	
	@Query("select u from Usuario u where u.email=?1")
	Optional<Usuario> porEmail(String email);
	
	boolean existsByEmail(String email);
}
