package com.suporte.training.service;


import com.suporte.iop.entity.IOPEntity;
import com.suporte.training.dto.TrainingRequestDTO;
import com.suporte.training.dto.TrainingResponseDTO;
import com.suporte.training.entity.TrainingEntity;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class TrainingService {

    @Inject
    EmbeddingStore<TextSegment> embeddingStore;

    @Inject
    EmbeddingModel embeddingModel;


    public List<TrainingResponseDTO> searchTraining() {

        List<TrainingEntity> entidades = TrainingEntity.listAll();

        return entidades.stream().map(TrainingResponseDTO::new).toList();

}


        public TrainingResponseDTO saveTraining (TrainingRequestDTO request){
            TrainingEntity training = TrainingRequestDTO.toDTO(request);

            training.persistAndFlush();

            processAndIngestNoPgVector(training);

            return new TrainingResponseDTO(training);

        }


        private void processAndIngestNoPgVector (TrainingEntity entity ){
            DocumentSplitter splitter = DocumentSplitters.recursive(6000, 200);

            Metadata metadata = Metadata.from("tipo_documento", "TRAINING")
                    .put("id_relacional", entity.id.toString())
                    .put("titulo", entity.titulo)
                    .put("tipoMes", entity.tipoMes.toString())
                    .put("chamadoReferencia", entity.chamadoReferencia);


            Document doc = Document.from(entity.text, metadata);

            List<TextSegment> subChunks = splitter.split(doc);

            String cabecalhoContexto = String.format("[GUIA DE DIAGNÓSTICO IOP | TIPO MES: %s | CHAMADO REFERENCIA: %s | TÍTULO: %s]\n",
                    entity.tipoMes.toString(), entity.chamadoReferencia, entity.titulo);

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

        private String extrairLinha (String texto, String prefixo){
            for (String linha : texto.split("\n")) {
                if (linha.trim().startsWith(prefixo)) {
                    return linha.replace(prefixo, "").trim();
                }
            }
            return "Desconhecido";
        }
    }
