
public class ColorStatus {
   int num=0;
   String col="";
   
   void setStatus(int num) {
      this.num = num;
   }
   
   void setColor() {
      if(num == 0)
         this.col = "\u001B[30m";
      else if(num == 1)
         this.col = "\u001B[31m";
      else if(num == 2)
         this.col = "\u001B[31m";
   }
}