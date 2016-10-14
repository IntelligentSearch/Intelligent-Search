
public class QueryWrapper {
    static String filename;
    String result;
  public static void main(String[] args) throws Exception{
  }
  public static QueryWrapper(String filename) {
      this.filename = filename;
  }
  
  public static String run()  throws Exception {
//       java.io.FileInputStream stream = new java.io.FileInputStream(filename);
//      java.io.Reader reader = new java.io.InputStreamReader(stream, "UTF-8");
//      Query query = new Query(reader);
      Query.main(new String[] {filename});
      //System.out.println(Query.getString() + "l");
      System.out.println("done with this shit");
      return Query.getString();
  }
}

