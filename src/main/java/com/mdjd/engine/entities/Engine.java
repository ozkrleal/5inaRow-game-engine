package com.mdjd.engine.entities;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="engine")
public class Engine {

    @Id private String gameID;

    private String firstPlayerUsername;

    private String secondPlayerUsername;

    private int[][] coordinatePlane;
    private int column;
    private int row;

    private int winner;

    private int lastPlayer;

    public Engine(){}

    public Engine(String firstPlayerUUID, String secondPlayerUUID) {
        this.lastPlayer = 2;
        this.winner = 0;
        this.firstPlayerUsername = firstPlayerUUID;
        this.secondPlayerUsername = secondPlayerUUID;
        this.coordinatePlane = fillPlaneWithEmpty();
//        this.gameID = gameID;

    }

    private int[][] fillPlaneWithEmpty() {
        int[][] value = new int[18][18];
        for (int row = 0; row < 18; row++) {        // Fill the board with EMPTYs
            for (int col = 0; col < 18; col++) {
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

    public int getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public void setLastPlayer(int lastPlayer) {
        this.lastPlayer = lastPlayer;
    }

    public int getWinner() {
        return winner;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setWinner(int winner) {
        this.winner = winner;
    }

    @Override
    public String toString() {
        return String.format(
                "Engine[gameID=%s, firstPlayerUsername='%s', secondPlayerUsername='%s']",
                gameID, firstPlayerUsername, secondPlayerUsername);
    }

}