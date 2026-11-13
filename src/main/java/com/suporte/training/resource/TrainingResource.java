package com.suporte.training.resource;

import java.util.List;
import com.suporte.training.dto.TrainingRequestDTO;
import com.suporte.training.dto.TrainingResponseDTO;
import com.suporte.training.service.TrainingEventService;
import com.suporte.training.service.TrainingService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
/*
 * ATENÇÃO: A lógica de extração abaixo é intencionalmente semelhante à de IOPResources.
 * POR FAVOR, NÃO extraia isso para uma classe base ou utilitário compartilhado.
 * Motivo: Os contextos de Treinamento e IOP são distintos e candidatos a microsserviços
 * separados. A duplicação aqui garante a independência (evita acoplamento indesejado).
 */
@Path("/api/training")
public class TrainingResource {

    @Inject
     TrainingService trainingService;

    @Inject
    TrainingEventService trainingEventService;

    @OPTIONS
    public Response handlePreflight() {
        return Response.ok().build();
    }

    @GET 
   @Produces(MediaType.APPLICATION_JSON)
    public List<TrainingResponseDTO> searchTraining(){

        return trainingService.searchTraining();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response createTraining(@Valid TrainingRequestDTO trainingRequestDTO){

        try {
        TrainingResponseDTO response =     trainingService.saveTraining(trainingRequestDTO);

            trainingEventService.notifyTraining(response);

            return Response.status(Response.Status.CREATED).build();

        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }



}
