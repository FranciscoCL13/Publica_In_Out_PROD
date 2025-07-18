package com.dev.publica_in_out;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
public class PeticionHTTP {

    public static CompletableFuture<JSONObject> ejecuta(String url, JSONObject objeto) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Configurar conexión
                HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");

                // Enviar JSON al servidor
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(objeto.toString().getBytes(StandardCharsets.UTF_8));
                }

                // Leer respuesta
                InputStreamReader isr = new InputStreamReader(
                        conn.getResponseCode() >= 400 ? conn.getErrorStream() : conn.getInputStream(),
                        StandardCharsets.UTF_8
                );

                String response = new BufferedReader(isr).lines().collect(Collectors.joining("\n"));

                // Imprimir respuesta completa (útil para depuración)
                System.out.println("🔍 Respuesta cruda del endpoint:\n" + response);

                // Validar que la respuesta sea JSON válido
                if (response != null && response.trim().startsWith("{")) {
                    return new JSONObject(response);
                } else {
                    throw new RuntimeException("La respuesta no es JSON válido: " + response);
                }

            } catch (Exception ex) {
                throw new RuntimeException("Error en la petición HTTP: " + ex.getMessage(), ex);
            }
        });
    }
}