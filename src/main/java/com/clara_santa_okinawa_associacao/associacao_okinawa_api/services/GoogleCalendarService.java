package com.clara_santa_okinawa_associacao.associacao_okinawa_api.services;

import com.clara_santa_okinawa_associacao.associacao_okinawa_api.entities.Evento;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class GoogleCalendarService {
    private static final ZoneId FUSO = ZoneId.of("America/Sao_Paulo");
    private final RestClient cliente = RestClient.create();
    private final boolean enabled;
    private final String calendarId;
    private final String clientId;
    private final String clientSecret;
    private final String refreshToken;

    public GoogleCalendarService(
            @Value("${app.google-calendar.enabled:false}") boolean enabled,
            @Value("${app.google-calendar.calendar-id:}") String calendarId,
            @Value("${app.google-calendar.client-id:}") String clientId,
            @Value("${app.google-calendar.client-secret:}") String clientSecret,
            @Value("${app.google-calendar.refresh-token:}") String refreshToken) {
        this.enabled = enabled;
        this.calendarId = calendarId;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.refreshToken = refreshToken;
    }

    public boolean habilitado() { return enabled; }

    public void sincronizar(Evento evento) {
        if (!enabled) return;
        if (calendarId.isBlank() || clientId.isBlank() || clientSecret.isBlank() || refreshToken.isBlank()) {
            throw new IllegalStateException("Configure as credenciais Google Calendar antes de habilitar");
        }
        String accessToken = accessToken();
        String googleId = evento.getGoogleEventId() == null
                ? String.format("a0sc%08x", evento.getId()) : evento.getGoogleEventId();
        URI calendario = URI.create("https://www.googleapis.com/calendar/v3/calendars/"
                + URLEncoder.encode(calendarId, StandardCharsets.UTF_8) + "/events");
        URI recurso = URI.create(calendario + "/" + googleId);
        if ("EXCLUIR".equals(evento.getOperacaoSincronizacao())) {
            try {
                cliente.delete().uri(recurso).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .retrieve().toBodilessEntity();
            } catch (RestClientResponseException ex) {
                if (ex.getStatusCode().value() != 404 && ex.getStatusCode().value() != 410) throw ex;
            }
            evento.setOperacaoSincronizacao(null);
            return;
        }
        Map<String, Object> corpo = Map.of(
                "id", googleId,
                "summary", "Reserva - " + evento.getCodigoEspaco().getDescricao(),
                "visibility", "private",
                "start", horario(evento, true),
                "end", horario(evento, false));
        if (evento.getGoogleEventId() != null || "ATUALIZAR".equals(evento.getOperacaoSincronizacao())) {
            cliente.put().uri(recurso).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_JSON).body(corpo).retrieve().toBodilessEntity();
        } else {
            try {
                cliente.post().uri(calendario).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON).body(corpo).retrieve().toBodilessEntity();
            } catch (RestClientResponseException ex) {
                if (ex.getStatusCode().value() != 409) throw ex;
                // Reexecução idempotente após falha na persistência local.
                cliente.put().uri(recurso).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON).body(corpo).retrieve().toBodilessEntity();
            }
        }
        evento.setGoogleCalendarId(calendarId);
        evento.setGoogleEventId(googleId);
        evento.setOperacaoSincronizacao(null);
    }

    private Map<String, String> horario(Evento evento, boolean inicio) {
        if (Boolean.TRUE.equals(evento.getDiaInteiro())) {
            LocalDate data = inicio ? evento.getDtInicio() : evento.getDtFim().plusDays(1);
            return Map.of("date", data.toString());
        }
        ZonedDateTime momento = ZonedDateTime.of(
                inicio ? evento.getDtInicio() : evento.getDtFim(),
                inicio ? evento.getHoraInicio() : evento.getHoraFim(), FUSO);
        return Map.of("dateTime", momento.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                "timeZone", FUSO.getId());
    }

    @SuppressWarnings("unchecked")
    private String accessToken() {
        LinkedMultiValueMap<String, String> formulario = new LinkedMultiValueMap<>();
        formulario.add("client_id", clientId);
        formulario.add("client_secret", clientSecret);
        formulario.add("refresh_token", refreshToken);
        formulario.add("grant_type", "refresh_token");
        Map<String, Object> retorno = cliente.post().uri("https://oauth2.googleapis.com/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(formulario).retrieve().body(Map.class);
        if (retorno == null || !(retorno.get("access_token") instanceof String token)) {
            throw new IllegalStateException("Não foi possível autenticar no Google Calendar");
        }
        return token;
    }
}