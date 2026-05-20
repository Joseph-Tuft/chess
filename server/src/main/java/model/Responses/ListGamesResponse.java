package model.Responses;

import java.util.ArrayList;

public record ListGamesResponse(ArrayList<GameDataResponse> games) {
}
