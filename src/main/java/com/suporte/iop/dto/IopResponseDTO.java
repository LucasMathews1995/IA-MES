package com.suporte.iop.dto;

import com.suporte.iop.entity.EquipmentType;
import com.suporte.iop.entity.IOPEntity;


public record IopResponseDTO(
        Long id,
        String titulo,
        EquipmentType equipamento,
        String chamadoReferencia,
        String texto
) {

    public IopResponseDTO(IOPEntity iopEntity) {
        this(
                iopEntity.id ,
                iopEntity.titulo,
                iopEntity.equipamento,
                iopEntity.chamadoReferencia,
                iopEntity.texto
        );
    }


}


