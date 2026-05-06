package Repositories;

import Entities.Producto;
import org.springframework.data.repository.CrudRepository;
public interface ProductoRepository extends CrudRepository<Producto,Long> {
}
