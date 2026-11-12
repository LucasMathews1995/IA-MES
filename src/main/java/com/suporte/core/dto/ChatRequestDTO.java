package com.suporte.core.dto;

import com.suporte.core.categoriasuporte.SuportCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChatRequestDTO(@NotNull(message = "A categoria de suporte é obrigatória.") SuportCategory categoriaSuporte , @NotBlank(message = "A pergunta não pode estar vazia.") String pergunta) {

       
 
}
