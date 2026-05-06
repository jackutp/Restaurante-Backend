package Services;

import Entities.MermaInsumos;
import Repositories.IngredienteRepository;
import Repositories.MermaInsumosRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class MermaInsumosServiceImpl implements MermaInsumosService {

    final private MermaInsumosRepository mermaRepository;
    final private IngredienteRepository ingredienteRepository;

    public MermaInsumosServiceImpl(MermaInsumosRepository mermaRepository,
                                   IngredienteRepository ingredienteRepository) {
        this.mermaRepository = mermaRepository;
        this.ingredienteRepository = ingredienteRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MermaInsumos> findAll() {
        return (List<MermaInsumos>) mermaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MermaInsumos> findById(Long id) {
        return mermaRepository.findById(id);
    }

    @Override
    @Transactional
    public Optional<MermaInsumos> save(Long ingredienteId, MermaInsumos merma) {
        return ingredienteRepository.findById(ingredienteId).map(ingrediente -> {
            merma.setIngrediente(ingrediente);
            return mermaRepository.save(merma);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<MermaInsumos> findByIngrediente(Long ingredienteId) {
        return mermaRepository.findByIngredienteIngredienteId(ingredienteId);
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        return mermaRepository.findById(id).map(m -> {
            mermaRepository.delete(m);
            return true;
        }).orElse(false);
    }
}