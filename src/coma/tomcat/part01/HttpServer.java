package coma.tomcat.part01;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {
	
	public static final String WEB_ROOT = System.getProperty("user.dir") + File.separator + "webroot";
	
	private static final String SHUTDOWN_COMMAND = "/SHUTDOWN";
	
	private boolean shutdown = false;
	
	public static void main(String[] args) {
		HttpServer server = new HttpServer();
		server.await();
	}
	
	public void await(){
		ServerSocket serverSocket = null;
		int port = 8080;
		
//		System.out.println("WEB_ROOT =====>" + WEB_ROOT);
		
		try {
			serverSocket = new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		while(!shutdown){
			Socket socket = null;
			InputStream input = null;
			OutputStream output = null;
			
			try {
				socket = serverSocket.accept();
				input = socket.getInputStream();
				output = socket.getOutputStream();
				
				//Request 객체 생성 및 parse 호출
				Request request = new Request(input);
				request.parse();
				
				//Response 객체생성
				Response response = new Response(output);
				response.setRequest(request);
				response.sendStaticResource();
				socket.close();
				
				//URL이 종료 명령이었는지 확인
				shutdown = request.getUri().equals(SHUTDOWN_COMMAND);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
	}
}
