package com.enzuo.web.http.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * @Classname handler
 * @Description
 * @Version 1.0.0
 * @Date 2023/10/6 8:55
 * @Created by Enzuo
 */

public class HttpHandler  extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();

        //http请求编解码器,请求解码，响应编码
        pipeline.addLast("serverCodec", new HttpServerCodec());
        //http请求报文聚合为完整报文，最大请求报文为10M
        pipeline.addLast("aggregator", new HttpObjectAggregator(10 * 1024 * 1024));
        //响应报文压缩
        pipeline.addLast("compress", new HttpContentCompressor());
        //业务处理handler
        pipeline.addLast("serverBusinessHandler",  new HttpServerBusinessHandler());
    }
}
