package com.suporte.training.dto;


import com.suporte.training.entity.TrainingEntity;
import com.suporte.training.entity.TrainingType;

public record TrainingResponseDTO(Long id, String titulo, TrainingType tipoMes  , String chamadoReferencia,String text){



    public TrainingResponseDTO  (TrainingEntity entity){
      this(
            entity.id, entity.titulo,
              entity.tipoMes,
              entity.chamadoReferencia,
              entity.text
      );
    }


}
