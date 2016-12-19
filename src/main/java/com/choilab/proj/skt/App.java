package com.choilab.proj.skt;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class App {

	private static final String external_prog = "./shell_sim.sh";
	// private static final String ip = "172.17.0.3";
	static String strOutput = "";

	public static void main(String[] args) {
		// if (args.length != 1) {
		// System.out.println("argument error");
		// System.exit(1);
		// }
		// read file

		// BufferedReader fileReader = null;
		// //String fileName = args[0];
		// String fileName = "./test_input.txt";
		// int randNum = Integer.parseInt(args[0]);
		//
		// try {
		// fileReader = new BufferedReader(new FileReader(fileName));
		// String line = null;
		// fileReader.readLine();
		// int rand = (int) (Math.random() * randNum) + 1;
		// int cnt = 0;
		//
		// double txLoss = -1;
		// double txDelay = -1;
		// double txJitter = -1;
		//
		// double rxLoss = -1;
		// double rxDelay = -1;
		// double rxJitter = -1;
		//
		// // 장비명 그룹명 장비아이피 수집시간 TX_LOSS TX_DELAY TX_JITTER TX 상태 RX_LOSS
		// // RX_DELAY RX_JITTER RX 상태
		// while ((line = fileReader.readLine()) != null) {
		// if (cnt < rand) {
		// cnt++;
		// continue;
		// } else {
		// String[] temp = line.split("\\t");
		// txLoss = Double.parseDouble(temp[4]);
		// txDelay = Double.parseDouble(temp[5]);
		// txJitter = Double.parseDouble(temp[6]);
		//
		// rxLoss = Double.parseDouble(temp[8]);
		// rxDelay = Double.parseDouble(temp[9]);
		// rxJitter = Double.parseDouble(temp[10]);
		// break;
		// }
		//
		// }
		// fileReader.close();

		// String output = exec(external_prog, txLoss+"", txDelay+"",
		// txJitter+"", rxLoss+"", rxDelay+"", rxJitter+"");
		// System.out.println(output);

		try {

			//String ip = "127.0.0.1";
			String ip = args[0];
			int port = Integer.parseInt(args[1]);
			double txLoss = Double.parseDouble(args[2]);
			double txDelay = Double.parseDouble(args[3]);
			double txJitter = Double.parseDouble(args[4]);

			double rxLoss = Double.parseDouble(args[5]);
			double rxDelay = Double.parseDouble(args[6]);
			double rxJitter = Double.parseDouble(args[7]);

			Messenger messenger = new Messenger(ip, port);

			System.out.println("AA");
			messenger.sendMsg("[CHECK]/" + txLoss + "/" + txDelay + "/" + txJitter + "/" + rxLoss + "/" + rxDelay + "/" + rxJitter);
			System.out.println("BB");
			//System.out.println("---txLoss : " + txLoss + " , txDelay : " + txDelay + " , txJitter : " + txJitter + " , rxLoss : " + rxLoss + ", rxDelay : " + rxDelay + ", rxJitter : " + rxJitter);
			String fromServer = messenger.getMsg();
			System.out.println("CC");
			// System.out.println("--From Server : " + fromServer);
			if (fromServer.startsWith("[HIT]")) {
				double throughput = Double.parseDouble(fromServer.split("/")[1]);
				// System.out.println("[HIT]/" + txLoss + "/" + txDelay+"/" +
				// txJitter + "/" + rxLoss + "/" + rxDelay + "/" + rxJitter);
				//System.out.println("--CACHE HIT");
				System.out.println("txLoss : " + txLoss + " , txDelay : " + txDelay + " , txJitter : " + txJitter + " , rxLoss : " + rxLoss + ", rxDelay : " + rxDelay + ", rxJitter : " + rxJitter);
				System.out.println("-[HIT] Throughput from Cache : " + throughput + " Mbps");
			} else {
				//System.out.println("[MISS]");
				//System.out.println("--Execute NS3-DCE .. ");
				String output = exec((txLoss/100) + "", (txDelay/1000) + "", (txJitter/1000) + "", (rxLoss/100) + "", (rxDelay/1000) + "", (rxJitter/1000) + "");
				System.out.println("txLoss : " + txLoss + " , txDelay : " + txDelay + " , txJitter : " + txJitter + " , rxLoss : " + rxLoss + ", rxDelay : " + rxDelay + ", rxJitter : " + rxJitter);
				System.out.println("-[MISS] Throughput from DCE : " + output);
				messenger.sendMsg(output);
			}

			messenger.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		//
		// // communicate with server
		// Socket socket = null;
		// BufferedReader reader = null;
		// PrintWriter writer = null;
		//
		// socket = new Socket(ip, 8888);
		// reader = new BufferedReader(new
		// InputStreamReader(socket.getInputStream()));
		// writer = new PrintWriter(new
		// OutputStreamWriter(socket.getOutputStream()));
		//
		// String toServer = "[CHECK]/" + txLoss + "/" + txDelay + "/" +
		// txJitter + "/" + rxLoss + "/" + rxDelay + "/" + rxJitter;
		// System.out.println(toServer);
		// System.out.println("--Send to server..");
		// writer.println(toServer);
		// writer.flush();
		//
		// String fromServer = reader.readLine();
		// String responseCode = fromServer.split("/")[0];
		// if (responseCode.equals("[ACK]")) {
		// double throughput = Double.parseDouble(fromServer.split("/")[1]);
		// System.out.println("[CACHE HIT] " + " Throughput : " + throughput);
		// } else {
		// System.out.println("[CACHE MISS]");
		// System.out.println("--Try to executue external program (NS3)..");
		// exec(txLoss+"", txDelay+"", txJitter+"", rxLoss+"", rxDelay+"",
		// rxJitter+"");
		// }
		/*
		 * }catch(
		 * 
		 * Exception e) { e.printStackTrace();
		 * 
		 * }
		 */

	}

	public static String exec(String... params) throws InterruptedException, IOException {
		String cmd_args = "./shell_sim.sh";
		for (String s : params) {
			cmd_args += " ";
			cmd_args += s;
		}
		//System.out.println("-- command arguments : " + cmd_args + "\n");

		Process process = Runtime.getRuntime().exec(cmd_args);
		// Process process = new ProcessBuilder(
		// cmd_args).start();
		final InputStream is = process.getInputStream();

		new Thread(new Runnable() {
			public void run() {
				try {
					String line;
					BufferedReader reader = new BufferedReader(new InputStreamReader(is));
					while ((line = reader.readLine()) != null) {
						//System.out.println("- " + line);
						strOutput = line;
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (is != null) {
						try {
							is.close();
						} catch (Exception e) {
							e.getMessage();
						} finally {
						}
					}
				}
			}
		}).start();
		// the outer thread waits for the process to finish
		process.waitFor();

		// System.out.println("[DONE]" + strOutput);
		return strOutput;
	}

}
