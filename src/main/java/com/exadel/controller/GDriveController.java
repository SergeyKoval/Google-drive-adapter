package com.exadel.controller;

import com.exadel.service.GDriveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;

@Controller
@RequestMapping("/gdrive")
public class GDriveController {

    @Autowired
    private GDriveService googleDriveService;

    @RequestMapping("/")
    public String getMainPageName() {
        return "index";
    }

    @RequestMapping(value = "/authenticate", method = RequestMethod.GET)
    public String redirectToAuthUrl(Model model) {
        if (googleDriveService.hasAccessToken()) {
            model.addAttribute("errorMessage", "You already have authorized");
            return "index";
        } else {
            return "redirect:" + googleDriveService.getAuthorizationUrl();
        }
    }

    @RequestMapping(value = "/acceptAuthCode", method = RequestMethod.GET)
    @ResponseBody
    public void finishDriveInit(@RequestParam String code) {
        googleDriveService.initAuthToken(code);
    }

    @RequestMapping(value = "/getTokens", method = RequestMethod.GET)
    @ResponseBody
    public String getTokens() {
        String response;

        if (googleDriveService.hasAccessToken()) {
            response = "Access token: " + googleDriveService.getAccessToken() + "/n" +
                              "Refresh token: " + googleDriveService.getRefreshToken();
        } else {
            response = "You are not authorized";
        }

        return response;
    }

    @RequestMapping(value = "/testUpload", method = RequestMethod.GET)
    @ResponseBody
    public void testUpload() {
        File file = new File("/home/pmitrafanau/Downloads/riverside.mp3");
        googleDriveService.uploadFile(file, "title", "description", "audio/mpeg3");
    }
}
