package com.suporte.core.strategy;

import com.suporte.core.categoriasuporte.SuportCategory;
import com.suporte.core.dto.QueryContextDTO;

public interface SuportAIStrategy {
    SuportCategory getCategory( );
    String process(QueryContextDTO context);
}