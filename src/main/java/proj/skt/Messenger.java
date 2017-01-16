package proj.skt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Messenger {
	private String ip;
	private int port;
	private Socket socket;
	private BufferedReader reader;
	private PrintWriter writer;

	public Messenger(String ip, int port) {
		try {
			this.ip = ip;
			this.port = port;
			
			socket = new Socket(ip, port);
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendMsg(String msg) {
		writer.println(msg);
		writer.flush();
	}

	//TO-DO : call back 형태로 구현 ㄱㄱ 
	public String getMsg() {
		String msg = null;
		try {
			msg = reader.readLine();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return msg;
	}
	
	public void close(){
		try {
			reader.close();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
