package com.suporte.iop.service;


import com.suporte.iop.dto.IopResponseDTO;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.operators.multi.processors.BroadcastProcessor;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class IopEventService {

    // Cria o canal de transmissão
    private final BroadcastProcessor<IopResponseDTO> processor = BroadcastProcessor.create();


    // Dispara a nova IOP para quem estiver ouvindo
    public void notificarNovaIop(IopResponseDTO iop) {
        processor.onNext(iop);
    }

    // Retorna a stream de dados para manter a conexão HTTP aberta
    public Multi<IopResponseDTO> obterStream() {
        return processor;
    }
}
