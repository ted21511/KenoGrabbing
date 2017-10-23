package com.kn.util;

import com.kn.util.Market;

public enum Market {
	
	// KN
//	SK("SK", "KN"),
//	MT("MT", "KN"),
//	BJ("BJ", "KN"),
	AU("AU", "KN"),
	CA("CA", "KN"),
	WCA("WCA", "KN");
	
    private final String marketName;
    private final String gameCode;

    Market(String marketName, String gameCode) {
        this.marketName = marketName;
        this.gameCode = gameCode;
    }

    public String getMarketName() {
        return marketName;
    }
    public String getGameCode() {
        return gameCode;
    }
    
}
