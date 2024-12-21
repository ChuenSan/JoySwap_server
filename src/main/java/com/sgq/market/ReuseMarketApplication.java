package com.sgq.market;

import com.sgq.market.socket.WebSocketServer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import static org.springframework.boot.Banner.Mode.OFF;

@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true)
@MapperScan("com.sgq.market.mapper")
public class ReuseMarketApplication {
  
  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(ReuseMarketApplication.class);
    app.setBannerMode(OFF);
    ConfigurableApplicationContext applicationContext = app.run(args);
    WebSocketServer.setUserService(applicationContext);
  }
  
}
