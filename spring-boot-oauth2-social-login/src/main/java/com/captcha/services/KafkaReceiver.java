package com.captcha.services;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import com.captcha.models.RandomString;
import com.captcha.models.RandomStringDao;


public class KafkaReceiver {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaReceiver.class);

    @Autowired
    private RandomStringDao _randomStringDao;

    @KafkaListener(topics = "${kafka.topic}")
    public void listen(@Payload String message) {
        RandomString randomString = new RandomString(message);
        randomString.setCreatedTime(new Date());
        randomString.setTime(60);
        randomString.setId(_randomStringDao.getMaxId()+1);
        _randomStringDao.save(randomString);
        LOG.info("received message='{}'", message);
    }

}
