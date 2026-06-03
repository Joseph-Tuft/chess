package model.responses;

public record RegisterResponse(String authToken,
                               String username) {
}
