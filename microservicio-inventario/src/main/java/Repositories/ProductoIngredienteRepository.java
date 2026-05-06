package Repositories;

import Entities.ProductoIngrediente;
import Entities.ProductoIngredienteId;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface ProductoIngredienteRepository extends CrudRepository<ProductoIngrediente, ProductoIngredienteId> {
    List<ProductoIngrediente> findByProductoProductoId(Long productoId);
    List<ProductoIngrediente> findByIngredienteIngredienteId(Long ingredienteId);
}