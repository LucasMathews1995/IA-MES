package com.suporte.iop.resource;

import com.suporte.iop.dto.IopResponseDTO;
import com.suporte.iop.service.IopEventService;
import com.suporte.iop.service.IopService;
import io.smallrye.mutiny.Multi;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

import com.suporte.iop.dto.IopRequestDTO;

import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.RestStreamElementType;

/*
 * ATENÇÃO: A lógica de extração abaixo é intencionalmente semelhante à de IOPResources.
 * POR FAVOR, NÃO extraia isso para uma classe base ou utilitário compartilhado.
 * Motivo: Os contextos de Treinamento e IOP são distintos e candidatos a microsserviços
 * separados. A duplicação aqui garante a independência (evita acoplamento indesejado).
 */
@Path("/api/iops")
public class IopResource {

    @Inject
    IopService iopService;

    @Inject
    IopEventService iopEventService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<IopResponseDTO> listarIops() {
        return iopService.listarIops();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response inserirIops(IopRequestDTO iopDTO) {
        try{
          IopResponseDTO response =   iopService.saveIOP(iopDTO);
            iopEventService.notificarNovaIop(response);
            return Response.status(Response.Status.CREATED).build();
        }catch (Exception e) {

            e.printStackTrace();
            return Response.serverError()
                    .entity("Erro interno ao tentar salvar a IOP: " + e.getMessage())
                    .build();
        }
    }
    @GET
    @Path("/stream")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @RestStreamElementType(MediaType.APPLICATION_JSON)
    public Multi<IopResponseDTO> streamIops() {
        return iopEventService.obterStream();
    }



    
}

