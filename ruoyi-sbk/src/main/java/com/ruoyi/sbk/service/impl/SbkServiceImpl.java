package com.ruoyi.sbk.service.impl;

import com.ruoyi.sbk.dto.Result;
import com.ruoyi.sbk.service.SbkService;
import com.ruoyi.sbk.util.SbkParamUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class SbkServiceImpl implements SbkService {
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Result getResult(String transCode, String keyInfo) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> httpBodys = SbkParamUtils.getHttpBodys(transCode, keyInfo);

        HttpEntity<Object> httpEntity = new HttpEntity<>(httpBodys, httpHeaders);

        return restTemplate.postForObject(SbkParamUtils.URL, httpEntity, Result.class);
    }
}
