package com.bn.server;

import io.netty.bootstrap.ServerBootstrap;
import com.bn.util.*;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class WebsocketChatServer 
{
    private int port;

    public WebsocketChatServer(int port) 
    {
        this.port = port;
    }

    public void doTask() throws Exception 
    {
    	//创建用于接收连接请求的多线程事件消息循环组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        //创建用于执行具体业务逻辑的的多线程事件消息循环组
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap(); 
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class) 
             .childHandler(new WebsocketChatServerInitializer())  
             //配置tcp参数，将BACKLOG设置为128
             .option(ChannelOption.SO_BACKLOG, 128)          
             .childOption(ChannelOption.SO_KEEPALIVE, true); 

            System.out.println("3D麻将服务器 启动了...");

            //绑定端口，并调用sync方法阻塞等待绑定工作结束
            ChannelFuture f = b.bind(port).sync(); 

            //调用sync阻塞主线程，保证服务端关闭后main方法才退出
            f.channel().closeFuture().sync();

        } finally 
        {
        	//优雅地关闭线程组
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
