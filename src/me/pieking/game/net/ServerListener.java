package me.pieking.game.net;

import com.jmr.wrapper.common.Connection;
import com.jmr.wrapper.common.listener.SocketListener;

public class ServerListener implements SocketListener {

	ServerStarter server;
	
	public ServerListener(ServerStarter serv) {
		this.server = serv;
	}
	
	@Override
	public void received(Connection con, Object object) {
//		System.out.println("Received: " + object);
		
		if(object instanceof String){
			try {
				server.recieve((String)object, con);
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

	@Override
	public void connected(Connection con) {
		System.out.println("New client connected.");
		server.awaitingInitialData.add(con);
	}

	@Override
	public void disconnected(Connection con) {
		System.out.println("Client has disconnected.");
		server.removeConnection(con);
	}
	
}