package com.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/model")
public class ModelController {

    @ResponseBody
    @RequestMapping("/list")
    public Map list() {
        Map result = new HashMap();
        result.put("msg", "/model/list");
        return result;
    }

    @ResponseBody
    @RequestMapping("/create")
    public Map create() {
        Map result = new HashMap();
        result.put("msg", "/model/create");
        return result;
    }

    @ResponseBody
    @RequestMapping("/update")
    public Map update() {
        Map result = new HashMap();
        result.put("msg", "/model/update");
        return result;
    }

    @ResponseBody
    @RequestMapping("/delete")
    public Map delete() {
        Map result = new HashMap();
        result.put("msg", "/model/delete");
        return result;
    }

}
