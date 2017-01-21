	/**
	 * @param args
	 */
import java.net.*;
import java.io.*;
//import java.util.Date;
//import java.text.SimpleDateFormat;

public class Server extends Thread{
	final static int WIN = 3;//WINDOW LENGHT
	final static int BUFSIZ = 10; //BUFSIZE
	final static byte NODATA = (byte)101;	//데이터가 없는경우 이 숫자로 지정해둔다.
	static byte[] packit = new byte[BUFSIZ];	//버퍼
	static int base = 0;
	
	//UI관련 변수
	static ColorStatus colors[] = new ColorStatus[10];   
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_RED = "\u001B[31m";
	static int arr[] = {0,1,2,3,4,5,6,7,8,9};
   //
	
	//버퍼를 초기화해준다
	public static void rdt_recv(){
		for(int i = 0; i < BUFSIZ; i++)
			packit[i] = NODATA;
	}
	
	//UI관련 메소드
	static void printBuf() {
      System.out.println("┌───────────────────────────────────────┐");
      System.out.println("│ "+colors[0].col+arr[0]+ANSI_RESET+" │ "+colors[1].col+arr[1]+ANSI_RESET+" │ "+colors[2].col+arr[2]+ANSI_RESET+" │ "+colors[3].col+arr[3]+ANSI_RESET+" │ "
                        +colors[4].col+arr[4]+ANSI_RESET+" │ "+colors[5].col+arr[5]+ANSI_RESET+" │ "+colors[6].col+arr[6]+ANSI_RESET+" │ "+colors[7].col+arr[7]+ANSI_RESET+" │ "
                        +colors[8].col+arr[8]+ANSI_RESET+" │ "+colors[9].col+arr[9]+ANSI_RESET+" │");
      System.out.println("└───────────────────────────────────────┘");
   }
	static void printwin(int base) {
		for(int i=0; i<base; i++) {
			System.out.print("    ");
		}
		
		System.out.println("└───────────┘");
	}

   static void printSend(int i) {
      System.out.println("["+arr[i]+"번"+"ACK"+"]"+"<<<<<<<<<<<");
      colors[i].setStatus(1);
      colors[i].setColor();
      printBuf();
      printwin(base);
   }
   
   static void printRcv(int i) {
      System.out.println("<=========="+"["+arr[i]+"번"+"ACK"+"]");
      colors[i].setStatus(2);
      colors[i].setColor();
      printBuf();
      printwin(base);
   }
   //
    
   
	public void run() {
		try{
			// 포트 8888번을 사용하는 소켓을 생성한다.
			DatagramSocket socket = new DatagramSocket(8888);
			DatagramPacket inPacket, outPacket;
			System.out.println("UDP소켓 생성");
			byte[] inMsg = new byte[1];
			byte[] outMsg = new byte[1];
			int packitNum;
			while(true) {
				// 데이터를 수신하기 위한 패킷을 생성한다.
				inPacket = new DatagramPacket(inMsg, inMsg.length);
				// 패킷을 통해 데이터를 수신(receive)한다.
				socket.receive(inPacket);
				System.out.println(">>>>>>>>>>>[" + inMsg[0] + "번Pakcet]");
				System.out.println("");
				packitNum = (int)inMsg[0];
				
				//클라이언트로부터 받은 패킷이 서버 윈도우에 포함되어인는지 확인
				if(packitNum >= base && packitNum < base + WIN){
					packit[packitNum] = (byte)packitNum;
				
					// 수신한 패킷으로 부터 client의 IP주소와 Port를 얻는다.
					InetAddress address = inPacket.getAddress();
					int port = inPacket.getPort();
					//서버의 현재 시간을 시분초 형태([hh:mm:ss])로 반환한다.
					//SimpleDateFormat sdf = new SimpleDateFormat("[hh:mm:ss]");
					//String time = sdf.format(new Date());
					//outMsg = time.getBytes(); // time을 byte배열로 변환한다.
					//패킷을 생성해서 client에게 전송(send)한다.
					outMsg = inMsg;
					outPacket = new DatagramPacket(outMsg, outMsg.length, address, port);
					try{
						sleep(500);
					}
					catch(Exception e){
						e.printStackTrace();
					}
					socket.send(outPacket);
					printSend(packitNum);
					//System.out.println("메세지 보냄 : " + outMsg[0]);
				}
				
				//base부분이 데이터가 들어오면 ++해줘서 윈도우를 옮김
				while(packit[base] != NODATA){
					//System.out.println("base : " + base);
					base++;
					if(base >= BUFSIZ - WIN){
						base = BUFSIZ - WIN;
						break;
					}
				}
			}
		}
		catch(IOException e){
			e.printStackTrace();
		}
	} // start()
	public static void main(String args[]) {
		rdt_recv();
		Server serv = new Server();
		
		//UI부분 초기화
		for(int j=0; j < colors.length; j++){       //루프를 돌면서 초기화
	         colors[j] = new ColorStatus();
	      }
	      
	      printBuf();
	      printwin(base);
		//
		serv.start(); // UDP서버를 실행시킨다.
	} // main
}