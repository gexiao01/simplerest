package com.dp.simplerest.control;

import org.springframework.stereotype.Component;

import com.dp.simplerest.annotation.Rest;
import com.dp.simplerest.util.JsonUtils;

@Component
@Rest
public class TestController {

    @Rest(path = "/test")
    public String index() {
        return "Hello World!";
    }

    @Rest(path = "/test/query")
    public Resolution query(long id) {
        Resolution resolution = new Resolution();
        resolution.setHeight(800);
        resolution.setWidth(600);
        return resolution;
    }

    @Rest(path = "/test/insert")
    public void insert(long id, String value) {
        System.out.println("Invoke insert id=" + id + " value=" + value);
    }

    @Rest(path = "/test/json")
    public Resolution json(Resolution r) {
        r.setHeight(r.getHeight() + 1);
        r.setWidth(r.getWidth() + 1);
        return r;
    }

    public void notbinded() {
    }

    public static void main(String[] args) {
        String json="{\"height\":10,\"width\":20}";
        JsonUtils.fromJsonString(Resolution.class, json);
    }
}
