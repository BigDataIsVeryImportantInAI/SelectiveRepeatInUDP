
	/**
	 * @param args
	 * 컴퓨터 네트워크 정의훈 교수님 텀 과제
	 * 선택적 반복 알고리즘 UDP로 구현
	 * 김은기, 송성근, 조낙원
	 */
import java.net.*;
import java.io.*;
import java.util.Timer;
import java.util.TimerTask;

public class Client extends Thread{
	final static int WIN = 3;//WINDOW LENGHT
	final static int BUFSIZ = 10; //BUFSIZE
	final static byte ACKED = (byte)101; //ACK를 받은경우 패킷안의 데이터를 이걸로 교환
	final static byte ERR = (byte)100;	//ERROR를 보낼 숫자
	static byte[] packit = new byte[BUFSIZ];	//보낼 전체 데이터
	//static byte[] in_window = new byte[WIN];
	static DatagramSocket datagramSocket = null;
	static InetAddress serverAddress = null;
	static Timer[] timer = new Timer[BUFSIZ];
	//Scanner sc = new Scanner(System.in);
	static int base = 0;
	int nextSQnum = 0;
	//int flowNum = 0;
	
	//UI부분 변수 선언
	static ColorStatus colors[] = new ColorStatus[10];   
	public static final String ANSI_RESET = "\u001B[0m";
   	public static final String ANSI_BLACK = "\u001B[30m";
   	public static final String ANSI_GREEN = "\u001B[32m";
   	public static final String ANSI_RED = "\u001B[31m";
	   
