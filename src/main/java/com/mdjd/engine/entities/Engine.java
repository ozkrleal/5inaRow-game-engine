package com.mdjd.engine.entities;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="engine")
public class Engine {

    @Id private int gameID;

    private String firstPlayerUsername;

    private String secondPlayerUsername;

    private int[][] coordinatePlane;

    private int lastPlaye;

    public Engine(){}

    public Engine(String firstPlayerUUID, String secondPlayerUUID, int gameID) {
        coordinatePlane = new int[13][13];
        this.firstPlayerUsername = firstPlayerUUID;
        this.secondPlayerUsername = secondPlayerUUID;
        this.gameID = gameID;

    }

    public String getFirstPlayerUsername() {
        return firstPlayerUsername;
    }

    public String getSecondPlayerUsername() {
        return secondPlayerUsername;
    }

    public int[][] getCoordinatePlane() {
        return coordinatePlane;
    }

    public int getLastPlaye() {
        return lastPlaye;
    }

    public int getGameID() {
        return gameID;
    }

    public void setFirstPlayerUsername(String firstPlayerUsername) {
        this.firstPlayerUsername = firstPlayerUsername;
    }

    public void setSecondPlayerUsername(String secondPlayerUsername) {
        this.secondPlayerUsername = secondPlayerUsername;
    }

    public void setCoordinatePlane(int[][] coordinatePlane) {
        this.coordinatePlane = coordinatePlane;
    }

    public void setLastPlaye(int lastPlaye) {
        this.lastPlaye = lastPlaye;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }
}
