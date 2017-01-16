package proj.skt;

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
	static String strOutput = "";

	public static void main(String[] args) {
		try {
			String ip = args[0];
			int port = Integer.parseInt(args[1]);
			double txLoss = Double.parseDouble(args[2]);
			double txDelay = Double.parseDouble(args[3]);
			double txJitter = Double.parseDouble(args[4]);

			double rxLoss = Double.parseDouble(args[5]);
			double rxDelay = Double.parseDouble(args[6]);
			double rxJitter = Double.parseDouble(args[7]);

			Messenger messenger = new Messenger(ip, port);

			messenger.sendMsg("[CHECK]/" + txLoss + "/" + txDelay + "/" + txJitter + "/" + rxLoss + "/" + rxDelay + "/" + rxJitter);
			String fromServer = messenger.getMsg();
			if (fromServer.startsWith("[HIT]")) {
				double throughput = Double.parseDouble(fromServer.split("/")[1]);
				System.out.println("txLoss : " + txLoss + " , txDelay : " + txDelay + " , txJitter : " + txJitter + " , rxLoss : " + rxLoss + ", rxDelay : " + rxDelay + ", rxJitter : " + rxJitter);
				System.out.println("-[HIT] Throughput from Cache : " + throughput + " Mbps");
			} else {
				String output = exec((txLoss/100) + "", (txDelay/1000) + "", (txJitter/1000) + "", (rxLoss/100) + "", (rxDelay/1000) + "", (rxJitter/1000) + "");
				System.out.println("txLoss : " + txLoss + " , txDelay : " + txDelay + " , txJitter : " + txJitter + " , rxLoss : " + rxLoss + ", rxDelay : " + rxDelay + ", rxJitter : " + rxJitter);
				System.out.println("-[MISS] Throughput from DCE : " + output);
				messenger.sendMsg(output);
			}

			messenger.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String exec(String... params) throws InterruptedException, IOException {
		String cmd_args = "./shell_sim.sh";
		for (String s : params) {
			cmd_args += " ";
			cmd_args += s;
		}

		Process process = Runtime.getRuntime().exec(cmd_args);
		final InputStream is = process.getInputStream();

		new Thread(new Runnable() {
			public void run() {
				try {
					String line;
					BufferedReader reader = new BufferedReader(new InputStreamReader(is));
					while ((line = reader.readLine()) != null) {
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
		process.waitFor();

		return strOutput;
	}

}
