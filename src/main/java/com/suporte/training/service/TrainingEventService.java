package com.suporte.training.service;


import com.suporte.iop.dto.IopResponseDTO;
import com.suporte.training.dto.TrainingResponseDTO;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.operators.multi.processors.BroadcastProcessor;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TrainingEventService {

    private final BroadcastProcessor <TrainingResponseDTO>  processor = BroadcastProcessor.create();



    public void notifyTraining(TrainingResponseDTO training) {
        processor.onNext(training);
    }


    public Multi<TrainingResponseDTO> obterStream() {
        return processor;
    }
}
