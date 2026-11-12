package com.suporte.training.resource;

import java.util.List;
import com.suporte.training.dto.TrainingRequestDTO;
import com.suporte.training.service.TrainingService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.OPTIONS;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
/*
 * ATENÇÃO: A lógica de extração abaixo é intencionalmente semelhante à de IOPResources.
 * POR FAVOR, NÃO extraia isso para uma classe base ou utilitário compartilhado.
 * Motivo: Os contextos de Treinamento e IOP são distintos e candidatos a microsserviços
 * separados. A duplicação aqui garante a independência (evita acoplamento indesejado).
 */
@Path("/api/training")
public class TrainingResources {

    @Inject
     TrainingService trainingService;

    @OPTIONS
    public Response handlePreflight() {
        return Response.ok().build();
    }

    @GET 
   @Produces(MediaType.APPLICATION_JSON)
    public List<TrainingRequestDTO> searchTraining(){

        return trainingService.searchTraining();
    }



}
