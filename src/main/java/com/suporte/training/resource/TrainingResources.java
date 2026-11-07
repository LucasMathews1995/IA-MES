package com.suporte.training.resource;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import com.suporte.training.dto.TrainingRequestDTO;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.OPTIONS;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/training")
public class TrainingResources {

    @OPTIONS
    public Response handlePreflight() {
        return Response.ok().build();
    }

    @GET 
   @Produces(MediaType.APPLICATION_JSON)
    public List<TrainingRequestDTO> buscarTraining(){
         List<TrainingRequestDTO> lista = new ArrayList<>();
          try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("training.markdown")) {
             if (inputStream == null) return lista;
             String conteudo = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            String[] blocos = conteudo.split("=== INÍCIO_TREINAMENTO ===");

            for (String bloco : blocos) {
                String iopLimpa = bloco.replace("=== FIM_TREINAMENTO ===", "").trim();
                if (iopLimpa.isBlank()) continue;

               
                String id = extrairLinha(iopLimpa, "ID:");
                String titulo = extrairLinha(iopLimpa, "TÍTULO:");
                String modulo = extrairLinha(iopLimpa, "MÓDULO:");

                lista.add(new TrainingRequestDTO(id, titulo, modulo, iopLimpa));
            }
    } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;}

     private String extrairLinha(String texto, String prefixo) {
        for (String linha : texto.split("\n")) {
            if (linha.trim().startsWith(prefixo)) {
                return linha.replace(prefixo, "").trim();
            }
        }
        return "Desconhecido";
    }

}
