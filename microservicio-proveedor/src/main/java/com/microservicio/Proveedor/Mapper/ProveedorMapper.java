package com.microservicio.Proveedor.Mapper;
import com.microservicio.Proveedor.Entities.Proveedor;
import com.microservicio.Proveedor.dto.ProveedorDTO;
import com.microservicio.Proveedor.dto.ProveedorRequestDTO;
import org.springframework.stereotype.Component;

@Component
public class ProveedorMapper {

    public ProveedorDTO toDTO(Proveedor proveedor) {
        if (proveedor == null) return null;

        ProveedorDTO dto = new ProveedorDTO();
        dto.setProveedorid(proveedor.getProveedorid());
        dto.setNombre(proveedor.getNombre());
        dto.setDescripcion(proveedor.getDescripcion());
        dto.setRuc(proveedor.getRuc());
        dto.setRazonSocial(proveedor.getRazonSocial());
        dto.setDireccionFiscal(proveedor.getDireccionFiscal());
        dto.setCreatedAt(proveedor.getCreatedAt());
        dto.setUpdatedAt(proveedor.getUpdatedAt());

        return dto;
    }

    public Proveedor toEntity(ProveedorRequestDTO dto) {
        if (dto == null) return null;

        Proveedor proveedor = new Proveedor();
        proveedor.setNombre(dto.getNombre());
        proveedor.setDescripcion(dto.getDescripcion());
        proveedor.setRuc(dto.getRuc());
        proveedor.setRazonSocial(dto.getRazonSocial());
        proveedor.setDireccionFiscal(dto.getDireccionFiscal());

        return proveedor;
    }
}