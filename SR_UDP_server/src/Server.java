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
	final static byte NODATA = (byte)101;	//�����Ͱ� ���°�� �� ���ڷ� �����صд�.
	static byte[] packit = new byte[BUFSIZ];	//����
	static int base = 0;
	
	//UI���� ����
	static ColorStatus colors[] = new ColorStatus[10];   
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_RED = "\u001B[31m";
	static int arr[] = {0,1,2,3,4,5,6,7,8,9};
   //
	
	//���۸� �ʱ�ȭ���ش�
	public static void rdt_recv(){
		for(int i = 0; i < BUFSIZ; i++)
			packit[i] = NODATA;
	}
	
	//UI���� �޼ҵ�
	static void printBuf() {
      System.out.println("����������������������������������������������������������������������������������");
      System.out.println("�� "+colors[0].col+arr[0]+ANSI_RESET+" �� "+colors[1].col+arr[1]+ANSI_RESET+" �� "+colors[2].col+arr[2]+ANSI_RESET+" �� "+colors[3].col+arr[3]+ANSI_RESET+" �� "
                        +colors[4].col+arr[4]+ANSI_RESET+" �� "+colors[5].col+arr[5]+ANSI_RESET+" �� "+colors[6].col+arr[6]+ANSI_RESET+" �� "+colors[7].col+arr[7]+ANSI_RESET+" �� "
                        +colors[8].col+arr[8]+ANSI_RESET+" �� "+colors[9].col+arr[9]+ANSI_RESET+" ��");
      System.out.println("����������������������������������������������������������������������������������");
   }
	static void printwin(int base) {
		for(int i=0; i<base; i++) {
			System.out.print("    ");
		}
		
		System.out.println("��������������������������");
	}

   static void printSend(int i) {
      System.out.println("["+arr[i]+"��"+"ACK"+"]"+"<<<<<<<<<<<");
      colors[i].setStatus(1);
      colors[i].setColor();
      printBuf();
      printwin(base);
   }
   
   static void printRcv(int i) {
      System.out.println("<=========="+"["+arr[i]+"��"+"ACK"+"]");
      colors[i].setStatus(2);
      colors[i].setColor();
      printBuf();
      printwin(base);
   }
   //
    
   
	public void run() {
		try{
			// ��Ʈ 8888���� ����ϴ� ������ �����Ѵ�.
			DatagramSocket socket = new DatagramSocket(8888);
			DatagramPacket inPacket, outPacket;
			System.out.println("UDP���� ����");
			byte[] inMsg = new byte[1];
			byte[] outMsg = new byte[1];
			int packitNum;
			while(true) {
				// �����͸� �����ϱ� ���� ��Ŷ�� �����Ѵ�.
				inPacket = new DatagramPacket(inMsg, inMsg.length);
				// ��Ŷ�� ���� �����͸� ����(receive)�Ѵ�.
				socket.receive(inPacket);
				System.out.println(">>>>>>>>>>>[" + inMsg[0] + "��Pakcet]");
				System.out.println("");
				packitNum = (int)inMsg[0];
				
				//Ŭ���̾�Ʈ�κ��� ���� ��Ŷ�� ���� �����쿡 ���ԵǾ��δ��� Ȯ��
				if(packitNum >= base && packitNum < base + WIN){
					packit[packitNum] = (byte)packitNum;
				
					// ������ ��Ŷ���� ���� client�� IP�ּҿ� Port�� ��´�.
					InetAddress address = inPacket.getAddress();
					int port = inPacket.getPort();
					//������ ���� �ð��� �ú��� ����([hh:mm:ss])�� ��ȯ�Ѵ�.
					//SimpleDateFormat sdf = new SimpleDateFormat("[hh:mm:ss]");
					//String time = sdf.format(new Date());
					//outMsg = time.getBytes(); // time�� byte�迭�� ��ȯ�Ѵ�.
					//��Ŷ�� �����ؼ� client���� ����(send)�Ѵ�.
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
					//System.out.println("�޼��� ���� : " + outMsg[0]);
				}
				
				//base�κ��� �����Ͱ� ������ ++���༭ �����츦 �ű�
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
		
		//UI�κ� �ʱ�ȭ
		for(int j=0; j < colors.length; j++){       //������ ���鼭 �ʱ�ȭ
	         colors[j] = new ColorStatus();
	      }
	      
	      printBuf();
	      printwin(base);
		//
		serv.start(); // UDP������ �����Ų��.
	} // main
}