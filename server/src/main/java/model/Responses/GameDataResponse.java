package model.Responses;

public record GameDataResponse(int gameID,
                               String whiteUsername,
                               String blackUsername,
                               String gameName) {
}
