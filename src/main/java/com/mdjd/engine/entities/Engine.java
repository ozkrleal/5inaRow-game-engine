package com.mdjd.engine.entities;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="engine")
public class Engine {

    @Id private String gameId;

    private String firstPlayerUsername;

    private String secondPlayerUsername;

    private int[][] coordinatePlane;
    private int column;
    private int row;

    private int winner;

    private String lastPlayer;

    public Engine(){}

    public Engine(String firstPlayerUUID, String secondPlayerUUID) {
        this.lastPlayer = secondPlayerUUID;
        this.winner = 0;
        this.column = -1;
        this.row = -1;
        this.firstPlayerUsername = firstPlayerUUID;
        this.secondPlayerUsername = secondPlayerUUID;
        this.coordinatePlane = fillPlaneWithEmpty();
//        this.gameId = gameId;

    }

    private int[][] fillPlaneWithEmpty() {
        int[][] value = new int[19][19];
        for (int row = 0; row < 19; row++) {        // Fill the board with EMPTYs
            for (int col = 0; col < 19; col++) {
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

    public String getLastPlayer() {
        return lastPlayer;
    }

    public String getGameId() {
        return gameId;
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

    public void setGameId(String gameId) {
        this.gameId = gameId;
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

    public void setLastPlayer(String lastPlayer) {
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
                "Engine[gameId=%s, firstPlayerUsername='%s', secondPlayerUsername='%s']",
                gameId, firstPlayerUsername, secondPlayerUsername);
    }

}