package com.suporte.iop.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.suporte.iop.dto.IOPDTO;

import io.quarkus.security.Authenticated;

@Path("/api/iops")
public class IOPResources {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<IOPDTO> listarIops() {
        List<IOPDTO> lista = new ArrayList<>();
        
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("iops.markdown")) {
            if (inputStream == null) return lista;

            String conteudo = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            String[] blocos = conteudo.split("=== INÍCIO_IOP ===");

            for (String bloco : blocos) {
                String iopLimpa = bloco.replace("=== FIM_IOP ===", "").trim();
                if (iopLimpa.isBlank()) continue;

               
                String id = extrairLinha(iopLimpa, "ID:");
                String titulo = extrairLinha(iopLimpa, "TÍTULO:");
                String modulo = extrairLinha(iopLimpa, "MÓDULO:");

                lista.add(new IOPDTO(id, titulo, modulo, iopLimpa));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    private String extrairLinha(String texto, String prefixo) {
        for (String linha : texto.split("\n")) {
            if (linha.trim().startsWith(prefixo)) {
                return linha.replace(prefixo, "").trim();
            }
        }
        return "Desconhecido";
    }

  

    
}