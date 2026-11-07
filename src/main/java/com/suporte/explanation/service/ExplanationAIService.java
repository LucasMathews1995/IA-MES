package com.suporte.explanation.service;

import com.suporte.core.categoriasuporte.SuportCategory;
import com.suporte.core.dto.ArquivoConfig;
import com.suporte.core.service.AbstractIngestService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class ExplanationAIService extends AbstractIngestService {
    @Override
    public SuportCategory getCategory() {
        return SuportCategory.EXPLANATION;
    }

  

    @Override
    public Response proccess(ArquivoConfig config) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'proccess'");
    }
}
