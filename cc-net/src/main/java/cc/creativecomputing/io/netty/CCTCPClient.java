package cc.creativecomputing.io.netty;

import java.net.InetSocketAddress;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.io.netty.codec.CCNetCodec;
import cc.creativecomputing.io.netty.codec.CCNetStringCodec;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class CCTCPClient<MessageType> extends CCClient<MessageType>{
	protected final String _myHost;

	public CCTCPClient(CCNetCodec<MessageType> theCodec, String theHost, int thePort) {
		super(theCodec, thePort);
		_myHost = theHost;
	}
	
	
	@Override
	public void createBootstrap(){
		Bootstrap myBootstrap = new Bootstrap();
		myBootstrap.group(_myGroup);
		myBootstrap.channel(NioSocketChannel.class);
		myBootstrap.remoteAddress(new InetSocketAddress(_myHost, _myPort));
		myBootstrap.handler(new ChannelInitializer<SocketChannel>() {

			@Override
			public void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(_myCodec.decoder());
				ch.pipeline().addLast(_myCodec.encoder(), new CCClientHandler());
			}
		});
		try {
			_myFuture = myBootstrap.connect();
			
			if(_myReconnectTime > 0){
				_myFuture.addListener((channelFuture) -> {
					if (_myFuture.isSuccess())return;

					CCLog.info("SCHEDULE RECONNECT");
					scheduleReconnect(_myFuture.channel().eventLoop());
				});
			}
			
			_myFuture.sync();
			_myIsConnected = true;
		} catch (Exception e) {
			e.printStackTrace();
//			throw new CCNetException(e);
		}
	}

	public static void main(String[] args) throws Exception {
		CCTCPClient<String> myClient = new CCTCPClient<String>(new CCNetStringCodec(),"127.0.0.1", 12345);
		myClient.connect();
		myClient.write("texone");
		myClient.write("textwo");
	}
}
