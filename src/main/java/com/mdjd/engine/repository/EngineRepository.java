package com.mdjd.engine.repository;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mdjd.engine.entities.Engine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Repository
public class EngineRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    private int currentPlayer;
    private String currentGameId;
    private Object lock = new Object();

    private enum Msg {
        DATABASE(0, "A database error has occured"),
        UNTURN_MOVE(1, "This user already moved"),
        EMPTY_SQUARE(2, "This is not an empty square"),
        START_NEW_GAME(3, "Finish the current game first"),
        NO_GAME_FOUND(4, "This Game Id is invalid"),
        PLAYER_RESIGNS(5, "Player resigns, you have won"),
        PLAYER_MOVED(6, "The player has moved"),
        DRAW(7, "The game ends in a draw"),
        GAME_FINISHED(8, "The game is finished"),
        NOT_YOUR_TURN(9, "It is not your turn"),
        YOUR_TURN(10, "It is your turn"),
        GAME_DELETED(11, "The game is removed");

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

        public ObjectNode toJson(boolean showCode) {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode value;
            if (showCode == true) {
                value = mapper.createObjectNode().put("code", code);
            } else {
                value = mapper.createObjectNode().put("message", description);
            }
            return value;
        }
    }

    public ResponseEntity create(String firstPlayer, String secondPlayer) {
        String responseValue;
        synchronized (lock) {
            String playerIsInGame = playerIsInGame(firstPlayer);
            if (playerIsInGame.equals("")) {
                Engine engine = new Engine(firstPlayer, secondPlayer);
                mongoTemplate.save(engine, "engine");
                System.out.print(engine);
                responseValue = new ObjectMapper().createObjectNode().put("game_id", engine.getGameId())
                        .put("firstPlayer", firstPlayer)
                        .put("secondPlayer", secondPlayer).toString();
            } else {
                responseValue = new ObjectMapper().createObjectNode().put("game_id", playerIsInGame)
                        .put("firstPlayer", secondPlayer)
                        .put("secondPlayer", firstPlayer).toString();
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(responseValue);
        }
    }

    private String playerIsInGame(String player) {
            List<ObjectId> allGameIds = findGameIds();
            for (ObjectId tempId : allGameIds) {
                String tempIdStr = tempId.toString();
                Engine engine = retreiveEngine(tempIdStr);
                if (engine.getSecondPlayerUsername().equals(player)) {
                    return engine.getGameId();
                }
            }
            return "";
    }

    private String calculateScore(String player, int moves) throws IOException {
        //String urlString = String.format("http://localhost:8080/5inarow/score?player=%s&moves=%2d", player, moves);
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://localhost:3001/5inarow/score");

        List<BasicNameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("userName", player));
        params.add(new BasicNameValuePair("numberOfMoves", String.valueOf(moves)));
        httpPost.setEntity(new UrlEncodedFormEntity(params));

        CloseableHttpResponse response = client.execute(httpPost);
        String str = response.getStatusLine().getReasonPhrase();
        client.close();
        return str;
//        URL url = new URL("http://localhost:3001/5inarow/score");
//        HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
//        httpCon.setRequestMethod("PUT");
//        httpCon.setRequestProperty( "userName", player);
//        httpCon.setRequestProperty( "numberOfMoves", String.valueOf(moves));
//        httpCon.setUseCaches( false );
//        //return ResponseEntity.status(httpCon.getResponseCode()).body(httpCon.getResponseMessage());
//        return httpCon.getResponseMessage();
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
        System.out.print(engine);
        return Msg.DATABASE.getCode();
    }

    public ResponseEntity gameState(String gameId, String player) {
        Engine engine = retreiveEngine(gameId);
        int[][] board = engine.getCoordinatePlane();
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode commonJsonData = mapper.createObjectNode().put("who_made_last_move",player)
                .put("row",engine.getRow())
                .put("column",engine.getColumn());

        boolean emptySpace = checkBoardIsFull(board);
        if (emptySpace == false) {
            return ResponseEntity.status(HttpStatus.OK).body(commonJsonData.put("code", Msg.DRAW.getCode()).toString());
        }

        if (engine.getWinner() != 0) {
            return ResponseEntity.status(HttpStatus.OK).body(commonJsonData.put("code", Msg.GAME_FINISHED.getCode()).toString());
        } else {
            String msgCode;
            if (player.equals(engine.getLastPlayer())) {
                msgCode = commonJsonData.put("code", Msg.NOT_YOUR_TURN.getCode()).toString();
            } else if (player.equals(engine.getFirstPlayerUsername()) || player.equals(engine.getSecondPlayerUsername())) {
                msgCode = commonJsonData.put("code", Msg.YOUR_TURN.getCode()).toString();
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Msg.NO_GAME_FOUND.toJson(true).toString());
            }

            return ResponseEntity.ok(msgCode);
        }
    }

    private int returnNumberOfPlayer(Engine engine, String player) {
        int value = 0;
        if (player.equals(engine.getFirstPlayerUsername())) {
            value = 1;
        } else if (player.equals(engine.getSecondPlayerUsername())) {
            value = 2;
        }
        return value;
    }

    public ResponseEntity move(String gameId, String player,int row, int col) {
        Engine engine = retreiveEngine(gameId);
        Update update = new Update();
        //do it here
        int[][] board;
        board = engine.getCoordinatePlane();
        currentGameId = engine.getGameId();
        currentPlayer = returnNumberOfPlayer(engine, player);

        if (player.equals(engine.getLastPlayer())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Msg.NOT_YOUR_TURN.toJson(true).put("who_made_last_move",player).toString());
        }
        else if (player.equals(engine.getFirstPlayerUsername()) || player.equals(engine.getSecondPlayerUsername())) {
            if (board[row][col] != 0) {
                return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body(Msg.EMPTY_SQUARE.toJson(true).toString());
            }

            board[row][col] = currentPlayer; //make the move
            update.set("row", row);
            update.set("column", col);
            update.set("coordinatePlane", board);
            update.set("lastPlayer", player);

            System.out.print("started :" + currentPlayer);

            if (winner(row, col, board)) {  // First, check for a winner.
                String res = saveHighScore(gameId, player);
                if (currentPlayer == 1) {
                    update.set("winner", 1);
                } else {
                    update.set("winner", 2);
                }
                System.out.print("winner");
                updateEngine(update);
                return ResponseEntity.status(HttpStatus.OK).body(Msg.GAME_FINISHED.toJson(true).put("save_score_response", res).toString());
            } else {
                updateEngine(update);

                boolean emptySpace = checkBoardIsFull(board);
                if (emptySpace == false) {
                    System.out.print("full");
                    return ResponseEntity.status(HttpStatus.OK).body(Msg.DRAW.toJson(true).toString());
                }

                return ResponseEntity.ok((Msg.PLAYER_MOVED.toJson(true)
                        .put("who_made_last_move", player)
                        .put("column", col).put("row", row)).toString());
            }
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Msg.NO_GAME_FOUND.toJson(true).toString());
        }
    }

    private boolean checkBoardIsFull(int[][] board) {
        boolean emptySpace = false;     // Check if the board is full.
        for (int i = 0; i < 18; i++)
            for (int j = 0; j < 18; j++)
                if (board[i][j] == 0)
                    emptySpace = true;
        return emptySpace;
    }

    private boolean winner(int row, int col, int[][] board) {
        //horizontal direction: 1 0
        //vertical direction: 0 1
        //first diagonal: 1 1
        //second diagonal: 1 -1
        //should check for >= 5 because it is possible that player place a piece in an empty square that joins to other short rows of pieces

        if (count(currentPlayer, row, col, 1, 0 , board) >= 5)
            return true;
        if (count(currentPlayer, row, col, 0, 1 , board) >= 5)
            return true;
        if (count(currentPlayer, row, col, 1, -1 , board) >= 5)
            return true;
        if (count(currentPlayer, row, col, 1, 1 , board) >= 5)
            return true;

          /* When we get to this point, we know that the game is not won. */

        return false;

    }

    private int count(int player, int row, int col, int dirX, int dirY, int[][] board) {

        int ct = 1;  // Number of pieces in a row belonging to the player.

        int r, c;    // A row and column to be examined

        r = row + dirX;  // Look at square in specified direction.
        c = col + dirY;
        while ( r >= 0 && r < 18 && c >= 0 && c < 18 && board[r][c] == player ) {
            // Square is on the board and contains one of the players's pieces.
            ct++;
            r += dirX;  // Go on to next square in this direction.
            c += dirY;
        }

        //    did not contain one of the player's pieces.

        r = row - dirX;  // Look in the opposite direction.
        c = col - dirY;
        while ( r >= 0 && r < 18 && c >= 0 && c < 18 && board[r][c] == player ) {
            // Square is on the board and contains one of the players's pieces.
            ct++;
            r -= dirX;   // Go on to next square in this direction.
            c -= dirY;
        }

        return ct;
    }

    private int calculateTheMoves(int player, int[][] board) {
        int moves = 0;
        for (int i = 0; i < 18; i++)
            for (int j = 0; j < 18; j++)
                if (board[i][j] == player)
                    moves++;
        return moves;
    }


    public ResponseEntity gameFinished(String gameId) {
        Engine engine = retreiveEngine(gameId);
        ObjectNode playersJson = new ObjectMapper().createObjectNode()
                .put("first_player", engine.getFirstPlayerUsername())
                .put("second_player", engine.getSecondPlayerUsername());
        removeTheGameDB(gameId);
        return ResponseEntity.ok(playersJson.put("Message :", Msg.GAME_DELETED.getDescription()).toString());
    }

    private String saveHighScore(String gameId, String player) {
        Engine engine = retreiveEngine(gameId);
        int[][] board;
        board = engine.getCoordinatePlane();
        int numberOfThePlayer = returnNumberOfPlayer(engine, player);
        try {
            return calculateScore(player, calculateTheMoves(numberOfThePlayer, board));
        } catch (IOException e) {
            e.printStackTrace();
            return e.toString();
        }
    }

    private void removeTheGameDB(String gameId) {
        Engine engine = mongoTemplate.findById(gameId, Engine.class, "engine");
        mongoTemplate.remove(engine);
    }

    @SuppressWarnings("unchecked")
    public List<ObjectId> findGameIds() {
        return mongoTemplate.getCollection("engine").distinct("_id");
    }


}
