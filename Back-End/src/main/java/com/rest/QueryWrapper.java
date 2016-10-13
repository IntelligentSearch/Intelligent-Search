
public class QueryWrapper {
  public static void main(String[] args) throws Exception{
      java.io.FileInputStream stream = new java.io.FileInputStream("./in1.txt");
      java.io.Reader reader = new java.io.InputStreamReader(stream, "UTF-8");
      Query l = new Query(reader);
      l.main(new String[] {"./in1.txt"});
      System.out.print(l.getString());
  }
}

