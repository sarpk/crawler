package crawl.cr;

public class ThreadBlock {
	   private static ThreadBlock instance = null;
	   protected ThreadBlock() {
	   }
	   public static ThreadBlock getInstance() {
	      if(instance == null) {
	         instance = new ThreadBlock();
	      }
	      return instance;
	   }
}
