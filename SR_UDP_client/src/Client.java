
	/**
	 * @param args
	 * ��ǻ�� ��Ʈ��ũ ������ ������ �� ����
	 * ������ �ݺ� �˰��� UDP�� ����
	 * ������, �ۼ���, ������
	 */
import java.net.*;
import java.io.*;
import java.util.Timer;
import java.util.TimerTask;

public class Client extends Thread{
	final static int WIN = 3;//WINDOW LENGHT
	final static int BUFSIZ = 10; //BUFSIZE
	final static byte ACKED = (byte)101; //ACK�� ������� ��Ŷ���� �����͸� �̰ɷ� ��ȯ
	final static byte ERR = (byte)100;	//ERROR�� ���� ����
	static byte[] packit = new byte[BUFSIZ];	//���� ��ü ������
	//static byte[] in_window = new byte[WIN];
	static DatagramSocket datagramSocket = null;
	static InetAddress serverAddress = null;
	static Timer[] timer = new Timer[BUFSIZ];
	//Scanner sc = new Scanner(System.in);
	static int base = 0;
	int nextSQnum = 0;
	//int flowNum = 0;
	
	//UI�κ� ���� ����
	static ColorStatus colors[] = new ColorStatus[10];   
	public static final String ANSI_RESET = "\u001B[0m";
   	public static final String ANSI_BLACK = "\u001B[30m";
   	public static final String ANSI_GREEN = "\u001B[32m";
   	public static final String ANSI_RED = "\u001B[31m";
	   
	static int arr[] = {0,1,2,3,4,5,6,7,8,9};
	//
	
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
      System.out.println("["+arr[i]+"��"+"Packet"+"]"+">>>>>>>>>>>");
      colors[i].setStatus(1);
      colors[i].setColor();
      printBuf();
      printwin(base);
	}
	
   	static void printRcv(int i) {
      System.out.println("<<<<<<<<<<<"+"["+arr[i]+"��"+"ACK"+"]");
      colors[i].setStatus(2);
      colors[i].setColor();
      printBuf();
      printwin(base);
      System.out.println("");
   	}
   	//
   	//���ø����̼ǿ��� ���� �����͸� �޴´ٴ� �Լ��� ��Ŷ �迭�� �ʱ�ȭ
	public static void rdt_send(){
		for(int i = 0; i < BUFSIZ; i++)
			packit[i] = (byte)i;
	}
	
	/*public static void set_window(){
		for(int i = base; i < WIN; i++)
			in_window[i] = (byte)i;
	}*/
	
	//ACK�� �޾������ WIN �� ��Ŷ�� ���� �˻��ؼ� �˸��� timer�� �����Ѵ�
	public static void check_win(byte[] msg){
		for(int i = base; i < base + WIN; i++){
			if(packit[i] == msg[0]){
				packit[i] = ACKED;
				//System.out.println("���� packit[i]�� : "+ i +" : "  + (byte)packit[i]);
				//System.out.println("=====" + msg[0]);
				timer[msg[0]].cancel();
				System.out.println("packet["+msg[0]+"]�� timer ����");
			}
		}
	}
	public void run(){
		try{
			datagramSocket = new DatagramSocket();
			System.out.println("UDP������ �����Ǿ����ϴ�");
			serverAddress = InetAddress.getByName("127.0.0.1");
			// �����Ͱ� ����� �������� byte�迭�� �����Ѵ�.
			
			while(true){
				//sc.next();
				//flowNum = sc.nextInt();
				//window�� �ִ� ���۸� ���� �ϳ��� ����Ʈ �迭�� ���� ����
				byte[] outmsg = new byte[1];
				if(nextSQnum < BUFSIZ){
					
					//���� ���ۺκ�!!
					//3��° ��Ŷ�� �̻��� ������ ������.
					if(nextSQnum == 3){
						outmsg[0] = ERR;
						DatagramPacket outPacket = new DatagramPacket(outmsg, outmsg.length, serverAddress, 8080);
						datagramSocket.send(outPacket); // DatagramPacket�� �����Ѵ�.
					}
					//���� ���� �κ�
					else{
						outmsg[0] = packit[nextSQnum];
						DatagramPacket outPacket = new DatagramPacket(outmsg, outmsg.length, serverAddress, 8888);
						datagramSocket.send(outPacket); // DatagramPacket�� �����Ѵ�.
					}
					//System.out.println(nextSQnum + "�� ° ��Ŷ�� ���½��ϴ�");
					printSend(nextSQnum);
					
					timer[nextSQnum] = new Timer();
					timer[nextSQnum].schedule(new WorkTask(nextSQnum), 3000, 3000);
					System.out.println("packit["+nextSQnum+"]�� timer ����");
					
					nextSQnum++;			//nextSQnum���� ��Ŷ ������ Ÿ�̸� ���������� ��ĭ ����������
					if(nextSQnum > BUFSIZ) //nextSQnum�� ���� ����� �Ѿ�� �ȳѾ�� �ٲ���
						nextSQnum = BUFSIZ;
					//System.out.println("nextSQnum :" + nextSQnum);
				}
				
				
				
				//ack�������� Ȯ���Ͽ� ack�� ���� ���̽��� ������ ���̽� ���� �÷��ش�.
				//2���뿡 �����ξ���.
				while(packit[base] == ACKED){
					//System.out.println("���� packit[base]�� : " + (byte)packit[base]);
					base++;
					if(base >= BUFSIZ - WIN){
						//System.out.println("/class/ifif");
						base = BUFSIZ - WIN;
						
					}
					//System.out.println("/class/ base���� �����Ǿ����ϴ� : " + base);
					//System.out.println("���� packit[base]�� : " + (byte)packit[base]);
					//System.out.println("���� packit[BUFSIZ-1]�� : " + (byte)packit[BUFSIZ-1]);
					/*if(base == 96){
						datagramSocket.close();
						break;
					}*/
				}
				//System.out.println("���� packit[BUFSIZ-1]�� : " + (byte)packit[BUFSIZ-1]);
				
				
				//�����쿡 �ִ� ���۸� ���� �������� ���
				while(nextSQnum >= base + WIN){
					//System.out.println("nextSQnum�� Window�� ������ϴ�.");
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
				//datagramSocket.receive(inPacket); // DatagramPacket�� �����Ѵ�.
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
		
		//UI�κ� �ʱ�ȭ
		for(int j=0; j < colors.length; j++){       //������ ���鼭 �ʱ�ȭ
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
				//System.out.println("ACK�� �޾ҽ��ϴ�.");
				printRcv((int)inmsg[0]);
				//System.out.println("������Ŷ�� : " + inmsg[0]);
				//System.out.println("��Ŷ�� : " + packit[inmsg[0]]);
				/*
				try {
					sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				check_win(inmsg);
				
				//ack�������� Ȯ���Ͽ� ack�� ���� ���̽��� ������ ���̽� ���� �÷��ش�.
				//2���뿡 �����ξ���.
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
					//System.out.println("/main/ base���� �����Ǿ����ϴ� : " + base);
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
			} // DatagramPacket�� �����Ѵ�.
			System.out.println(SQnum + "�� ° ��Ŷ�� '������'�߽��ϴ�");
			printSend(SQnum);
		}
	}
}

