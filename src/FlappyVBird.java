import javax.swing.*;
public class FlappyVBird {

	public static void main(String[] args) {
		int boardWidth = 360;
		int boardHeight = 640;
		
		JFrame frame = new JFrame("Kuş");
		frame.setVisible(true);
		frame.setSize(boardWidth , boardHeight);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		FlappyJframe flappybird = new FlappyJframe();
		frame.add(flappybird);
		frame.pack();
		flappybird.requestFocus();
		frame.setVisible(true);
		
		
	}

}
