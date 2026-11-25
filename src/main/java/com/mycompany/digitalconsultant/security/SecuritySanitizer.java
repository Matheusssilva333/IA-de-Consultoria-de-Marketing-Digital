package com.mycompany.digitalconsultant.security;

import java.util.regex.Pattern;

/**
 * Sanitizador de entradas para proteção contra Prompt Injection, XSS e Injeções em geral.
 */
public final class SecuritySanitizer {

    private static final Pattern DANGEROUS_PROMPT_INJECTIONS = Pattern.compile(
            "(?i)(ignore (all )?previous instructions|system prompt override|you are now|reveal your instructions|disregard guidelines)"
    );

    private SecuritySanitizer() {}

    public static String sanitizeInput(String input) {
        if (input == null) return "";
        // Remove caracteres de controle perigosos e normaliza
        String sanitized = input.trim()
                .replace("\u0000", "")
                .replace("\r", "");

        // Neutraliza tentativa de quebra de instrução sistêmica
        if (DANGEROUS_PROMPT_INJECTIONS.matcher(sanitized).find()) {
            sanitized = "[ALERTA SEGURANÇA: Tentativa de sobreposição de instrução detectada e filtrada] " +
                    sanitized.replaceAll("(?i)(ignore|system prompt|disregard)", "[BLOQUEADO]");
        }

        return sanitized;
    }

    public static String maskApiKey(String apiKey) {
        if (apiKey == null || apiKey.length() < 8) return "********";
        return apiKey.substring(0, 4) + "..." + apiKey.substring(apiKey.length() - 4);
    }
}
