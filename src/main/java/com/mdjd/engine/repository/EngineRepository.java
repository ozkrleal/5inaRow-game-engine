package com.mdjd.engine.repository;


import com.mdjd.engine.entities.Engine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EngineRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    private int currentPlayer;
    private int[][] board;
    private String currentGameId;

    private enum Msg {
        DATABASE(0, "A database error has occured."),
        UNTURN_MOVE(1, "This user already moved."),
        EMPTY_SQUARE(2, "This is not an empty square"),
        START_NEW_GAME(3, "Finish the current game first!"),
        NO_GAME_FOUND(4, "This Game Id is invalid"),
        PLAYER_RESIGNS(5, "Player resigns, you have won"),
        DRAW(6, "The game ends in a draw."),
        GAME_FINISHED(7, "The game is finished"),
        PLAYER_MOVED(7, "The player has moved");


        private final int code;
        private final String description;

        private Msg(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        public int getCode() {
            return code;
        }

        @Override
        public String toString() {
            return code + ": " + description;
        }
    }

    public ResponseEntity create(String firstPlayer, String secondPlayer) {
//        int gameId = getNewGameId();
        Engine engine = new Engine(firstPlayer, secondPlayer);
        mongoTemplate.save(engine, "engine");
        System.out.print(engine);
        return ResponseEntity.status(HttpStatus.CREATED).body(engine.getGameID());
    }

//    private int getNewGameId() {
//        int counter = 1;
//        int i = 0;
//        List<Integer> list = findGameIds();
//
//        while (i<list.size()) {
//            if (counter == list.get(i)) {
//                counter++;
//                i = 0;
//            } else {
//                ++i;
//            }
//        }
//
//        return counter;
//    }

    private void calculateScore(int player, String gameId) {
//        if(player == 0) {
//            calculateScore(1, gameId);
//            calculateScore(2, gameId);
//        } else {
//            calculateScore(player, gameId);
//        }
    }

    private Engine retreiveEngine(String gameId) {
        Criteria c = Criteria.where("_id").is(gameId);
        Query query = new Query().addCriteria(c);
        Engine engine = mongoTemplate.findOne(query, Engine.class);
        return engine;
    }

    private int updateEngine(Update update) {
        mongoTemplate.updateFirst(new Query(Criteria.where("_id").is(currentGameId)), update, "engine");
        Engine engine =mongoTemplate.findOne(new Query(Criteria.where("_id").is(currentGameId)), Engine.class);

        return Msg.DATABASE.getCode();
    }

    public ResponseEntity move(String gameId, int player,int row, int col) {
        Engine engine = retreiveEngine(gameId);
        Update update = new Update();
        //do it here

        board = engine.getCoordinatePlane();
        currentPlayer = player;
        currentGameId = engine.getGameID();

        if (currentPlayer == engine.getLastPlayer()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Msg.UNTURN_MOVE.description);
        }

        if ( board[row][col] != 0 ) {
            return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body(Msg.EMPTY_SQUARE.description);
        }

        board[row][col] = player; //make the move
        update.set("coordinatePlane",board);
        update.set("lastPlayer",currentPlayer);

        if (winner(row,col)) {  // First, check for a winner.
            String str;
            if (currentPlayer == 1)
                str = gameFinished(gameId, 1);
            else
                str = gameFinished(gameId, 2);
            return ResponseEntity.status(HttpStatus.RESET_CONTENT).body(str);
        }

        boolean emptySpace = false;     // Check if the board is full.
        for (int i = 0; i < 13; i++)
            for (int j = 0; j < 13; j++)
                if (board[i][j] == 0)
                    emptySpace = true;
        if (emptySpace == false) {
            String str = gameFinished(gameId, 0);
            return ResponseEntity.status(HttpStatus.RESET_CONTENT).body(str);
        }
        updateEngine(update);
        return ResponseEntity.ok(Msg.PLAYER_MOVED.description);
    }

    private boolean winner(int row, int col) {
        //horizontal direction: 1 0
        //vertical direction: 0 1
        //first diagonal: 1 1
        //second diagonal: 1 -1
        //should check for >= 5 because it is possible that player place a piece in an empty square that joins to other short rows of pieces

        if (count(currentPlayer, row, col, 1, 0 ) >= 5)
            return true;
        if (count(currentPlayer, row, col, 0, 1 ) >= 5)
            return true;
        if (count(currentPlayer, row, col, 1, -1 ) >= 5)
            return true;
        if (count(currentPlayer, row, col, 1, 1 ) >= 5)
            return true;

          /* When we get to this point, we know that the game is not won. */

        return false;

    }

    private int count(int player, int row, int col, int dirX, int dirY) {

        int ct = 1;  // Number of pieces in a row belonging to the player.

        int r, c;    // A row and column to be examined

        r = row + dirX;  // Look at square in specified direction.
        c = col + dirY;
        while ( r >= 0 && r < 13 && c >= 0 && c < 13 && board[r][c] == player ) {
            // Square is on the board and contains one of the players's pieces.
            ct++;
            r += dirX;  // Go on to next square in this direction.
            c += dirY;
        }

        //    did not contain one of the player's pieces.

        r = row - dirX;  // Look in the opposite direction.
        c = col - dirY;
        while ( r >= 0 && r < 13 && c >= 0 && c < 13 && board[r][c] == player ) {
            // Square is on the board and contains one of the players's pieces.
            ct++;
            r -= dirX;   // Go on to next square in this direction.
            c -= dirY;
        }

        return ct;
    }


    public String gameFinished(String gameId, int player) {
        Engine engine = new Engine();
        calculateScore(player, gameId);
        //do it here

        return "";
    }

    @SuppressWarnings("unchecked")
    public List<Integer> findGameIds() {
        return mongoTemplate.getCollection("engine").distinct("_id");
    }


}
