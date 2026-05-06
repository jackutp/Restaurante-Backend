package Repositories;

import Entities.MermaInsumos;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface MermaInsumosRepository extends CrudRepository<MermaInsumos, Long> {
    List<MermaInsumos> findByIngredienteIngredienteId(Long ingredienteId);
}