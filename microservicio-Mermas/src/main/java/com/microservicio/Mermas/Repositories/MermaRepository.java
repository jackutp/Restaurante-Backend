package com.microservicio.Mermas.Repositories;

import com.microservicio.Mermas.Entities.Merma;
import com.microservicio.Mermas.Entities.TipoMerma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MermaRepository extends JpaRepository<Merma, Integer> {
    List<Merma> findByTipoMerma(TipoMerma tipo);
}