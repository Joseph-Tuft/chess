package model.responses;

import java.util.ArrayList;

public record ListGamesResponse(ArrayList<GameDataResponse> games) {
}