	static int arr[] = {0,1,2,3,4,5,6,7,8,9};
	//
	
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
      System.out.println("["+arr[i]+"번"+"Packet"+"]"+">>>>>>>>>>>");
      colors[i].setStatus(1);
      colors[i].setColor();
      printBuf();
      printwin(base);
	}
	
   	static void printRcv(int i) {
      System.out.println("<<<<<<<<<<<"+"["+arr[i]+"번"+"ACK"+"]");
      colors[i].setStatus(2);
      colors[i].setColor();
      printBuf();
      printwin(base);
      System.out.println("");
   	}
   	//
   	//어플리케이션에서 보낼 데이터를 받는다는 함수로 패킷 배열을 초기화
	public static void rdt_send(){
		for(int i = 0; i < BUFSIZ; i++)
			packit[i] = (byte)i;
	}
	
	/*public static void set_window(){
		for(int i = base; i < WIN; i++)
			in_window[i] = (byte)i;
	}*/
	
	//ACK를 받았을경우 WIN 내 패킷을 전부 검사해서 알맞은 timer를 종료한다
	public static void check_win(byte[] msg){
		for(int i = base; i < base + WIN; i++){
			if(packit[i] == msg[0]){
				packit[i] = ACKED;
				//System.out.println("현재 packit[i]값 : "+ i +" : "  + (byte)packit[i]);
				//System.out.println("=====" + msg[0]);
				timer[msg[0]].cancel();
				System.out.println("packet["+msg[0]+"]의 timer 종료");
			}
		}
	}
	public void run(){
		try{
			datagramSocket = new DatagramSocket();
			System.out.println("UDP소켓이 생성되었습니다");
			serverAddress = InetAddress.getByName("127.0.0.1");
			// 데이터가 저장될 공간으로 byte배열을 생성한다.
			
			while(true){
				//sc.next();
				//flowNum = sc.nextInt();
				//window에 있는 버퍼를 전부 하나의 바이트 배열로 만들어서 보냄
				byte[] outmsg = new byte[1];
				if(nextSQnum < BUFSIZ){
					
					//오류 전송부분!!
					//3번째 패킷을 이상한 값으로 보낸다.
					if(nextSQnum == 3){
						outmsg[0] = ERR;
						DatagramPacket outPacket = new DatagramPacket(outmsg, outmsg.length, serverAddress, 8080);
						datagramSocket.send(outPacket); // DatagramPacket을 전송한다.
					}
					//정상 전송 부분
					else{
						outmsg[0] = packit[nextSQnum];
						DatagramPacket outPacket = new DatagramPacket(outmsg, outmsg.length, serverAddress, 8888);
						datagramSocket.send(outPacket); // DatagramPacket을 전송한다.
					}
					//System.out.println(nextSQnum + "번 째 패킷을 보냈습니다");
					printSend(nextSQnum);
					
					timer[nextSQnum] = new Timer();
					timer[nextSQnum].schedule(new WorkTask(nextSQnum), 3000, 3000);
					System.out.println("packit["+nextSQnum+"]의 timer 시작");
					
					nextSQnum++;			//nextSQnum으로 패킷 보내고 타이머 실행했으면 한칸 증가시켜줌
					if(nextSQnum > BUFSIZ) //nextSQnum이 버프 사이즈를 넘어가면 안넘어가게 바꿔줌
						nextSQnum = BUFSIZ;
					//System.out.println("nextSQnum :" + nextSQnum);
				}
				
				
				
				//ack받은것을 확인하여 ack를 받은 베이스가 있으면 베이스 값을 올려준다.
				//2군대에 만들어두었다.
				while(packit[base] == ACKED){
					//System.out.println("현재 packit[base]값 : " + (byte)packit[base]);
					base++;
					if(base >= BUFSIZ - WIN){
						//System.out.println("/class/ifif");
						base = BUFSIZ - WIN;
						
					}
					//System.out.println("/class/ base값이 증가되었습니다 : " + base);
					//System.out.println("현재 packit[base]값 : " + (byte)packit[base]);
					//System.out.println("현재 packit[BUFSIZ-1]값 : " + (byte)packit[BUFSIZ-1]);
					/*if(base == 96){
						datagramSocket.close();
						break;
					}*/
				}
				//System.out.println("현재 packit[BUFSIZ-1]값 : " + (byte)packit[BUFSIZ-1]);
				
				
				//윈도우에 있는 버퍼를 전부 보냈으면 대기
				while(nextSQnum >= base + WIN){
					//System.out.println("nextSQnum이 Window를 벗어났습니다.");
					try {
						this.sleep(100);
						//break;
						if(packit[BUFSIZ-1] == ACKED){
							//System.out.println("/class/ififif");
							datagramSocket.close();
							break;
						}
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				
				//DatagramPacket inPacket = new DatagramPacket(msg, msg.length);
				//datagramSocket.receive(inPacket); // DatagramPacket을 수신한다.
				//System.out.println("current server time :" + new String(inPacket.getData()));
				//datagramSocket.close();
			}
		}
		catch(UnknownHostException e){
			e.printStackTrace();
		}
		
		catch(IOException e){
			e.printStackTrace();
		}
		
	} // start()
	public static void main(String args[]) {
		rdt_send();
		Client clnt = new Client();
		
		//UI부분 초기화
		for(int j=0; j < colors.length; j++){       //루프를 돌면서 초기화
	         colors[j] = new ColorStatus();
	    }
	      
	    printBuf();
	    printwin(base);
		//
		
		clnt.start();
		
		while(true){
			byte[] inmsg = new byte[1];
			try{
				try {
					sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				DatagramPacket inPacket = new DatagramPacket(inmsg, inmsg.length);
				
				datagramSocket.receive(inPacket);
				//System.out.println("ACK을 받았습니다.");
				printRcv((int)inmsg[0]);
				//System.out.println("받은패킷값 : " + inmsg[0]);
				//System.out.println("패킷값 : " + packit[inmsg[0]]);
				/*
				try {
					sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				check_win(inmsg);
				
				//ack받은것을 확인하여 ack를 받은 베이스가 있으면 베이스 값을 올려준다.
				//2군대에 만들어두었다.
				while(packit[base] == ACKED){
					base++;
					if(packit[BUFSIZ-1] == ACKED){
						//System.out.println("/main/ififif");
						datagramSocket.close();
						System.exit(0);
					}
					if(base >= BUFSIZ - WIN){
						//System.out.println("/main/ifif");
						base = BUFSIZ - WIN;
						break;

					}
					//System.out.println("/main/ base값이 증가되었습니다 : " + base);
					/*if(base == 96){
						datagramSocket.close();
						break;
					}*/
				}
			
			}
			catch(NullPointerException e){
				e.printStackTrace();
			}
			catch(UnknownHostException e){
				e.printStackTrace();
			}
			
			catch(IOException e){
				e.printStackTrace();
			}
			
				
		}
		
	} // main
	/*
	static class MyTimer extends Timer{
		int SQnum = 0;
		MyTimer(int SQnum){
			this.SQnum = SQnum;
		}
	}*/
	public static class WorkTask extends TimerTask {
		int SQnum = 0;
		WorkTask(int nextSQnum){
			this.SQnum = nextSQnum;
		}
		@Override
		public void run() {
			byte[] outmsg = new byte[1];
			outmsg[0] = packit[SQnum];
			DatagramPacket outPacket = new DatagramPacket(outmsg, outmsg.length, serverAddress, 8888);
			try {
				datagramSocket.send(outPacket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // DatagramPacket을 전송한다.
			System.out.println(SQnum + "번 째 패킷을 '재전송'했습니다");
			printSend(SQnum);
		}
	}
}

