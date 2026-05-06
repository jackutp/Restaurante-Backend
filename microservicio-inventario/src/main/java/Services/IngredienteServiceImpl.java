package Services;

import Entities.EstadoIngrediente;
import Entities.Ingrediente;
import Repositories.IngredienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class IngredienteServiceImpl implements IngredienteService {

    final private IngredienteRepository repository;

    public IngredienteServiceImpl(IngredienteRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ingrediente> findAll() {
        return (List<Ingrediente>) repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Ingrediente> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional
    public Ingrediente save(Ingrediente ingrediente) {
        // Asigna estado inicial según stock
        if (ingrediente.getStock() == 0) {
            ingrediente.setEstado(EstadoIngrediente.VACIO);
        } else if (ingrediente.getEstado() == null) {
            ingrediente.setEstado(EstadoIngrediente.DISPONIBLE);
        }
        return repository.save(ingrediente);
    }

    @Override
    @Transactional
    public Optional<Ingrediente> update(Long id, Ingrediente ingrediente) {
        return repository.findById(id).map(existing -> {
            existing.setNombre(ingrediente.getNombre());
            existing.setUnidad(ingrediente.getUnidad());
            existing.setStock(ingrediente.getStock());
            existing.setEstado(ingrediente.getEstado());
            return repository.save(existing);
        });
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        return repository.findById(id).map(i -> {
            repository.delete(i);
            return true;
        }).orElse(false);
    }

    @Override
    @Transactional
    public Optional<Ingrediente> reducirStock(Long id, Integer cantidad) {
        return repository.findById(id).map(i -> {
            i.reducirStock(cantidad);
            return repository.save(i);
        });
    }

    @Override
    @Transactional
    public Optional<Ingrediente> aumentarStock(Long id, Integer cantidad) {
        return repository.findById(id).map(i -> {
            i.aumentarStock(cantidad);
            return repository.save(i);
        });
    }
}