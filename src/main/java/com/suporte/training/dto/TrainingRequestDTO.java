package com.suporte.training.dto;

import com.suporte.training.entity.TrainingEntity;
import com.suporte.training.entity.TrainingType;
import jakarta.validation.constraints.NotBlank;

public record TrainingRequestDTO(@NotBlank(message = "titulo não pode ser nulo") String titulo,
                                 @NotBlank(message =  "O tipo do MES não pode ser nulo") TrainingType tipoMes,
                                 String chamadoReferencia,
                                 @NotBlank(message  = "o texto não pode ser nulo") String  text) {

    public static TrainingEntity toDTO (TrainingRequestDTO dto){
        return new TrainingEntity(
                dto.titulo(),
                dto.tipoMes(),
                dto.chamadoReferencia(),
                dto.text()
        );
    }



}
