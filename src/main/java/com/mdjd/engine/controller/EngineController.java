package com.mdjd.engine.controller;


import com.mdjd.engine.entities.Engine;
import com.mdjd.engine.repository.EngineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class EngineController {

    @Autowired
    private EngineRepository repository;

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity create(@RequestBody Players player) {
        return repository.create(player.getFirstPlayer(), player.getSecondPlayer());
    }


    @RequestMapping(value = "/processing", method = RequestMethod.PUT)
    public ResponseEntity move(@RequestBody Engine engine) {
        return repository.move(engine.getGameId(), engine.getLastPlayer(), engine.getRow(), engine.getColumn());
    }


    @RequestMapping(value = "/quit", method = RequestMethod.DELETE)
    public ResponseEntity gameFinished(@RequestParam("gameId") String gameId) {
        return repository.gameFinished(gameId);
    }

    @RequestMapping(value = "/gamesState", method = RequestMethod.GET)
    public ResponseEntity gameState(@RequestParam("gameId") String gameId, @RequestParam("player") String player) {
        return repository.gameState(gameId, player);
    }

    private static class Players {

        private String firstPlayer;

        private String secondPlayer;

        public Players() {
        }

        public Players(String firstPlayer, String secondPlayer) {
            this.firstPlayer = firstPlayer;
            this.secondPlayer = secondPlayer;
        }

        public String getFirstPlayer() {
            return firstPlayer;
        }

        public String getSecondPlayer() {
            return secondPlayer;
        }

        public void setFirstPlayer(String firstPlayer) {
            this.firstPlayer = firstPlayer;
        }

        public void setSecondPlayer(String secondPlayer) {
            this.secondPlayer = secondPlayer;
        }
    }

}
