package org.ronald.springcloud.msvc.usuarios.service;

import java.util.List;
import java.util.Optional;

import org.ronald.springcloud.msvc.usuarios.clients.CursoClienteRest;
import org.ronald.springcloud.msvc.usuarios.models.entity.Usuario;
import org.ronald.springcloud.msvc.usuarios.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioServiceImpl implements UsuariosService {

	@Autowired
	private UsuarioRepository usuarioDao;
	
	@Autowired
	private CursoClienteRest client;

	@Override
	@Transactional(readOnly = true)
	public List<Usuario> listar() {
		return (List<Usuario>) usuarioDao.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Usuario> porId(Long id) {
		// TODO Auto-generated method stub
		return usuarioDao.findById(id);
	}

	@Override
	@Transactional
	public Usuario guardar(Usuario usuario) {
		// TODO Auto-generated method stub
		return usuarioDao.save(usuario);
	}

	@Override
	@Transactional
	public void eliminar(Long id) {
		usuarioDao.deleteById(id);
		client.eliminarCursoUsuario(id);
	}

	@Override
	public Optional<Usuario> porEmail(String email) {
		// TODO Auto-generated method stub
		return usuarioDao.findByEmail(email);
	}

	@Override
	public boolean existePorEmail(String email) {
		// TODO Auto-generated method stub
		return usuarioDao.existsByEmail(email);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Usuario> listarPorIds(Iterable<Long> ids) {
		// TODO Auto-generated method stub
		return (List<Usuario>) usuarioDao.findAllById(ids);
	}

}
