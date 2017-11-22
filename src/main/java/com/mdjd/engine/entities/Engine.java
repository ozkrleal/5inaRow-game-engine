package com.mdjd.engine.entities;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="engine")
public class Engine {

    @Id private String gameID;

    private String firstPlayerUsername;

    private String secondPlayerUsername;

    private int[][] coordinatePlane;

    private int lastPlayer;

    public Engine(){}

    public Engine(String firstPlayerUUID, String secondPlayerUUID) {
        this.lastPlayer = 1;
        this.firstPlayerUsername = firstPlayerUUID;
        this.secondPlayerUsername = secondPlayerUUID;
        this.coordinatePlane = fillPlaneWithEmpty();
//        this.gameID = gameID;

    }

    private int[][] fillPlaneWithEmpty() {
        int[][] value = new int[13][13];
        for (int row = 0; row < 13; row++) {        // Fill the board with EMPTYs
            for (int col = 0; col < 13; col++) {
                value[row][col] = 0;
            }
        }
        return value;
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

    public int getLastPlayer() {
        return lastPlayer;
    }

    public String getGameID() {
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

    public void setLastPlaye(int lastPlayer) {
        this.lastPlayer = lastPlayer;
    }

    public void setGameID(String gameID) {
        this.gameID = gameID;
    }

    @Override
    public String toString() {
        return String.format(
                "Engine[gameID=%s, firstPlayerUsername='%s', secondPlayerUsername='%s']",
                gameID, firstPlayerUsername, secondPlayerUsername);
    }

}
