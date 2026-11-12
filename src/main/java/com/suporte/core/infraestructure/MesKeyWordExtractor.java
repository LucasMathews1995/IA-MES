package com.suporte.core.infraestructure;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.suporte.core.dto.QueryContextDTO;

import jakarta.enterprise.context.ApplicationScoped;


@ApplicationScoped 
public class MesKeyWordExtractor {


private static final Set<String> EQUIPAMENTOS_CONHECIDOS = Set.of(
        "LRC", "LRC2", "LER", "LACF4", "EMB", "MCC", "LTNO", "LCG"
    );
    
    private static final Pattern PATTERN_TELA = Pattern.compile(
        "\\b(?:tela\\s+|transação\\s+)?([1-9][0-9]{2,3}|[0-9][A-Z][0-9]{2})\\b", 
        Pattern.CASE_INSENSITIVE
    );

    private static final Pattern PATTERN_PROCEDURE = Pattern.compile(
        "\\b(SP_[A-Z0-9_]+|PRC_[A-Z0-9_]+)\\b", 
        Pattern.CASE_INSENSITIVE
    );

    private static final Pattern PATTERN_CONSTRAINT = Pattern.compile(
        "\\b(IX_[A-Z0-9_]+|PK_[A-Z0-9_]+)\\b", 
        Pattern.CASE_INSENSITIVE
    );


    public QueryContextDTO extrair(String rawText) {
        if (rawText == null || rawText.isBlank()) {
            return new QueryContextDTO("", "", Set.of(), Set.of(), Set.of(), Set.of());
        }

        String upperText = rawText.toUpperCase();


        Set<String> equipamentos = new HashSet<>();
        for (String eq : EQUIPAMENTOS_CONHECIDOS) {
            if (upperText.contains(eq)) {
                equipamentos.add(eq);
            }
        }

        // 2. Extração de Telas
        Set<String> telas = extrairMatches(PATTERN_TELA, upperText);

        // 3. Extração de Procedures
        Set<String> procedures = extrairMatches(PATTERN_PROCEDURE, upperText);

        // 4. Extração de Constraints
        Set<String> constraints = extrairMatches(PATTERN_CONSTRAINT, upperText);

        return new QueryContextDTO(rawText, rawText.trim(), telas, equipamentos, procedures, constraints);
    }

    private Set<String> extrairMatches(Pattern pattern, String text) {
        Set<String> resultados = new HashSet<>();
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            resultados.add(matcher.group(1));
        }
        return resultados;
    }
}