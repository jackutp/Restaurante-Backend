package com.microservicio.inventario.Mapper;
import com.microservicio.inventario.Entities.Ingrediente;
import com.microservicio.inventario.Entities.MermaInsumos;
import com.microservicio.inventario.Entities.Producto;
import com.microservicio.inventario.Entities.ProductoIngrediente;
import com.microservicio.inventario.dto.IngredienteDTO;
import com.microservicio.inventario.dto.MermaDTO;
import com.microservicio.inventario.dto.ProductoDTO;
import com.microservicio.inventario.dto.ProductoIngredienteDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class InventarioMapper {

    // --- Producto ---
    public ProductoDTO toProductoDTO(Producto producto) {
        if (producto == null) return null;

        List<IngredienteDTO> ingredienteDTOs = new ArrayList<>();
        if (producto.getIngredientes() != null) {
            // Se mapean los ProductoIngrediente a IngredienteDTO (o ProductoIngredienteDTO según la necesidad del DTO actual)
            // Según ProductoDTO.java, requiere List<IngredienteDTO> (esto podría causar confusión si no incluimos la cantidad de la tabla intermedia).
            // Pero en ProductoDTO vimos que tiene List<IngredienteDTO>. Vamos a llenar lo básico.
            ingredienteDTOs = producto.getIngredientes().stream()
                    .map(pi -> {
                        IngredienteDTO dto = toIngredienteDTOSimple(pi.getIngrediente());
                        // Nota: La cantidad de ProductoIngrediente no se refleja en IngredienteDTO actualmente.
                        // Para evitar romper el DTO actual, devolvemos el IngredienteDTO simple.
                        return dto;
                    })
                    .collect(Collectors.toList());
        }

        return ProductoDTO.builder()
                .productoId(producto.getProductoId())
                .nombre(producto.getNombre())
                .descripcion(producto.getDescripcion())
                .precio(producto.getPrecio())
                .categoria(producto.getCategoria())
                .ingredientes(ingredienteDTOs)
                .build();
    }

    // --- Ingrediente ---
    public IngredienteDTO toIngredienteDTO(Ingrediente ingrediente) {
        if (ingrediente == null) return null;

        List<MermaDTO> mermasDTO = new ArrayList<>();
        if (ingrediente.getMermas() != null) {
            mermasDTO = ingrediente.getMermas().stream()
                    .map(this::toMermaDTO)
                    .collect(Collectors.toList());
        }

        return IngredienteDTO.builder()
                .ingredienteId(ingrediente.getIngredienteId())
                .nombre(ingrediente.getNombre())
                .unidad(ingrediente.getUnidad())
                .stock(ingrediente.getStock())
                .estado(ingrediente.getEstado())
                .mermas(mermasDTO)
                .build();
    }

    // Versión simple de IngredienteDTO para evitar recursión si fuera necesario o listas pesadas
    public IngredienteDTO toIngredienteDTOSimple(Ingrediente ingrediente) {
        if (ingrediente == null) return null;
        return IngredienteDTO.builder()
                .ingredienteId(ingrediente.getIngredienteId())
                .nombre(ingrediente.getNombre())
                .unidad(ingrediente.getUnidad())
                .stock(ingrediente.getStock())
                .estado(ingrediente.getEstado())
                .mermas(new ArrayList<>()) // Sin mermas para no cargar demasiado en listas anidadas
                .build();
    }

    // --- Merma ---
    public MermaDTO toMermaDTO(MermaInsumos merma) {
        if (merma == null) return null;

        return MermaDTO.builder()
                .mermaInsumoId(merma.getMermaInsumoId())
                .motivo(merma.getMotivo())
                .fecha(merma.getFecha())
                .ingredienteId(merma.getIngrediente() != null ? merma.getIngrediente().getIngredienteId() : null)
                .build();
    }

    // --- ProductoIngrediente ---
    public ProductoIngredienteDTO toProductoIngredienteDTO(ProductoIngrediente pi) {
        if (pi == null) return null;

        return ProductoIngredienteDTO.builder()
                .productoId(pi.getProducto() != null ? pi.getProducto().getProductoId() : null)
                .ingredienteId(pi.getIngrediente() != null ? pi.getIngrediente().getIngredienteId() : null)
                .cantidad(pi.getCantidad())
                .build();
    }
}
