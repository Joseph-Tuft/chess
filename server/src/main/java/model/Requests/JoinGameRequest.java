package model.Requests;

public record JoinGameRequest(String playerColor, int gameID, String authToken) {
}
