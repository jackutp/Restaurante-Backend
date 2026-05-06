package Services;

import Entities.Ingrediente;
import java.util.List;
import java.util.Optional;

public interface IngredienteService {
    List<Ingrediente> findAll();
    Optional<Ingrediente> findById(Long id);
    Ingrediente save(Ingrediente ingrediente);
    Optional<Ingrediente> update(Long id, Ingrediente ingrediente);
    boolean deleteById(Long id);
    Optional<Ingrediente> reducirStock(Long id, Integer cantidad);
    Optional<Ingrediente> aumentarStock(Long id, Integer cantidad);
}