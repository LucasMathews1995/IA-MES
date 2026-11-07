package com.suporte.core.strategy;

import com.suporte.core.categoriasuporte.SuportCategory;
import com.suporte.core.dto.ArquivoConfig;

import jakarta.ws.rs.core.Response;

public interface IngestionSuportStrategy {


    public SuportCategory getCategory();
    public Response proccess (ArquivoConfig config);


}
