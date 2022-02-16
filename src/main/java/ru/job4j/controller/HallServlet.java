package ru.job4j.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.job4j.models.Ticket;
import ru.job4j.persistence.DbStore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class HallServlet extends HttpServlet {

    private DbStore db = DbStore.instOf();
    private static final Gson GSON = new GsonBuilder().create();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Ticket> tickets = db.findAllTicketsBySessionId(1);
        Map<Integer, List<Ticket>> map = new LinkedHashMap<>();
        //группируем билеты по ряду
        tickets.forEach(ticket -> {
            map.computeIfAbsent(ticket.getRow(), k -> new ArrayList<>()).add(ticket);
        });
        //получаем список списков билетов, каждый внутренний список билетов - отдельный ряд
        Collection<List<Ticket>> listForJson = map.values();
        resp.setContentType("application/json; charset=utf-8");
        OutputStream output = resp.getOutputStream();
        String json = GSON.toJson(listForJson);
        output.write(json.getBytes(StandardCharsets.UTF_8));
        output.flush();
        output.close();
    }
}
