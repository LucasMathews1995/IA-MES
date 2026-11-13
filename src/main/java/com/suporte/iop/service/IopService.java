package com.suporte.iop.service;


import com.suporte.iop.dto.IopRequestDTO;
import com.suporte.iop.dto.IopResponseDTO;
import com.suporte.iop.entity.IOPEntity;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class IopService {


    @Inject
    EmbeddingModel embeddingModel;

    @Inject
    EmbeddingStore<TextSegment> embeddingStore;


    public List<IopResponseDTO> listarIops() {
        List<IOPEntity> entidades = IOPEntity.listAll();
        return entidades.stream()
                .map(IopResponseDTO::new)
                .toList();
    }



    @Transactional
    public IopResponseDTO saveIOP(IopRequestDTO dto) {

        IOPEntity iop = new IOPEntity();
        iop.titulo = dto.titulo();
        iop.equipamento = dto.equipamento();
        iop.chamadoReferencia = dto.chamadoReferencia();
        iop.texto = dto.text();

        iop.persistAndFlush();

        processAndIngestNoPgVector(iop);

return new IopResponseDTO(iop);
    }


    private void processAndIngestNoPgVector(IOPEntity entity ) {
        DocumentSplitter splitter = DocumentSplitters.recursive(6000, 200);

        Metadata metadata = Metadata.from("tipo_documento", "IOP")
                .put("id_relacional", entity.id.toString())
                .put("titulo", entity.titulo)
                .put("equipamento", entity.equipamento.toString())
                .put("chamadoReferencia",entity.chamadoReferencia);


        Document doc = Document.from(entity.texto, metadata);
        List<TextSegment> subChunks = splitter.split(doc);

        String cabecalhoContexto = String.format("[GUIA DE DIAGNÓSTICO IOP | EQUIPAMENTO: %s | CHAMADO REFERENCIA: %s | TÍTULO: %s]\n",
                entity.equipamento, entity.chamadoReferencia, entity.titulo);

        List<TextSegment> todosSegmentos = new ArrayList<>();
        for (TextSegment seg : subChunks) {
            String textoEnriquecido = seg.text().startsWith("[GUIA DE DIAGNÓSTICO")
                    ? seg.text()
                    : cabecalhoContexto + seg.text();

            todosSegmentos.add(TextSegment.from(textoEnriquecido, seg.metadata()));
        }


        if (!todosSegmentos.isEmpty()) {
            var embeddings = embeddingModel.embedAll(todosSegmentos).content();
            embeddingStore.addAll(embeddings, todosSegmentos);
        }
    }








}
