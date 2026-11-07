package com.suporte.core.dto;

import java.util.Set;

public record QueryContextDTO(
     String rawText,
    String cleanQuery,
    Set<String> telas,
    Set<String> equipamentos,
    Set<String> procedures,
    Set<String> constraints) {

   
 
    public boolean hasFilters() {
        return (telas != null && !telas.isEmpty())
            || (equipamentos != null && !equipamentos.isEmpty())
            || (procedures != null && !procedures.isEmpty())
            || (constraints != null && !constraints.isEmpty());
    }

}
