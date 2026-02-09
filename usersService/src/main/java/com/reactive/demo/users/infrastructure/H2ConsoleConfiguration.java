package com.reactive.demo.users.infrastructure;

import org.h2.tools.Server;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;

import java.sql.SQLException;

@Configuration
//@Profile({"dev", "test"})
@Profile("!prod & !production")
public class H2ConsoleConfiguration {

    //h2 is a webinterface so it needs a webserver to run
    private Server webServer;



    //method  to start  the webserver when the spring application started

    @EventListener(ApplicationStartedEvent.class)
    public void start() throws SQLException {
        String WEB_PORT = "8082";
        this.webServer = Server.createWebServer("-webPort", WEB_PORT).start();
    }

//    to stop the webserver to stop
    @EventListener(ContextClosedEvent.class)
    public void stop(){
        this.webServer.stop();
    }




}
