package com.pkaras;

import java.util.Map;

public record GameResponse(Map<Player, String> messageToPlayers, String messageToGame) {}
