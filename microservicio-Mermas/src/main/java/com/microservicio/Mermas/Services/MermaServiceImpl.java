package com.microservicio.Mermas.Services;
import com.microservicio.Mermas.Entities.Merma;
import com.microservicio.Mermas.Entities.TipoMerma;
import com.microservicio.Mermas.Repositories.MermaRepository;
import com.microservicio.Mermas.dto.*;
import com.microservicio.Mermas.Mapper.MermaMapper;
import com.microservicio.Mermas.Services.feign.InsumoFeignClient;
import com.microservicio.Mermas.Services.feign.ProductoFeignClient;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Service
public class MermaServiceImpl implements MermaService {

    private final MermaRepository mermaRepository;
    private final MermaMapper mermaMapper;
    private final ProductoFeignClient productoFeignClient;
    private final InsumoFeignClient insumoFeignClient;

    public MermaServiceImpl(MermaRepository mermaRepository,
                            MermaMapper mermaMapper,
                            ProductoFeignClient productoFeignClient,
                            InsumoFeignClient insumoFeignClient) {
        this.mermaRepository = mermaRepository;
        this.mermaMapper = mermaMapper;
        this.productoFeignClient = productoFeignClient;
        this.insumoFeignClient = insumoFeignClient;
    }
    @Override
    public List<MermaDTO> findAll() {
        return mermaRepository.findAll()
                .stream()
                .map(mermaMapper::toDTO)
                .collect(Collectors.toList());
    }
    @Override
    public Optional<MermaDTO> findById(Integer id) {
        return mermaRepository.findById(id)
                .map(mermaMapper::toDTO);
    }
    @Override
    @Transactional
    public MermaDTO save(MermaRequestDTO mermaDTO) {
        Merma merma = mermaMapper.toEntity(mermaDTO);
        Merma saved = mermaRepository.save(merma);
        return mermaMapper.toDTO(saved);
    }
    @Override
    @Transactional
    public MermaDTO update(Integer id, MermaRequestDTO mermaDTO) {
        Merma existingMerma = mermaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Merma no encontrada con id: " + id));
        existingMerma.setTipoMerma(mermaDTO.getTipoMerma());
        existingMerma.setNombreMerma(mermaDTO.getNombreMerma());
        existingMerma.setCantidad(mermaDTO.getCantidad());
        existingMerma.setMotivo(mermaDTO.getMotivo());
        existingMerma.setReferenciaId(mermaDTO.getReferenciaId());
        existingMerma.setUnidadMedida(mermaDTO.getUnidadMedida());

        Merma updated = mermaRepository.save(existingMerma);
        return mermaMapper.toDTO(updated);
    }
    @Override
    @Transactional
    public void delete(Integer id) {
        if (!mermaRepository.existsById(id)) {
            throw new EntityNotFoundException("Merma no encontrada con id: " + id);
        }
        mermaRepository.deleteById(id);
    }
    @Override
    public List<MermaDTO> findByTipo(TipoMerma tipo) {
        return mermaRepository.findByTipoMerma(tipo)
                .stream()
                .map(mermaMapper::toDTO)
                .collect(Collectors.toList());
    }
    @Override
    public List<ProductoDTO> getProductos() {
        return productoFeignClient.getAllProductos();
    }
    @Override
    public List<InsumoDTO> getInsumos() {
        return insumoFeignClient.getAllInsumos();
    }
}