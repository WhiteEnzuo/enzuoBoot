package com.enzuo.web.http.server;

import com.enzuo.web.http.handler.HttpHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.bootstrap.HttpServer;

/**
 * @Classname HttpHandler
 * @Description
 * @Version 1.0.0
 * @Date 2023/10/6 8:33
 * @Created by Enzuo
 */
@Slf4j
public class HttpServerSocket{
    private int port;
    private HttpServerSocket(){}
    public HttpServerSocket(int port){
        this.port=port;
    }
    public void start(){
        EventLoopGroup boss = new NioEventLoopGroup(1);
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new HttpHandler());
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            log.info("服务器已开启......");
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }

    }
}


