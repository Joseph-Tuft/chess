package model.Requests;

public record CreateGameRequest(String gameName,
                                String authToken) {
}
