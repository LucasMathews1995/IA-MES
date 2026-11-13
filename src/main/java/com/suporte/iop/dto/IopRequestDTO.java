package com.suporte.iop.dto;

import com.suporte.iop.entity.EquipmentType;
import jakarta.validation.constraints.NotBlank;




public record IopRequestDTO(
         @NotBlank(message = "necessário ter o título") String titulo,
        @NotBlank (message = "Necessário ter o equipamento") EquipmentType equipamento,
         String chamadoReferencia,
        @NotBlank(message  = "Texto não pode ser vazio ou nulo")    String text) {




}
