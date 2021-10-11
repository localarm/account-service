package com.pavel.account_service.controllers;

import com.pavel.account_service.services.AccountAccessException;
import com.pavel.account_service.services.AccountService;
import com.pavel.account_service.services.StatisticService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class AccountController {

    private final static Logger LOGGER = LoggerFactory.getLogger(AccountController.class);
    private final AccountService accountService;
    private final StatisticService statisticService;

    @Autowired
    public AccountController(AccountService accountService, StatisticService statisticService) {
        this.accountService = accountService;
        this.statisticService = statisticService;
    }

    @PutMapping("/accounts/{id}")
    public ResponseEntity addAmount(@PathVariable("id") Integer id, @RequestBody Amount amount){
        if (amount.getAmount() == null || amount.getAmount() == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        try {
            accountService.addAmount(id, amount.getAmount());
            return ResponseEntity.ok().build();
        } catch (AccountAccessException e) {
            LOGGER.warn("Failed to set balance = {} to id = {}", amount.getAmount(), id, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            LOGGER.warn("Unexpected exception occurred", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/accounts/{id}")
    public ResponseEntity<Amount> getAmount(@PathVariable("id") Integer id){
        try {
            Long balance = accountService.getAmount(id);
            return new ResponseEntity<>(new Amount(balance), HttpStatus.OK);
        } catch (AccountAccessException e) {
            LOGGER.warn("Failed to get amount by id = {}", id, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            LOGGER.warn("Unexpected exception occurred", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

        @GetMapping("/statistics/reset")
    public ResponseEntity refreshStatistic() {
        statisticService.resetStatistic();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/statistics/get")
    public ResponseEntity<StatisticInfo> getAmountStatistic() {
        return ResponseEntity.ok(new StatisticInfo(statisticService.getTotalCountOfGetAmount(),
                statisticService.getCurrentCountOfGetAmount()));
    }

    @GetMapping("/statistics/add")
    public ResponseEntity<StatisticInfo> setAmountStatistic() {
        return ResponseEntity.ok(new StatisticInfo(statisticService.getTotalCountOfAddAmount(),
                statisticService.getCurrentCountOfAddAmount()));
    }
}
