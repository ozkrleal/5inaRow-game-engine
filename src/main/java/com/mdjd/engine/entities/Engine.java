package com.mdjd.engine.entities;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="engine")
public class Engine {

    @Id private int gameID;

    private String firstPlayerUUID;

    private String secondPlayerUUID;

    private int[][] coordinatePlane;

    private int currentPlayer;

    public Engine(){}

    public Engine(String firstPlayerUUID, String secondPlayerUUID, int gameID) {
        coordinatePlane = new int[14][14];
        this.firstPlayerUUID = firstPlayerUUID;
        this.secondPlayerUUID = secondPlayerUUID;
        this.gameID = gameID;

    }

    public String getFirstPlayerUUID() {
        return firstPlayerUUID;
    }

    public String getSecondPlayerUUID() {
        return secondPlayerUUID;
    }

    public int[][] getCoordinatePlane() {
        return coordinatePlane;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public int getGameID() {
        return gameID;
    }

    public void setFirstPlayerUUID(String firstPlayerUUID) {
        this.firstPlayerUUID = firstPlayerUUID;
    }

    public void setSecondPlayerUUID(String secondPlayerUUID) {
        this.secondPlayerUUID = secondPlayerUUID;
    }

    public void setCoordinatePlane(int[][] coordinatePlane) {
        this.coordinatePlane = coordinatePlane;
    }

    public void setCurrentPlayer(int currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }
}
