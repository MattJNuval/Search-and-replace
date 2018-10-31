import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main {
  public static void main(final String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        final Application frame = new Application();
        frame.setTitle("Mathew's word find and replace");
        frame.setSize(600, 550);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
      }
    });
  }
}
