package com.microservicio.Mermas.Services;

import com.microservicio.Mermas.dto.MermaDTO;
import com.microservicio.Mermas.dto.MermaRequestDTO;

import java.util.List;

public interface MermaServiceWrite {
    MermaDTO save(MermaRequestDTO mermaDTO);
    MermaDTO update(Integer id, MermaRequestDTO mermaDTO);
    void delete(Integer id);
}
