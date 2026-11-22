package com.clara_santa_okinawa_associacao.associacao_okinawa_api.dtos.noticia;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Noticia;
import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Noticia.Status;
import org.springframework.data.domain.Page;
import java.time.LocalDate;
import java.util.List;

public record NoticiaResponse(Integer id, LocalDate dataCriacao, String titulo, String classificacao,
                             String conteudo, Status status, String publico, String imagemUrl) {
    public static NoticiaResponse de(Noticia noticia) {
        String imagemUrl = noticia.getImagem() == null ? null : "/noticias/" + noticia.getId() + "/imagem";
        return new NoticiaResponse(noticia.getId(), noticia.getDataCriacao(), noticia.getTitulo(),
                noticia.getClassificacao(), noticia.getConteudo(), noticia.getStatus(), noticia.getPublico(), imagemUrl);
    }

    public record Pagina(List<NoticiaResponse> itens, int pagina, int tamanho, int totalPaginas, long totalItens) {
        public static Pagina de(Page<Noticia> noticias) {
            return new Pagina(noticias.map(NoticiaResponse::de).getContent(), noticias.getNumber(),
                    noticias.getSize(), noticias.getTotalPages(), noticias.getTotalElements());
        }
    }
}
