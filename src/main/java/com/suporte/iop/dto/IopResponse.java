package com.suporte.iop.dto;

import com.suporte.iop.entity.IOPEntity;

public record IopResponse(
        String id,
        String titulo,
        String equipamento,
        String chamadoReferencia,
        String texto
) {

    public IopResponse(IOPEntity iopEntity) {
        this(
                iopEntity.id != null ? iopEntity.id.toString() : null,
                iopEntity.titulo,
                iopEntity.equipamento,
                iopEntity.chamadoReferencia,
                iopEntity.texto
        );
    }
}


