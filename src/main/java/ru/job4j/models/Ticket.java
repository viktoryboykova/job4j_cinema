package ru.job4j.models;

import java.util.Objects;

public class Ticket {

    private int id; 
    private int price;
    private int sessionId;
    private int row;
    private int cell;
    private int accountId;
    private boolean available;

    public Ticket(int row, int cell) {
        this.row = row;
        this.cell = cell;
    }

    public Ticket(int id, int price, int sessionId, int row, int cell, int accountId, boolean available) {
        this.id = id;
        this.price = price;
        this.sessionId = sessionId;
        this.row = row;
        this.cell = cell;
        this.accountId = accountId;
        this.available = available;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCell() {
        return cell;
    }

    public void setCell(int cell) {
        this.cell = cell;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Ticket ticket = (Ticket) o;
        return id == ticket.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", price=" + price +
                ", sessionId=" + sessionId +
                ", row=" + row +
                ", cell=" + cell +
                ", accountId=" + accountId +
                ", available=" + available +
                '}';
    }
}
