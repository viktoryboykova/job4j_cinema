package ru.job4j.persistence;

import org.apache.commons.dbcp2.BasicDataSource;
import ru.job4j.models.Account;
import ru.job4j.models.Ticket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

public class DbStore {

    private final BasicDataSource pool = new BasicDataSource();

    private DbStore() {
        Properties cfg = new Properties();
        try (BufferedReader io = new BufferedReader(
                new InputStreamReader(
                        DbStore.class.getClassLoader()
                                .getResourceAsStream("db.properties")
                )
        )) {
            cfg.load(io);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        pool.setDriverClassName(cfg.getProperty("jdbc.driver"));
        pool.setUrl(cfg.getProperty("jdbc.url"));
        pool.setUsername(cfg.getProperty("jdbc.username"));
        pool.setPassword(cfg.getProperty("jdbc.password"));
        pool.setMinIdle(5);
        pool.setMaxIdle(10);
        pool.setMaxOpenPreparedStatements(100);
    }

    private static final class Lazy {
        private static final DbStore INST = new DbStore();
    }

    public static DbStore instOf() {
        return Lazy.INST;
    }

    public Account saveAccount(Account account) {
        if (account.getId() == 0) {
           return createAccount(account);
        } else {
            updateAccount(account);
        }
        return account;
    }

    private Account createAccount(Account account) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement("INSERT INTO account(username, email, phone) VALUES (?, ?, ?)",
                     PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, account.getUsername());
            ps.setString(2, account.getEmail());
            ps.setString(3, account.getPhone());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    account.setId(id.getInt(1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return account;
    }

    private void updateAccount(Account account) {
        try (Connection cn = pool.getConnection();
             PreparedStatement statement =
                     cn.prepareStatement("UPDATE account SET username = ?, email = ?, phone = ? WHERE id = ?")) {
            statement.setString(1, account.getUsername());
            statement.setString(2, account.getEmail());
            statement.setString(3, account.getPhone());
            statement.setInt(4, account.getId());
            statement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Collection<Account> findAllAccounts() {
        List<Account> accounts = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement("SELECT * FROM account")
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    accounts.add(
                            new Account(
                                    it.getInt("id"),
                                    it.getString("username"),
                                    it.getString("email"),
                                    it.getString("phone")
                            ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return accounts;
    }

    public Account findAccountByPhoneAndEmail(String phone, String email) {
        Account account = null;
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement("SELECT * FROM account WHERE email = ? AND phone = ?")
        ) {
            ps.setString(1, email);
            ps.setString(2, phone);
            try (ResultSet it = ps.executeQuery()) {
                if (it.next()) {
                    account = new Account(
                            it.getInt("id"),
                            it.getString("username"),
                            it.getString("email"),
                            it.getString("phone")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return account;
    }

    public void deleteAccount(int id) {
        try (Connection cn = pool.getConnection();
             PreparedStatement statement =
                     cn.prepareStatement("DELETE FROM account WHERE id = ?")) {
            statement.setInt(1, id);
            statement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveTicket(Ticket ticket) {
        if (ticket.getId() == 0) {
            createTicket(ticket);
        } else {
            updateTicket(ticket);
        }
    }

    private void createTicket(Ticket ticket) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement("INSERT INTO ticket(price, session_id, row, cell, available, account_id) VALUES (?, ?, ?, ?, ?, ?)",
                     PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setInt(1, ticket.getPrice());
            ps.setInt(2, ticket.getSessionId());
            ps.setInt(3, ticket.getRow());
            ps.setInt(4, ticket.getCell());
            ps.setBoolean(5, ticket.isAvailable());
            ps.setInt(6, ticket.getAccountId());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    ticket.setId(id.getInt(1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateTicket(Ticket ticket) {
        try (Connection cn = pool.getConnection();
             PreparedStatement statement =
                     cn.prepareStatement("UPDATE ticket SET price = ?, session_id = ?, row = ?, cell = ?, available = ?, account_id = ? WHERE id = ?")) {
            statement.setInt(1, ticket.getPrice());
            statement.setInt(2, ticket.getSessionId());
            statement.setInt(3, ticket.getRow());
            statement.setInt(4, ticket.getCell());
            statement.setBoolean(5, ticket.isAvailable());
            statement.setInt(6, ticket.getAccountId());
            statement.setInt(7, ticket.getId());
            statement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Ticket> findAllTicketsBySessionId(int sessionId) {
        List<Ticket> tickets = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement("SELECT * FROM ticket WHERE session_id = ?")
        ) {
            ps.setInt(1, sessionId);
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    tickets.add(
                            new Ticket(
                                    it.getInt("id"),
                                    it.getInt("price"),
                                    it.getInt("session_id"),
                                    it.getInt("row"),
                                    it.getInt("cell"),
                                    it.getInt("account_id"),
                                    it.getBoolean("available")

                            ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tickets;
    }

    public Ticket findTicketById(int id) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement("SELECT * FROM ticket WHERE id = ?")
        ) {
            ps.setInt(1, id);
            try (ResultSet it = ps.executeQuery()) {
                if (it.next()) {
                    return new Ticket(
                            it.getInt("id"),
                            it.getInt("price"),
                            it.getInt("session_id"),
                            it.getInt("row"),
                            it.getInt("cell"),
                            it.getInt("account_id"),
                            it.getBoolean("available")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteTicket(int id) {
        try (Connection cn = pool.getConnection();
             PreparedStatement statement =
                     cn.prepareStatement("DELETE FROM ticket WHERE id = ?")) {
            statement.setInt(1, id);
            statement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
