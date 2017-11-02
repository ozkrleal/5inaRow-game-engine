package com.mdjd.engine.repository;


import com.mdjd.engine.entities.Engine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EngineRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    public int create(String firstPlayer, String secondPlayer) {
        int gameId = getNewGameId();
        Engine engine = new Engine(firstPlayer, secondPlayer, gameId);
        return gameId;
    }

    private int getNewGameId() {
        int counter = 1;
        int i = 0;
        List<Integer> list = findGameIds();

        while (i<list.size()) {
            if (counter == list.get(i)) {
                counter++;
                i = 0;
            } else {
                ++i;
            }
        }

        return counter;
    }

    private void calculateScore(int player, int gameId) {
//        if(player == 0) {
//            calculateScore(1, gameId);
//            calculateScore(2, gameId);
//        } else {
//            calculateScore(player, gameId);
//        }
    }

    public Engine move(int gameId, int player,int[][] coordinate) {
        Update update = new Update();
        //do it here

        mongoTemplate.updateFirst(new Query(Criteria.where("_id").is(gameId)), update, "engine");
        Engine engine = mongoTemplate.findOne(new Query(Criteria.where("_id").is(gameId)), Engine.class);
        return engine;
    }

    public Engine gameFinished(int gameId, int player) {
        Engine engine = new Engine();
        calculateScore(player, gameId);
        //do it here

        return engine;
    }

    @SuppressWarnings("unchecked")
    public List<Integer> findGameIds() {
        return mongoTemplate.getCollection("engine").distinct("gameId");
    }


}
