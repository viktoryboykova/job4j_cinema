package ru.job4j.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.job4j.models.Account;
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

public class PaymentServlet extends HttpServlet {

    private static final Gson GSON = new GsonBuilder().create();
    private final DbStore db = DbStore.instOf();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Ticket> checkedTickets = new ArrayList<>();
        for (String ticketId : toGetTicketsIds(req)) {
            checkedTickets.add(db.findTicketById(Integer.parseInt(ticketId)));
        }
        resp.setContentType("application/json; charset=utf-8");
        OutputStream output = resp.getOutputStream();
        String json = GSON.toJson(checkedTickets);
        output.write(json.getBytes(StandardCharsets.UTF_8));
        output.flush();
        output.close();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String phone = req.getParameter("phone");
        String email = req.getParameter("email");
        Account account = db.findAccountByPhoneAndEmail(phone, email);
        if (account == null) {
             account = db.saveAccount(new Account(0, username, email, phone));
        }
        for (String ticketId : toGetTicketsIds(req)) {
            Ticket ticket = db.findTicketById(Integer.parseInt(ticketId));
            ticket.setAvailable(false);
            ticket.setAccountId(account.getId());
            db.saveTicket(ticket);
        }
        resp.sendRedirect("http://localhost:8080/cinema/finish.html");
    }

    private String[] toGetTicketsIds(HttpServletRequest req) {
        String ids = req.getParameter("ids");
        return ids.split(",");
    }
}
