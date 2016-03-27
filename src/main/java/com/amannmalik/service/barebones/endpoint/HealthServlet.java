package com.amannmalik.service.barebones.endpoint;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by Amann Malik (amannmalik@gmail.com) on 7/12/2015.
 */


@WebServlet
@ApplicationScoped
public class HealthServlet extends HttpServlet {

    private static final long MIN_AVAILABLE_BYTES = 10485760L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        FileStore homeStore = Files.getFileStore(Paths.get(System.getProperty("user.home")));
        long available = homeStore.getUsableSpace();
        long total = homeStore.getUsableSpace();

        resp.setContentType("application/json");
        try (JsonWriter writer = Json.createWriter(resp.getOutputStream())) {
            JsonObject healthResponse = Json.createObjectBuilder()
                    .add("status", "UP")
                    .add("diskSpace", Json.createObjectBuilder()
                                    .add("status", available < MIN_AVAILABLE_BYTES ? "DOWN" : "UP")
                                    .add("total", total)
                                    .add("free", available)
                                    .add("threshold", MIN_AVAILABLE_BYTES)
                    ).build();
            writer.writeObject(healthResponse);
        }

    }

    @Override
    public void init() throws ServletException {
    }

    @Override
    public void destroy() {
    }
}