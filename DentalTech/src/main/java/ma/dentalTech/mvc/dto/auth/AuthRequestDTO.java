package ma.dentalTech.mvc.dto.auth;

/**
 * DTO du prof : Utilise la syntaxe Record.
 */
public record AuthRequestDTO(
        String login,
        String password
) {}